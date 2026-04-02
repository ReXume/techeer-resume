package com.techeer.backend.api.job.adapter.out.search;

public final class JobPostingIndexMapping {

	public static final String INDEX_NAME = "job_postings";

	public static final String MAPPING_JSON = """
		{
		  "settings": {
		    "analysis": {
		      "analyzer": {
		        "nori_analyzer": {
		          "type": "custom",
		          "tokenizer": "nori_tokenizer",
		          "filter": ["nori_part_of_speech", "lowercase"]
		        }
		      }
		    }
		  },
		  "mappings": {
		    "properties": {
		      "title": {
		        "type": "text",
		        "analyzer": "nori_analyzer",
		        "fields": {
		          "keyword": { "type": "keyword" }
		        }
		      },
		      "description": {
		        "type": "text",
		        "analyzer": "nori_analyzer"
		      },
		      "companyName": {
		        "type": "text",
		        "analyzer": "nori_analyzer",
		        "fields": {
		          "keyword": { "type": "keyword" }
		        }
		      },
		      "position": { "type": "keyword" },
		      "experienceLevel": { "type": "keyword" },
		      "requiredSkills": { "type": "keyword" },
		      "location": {
		        "type": "text",
		        "analyzer": "nori_analyzer",
		        "fields": {
		          "keyword": { "type": "keyword" }
		        }
		      },
		      "salaryMin": { "type": "long" },
		      "salaryMax": { "type": "long" },
		      "deadline": { "type": "date" },
		      "status": { "type": "keyword" },
		      "source": { "type": "keyword" },
		      "viewCount": { "type": "long" },
		      "applyClickCount": { "type": "long" },
		      "createdAt": { "type": "date" },
		      "updatedAt": { "type": "date" },
		      "title_suggest": {
		        "type": "completion"
		      },
		      "companyName_suggest": {
		        "type": "completion"
		      },
		      "skill_suggest": {
		        "type": "completion"
		      }
		    }
		  }
		}
		""";

	private JobPostingIndexMapping() {
	}

}
