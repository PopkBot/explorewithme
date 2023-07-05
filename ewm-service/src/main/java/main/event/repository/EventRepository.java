package main.event.repository;

import main.category.projection.CategoryCountProjection;
import main.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    @Query(nativeQuery = true, value = "Select COUNT(category_id) AS countId FROM events WHERE category_id = ?1")
    CategoryCountProjection findCategoryUsages(Long categoryId);

    @Query(nativeQuery = true, value = "Select * FROM events WHERE id IN (?1)")
    Set<Event> findAllByIds(List<Long> eventIds);
}
