package com.example.web.dto.category.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCategoryRequest {

    private String name;
    private String slug;
    private Long parentId;
}