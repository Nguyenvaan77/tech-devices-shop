package com.example.web.dto.attribute.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAttributeTypeRequest {
    private String name;
}
