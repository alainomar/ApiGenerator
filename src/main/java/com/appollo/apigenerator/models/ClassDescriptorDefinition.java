package com.appollo.apigenerator.models;

import com.appollo.apigenerator.controllers.Generator;
import io.swagger.v3.oas.models.media.Schema;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.lang.String;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassDescriptorDefinition extends ClassDescriptor {
    private Boolean enumClass;
    /*
        private String $uri;

       private String title;

    */
    private String edoType;
    private List<String> keys;

    public ClassDescriptorDefinition(Generator generator, Map.Entry<String, Schema> item, String $uri) {
        super();

        String name = item.getKey();
        Schema schema = item.getValue();

        List <String> keys = schema.getEnum();

        this.set$uri($uri);
        this.setTitle(StringUtils.capitalize(name));
        this.setEnumClass(true);
        this.setEdoType("");

        keys.replaceAll(new MyUpperCaseOperator());
        this.setKeys(keys);
    }
}

class MyUpperCaseOperator implements UnaryOperator<String> {

    public String apply(String t) {

        //Add your custom logic here
        return t.toUpperCase();
    }
}
