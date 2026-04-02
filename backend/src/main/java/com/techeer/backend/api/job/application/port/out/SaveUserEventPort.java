package com.techeer.backend.api.job.application.port.out;

import com.techeer.backend.api.job.domain.UserEvent;

public interface SaveUserEventPort {

    UserEvent saveUserEvent(UserEvent userEvent);

}
