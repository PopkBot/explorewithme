package main.compilation.dto;

import lombok.Data;

import java.util.Set;

public class CompilationUpdateDto extends CompilationInputDto{
    private Set<Long> events;
}
