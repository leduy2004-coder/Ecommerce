package com.ecommerce.post.dto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.List;

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
}