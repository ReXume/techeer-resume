package com.techeer.backend.api.tag.techStack.service;

import com.techeer.backend.api.tag.techStack.domain.TechStack;
import com.techeer.backend.api.tag.techStack.repository.TechStackRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class TechStackService {
    private final TechStackRepository techStackRepository;

    @Transactional
    public List<TechStack> findOrCreateTechStacks(List<String> techStackNames) {
        return techStackNames.stream()
                .map(techName -> findByName(techName)
                        .orElseGet(() -> save(new TechStack(techName))))
                .collect(Collectors.toList());
    }

    // 이름으로 TechStack을 찾는 로직 구현
    public Optional<TechStack> findByName(String name) {
        return techStackRepository.findByName(name);
    }

    // TechStack 저장 로직 구현
    @Transactional
    public TechStack save(TechStack techStack) {
        return techStackRepository.save(techStack);
    }

    @Transactional
    public List<TechStack> saveTechStacks(List<String> techStackNames) {
        return techStackRepository.saveAll(
                techStackNames.stream()
                        .map(techStackName -> TechStack.builder()
                                .name(techStackName)
                                .build())
                        .toList()
        );
    }

    @Transactional
    public TechStack saveTechStack(String techStackName) {
        return techStackRepository.save(
                TechStack.builder()
                        .name(techStackName)
                        .build()
        );
    }

    public List<TechStack> getTechStacksByName(List<String> techStackNames) {
        return techStackRepository.findByNameIn(techStackNames);
    }
}
