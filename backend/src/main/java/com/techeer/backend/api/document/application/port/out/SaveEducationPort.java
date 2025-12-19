package com.techeer.backend.api.document.application.port.out;

import com.techeer.backend.api.document.domain.Education;

public interface SaveEducationPort {

	Education saveEducation(Education education);

}
