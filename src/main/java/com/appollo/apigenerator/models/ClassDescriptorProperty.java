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
    private int order;

    public ClassDescriptorProperty(Schema value, int order) {

        if(value.get$ref() != null){
            this.type = "ref";
        } else {
            this.type = value.getType();
        }
        this.order = order;
    }
}
