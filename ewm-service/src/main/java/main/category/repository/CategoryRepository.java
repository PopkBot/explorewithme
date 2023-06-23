package main.category.repository;

import main.category.dto.CategoryDto;
import main.category.model.Category;
import main.category.projection.CategoryIdProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    Optional<CategoryIdProjection> findByName(String name);
}
