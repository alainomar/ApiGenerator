package com.appollo.apigenerator.models;

import io.swagger.v3.oas.models.media.Schema;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassDescriptorReferencedProperty extends ClassDescriptorProperty{
    private String ref;

    public ClassDescriptorReferencedProperty(Schema schema, int order) {
        super(schema, order);
        this.ref = "";
    }
}
