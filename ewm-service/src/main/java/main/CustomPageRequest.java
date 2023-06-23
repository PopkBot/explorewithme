package main;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class CustomPageRequest extends PageRequest {
    /**
     * Creates a new {@link PageRequest} with sort parameters applied.
     *
     * @param page zero-based page index, must not be negative.
     * @param size the size of the page to be returned, must be greater than 0.
     * @param sort must not be {@literal null}, use {@link Sort#unsorted()} instead.
     */
    public CustomPageRequest(int page, int size, Sort sort) {
        super(page, size, sort);
    }

    public CustomPageRequest(int page, int size) {
        super(page, size, Sort.unsorted());
    }

    @Override
    public int getPageNumber() {
        return (int) (getOffset() / getPageSize());
    }
}
