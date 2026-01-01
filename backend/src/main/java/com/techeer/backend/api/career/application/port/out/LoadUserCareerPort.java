package com.techeer.backend.api.career.application.port.out;

import com.techeer.backend.api.career.domain.UserCareer;
import java.util.Optional;

public interface LoadUserCareerPort {

    Optional<UserCareer> findById(Long id);

}
