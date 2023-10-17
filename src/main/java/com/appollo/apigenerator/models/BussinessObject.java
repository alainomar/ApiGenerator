package com.appollo.apigenerator.models;

import lombok.*;

@Data
@Setter
@Getter

@AllArgsConstructor
@NoArgsConstructor
public class BussinessObject {
    private String $uri;
    private ClassDescriptor[] classDescriptors;


    public String setUri(String uri) {
        return this.$uri = uri;
    }

    public String getUri() {
        return $uri;
    }
}
