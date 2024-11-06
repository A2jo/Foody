package com.my.foody.domain.home.service;

import com.my.foody.domain.category.repo.CategoryRepository;
import com.my.foody.domain.home.dto.resp.MainHomeRespDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeService {

    private final CategoryRepository categoryRepository;

    public HomeService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public MainHomeRespDto getMainHome() {
        // 카테고리 데이터를 가져와서 MainHomeRespDto 형식으로 변환
        List<MainHomeRespDto.CategoryDto> categories = categoryRepository.findAll().stream()
                .map(category -> new MainHomeRespDto.CategoryDto(category.getId(), category.getName()))
                .collect(Collectors.toList());

        MainHomeRespDto responseDto = new MainHomeRespDto();
        responseDto.setCategories(categories);
        return responseDto;
    }
}
