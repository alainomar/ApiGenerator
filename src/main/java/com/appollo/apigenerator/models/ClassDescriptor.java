package com.appollo.apigenerator.models;

import io.swagger.v3.oas.models.media.Schema;
import lombok.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClassDescriptor {
    private Boolean objectClass;
    private String $uri;
    private String title;
    private Map<String, ClassDescriptorProperty> properties;

    public ClassDescriptor(Schema schema) {

        if(schema.getType().equals("object")){
            this.objectClass = true;
        } else {
            this.objectClass = false;
        };
        this.set$uri("ljnljnljnl");
        this.title = schema.getTitle();
        this.properties = new HashMap<String, ClassDescriptorProperty>();
        Map<String, Schema> inputProperties = schema.getProperties();

        AtomicInteger order = new AtomicInteger();
        inputProperties.forEach((key, value) -> {
            order.addAndGet(1);
            properties.put(key, new ClassDescriptorProperty(value, order.get()));
        });
    }

    public void set$uri(String $uri) {
        this.$uri = $uri;
    }
    public String get$uri(){
         return this.$uri;
    }

}
