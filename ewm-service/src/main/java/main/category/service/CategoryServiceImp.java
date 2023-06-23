package main.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.CustomPageRequest;
import main.category.dto.CategoryDto;
import main.category.dto.CategoryInputDto;
import main.category.mapper.CategoryMapper;
import main.category.model.Category;
import main.category.repository.CategoryRepository;
import main.exceptions.ObjectAlreadyExistsException;
import main.exceptions.ObjectNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService{

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryInputDto inputDto) {
        if(categoryRepository.findByName(inputDto.getName()).isPresent()){
            throw new ObjectAlreadyExistsException("Category already exists");
        }
        Category category = categoryRepository.save(categoryMapper.convertToCategory(inputDto));
        log.info("Category has been created {}",category);
        return categoryMapper.convertToDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                ()-> new ObjectNotFoundException("Category not found")
        );
        categoryRepository.delete(category);
        log.info("Category has been deleted");
    }

    @Override
    @Transactional
    public CategoryDto patchCategory(CategoryInputDto inputDto, Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                ()-> new ObjectNotFoundException("Category not found")
        );
        category.setName(inputDto.getName());
        category = categoryRepository.save(category);
        log.info("Category has been updated {}",category);
        return categoryMapper.convertToDto(category);
    }

    @Override
    public List<CategoryDto> getListOfCategories(Integer from, Integer size) {
        Pageable page = new CustomPageRequest(from,size);
        List<CategoryDto> categoryDtos = categoryRepository.findAll(page).getContent()
                .stream().map(categoryMapper::convertToDto).collect(Collectors.toList());
        log.info("List of categories has been returned");
        return categoryDtos;
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                ()-> new ObjectNotFoundException("Category not found")
        );
        log.info("Category has been returned {}",category);
        return categoryMapper.convertToDto(category);
    }


}
