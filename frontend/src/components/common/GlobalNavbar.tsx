import { useState, useEffect, useRef } from "react";
import { Search, User, Menu, X, ChevronDown } from "lucide-react";
import { useNavigate, useLocation, Link } from "react-router-dom";
import useAuthStore from "../../store/authStore";
import useJobStore from "../../store/jobStore";

const NAV_LINKS = [
  { label: "채용공고", href: "/jobs" },
  { label: "이력서", href: "/" },
  { label: "마이페이지", href: "/myInfo" },
];

function GlobalNavbar() {
  const navigate = useNavigate();
  const location = useLocation();
  const { checkAuth, userData, isAuthenticated, logout } = useAuthStore();
  const { setSearchQuery } = useJobStore();

  const [searchText, setSearchText] = useState("");
  const [mobileOpen, setMobileOpen] = useState(false);
  const [profileOpen, setProfileOpen] = useState(false);
  const profileRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    checkAuth();
  }, [checkAuth]);

  // Close dropdown on outside click
  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (profileRef.current && !profileRef.current.contains(e.target as Node)) {
        setProfileOpen(false);
      }
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  // Close mobile menu on route change
  useEffect(() => {
    setMobileOpen(false);
  }, [location.pathname]);

  const handleSearch = () => {
    const q = searchText.trim();
    if (!q) return;
    setSearchQuery(q);
    navigate(`/jobs/search?q=${encodeURIComponent(q)}`);
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === "Enter") handleSearch();
  };

  const handleLogout = async () => {
    await logout();
    setProfileOpen(false);
    navigate("/");
  };

  const isActive = (href: string) =>
    href === "/" ? location.pathname === "/" : location.pathname.startsWith(href);

  return (
    <header className="bg-white border-b border-gray-200 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link to="/" className="flex-shrink-0">
            <span className="text-2xl font-bold text-gray-900">
              Re<span className="text-blue-600">X</span>ume
            </span>
          </Link>

          {/* Desktop nav links */}
          <nav className="hidden md:flex items-center gap-1 ml-8">
            {NAV_LINKS.map((link) => (
              <Link
                key={link.href}
                to={link.href}
                className={`px-4 py-2 rounded-md text-sm font-medium transition-colors ${
                  isActive(link.href)
                    ? "text-blue-600 bg-blue-50"
                    : "text-gray-600 hover:text-gray-900 hover:bg-gray-100"
                }`}
              >
                {link.label}
              </Link>
            ))}
          </nav>

          {/* Search bar */}
          <div className="hidden md:flex flex-1 max-w-md mx-6 relative">
            <input
              type="text"
              placeholder="직무, 회사, 기술스택 검색"
              className="w-full pl-4 pr-10 py-2 text-sm rounded-full border border-gray-300 bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent focus:bg-white transition"
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              onKeyDown={handleKeyDown}
            />
            <button
              onClick={handleSearch}
              className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-blue-600 transition"
              aria-label="검색"
            >
              <Search className="w-4 h-4" />
            </button>
          </div>

          {/* Auth area */}
          <div className="hidden md:flex items-center gap-3">
            {isAuthenticated ? (
              <div className="relative" ref={profileRef}>
                <button
                  onClick={() => setProfileOpen((v) => !v)}
                  className="flex items-center gap-2 px-3 py-2 rounded-full bg-gray-100 hover:bg-gray-200 transition text-sm font-medium text-gray-700"
                >
                  <div className="w-7 h-7 rounded-full bg-blue-100 flex items-center justify-center">
                    <User className="w-4 h-4 text-blue-600" />
                  </div>
                  <span className="hidden lg:block">{userData?.username}</span>
                  <ChevronDown className="w-3 h-3" />
                </button>
                {profileOpen && (
                  <div className="absolute right-0 mt-2 w-44 bg-white border border-gray-200 rounded-xl shadow-lg py-1 z-50">
                    <Link
                      to="/myInfo"
                      className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                      onClick={() => setProfileOpen(false)}
                    >
                      마이페이지
                    </Link>
                    <Link
                      to="/upload"
                      className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                      onClick={() => setProfileOpen(false)}
                    >
                      이력서 업로드
                    </Link>
                    <hr className="my-1 border-gray-100" />
                    <button
                      onClick={handleLogout}
                      className="block w-full text-left px-4 py-2 text-sm text-red-500 hover:bg-gray-50"
                    >
                      로그아웃
                    </button>
                  </div>
                )}
              </div>
            ) : (
              <Link
                to="/login"
                className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition"
              >
                로그인
              </Link>
            )}
          </div>

          {/* Mobile hamburger */}
          <button
            className="md:hidden p-2 rounded-md text-gray-500 hover:bg-gray-100 transition"
            onClick={() => setMobileOpen((v) => !v)}
            aria-label="메뉴"
          >
            {mobileOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
          </button>
        </div>
      </div>

      {/* Mobile menu */}
      {mobileOpen && (
        <div className="md:hidden border-t border-gray-100 bg-white">
          {/* Mobile search */}
          <div className="px-4 py-3">
            <div className="relative">
              <input
                type="text"
                placeholder="직무, 회사, 기술스택 검색"
                className="w-full pl-4 pr-10 py-2 text-sm rounded-full border border-gray-300 bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500"
                value={searchText}
                onChange={(e) => setSearchText(e.target.value)}
                onKeyDown={handleKeyDown}
              />
              <button
                onClick={handleSearch}
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400"
              >
                <Search className="w-4 h-4" />
              </button>
            </div>
          </div>
          {/* Mobile nav links */}
          <nav className="px-4 pb-3 space-y-1">
            {NAV_LINKS.map((link) => (
              <Link
                key={link.href}
                to={link.href}
                className={`block px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                  isActive(link.href)
                    ? "text-blue-600 bg-blue-50"
                    : "text-gray-700 hover:bg-gray-100"
                }`}
              >
                {link.label}
              </Link>
            ))}
          </nav>
          {/* Mobile auth */}
          <div className="px-4 pb-4">
            {isAuthenticated ? (
              <div className="space-y-1">
                <p className="text-xs text-gray-400 px-4 pb-1">{userData?.username}</p>
                <Link
                  to="/myInfo"
                  className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-lg"
                >
                  마이페이지
                </Link>
                <Link
                  to="/upload"
                  className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 rounded-lg"
                >
                  이력서 업로드
                </Link>
                <button
                  onClick={handleLogout}
                  className="block w-full text-left px-4 py-2 text-sm text-red-500 hover:bg-gray-50 rounded-lg"
                >
                  로그아웃
                </button>
              </div>
            ) : (
              <Link
                to="/login"
                className="block w-full text-center px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition"
              >
                로그인
              </Link>
            )}
          </div>
        </div>
      )}
    </header>
  );
}

export default GlobalNavbar;
