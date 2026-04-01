package com.techeer.backend.api.user.application.port.out;

import com.techeer.backend.api.user.domain.User;

public interface SaveUserPort {

	User saveUser(User user);

}
