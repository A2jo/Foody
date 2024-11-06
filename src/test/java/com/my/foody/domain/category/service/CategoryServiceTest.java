package com.my.foody.domain.category.service;


import com.my.foody.domain.category.entity.Category;
import com.my.foody.domain.category.repo.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CategoryServiceTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void shouldInitializeCategories() {

        List<Category> categories = categoryRepository.findAll();

        assertThat(categories).isNotEmpty();
        assertThat(categories).extracting("name").contains("한식","일식", "중식", "양식","분식");
        assertThat(categories).extracting("id").contains(1L, 2L, 3L, 4L, 5L);
    }
}