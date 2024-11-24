package org.example.testapi.product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FindProductResponse {
    private Integer id;
    private String name;
    private String description;
    private Double price;

}
