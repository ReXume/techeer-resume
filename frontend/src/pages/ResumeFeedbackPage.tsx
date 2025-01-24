import { useEffect, useState } from "react";
import Layout from "../components/Layout/Layout";
import MainContainer from "../components/resumeoverview/MainContainer";
import ResumeOverview from "../components/resumeoverview/ResumeOverview";
import CommentSection from "../components/comment/CommentSection.tsx";
import LoadingSpinner from "../components/UI/LoadingSpinner";
import ErrorMessage from "../components/UI/ErrorMessage";
import {
  addFeedbackApi,
  deleteFeedbackApi,
  getResumeApi,
  postAiFeedback,
} from "../api/feedbackApi.ts";
import { AddFeedbackPoint, FeedbackPoint, ResumeData } from "../types.ts";
import useResumeStore from "../store/ResumeStore.ts";
import { useParams } from "react-router-dom";
import { Bookmark, BookmarkMinus } from "lucide-react";
import { postBookmark, deleteBookmarkById } from "../api/bookMarkApi.ts";

function ResumeFeedbackPage() {
  const [resumeData, setResumeData] = useState<ResumeData | null>(null); //이력서 데이터를 관리
  const [feedbackPoints, setFeedbackPoints] = useState<FeedbackPoint[]>([]); //피드백 데이터를 관리
  const [hoveredCommentId, setHoveredCommentId] = useState<number | null>(null); //마우스 호버시 코멘트 아이디를 관리
  const [loading, setLoading] = useState<boolean>(true); //로딩 상태를 관리
  const [error, setError] = useState<string | null>(null); //에러 상태를 관리
  const { setResumeUrl } = useResumeStore(); //이력서 아이디와 이력서 URL을 관리 -> 이게 왜 필요한지 모르겠음
  const { id } = useParams(); // useParams로 URL의 id 파라미터 가져오기
  const [isBookmarked, setIsBookmarked] = useState<boolean>(false); // 북마크 상태
  const [bookmarkId, setBookmarkId] = useState<number | null>(null); // 북마크 ID 저장

  useEffect(() => {
    const fetchData = async () => {
      console.log("api호출");
      if (!id) {
        setError("Resume ID is missing.");
        return;
      }

      try {
        setLoading(true);
        setError(null);
        const data = await getResumeApi(Number(id)); //여기서 레쥬메 아이디로 관리를 해서 데이터를 불러옴
        setResumeData(data);
        // 북마크 상태 초기화
        const bookmarkResponse = await postBookmark(Number(id)); // userId 제거, resumeId만 사용
        if (bookmarkResponse) {
          setIsBookmarked(true);
          setBookmarkId(bookmarkResponse);
        }
        setResumeUrl(data.fileUrl);
        console.log("id:", id);
        setFeedbackPoints(data.feedbackResponses || []); // 관련된 설정들
      } catch (error) {
        console.error("Failed to fetch resume data", error);
        setError("Failed to fetch resume data. Please try again later.");
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id]);

  // 북마크 토글 기능
  const toggleBookmark = async () => {
    if (!id) {
      setError("Resume ID is missing.");
      return;
    }

    try {
      if (isBookmarked) {
        // 북마크 해제
        if (bookmarkId !== null) {
          await deleteBookmarkById(bookmarkId);
        }
        setIsBookmarked(false);
        setBookmarkId(null);
      } else {
        // 북마크 추가
        const response = await postBookmark(Number(id)); // userId 제거, resumeId만 사용
        console.log("포스트북마크 값", response);
        setIsBookmarked(true);
        setBookmarkId(response); // 서버로부터 받은 북마크 ID 저장
        console.log("북마크 아이디 생성", response);
      }
    } catch (error) {
      console.error("Failed to toggle bookmark", error);
      alert("북마크 상태를 변경할 수 없습니다. 다시 시도해주세요.");
    }
  };

  const handleAiFeedback = async () => {
    setLoading(true);
    try {
      const aiFeedback = await postAiFeedback(Number(id));
      setFeedbackPoints((prevPoints) => [
        ...prevPoints,
        ...aiFeedback.feedbacks,
      ]);
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
    } catch (error) {
      setError("Failed to retrieve AI feedback.");
    } finally {
      setLoading(false);
    }
  };

  const addFeedbackPoint = async (point: Omit<AddFeedbackPoint, "id">) => {
    if (
      !point.content ||
      point.xCoordinate === undefined ||
      point.yCoordinate === undefined
    ) {
      setError("All fields are required to add a feedback point.");
      return;
    }

    try {
      setLoading(true);
      const newPoint: AddFeedbackPoint = {
        xCoordinate: point.xCoordinate,
        yCoordinate: point.yCoordinate,
        content: point.content,
        pageNumber: 1,
      };
      await addFeedbackApi(Number(id), newPoint);
      const updatedData = await getResumeApi(Number(id));
      console.log("업데이트 데이터: ", updatedData);
      setFeedbackPoints(updatedData.feedbackResponses);
    } catch (error) {
      console.error("Failed to add feedback point", error);
      setError("Failed to add feedback point. Please try again later.");
    } finally {
      setLoading(false);
    }
  };

  const deleteFeedbackPoint = async (id: number) => {
    try {
      setLoading(true);
      setError(null);

      // 피드백 점 삭제 API 호출
      await deleteFeedbackApi(Number(id), id);

      // 삭제 후 상태 갱신
      setFeedbackPoints((prevComments) =>
        (prevComments || []).filter((item) => item.id !== id)
      );
      console.log("Deleted feedback point: ", id);
    } catch (error) {
      console.error("Failed to delete feedback point", error);
      setError("Failed to delete feedback point. Please try again later.");
    } finally {
      setLoading(false);
    }
  };

  const editFeedbackPoint = (updatedItem: AddFeedbackPoint) => {
    console.log("Edit feedback point: ", updatedItem);
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return <ErrorMessage message={error} />;
  }

  if (!resumeData) {
    return <div>No resume data available.</div>;
  }

  return (
    <div className="flex flex-col flex-grow ">
      <Layout
        sidebar={
          <div className="flex flex-col justify-between bg-white p-2 mt-10">
            {/* 북마크 버튼 */}
            <button
              onClick={toggleBookmark}
              className={`flex items-center px-6 py-3 rounded-lg ${
                isBookmarked
                  ? "bg-yellow-100 text-yellow-900"
                  : "text-gray-500 hover:bg-gray-50"
              }`}
            >
              {isBookmarked ? (
                <>
                  {/* <BookmarkMinus className="w-5 h-5 mr-2" />
                  북마크 제거 */}
                  <Bookmark className="w-5 h-5 mr-2" />
                  북마크 추가
                </>
              ) : (
                <>
                  {/* <Bookmark className="w-5 h-5 mr-2" />
                  북마크 추가 */}
                  <BookmarkMinus className="w-5 h-5 mr-2" />
                  북마크 제거
                </>
              )}
            </button>

            {/* Resume Overview */}
            <ResumeOverview
              userName={resumeData.userName}
              position={resumeData.position}
              career={resumeData.career}
              techStackNames={resumeData.techStackNames}
              fileUrl={resumeData.fileUrl}
              isLoading={loading}
            />

            {/* Comment Section */}
            <div className="overflow-y-auto mt-2">
              <CommentSection
                feedbackPoints={feedbackPoints}
                addFeedbackPoint={addFeedbackPoint}
                deleteFeedbackPoint={deleteFeedbackPoint}
                editFeedbackPoint={editFeedbackPoint}
                hoveredCommentId={hoveredCommentId}
                handleAiFeedback={handleAiFeedback}
                setHoveredCommentId={setHoveredCommentId}
              />
            </div>
          </div>
        }
      >
        <MainContainer
          feedbackPoints={feedbackPoints}
          addFeedbackPoint={addFeedbackPoint}
          deleteFeedbackPoint={deleteFeedbackPoint}
          editFeedbackPoint={editFeedbackPoint}
          hoveredCommentId={hoveredCommentId}
          setHoveredCommentId={setHoveredCommentId}
          laterResumeId={resumeData.laterResumeId}
          previousResumeId={resumeData.previousResumeId}
        />
      </Layout>
    </div>
  );
}

export default ResumeFeedbackPage;
