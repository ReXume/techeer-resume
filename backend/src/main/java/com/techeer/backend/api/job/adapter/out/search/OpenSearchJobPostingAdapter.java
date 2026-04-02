package com.techeer.backend.api.job.adapter.out.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techeer.backend.api.job.application.port.out.SearchJobPostingPort;
import com.techeer.backend.api.job.domain.JobPosting;
import com.techeer.backend.api.job.dto.request.SearchCriteria;
import com.techeer.backend.api.job.dto.response.AutocompleteResponse;
import com.techeer.backend.api.job.dto.response.JobSearchResponse;
import com.techeer.backend.api.job.dto.response.JobSearchResponse.JobSearchHit;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch._types.aggregations.Aggregate;
import org.opensearch.client.opensearch._types.aggregations.Aggregation;
import org.opensearch.client.opensearch._types.aggregations.StringTermsBucket;
import org.opensearch.client.opensearch._types.query_dsl.BoolQuery;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScore;
import org.opensearch.client.opensearch._types.query_dsl.FunctionScoreQuery;
import org.opensearch.client.opensearch._types.query_dsl.MatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.MoreLikeThisQuery;
import org.opensearch.client.opensearch._types.query_dsl.MultiMatchQuery;
import org.opensearch.client.opensearch._types.query_dsl.Query;
import org.opensearch.client.opensearch._types.query_dsl.RangeQuery;
import org.opensearch.client.opensearch._types.query_dsl.TermQuery;
import org.opensearch.client.opensearch._types.query_dsl.TermsQuery;
import org.opensearch.client.opensearch._types.query_dsl.TermsQueryField;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.core.search.CompletionSuggest;
import org.opensearch.client.opensearch.core.search.CompletionSuggester;
import org.opensearch.client.opensearch.core.search.FieldSuggester;
import org.opensearch.client.opensearch.core.search.Hit;
import org.opensearch.client.opensearch.core.search.Suggester;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.ExistsRequest;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OpenSearchJobPostingAdapter implements SearchJobPostingPort {

	private static final String INDEX = JobPostingIndexMapping.INDEX_NAME;

	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

	private final OpenSearchClient client;

	private final ObjectMapper objectMapper;

	@PostConstruct
	public void ensureIndexExists() {
		try {
			boolean exists = client.indices().exists(ExistsRequest.of(e -> e.index(INDEX))).value();
			if (!exists) {
				client.indices().create(CreateIndexRequest.of(c -> c
					.index(INDEX)));
				log.info("Created OpenSearch index: {}", INDEX);
			}
		}
		catch (IOException e) {
			log.warn("Failed to ensure OpenSearch index exists: {}", e.getMessage());
		}
	}

	@Override
	public void indexJobPosting(JobPosting jobPosting) {
		Map<String, Object> doc = buildDocument(jobPosting);
		try {
			client.index(IndexRequest.of(i -> i
				.index(INDEX)
				.id(String.valueOf(jobPosting.getId()))
				.document(doc)));
		}
		catch (IOException e) {
			log.error("Failed to index job posting id={}: {}", jobPosting.getId(), e.getMessage());
		}
	}

	@Override
	public JobSearchResponse searchJobPostings(SearchCriteria criteria) {
		List<Query> mustQueries = new ArrayList<>();
		List<Query> filterQueries = new ArrayList<>();

		if (criteria.query() != null && !criteria.query().isBlank()) {
			mustQueries.add(Query.of(q -> q.multiMatch(MultiMatchQuery.of(m -> m
				.query(criteria.query())
				.fields(List.of("title^3", "description^1", "companyName^2", "requiredSkills^2"))))));
		}

		if (criteria.position() != null) {
			filterQueries.add(termFilter("position", criteria.position()));
		}
		if (criteria.experienceLevel() != null) {
			filterQueries.add(termFilter("experienceLevel", criteria.experienceLevel()));
		}
		if (criteria.source() != null) {
			filterQueries.add(termFilter("source", criteria.source()));
		}
		if (criteria.location() != null) {
			filterQueries.add(Query.of(q -> q.match(MatchQuery.of(m -> m
				.field("location")
				.query(FieldValue.of(criteria.location()))))));
		}
		if (criteria.skills() != null && !criteria.skills().isEmpty()) {
			List<FieldValue> skillValues = criteria.skills().stream()
				.map(FieldValue::of)
				.toList();
			filterQueries.add(Query.of(q -> q.terms(TermsQuery.of(t -> t
				.field("requiredSkills")
				.terms(TermsQueryField.of(tf -> tf.value(skillValues)))))));
		}
		if (criteria.salaryMin() != null || criteria.salaryMax() != null) {
			filterQueries.add(buildSalaryRangeFilter(criteria.salaryMin(), criteria.salaryMax()));
		}

		BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
		if (!mustQueries.isEmpty()) {
			boolBuilder.must(mustQueries);
		}
		else {
			boolBuilder.must(Query.of(q -> q.matchAll(m -> m)));
		}
		boolBuilder.filter(filterQueries);

		Query baseQuery = Query.of(q -> q.bool(boolBuilder.build()));

		// function_score for recency decay using script_score as portable alternative
		Query finalQuery = Query.of(q -> q.functionScore(FunctionScoreQuery.of(fs -> fs
			.query(baseQuery)
			.functions(FunctionScore.of(f -> f
				.scriptScore(ss -> ss
					.script(sc -> sc.inline(i -> i
						.source("Math.max(0, 1.0 - (System.currentTimeMillis() - doc['createdAt'].value.millis) / (30.0 * 86400000))")
						.lang("painless")))))))));

		int from = criteria.page() * criteria.size();

		Map<String, Aggregation> aggregations = buildFacetAggregations();

		try {
			int size = criteria.size();
			int page = criteria.page();
			SearchResponse<Map> response = client.search(SearchRequest.of(s -> s
				.index(INDEX)
				.query(finalQuery)
				.from(from)
				.size(size)
				.aggregations(aggregations)), Map.class);

			List<JobSearchHit> hits = response.hits().hits().stream()
				.map(this::toHit)
				.toList();

			long total = response.hits().total() != null ? response.hits().total().value() : 0L;
			Map<String, Map<String, Long>> facets = extractFacets(response.aggregations());

			return JobSearchResponse.builder()
				.results(hits)
				.facets(facets)
				.totalCount(total)
				.page(page)
				.size(size)
				.totalPages(size > 0 ? (int) Math.ceil((double) total / size) : 0)
				.build();
		}
		catch (IOException e) {
			log.error("Search failed: {}", e.getMessage());
			return emptyResponse(criteria.page(), criteria.size());
		}
	}

	@Override
	public AutocompleteResponse autocomplete(String prefix) {
		try {
			SearchResponse<Map> response = client.search(SearchRequest.of(s -> s
				.index(INDEX)
				.suggest(Suggester.of(sg -> sg
					.text(prefix)
					.suggesters("title_suggest", FieldSuggester.of(fs -> fs
						.completion(CompletionSuggester.of(c -> c
							.field("title_suggest")
							.size(5)))))
					.suggesters("companyName_suggest", FieldSuggester.of(fs -> fs
						.completion(CompletionSuggester.of(c -> c
							.field("companyName_suggest")
							.size(5)))))
					.suggesters("skill_suggest", FieldSuggester.of(fs -> fs
						.completion(CompletionSuggester.of(c -> c
							.field("skill_suggest")
							.size(5)))))))),
				Map.class);


			List<String> suggestions = new ArrayList<>();
			extractSuggestions(response, "title_suggest", suggestions);
			extractSuggestions(response, "companyName_suggest", suggestions);
			extractSuggestions(response, "skill_suggest", suggestions);

			return AutocompleteResponse.builder().suggestions(suggestions).build();
		}
		catch (IOException e) {
			log.error("Autocomplete failed: {}", e.getMessage());
			return AutocompleteResponse.builder().suggestions(List.of()).build();
		}
	}

	@Override
	public JobSearchResponse findSimilar(Long jobPostingId, int page, int size) {
		try {
			SearchResponse<Map> response = client.search(SearchRequest.of(s -> s
				.index(INDEX)
				.query(Query.of(q -> q.moreLikeThis(MoreLikeThisQuery.of(mlt -> mlt
					.fields(List.of("title", "description", "requiredSkills", "position"))
					.like(l -> l.document(d -> d.index(INDEX).id(String.valueOf(jobPostingId))))
					.minTermFreq(1)
					.maxQueryTerms(12)))))
				.from(page * size)
				.size(size)), Map.class);

			List<JobSearchHit> hits = response.hits().hits().stream()
				.map(this::toHit)
				.toList();

			long total = response.hits().total() != null ? response.hits().total().value() : 0L;

			return JobSearchResponse.builder()
				.results(hits)
				.facets(Map.of())
				.totalCount(total)
				.page(page)
				.size(size)
				.totalPages(size > 0 ? (int) Math.ceil((double) total / size) : 0)
				.build();
		}
		catch (IOException e) {
			log.error("findSimilar failed: {}", e.getMessage());
			return emptyResponse(page, size);
		}
	}

	@Override
	public void deleteFromIndex(Long id) {
		try {
			client.delete(d -> d.index(INDEX).id(String.valueOf(id)));
		}
		catch (IOException e) {
			log.error("Failed to delete job posting id={} from index: {}", id, e.getMessage());
		}
	}

	// --- helpers ---

	private Map<String, Object> buildDocument(JobPosting jobPosting) {
		Map<String, Object> doc = new HashMap<>();
		doc.put("title", jobPosting.getTitle());
		doc.put("description", jobPosting.getContents());
		doc.put("companyName", jobPosting.getCompany().getName());
		doc.put("position", "");
		doc.put("experienceLevel", jobPosting.getExpYears() != null ? String.valueOf(jobPosting.getExpYears()) : "");
		doc.put("requiredSkills", List.of());
		doc.put("location", "");

		// salary from embedded SalaryRange
		Long salaryMin = 0L;
		Long salaryMax = 0L;
		if (jobPosting.getSalaryRange() != null) {
			salaryMin = jobPosting.getSalaryRange().getMin() != null ? jobPosting.getSalaryRange().getMin() : 0L;
			salaryMax = jobPosting.getSalaryRange().getMax() != null ? jobPosting.getSalaryRange().getMax() : 0L;
		}
		doc.put("salaryMin", salaryMin);
		doc.put("salaryMax", salaryMax);

		doc.put("status", jobPosting.getStatus().name());

		// source from SourceInfo if available, fallback to sourceType
		String source = jobPosting.getSourceInfo() != null && jobPosting.getSourceInfo().getSource() != null
			? jobPosting.getSourceInfo().getSource().name()
			: jobPosting.getSourceType().name();
		doc.put("source", source);

		doc.put("viewCount", jobPosting.getViewCount() != null ? jobPosting.getViewCount() : 0L);
		doc.put("applyClickCount", jobPosting.getApplyClickCount() != null ? jobPosting.getApplyClickCount() : 0L);

		if (jobPosting.getCreatedAt() != null) {
			doc.put("createdAt", jobPosting.getCreatedAt().format(DATE_FMT));
		}
		if (jobPosting.getUpdatedAt() != null) {
			doc.put("updatedAt", jobPosting.getUpdatedAt().format(DATE_FMT));
		}

		// completion suggesters
		doc.put("title_suggest", Map.of("input", List.of(jobPosting.getTitle())));
		doc.put("companyName_suggest", Map.of("input", List.of(jobPosting.getCompany().getName())));
		doc.put("skill_suggest", Map.of("input", List.of()));
		return doc;
	}

	@SuppressWarnings("unchecked")
	private JobSearchHit toHit(Hit<Map> hit) {
		Map<String, Object> src = hit.source() != null ? hit.source() : Map.of();
		return JobSearchHit.builder()
			.id(hit.id() != null ? Long.parseLong(hit.id()) : null)
			.title((String) src.getOrDefault("title", ""))
			.companyName((String) src.getOrDefault("companyName", ""))
			.position((String) src.getOrDefault("position", ""))
			.experienceLevel((String) src.getOrDefault("experienceLevel", ""))
			.requiredSkills((List<String>) src.getOrDefault("requiredSkills", List.of()))
			.location((String) src.getOrDefault("location", ""))
			.salaryMin(toLong(src.get("salaryMin")))
			.salaryMax(toLong(src.get("salaryMax")))
			.deadline((String) src.getOrDefault("deadline", null))
			.status((String) src.getOrDefault("status", ""))
			.source((String) src.getOrDefault("source", ""))
			.viewCount(toLong(src.get("viewCount")))
			.applyClickCount(toLong(src.get("applyClickCount")))
			.createdAt((String) src.getOrDefault("createdAt", null))
			.score(hit.score() != null ? hit.score() : 0.0)
			.build();
	}

	private long toLong(Object val) {
		if (val == null) return 0L;
		if (val instanceof Number n) return n.longValue();
		try {
			return Long.parseLong(val.toString());
		}
		catch (NumberFormatException e) {
			return 0L;
		}
	}

	private Query termFilter(String field, String value) {
		return Query.of(q -> q.term(TermQuery.of(t -> t.field(field).value(FieldValue.of(value)))));
	}

	private Query buildSalaryRangeFilter(Long min, Long max) {
		return Query.of(q -> q.range(RangeQuery.of(r -> {
			r.field("salaryMin");
			if (min != null) r.gte(JsonData.of(min));
			if (max != null) r.lte(JsonData.of(max));
			return r;
		})));
	}

	private Map<String, Aggregation> buildFacetAggregations() {
		Map<String, Aggregation> aggs = new LinkedHashMap<>();
		for (String field : List.of("position", "experienceLevel", "source", "requiredSkills")) {
			aggs.put(field + "_facet", Aggregation.of(a -> a
				.terms(t -> t.field(field).size(20))));
		}
		return aggs;
	}

	private Map<String, Map<String, Long>> extractFacets(Map<String, Aggregate> aggregations) {
		Map<String, Map<String, Long>> facets = new LinkedHashMap<>();
		if (aggregations == null) return facets;
		for (String facetKey : List.of("position_facet", "experienceLevel_facet", "source_facet", "requiredSkills_facet")) {
			Aggregate agg = aggregations.get(facetKey);
			if (agg == null) continue;
			Map<String, Long> counts = new LinkedHashMap<>();
			if (agg.isSterms()) {
				for (StringTermsBucket bucket : agg.sterms().buckets().array()) {
					counts.put(bucket.key(), bucket.docCount());
				}
			}
			String displayKey = facetKey.replace("_facet", "");
			facets.put(displayKey, counts);
		}
		return facets;
	}

	@SuppressWarnings("unchecked")
	private void extractSuggestions(SearchResponse<Map> response, String name, List<String> out) {
		if (response.suggest() == null) return;
		var suggests = response.suggest().get(name);
		if (suggests == null) return;
		for (var suggest : suggests) {
			if (suggest.isCompletion()) {
				CompletionSuggest<Map> cs = suggest.completion();
				cs.options().forEach(opt -> {
					if (opt.text() != null) {
						out.add(opt.text());
					}
				});
			}
		}
	}

	private JobSearchResponse emptyResponse(int page, int size) {
		return JobSearchResponse.builder()
			.results(List.of())
			.facets(Map.of())
			.totalCount(0L)
			.page(page)
			.size(size)
			.totalPages(0)
			.build();
	}

}
