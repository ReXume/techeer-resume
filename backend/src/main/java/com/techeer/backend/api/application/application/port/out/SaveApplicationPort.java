package com.techeer.backend.api.application.application.port.out;

import com.techeer.backend.api.application.domain.Application;

public interface SaveApplicationPort {
	Application saveApplication(Application application);
}

