package com.techeer.backend.api.document.application.port.in;

public interface DeleteEducationUseCase {

    void deleteEducation(Long educationId, Long userId);

}
