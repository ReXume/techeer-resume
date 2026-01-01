package com.techeer.backend.api.user.application.port.out;

import com.techeer.backend.api.user.domain.User;
import java.util.Optional;

public interface LoadUserPort {

    Optional<User> findById(Long id);

}
