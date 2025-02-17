import { useState, useEffect, useRef, useCallback } from "react";
import { useInfiniteQuery } from "@tanstack/react-query";
import { getResumeList, viewResume } from "../api/resumeApi";
import { useNavigate } from "react-router-dom";
import Navbar from "../components/common/Navbar";
import BannerCard from "../components/MainPage/BannerCard";
import Category from "../components/MainPage/Category";
import PostCard from "../components/common/PostCard";
import man1 from "../assets/man1.webp";
import man2 from "../assets/man2.webp";
import PositionModal from "../components/Search/PositionModal";
import CareerModal from "../components/Search/CareerModal";
import useFilterStore from "../store/useFilterStore";
import { PostCardsType } from "../dataType.ts";
import useResumeStore from "../store/ResumeStore.ts";

function MainPage() {
  const navigate = useNavigate();
  const { setResumeId } = useResumeStore();
  const moveToResume = async (resumeId: number) => {
    try {
      const response = await viewResume(resumeId);
      setResumeId(response.resume_id);
      navigate(`/feedback/${response.resume_id}`);
      return response;
    } catch (error) {
      console.error("이력서 조회 오류:", error);
      throw error;
    }
  };

  const [isPositionOpen, setIsPositionOpen] = useState(false);
  const [isCareerOpen, setIsCareerOpen] = useState(false);

  const [positionTitle, setPositionTitle] = useState("포지션"); // 카테고리에 표시될 포지션 제목
  const [careerTitle, setCareerTitle] = useState("경력"); // 경력 카테고리 제목
  const { positions, min_career, max_career, setCareerRange, setPositions } =
    useFilterStore();
  const [filteredData, setFilteredData] = useState<PostCardsType[] | null>(
    null
  ); // 필터링된 데이터를 저장

  const fetchPostCards = async (page: number, size = 8) => {
    try {
      const resumeList = await getResumeList(page, size);
      return resumeList;
    } catch (error) {
      console.error("포스트카드 조회 오류:", error);
      throw error;
    }
  };

  const { data, fetchNextPage, hasNextPage, isFetchingNextPage } =
    useInfiniteQuery({
      queryKey: ["postCards"],
      queryFn: async ({ pageParam = 0 }) => {
        return fetchPostCards(pageParam);
      },
      getNextPageParam: (lastPage, allPages) => {
        // 응답 데이터가 빈 배열이 아니면 다음 페이지를 요청
        if (lastPage.length > 0) {
          return allPages.length; // 다음 페이지 번호
        } else {
          return undefined; // 더 이상 요청할 페이지 없음
        }
      },
      initialPageParam: 0,
    });

  const handleApplyPosition = (selectedPosition: string | null) => {
    setPositions(selectedPosition ? [selectedPosition] : []); // 상태 업데이트
    setPositionTitle(selectedPosition || "포지션"); // 선택된 포지션을 제목에 반영
    setIsPositionOpen(false); // 모달 닫기
  };

  const handleApplyCareer = (min: number, max: number) => {
    setCareerRange(min, max);
    setCareerTitle(`${min}년 ~ ${max}년`); // 경력 범위를 제목에 반영
    setIsCareerOpen(false); // 모달 닫기
  };

  const applyFilters = useCallback(() => {
    if (!data?.pages) return; // data가 준비되지 않으면 필터링을 실행하지 않음

    const filteredData = data.pages.flatMap((page) =>
      page.filter((post: PostCardsType) => {
        // 포지션 필터링: 필터가 적용되었으면 해당 포지션에 일치해야 함
        const positionMatch =
          positionTitle === "포지션" ? true : positions.includes(post.position);

        // 경력 필터링: 필터가 적용되었으면 경력 범위에 속해야 함
        const careerMatch =
          careerTitle === "경력"
            ? true
            : post.career >= min_career && post.career <= max_career;

        return positionMatch && careerMatch;
      })
    );

    setFilteredData(filteredData);
  }, [
    data?.pages,
    positions,
    min_career,
    max_career,
    positionTitle,
    careerTitle,
  ]);

  useEffect(() => {
    if (data?.pages) {
      applyFilters();
    }
  }, [data?.pages, positions, min_career, max_career, applyFilters]);

  useEffect(() => {
    applyFilters();
  }, [positions, min_career, max_career, applyFilters]);

  const loadMoreRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (!hasNextPage || isFetchingNextPage) return;
    const observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting) {
          fetchNextPage();
        }
      },
      { root: null, rootMargin: "0px", threshold: 0.1 }
    );

    const loadMoreCurrent = loadMoreRef.current;
    if (loadMoreCurrent) observer.observe(loadMoreCurrent);

    return () => {
      if (loadMoreCurrent) observer.unobserve(loadMoreCurrent);
    };
  }, [hasNextPage, fetchNextPage, isFetchingNextPage]);

  return (
    <div className="w-full bg-[#D7E1F5]">
      <div className="pt-5">
        <Navbar />
        <div className="flex justify-center p-5 space-x-4 max-w-screen-xl mx-auto">
          <BannerCard
            title="내가 지원할 기업은?"
            comment="채용 공고를 한 번에 볼 수 있습니다."
            btncomment="지금 확인하기"
            imgurl={man1}
            pageurl=""
          />
          <BannerCard
            title={
              <>
                이력서 피드백이 <br />
                필요할때?
              </>
            }
            comment="이력서를 등록하고 피드백을 받을 수 있습니다."
            btncomment="등록하러 가기"
            imgurl={man2}
            pageurl="upload"
          />
        </div>
      </div>

      <div className="w-full bg-white relative">
        <div className="p-6">
          <div className="max-w-screen-xl mx-auto py-6 relative">
            <div className="flex space-x-4">
              <Category title="조회순" options={["인기순", "최신순"]} />
              <Category
                title={positionTitle} // 선택한 포지션 반영
                onClick={() => setIsPositionOpen(true)}
              />
              <Category
                title={careerTitle} // 선택한 경력 범위 반영
                onClick={() => setIsCareerOpen(true)}
              />
            </div>

            {isPositionOpen && (
              <PositionModal
                isOpen={isPositionOpen}
                onClose={() => setIsPositionOpen(false)}
                onApply={handleApplyPosition}
              />
            )}
            {isCareerOpen && (
              <CareerModal
                isOpen={isCareerOpen}
                onClose={() => setIsCareerOpen(false)}
                onApply={handleApplyCareer} // 선택된 경력 범위 처리
              />
            )}
          </div>

          <div className="flex justify-center">
            {filteredData && filteredData.length > 0 ? (
              <div className="grid grid-cols-1 min-[700px]:grid-cols-1 md:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4 gap-6 p-5">
                {filteredData.map((post: PostCardsType) => (
                  <PostCard
                    key={post.resume_id}
                    name={post.user_name}
                    role={post.position}
                    experience={post.career}
                    skills={post.tech_stack_names}
                    onClick={() => moveToResume(Number(post.resume_id))}
                  />
                ))}
              </div>
            ) : (
              <div className="flex justify-center items-center text-center my-8 text-lg">
                카테고리에 해당하는 이력서가 없습니다
              </div>
            )}
          </div>

          <div ref={loadMoreRef} className="h-1" />
        </div>
      </div>
    </div>
  );
}

export default MainPage;
