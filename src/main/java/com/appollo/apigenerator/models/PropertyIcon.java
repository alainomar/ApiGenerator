package com.appollo.apigenerator.models;

import lombok.*;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PropertyIcon {
    private String icon;
    private String color;
    private Boolean before;
}
