import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import logo from "../../assets/logo.svg";
import search from "../../assets/search-normal.svg";
import useSearchStore from "../../store/SearchStore.ts";

import { User } from "lucide-react";
import authStore from "../../store/authStore.ts";

const BASE_URL = import.meta.env.VITE_API_BASE_URL;

function Navbar() {
  const navigate = useNavigate();
  const [searchText, setSearchText] = useState<string>(""); // 검색어 상태 관리
  const { setSearchName } = useSearchStore();
  const { checkAuth, user, isAuthenticated } = authStore();

  useEffect(() => {
    checkAuth();
  }, [checkAuth]);

  const moveMainPage = () => {
    navigate("/");
  };

  const moveLoginPage = () => {
    navigate("/login");
  };

  const moveMyPage = () => {
    navigate("/myInfo");
  };

  const searchName = () => {
    if (searchText === "") {
      alert("검색어를 입력해주세요!");
      return;
    }

    try {
      setSearchName(searchText);
      navigate(`/search?user_name=${searchText}`);
    } catch (error) {
      alert("검색 실패 ");
      console.log(error);
    }
  };

  const handleKeyPress = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === "Enter") {
      searchName();
    }
  };

  return (
    <div className="w-full h-12 px-4 bg-transparent -mt-1 mb-2">
      <div className="flex flex-row justify-between items-center">
        {/* 로고 */}
        <div className="pl-10">
          <img
            src={logo}
            alt="logo"
            className="w-32 h-6 hover:cursor-pointer"
            onClick={moveMainPage}
          />
        </div>

        {/* 검색 바 */}
        <div className="flex-grow max-w-lg mx-4">
          <div className="mw-140 h-10 rounded-full bg-gray-100 hover:ring-2 hover:ring-blue-500 flex items-center px-4">
            <input
              id="search-box"
              className="flex-1 bg-transparent text-gray-950 placeholder-gray-500 outline-none lg:text-base ml-2"
              placeholder="검색어를 입력하세요."
              aria-label="search-box"
              autoComplete="off"
              name="search"
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              onKeyPress={handleKeyPress}
            />
            <img
              src={search}
              alt="search"
              className="mr-1 w-6 h-5 hover:cursor-pointer md:mr-0 sm:mr-0 mr-4"
              onClick={searchName}
            />
          </div>
        </div>

        {/* 프로필 */}
        <div className="flex items-center pr-10">
          {isAuthenticated === true ? (
            <>
              <div className="w-12 h-12 bg-gray-200 rounded-full flex items-center justify-center">
                <User className="w-8 h-8 text-gray-400" onClick={moveMyPage} />
              </div>
              <p
                className="hidden sm:block ml-3 mb-[1px] text-base lg:text-[1.2rem] hover:cursor-pointer"
                onClick={moveMyPage}
              >
                {user}
              </p>
            </>
          ) : (
            <button
              className="px-4 py-1 bg-blue-500 text-white rounded-lg hover:bg-blue-600"
              onClick={moveLoginPage}
            >
              로그인
            </button>
          )}
        </div>
      </div>
      <div className="absolute mt-4 left-0 w-full h-[1px] bg-gray-300" />
    </div>
  );
}

export default Navbar;
