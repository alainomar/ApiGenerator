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


    public void setUri(String uri) {
        this.$uri = uri;
    }

    public String getUri() {
        return $uri;
    }
}
