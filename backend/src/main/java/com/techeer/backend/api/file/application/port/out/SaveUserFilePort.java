package com.techeer.backend.api.file.application.port.out;

import com.techeer.backend.api.file.domain.UserFile;

public interface SaveUserFilePort {
    UserFile saveUserFile(UserFile userFile);
}

