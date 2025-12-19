package com.techeer.backend.api.career.application.port.out;

import com.techeer.backend.api.career.domain.UserCareer;

public interface SaveUserCareerPort {

	UserCareer saveUserCareer(UserCareer userCareer);

}
