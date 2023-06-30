package main.compilation.repository;

import main.compilation.model.Compilation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {

    Optional<Compilation> findByTitle(String title);

    Page<Compilation> findAllByPinned(Boolean pinned, Pageable pageable);
}
