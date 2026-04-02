import { useSearchParams } from "react-router-dom";
import JobListPage from "./JobListPage";

// JobSearchPage is an alias of JobListPage — the query param `q` is read inside JobListPage.
function JobSearchPage() {
  const [searchParams] = useSearchParams();
  const q = searchParams.get("q");
  // JobListPage already handles /jobs/search?q=... by reading searchParams
  return <JobListPage key={q ?? ""} />;
}

export default JobSearchPage;
