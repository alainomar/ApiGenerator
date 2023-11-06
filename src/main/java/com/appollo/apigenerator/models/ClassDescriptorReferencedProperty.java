package com.appollo.apigenerator.models;

import com.appollo.apigenerator.controllers.Generator;
import io.swagger.v3.oas.models.media.Schema;
import lombok.*;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassDescriptorReferencedProperty extends ClassDescriptorProperty{
    private String ref;

    public ClassDescriptorReferencedProperty(Generator generator, Schema schema, int order) {
        super(schema, order);
        String ref = schema.get$ref();
        String path = ref.substring(ref.lastIndexOf("/") + 1, ref.length());

        this.ref = generator.getBOMPath(true, generator.mapRefPath, path) + "#" + path;
    }
}
