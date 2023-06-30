package main.category.repository;

import main.category.model.Category;
import main.category.projection.CategoryCountProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    Optional<Category> findByName(String name);
}
