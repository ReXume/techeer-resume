// <reference types="vitest" />
import { vi } from "vitest";
import { create } from "zustand";

// 1. CategoryStore 모킹
vi.mock("../../store/CategoryStore", () => {
  const useMockStore = create((set) => ({
    positionCategory: [],
    careerCategory: [],
    setPositionCategory: (positions: string[]) =>
      set({ positionCategory: positions }),
    setCareerCategory: (career: string[]) => set({ careerCategory: career }),
    deleteCareer: () => set({ careerCategory: [] }),
    deletePosition: () => set({ positionCategory: [] }),
  }));
  // 모킹하기 위해 생성
  return {
    __esModule: true,
    default: useMockStore,
    //2. useMockStore 이름의 모듈로 반환
  };
});

import { render, screen, fireEvent } from "@testing-library/react";
import "@testing-library/jest-dom";
import { describe, it, expect, beforeEach } from "vitest";
import PositionModal from "../../components/Search/PositionModal";
import useCategoryStore from "../../store/CategoryStore";
// 1. 필요한 모듈 import

describe("PositionModal 컴포넌트 테스트", () => {
  let onClose: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    onClose = vi.fn();
    // 각 테스트 전에 스토어 상태를 초기화
    useCategoryStore.setState({ positionCategory: [], careerCategory: [] });
  });

  it("모달 렌더링 확인", () => {
    // Given : 사용자가 모달을 사용하고 싶음
    render(<PositionModal isOpen={true} onClose={onClose} />);

    //  Then: 모달 타이틀과 모든 포지션이 렌더링
    expect(screen.getByText("포지션")).toBeInTheDocument();

    const positions = [
      "개발 전체",
      "소프트웨어 엔지니어",
      "웹 개발자",
      "서버 개발자",
      "DevOps",
      "프론트엔드 개발자",
      "안드로이드 개발자",
      "IOS 개발자",
      "임베디드 개발자",
      "데이터 엔지니어",
    ];

    positions.forEach((position) => {
      expect(screen.getByText(position)).toBeInTheDocument();
    });
  });

  it("포지션 선택 기능 확인", () => {
    // Given: 모달이 열려있는 상태
    render(<PositionModal isOpen={true} onClose={onClose} />);

    const positionToSelect = "웹 개발자";
    const positionElement = screen.getByText(positionToSelect);

    // When: 포지션을 클릭
    fireEvent.click(positionElement);

    // Then: 선택된 포지션의 배경색 변경되었는지 확인 (bg-gray-100 클래스)
    expect(positionElement.parentElement).toHaveClass("bg-gray-100");
  });

  it("초기화 버튼 확인", () => {
    // Given: 모달이 열려있는 상태
    render(<PositionModal isOpen={true} onClose={onClose} />);

    const positionToSelect = "웹 개발자";
    const positionElement = screen.getByText(positionToSelect);
    const resetButton = screen.getByText("초기화");

    // When: 포지션을 선택하고 초기화 버튼을 클릭
    fireEvent.click(positionElement);
    expect(positionElement.parentElement).toHaveClass("bg-gray-100");

    fireEvent.click(resetButton);

    // Then: 선택된 포지션의 배경색이 초기화되고 스토어 상태가 빈 배열인지 확인
    expect(positionElement.parentElement).toHaveClass("bg-white");
    expect(useCategoryStore.getState().positionCategory).toEqual([]);
  });

  it("적용 버튼을 확인", () => {
    // Given: 모달이 열려있는 상태
    render(<PositionModal isOpen={true} onClose={onClose} />);

    const positionToSelect = "웹 개발자";
    const positionElement = screen.getByText(positionToSelect);
    const applyButton = screen.getByText("적용");

    // When: 포지션을 선택하고 적용 버튼을 클릭
    fireEvent.click(positionElement);
    fireEvent.click(applyButton);

    // Then: Zustand 스토어에 선택된 포지션이 저장되고 onClose가 호출되었는지 확인
    expect(useCategoryStore.getState().positionCategory).toContain(
      positionToSelect
    );
    expect(onClose).toHaveBeenCalled();
  });

  it("onClose확인", () => {
    // Given: 모달이 열려있는 상태와 외부 요소가 있는 경우
    render(
      <div>
        <PositionModal isOpen={true} onClose={onClose} />
        <div data-testid="outside">Outside</div>
      </div>
    );

    const outsideElement = screen.getByTestId("outside");

    // When: 모달 외부를 클릭
    fireEvent.mouseDown(outsideElement);

    // Then: onClose가 호출되어 모달이 닫히는지 확인
    expect(onClose).toHaveBeenCalled();
  });
});
