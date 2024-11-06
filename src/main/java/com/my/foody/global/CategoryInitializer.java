package com.my.foody.global;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import com.my.foody.domain.category.entity.Category;
import com.my.foody.domain.category.repo.CategoryRepository;

@Component
public class CategoryInitializer {

    private final CategoryRepository categoryRepository;

    public CategoryInitializer(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @PostConstruct
    public void init() {
        // 카테고리가 아직 없을 경우에만 초기 데이터를 삽입합니다.
        if (categoryRepository.count() == 0) {
            categoryRepository.save(new Category("한식"));
            categoryRepository.save(new Category("일식"));
            categoryRepository.save(new Category("중식"));
            categoryRepository.save(new Category("양식"));
            categoryRepository.save(new Category("분식"));
            System.out.println("초기 카테고리 데이터가 삽입되었습니다.");
        } else {
            System.out.println("카테고리 데이터가 이미 존재합니다.");
        }
    }
}

