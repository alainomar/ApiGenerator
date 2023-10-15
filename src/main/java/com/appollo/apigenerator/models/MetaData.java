package com.appollo.apigenerator.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"artifactType", "artifactSubtype"})
public class MetaData {
    @JsonProperty("artifactType")
    private String artifactType;
    @JsonProperty("artifactSubtype")
    private String artifactSubtype;
}
