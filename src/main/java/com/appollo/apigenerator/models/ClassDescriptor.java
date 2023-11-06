package com.appollo.apigenerator.models;

import com.appollo.apigenerator.controllers.Generator;
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
    private String $uri;
    private String title;

    public void set$uri(String $uri) {
        this.$uri = $uri;
    }
    public String get$uri(){
         return this.$uri;
    }

}
