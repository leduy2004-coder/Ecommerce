package com.ecommerce.search.controller;

import com.ecommerce.search.dto.ApiResponse;
import com.ecommerce.search.service.ESTagService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TagSearchController {

    ESTagService esTagService;

    @GetMapping("/tags")
    public ApiResponse<List<String>> searchItems(@RequestParam(required = false) String keyword) throws IOException {
        log.info("name tag search: {}", keyword);

        List<String> response = esTagService.searchTagsByName(keyword);
        return ApiResponse.<List<String>>builder()
                .result(response)
                .build();
    }
}
