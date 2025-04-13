package com.ecommerce.search.dto.response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchResponse {

    private String id;

    private String name;

    private String thumbnailUrl;

    private long price;


    private int sold;

}
