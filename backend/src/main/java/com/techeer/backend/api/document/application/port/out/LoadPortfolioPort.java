package com.techeer.backend.api.document.application.port.out;

import com.techeer.backend.api.document.domain.Portfolio;
import com.techeer.backend.api.user.domain.User;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface LoadPortfolioPort {

	Optional<Portfolio> findById(Long id);

	Slice<Portfolio> findAllByUser(User user, Pageable pageable);

}
