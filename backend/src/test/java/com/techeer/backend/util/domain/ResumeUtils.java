package com.techeer.backend.util.domain;

import com.techeer.backend.api.resume.domain.Resume;
import com.techeer.backend.api.tag.position.Position;

public class ResumeUtils {
    public static Resume newInstance() {
        return Resume.builder()
                .user(UserUtils.newInstance()) // 연관된 User 객체 추가
                .name("test_resume")
                .career(3) // 경력 (예: 3년)
                .position(Position.BACKEND) // 실제 Position Enum 값을 확인 후 넣어주세요
                .resumePdf(null) // ResumePdf를 넣을 경우 적절한 객체를 생성해야 함
                .build();
    }
}