package com.appollo.apigenerator.models;

import com.appollo.apigenerator.controllers.Generator;
import io.swagger.v3.oas.models.media.Schema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassDescriptorBusiness extends ClassDescriptor{
    private Boolean objectClass;

    private Map<String, ClassDescriptorProperty> properties;

    public ClassDescriptorBusiness(Generator generator, Map.Entry<String, Schema> item, String uri) {
        super();

        String property = item.getKey();
        Schema schema = item.getValue();

        String type = schema.getType();
        List<Schema> oneOf = schema.getOneOf();
        List<Schema> anyOf = schema.getAnyOf();
        List<Schema> allOf = schema.getAllOf();

        // It s an online object (this is a not compouned object)
        if(type!=null && (type.equals("object"))){   // revisar est luego
            this.objectClass = true;
        } else {
            if (oneOf!=null || anyOf!=null || allOf!=null) {
                this.objectClass = true;
            } else {
                this.objectClass = false;
            }
        };
        this.set$uri(uri + "#" + property);
        this.setTitle(property);
        this.properties = new HashMap<String, ClassDescriptorProperty>();

        Map<String, Schema> inputProperties = schema.getProperties();
        if(inputProperties!= null){
            int order = 0;
            for(Map.Entry<String, Schema> entry : inputProperties.entrySet()){
                String key = entry.getKey();
                Schema value = entry.getValue();

                List anEnum = value.getEnum();
                if(anEnum!=null){
                    generator.addBO(false, key, entry);
                } else {
                    if(value.get$ref()!= null){
                        properties.put(key, new ClassDescriptorReferencedProperty(generator, value, order));
                    } else {
                        properties.put(key, new ClassDescriptorPrimaryProperty(key, value, order));
                    }
                }

                order++;
            }
        }

        Schema itemProperties = schema.getItems();
        if(itemProperties!=null){
            // treatment for schema items
        }

        if(oneOf!=null){
            this.checkDiscriminatorProperties(generator, oneOf);
        }

        if(anyOf!=null){
            this.checkDiscriminatorProperties(generator, anyOf);
        }

        if(allOf!=null){
            this.checkDiscriminatorProperties(generator, allOf);
        }
    }

    public void checkDiscriminatorProperties(Generator generator, List<Schema> listSchema) {
        int order = 0;
        for(Schema schema : listSchema){

            List anEnum = schema.getEnum();
            String title = schema.getTitle();

            if(anEnum!=null){
                generator.addBO(false, title, (Map.Entry <String, Schema>)schema);
            } else {
                String type = schema.getType();
                Map<String, Schema> properties = schema.getProperties();
                String ref = schema.get$ref();

                if(type!= null && type.equals("object") && properties!=null){
                    // check out properties
                    for(Map.Entry<String, Schema> schemaEntry : properties.entrySet()){
                        String key = schemaEntry.getKey();
                        Schema property = schemaEntry.getValue();

                        List propertyEnum = property.getEnum();
                        if(propertyEnum!=null){
                            generator.addBO(false, key, schemaEntry);
                        } else {
                            this.properties.put(key, new ClassDescriptorPrimaryProperty(key, property, order));
                            order++;
                        }
                    }
                }
                if (ref!=null){
                    String key = ref.substring(ref.lastIndexOf("/") + 1, ref.length());
                    this.properties.put(key, new ClassDescriptorReferencedProperty(generator, schema, order));
                    order++;
                }
            }
        }
    }
}
