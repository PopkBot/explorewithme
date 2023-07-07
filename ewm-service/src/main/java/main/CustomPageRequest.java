package main;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@AllArgsConstructor
public class CustomPageRequest implements Pageable {

    private int offset;
    private int limit;
    private Sort sort;

    public CustomPageRequest(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
        this.sort = Sort.unsorted();
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new CustomPageRequest(getPageSize(), (int) (getOffset() + getPageSize()));
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious() ?
                new CustomPageRequest(getPageSize(), (int) (getOffset() - getPageSize())) : this;
    }

    @Override
    public Pageable first() {
        return new CustomPageRequest(getPageSize(), 0);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new CustomPageRequest(pageNumber, this.getPageSize(), this.getSort());
    }

    @Override
    public boolean hasPrevious() {
        return offset > limit;
    }
}