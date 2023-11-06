package com.appollo.apigenerator.models;

import com.appollo.apigenerator.controllers.Generator;
import io.swagger.v3.oas.models.media.Schema;
import lombok.*;

import java.util.ArrayList;
import java.util.Map;

@Data
@Setter
@Getter

@AllArgsConstructor
@NoArgsConstructor
public class BusinessObject {
    private String $uri;
    private ArrayList<ClassDescriptor> classDescriptors;

    public String setUri(String uri) {
        return this.$uri = uri;
    }

    public String getUri() {
        return $uri;
    }

    public void appendClassDescriptor(Boolean isBOM, Generator generator, Map.Entry<String,Schema> propertyItem) {
        if (this.classDescriptors == null) {
            this.classDescriptors = new ArrayList<ClassDescriptor>();
        }
        if(isBOM){
            this.classDescriptors.add(new ClassDescriptorBusiness(generator ,propertyItem, this.$uri));
        } else {
            this.classDescriptors.add(new ClassDescriptorDefinition(generator ,propertyItem, this.$uri));
        }

    }
}
