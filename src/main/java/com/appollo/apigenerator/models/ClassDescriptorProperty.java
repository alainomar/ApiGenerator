package com.appollo.apigenerator.models;

import io.swagger.v3.oas.models.media.Schema;
import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClassDescriptorProperty {
    private String type;
    private Integer order;

    public ClassDescriptorProperty(Schema value, int order) {
        this.type = "ref";
        if(value.getType() != null){
            this.type = "string";
        }
        this.order = order;
    }
}
