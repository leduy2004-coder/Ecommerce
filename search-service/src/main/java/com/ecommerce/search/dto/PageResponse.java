package com.ecommerce.search.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PageResponse<T> {
    //fe gui xuong
    int currentPage;
    int pageSize;

    long totalElements;
    int totalPages;

    @Builder.Default
    private List<T> data = Collections.emptyList();

    public <R> PageResponse<R> map(Function<? super T, ? extends R> converter) {
        List<R> mappedData = this.data.stream()
                .map(converter)
                .collect(Collectors.toList());

        return PageResponse.<R>builder()
                .currentPage(this.currentPage)
                .pageSize(this.pageSize)
                .totalElements(this.totalElements)
                .totalPages(this.totalPages)
                .data(mappedData)
                .build();
    }
}