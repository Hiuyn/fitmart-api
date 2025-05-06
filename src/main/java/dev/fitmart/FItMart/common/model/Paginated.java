package dev.fitmart.FItMart.common.model;

import lombok.Data;

@Data
public class Paginated<T> {
    private T data;
    private Pagination pagination;

    @Data
    public static class Pagination {
        private long total;
        private int count;
        private int perPage;
        private int currentPage;
        private int totalPages;
    }
}
