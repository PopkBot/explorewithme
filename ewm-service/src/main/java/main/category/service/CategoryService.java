package main.category.service;

import main.category.dto.CategoryDto;
import main.category.dto.CategoryInputDto;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryInputDto inputDto);

    void deleteCategory(Long id);

    CategoryDto patchCategory(CategoryInputDto inputDto, Long id);

    List<CategoryDto> getListOfCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long id);
}
