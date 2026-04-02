package com.techeer.backend.api.job.adapter.out.persistence;

import com.techeer.backend.api.job.application.port.out.LoadUserEventPort;
import com.techeer.backend.api.job.application.port.out.SaveUserEventPort;
import com.techeer.backend.api.job.domain.EventType;
import com.techeer.backend.api.job.domain.UserEvent;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserEventPersistenceAdapter implements SaveUserEventPort, LoadUserEventPort {

    private final UserEventJpaRepository userEventJpaRepository;

    @Override
    public UserEvent saveUserEvent(UserEvent userEvent) {
        return userEventJpaRepository.save(userEvent);
    }

    @Override
    public Page<UserEvent> findByUserIdAndEventType(Long userId, EventType eventType, Pageable pageable) {
        return userEventJpaRepository.findByUserIdAndEventType(userId, eventType, pageable);
    }

    @Override
    public List<UserEvent> findTopJobPostingsByEventType(EventType eventType, int limit) {
        return userEventJpaRepository.findTopJobPostingsByEventType(eventType, PageRequest.of(0, limit));
    }

}
