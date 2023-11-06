package com.appollo.apigenerator.models;

import lombok.*;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormatDescription {
    private String formatType;

    public void FormatDescription(String formatType) {
        this.formatType = formatType;
    }
}
