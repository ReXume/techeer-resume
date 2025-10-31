package com.techeer.backend.api.bookmark.controller;

import com.techeer.backend.api.bookmark.converter.BookmarkConverter;
import com.techeer.backend.api.bookmark.domain.Bookmark;
import com.techeer.backend.api.bookmark.dto.BookmarkResponse;
import com.techeer.backend.api.bookmark.service.BookmarkService;
import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.service.UserService;
import com.techeer.backend.global.dto.ApiResponse;
import com.techeer.backend.global.success.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "북마크")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmarks/")
public class BookmarkController {

    private final BookmarkService bookmarkService;
    private final UserService userService;

    @Operation(summary = "북마크 등록", description = "관심있는 이력서를 북마크로 등록합니다.")
    @PostMapping("/{resume_id}")
    public ResponseEntity<ApiResponse<BookmarkResponse>> addBookmark(@PathVariable("resume_id") Long resumeId) {
        User user = userService.getLoginUser();

        Bookmark bookmark = bookmarkService.addBookmark(user, resumeId);

        BookmarkResponse response = BookmarkConverter.toBookmarkResponse(bookmark);

        return ResponseEntity.ok(ApiResponse.success(response, SuccessCode.OK.getMessage()));
    }

    @Operation(summary = "북마크 삭제", description = "북마크를 해제합니다.")
    @DeleteMapping("/{bookmark_id}")
    public ResponseEntity<ApiResponse<Void>> removeBookmark(@PathVariable("bookmark_id") Long bookmarkId) {
        User user = userService.getLoginUser();
        bookmarkService.removeBookmark(user, bookmarkId);
        return ResponseEntity.ok(ApiResponse.noContent(SuccessCode.NO_CONTENT.getMessage()));
    }

    @Operation(summary = "북마크 조회", description = "사용자와 관련된 북마크를 모두 조회합니다.")
    @GetMapping("/users/{user_id}")
    public ResponseEntity<ApiResponse<List<BookmarkResponse>>> getBookmarksByUserId(
            @PathVariable("user_id") Long userId) {
        User user = userService.getLoginUser();

        List<Bookmark> bookmarks = bookmarkService.getBookmarksByUserId(user.getId());
        List<BookmarkResponse> bookmarkResponses = BookmarkConverter.toBookmarkResponses(bookmarks);
        return ResponseEntity.ok(ApiResponse.success(bookmarkResponses, SuccessCode.OK.getMessage()));
    }

//    @Operation(summary = "단일 북마크 조회", description = "하나의 북마크만 검색해서 조회합니다.")
//    @GetMapping("/{bookmark_id}")
//    public CommonResponse<BookmarkResponse> getBookmarkById(
//            @PathVariable("bookmark_id") Long bookmarkId) {
//        BookmarkResponse bookmark = bookmarkService.getBookmarkById(bookmarkId);
//        return CommonResponse.of(SuccessCode.OK, bookmark);
//    }
}
