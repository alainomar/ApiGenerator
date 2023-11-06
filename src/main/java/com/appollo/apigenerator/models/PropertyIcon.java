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

    public void PropertyIcon(String icon, String color, Boolean before) {
        this.icon = icon;
        this.color = color;
        this.before = before;
    }
}

