package com.techeer.backend;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.techeer.backend.api.tag.techStack.domain.TechStack;
import com.techeer.backend.api.tag.techStack.repository.TechStackRepository;
import com.techeer.backend.api.tag.techStack.service.TechStackService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class techStackServiceTest {

    @MockBean
    private TechStackRepository techStackRepository;

    @Autowired
    private TechStackService techStackService;

    @Test
    @DisplayName("기술 스택 이름으로 기술 가져와지는지 테스트")
    public void TestTechStacksByName() {
        // given
        List<String> techStackNames = Arrays.asList("Java", "Spring Boot", "JPA");
        List<TechStack> mockTechStacks = Arrays.asList(
                new TechStack("Java"),
                new TechStack("Spring Boot"),
                new TechStack("JPA")
        );

        Mockito.when(techStackRepository.findByNameIn(techStackNames)).thenReturn(mockTechStacks);

        // when
        List<TechStack> techStacks = techStackService.getTechStacksByName(techStackNames);

        // then
        assertThat(techStacks).isNotEmpty();
        assertThat(techStacks).hasSize(3);
        assertThat(techStacks).extracting(TechStack::getName).containsExactlyInAnyOrder("Java", "Spring Boot", "JPA");
    }
}