package com.techeer.backend.api.job.application.port.out;

import com.techeer.backend.api.job.domain.EventType;
import com.techeer.backend.api.job.domain.UserEvent;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoadUserEventPort {

    Page<UserEvent> findByUserIdAndEventType(Long userId, EventType eventType, Pageable pageable);

    List<UserEvent> findTopJobPostingsByEventType(EventType eventType, int limit);

}
