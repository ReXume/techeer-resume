package com.techeer.backend.api.bookmark.application.port.out;

import com.techeer.backend.api.bookmark.domain.Bookmark;

public interface SaveBookmarkPort {
	Bookmark saveBookmark(Bookmark bookmark);
}

