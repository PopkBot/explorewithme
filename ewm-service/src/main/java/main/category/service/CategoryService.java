package main.category.service;

import main.category.dto.CategoryDto;
import main.category.dto.CategoryInputDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(CategoryInputDto inputDto);

    void deleteCategory(Long id);

    CategoryDto patchCategory(CategoryInputDto inputDto, Long id);

    List<CategoryDto> getListOfCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long id);
}
