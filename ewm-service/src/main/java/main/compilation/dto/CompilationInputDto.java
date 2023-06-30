package main.compilation.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class CompilationInputDto {
    private Set<Long> events = new HashSet<>();
    private Long id;
    private Boolean pinned;
    private String title;
}
