package com.appollo.apigenerator.controllers;

import com.appollo.apigenerator.models.BusinessObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.callbacks.Callback;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/generator")
public class Generator {

    public Map<String, Map<String, Integer>> mapRefPath = new HashMap<>();
    public Map<String, Map<String, Integer>> mapDefRefPath = new HashMap<>();
    public Map<String, BusinessObject> businessObjects = new HashMap<String, BusinessObject>();
    public Map<String, BusinessObject> definitionObjects = new HashMap<String, BusinessObject>();

    public String getBOMPath(Boolean isBOM, Map<String, Map<String, Integer>> mapRefPath, String inputBom){
        String bom = isBOM ? inputBom.substring(0, inputBom.length() - 3) : inputBom;

        String bomPath = "";
        if (mapRefPath.containsKey(bom)) {
            Map<String, Integer> mapPathCount = mapRefPath.get(bom);
            // Multiple path occurrences
            if(mapPathCount.size() > 1){
                bomPath =  "/Common/.Common";
            }

            // Just One path BUT...
            if (mapPathCount.size() == 1){
                for(Map.Entry<String, Integer> entry : mapPathCount.entrySet()){
                    Integer count = entry.getValue();
                    String path = entry.getKey().substring(1, entry.getKey().length());
                    // Several ocurrences in same path
                    if(count > 1){
                        bomPath = "/" + StringUtils.capitalize(path) + "/.Common" ;
                    } else {
                        // Just one occurence, one path and BO name and path are the same
                        if (bom.equals(StringUtils.capitalize(path))){
                            bomPath = "/" + bom +  "/." + bom;
                        } else {
                            // Just one occurence, one path and BO name and path are NOT the same
                            bomPath = "/" + StringUtils.capitalize(path) +  "/.Common";
                        }
                    }
                }
            }
        } else {
            // BO NOT mapped yet
            bomPath = "/" + bom +  "/." + bom;
        }
        return bomPath;
    }
    public void addRefPath(Boolean isBOM, Map<String, Map<String, Integer>> mapRefPath, @NotNull String ref, @NotNull String path){
        // Register BOM 's path
        // Verify if tag exists in  mapRefPath
        // Cleanning ref and turning in tag and path into pathKey
        String tag1 = ref.substring(ref.lastIndexOf("/") + 1);
        String tag = isBOM ? tag1.substring(0, tag1.length() - 3) : tag1.substring(0, tag1.length());
        String pathKey = "";

        if(path.indexOf("/", 1) == -1){
            pathKey = path.substring(0,  path.length());
        } else {
            pathKey = path.substring(0,  path.indexOf("/", 1));
        }

        if (mapRefPath.containsKey(tag)) {
            // Adds pathKey to existing tag
            // the BO is referenced in this tag
            if(mapRefPath.get(tag).containsKey(pathKey)) {
                // Update mapPath
                // the path was already registered but now referenced in deeper call/parameter etc
                Integer count = mapRefPath.get(tag).get(pathKey);
                mapRefPath.get(tag).replace(pathKey, count + 1);
            } else {
                // Adds new pathKey to existing tag
                // Very first occurrence of BO in this path
                mapRefPath.get(tag).put(pathKey, 1);
            }
        } else {
            // Create new mapPathCount for new tag
            // BO referenced in path for first time in the API
            Map<String, Integer> mapPathCount = new HashMap<>();
            mapPathCount.put(pathKey, 1);
            mapRefPath.put(tag, mapPathCount);
        }
    }
    public void checkPathItem(@NotNull PathItem pathItem, String path){
        // Loop over PathItem Parameters
        List<Parameter> parameters = pathItem.getParameters();
        if(parameters!=null){
            this.loopOverParameters(parameters, path);
        }

        // Loop over PathItem Operations
        List<Operation> operations = pathItem.readOperations();
        if (operations!= null) {
            this.loopOverOperations(operations, path);
        }
    }
    public void loopOverParameters(List<Parameter> parameters, String path) {
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                // Has parameter ref?
                if (parameter.get$ref()!=null) {
                    String ref = parameter.get$ref();

                    // Adds ref to path
                    this.addRefPath(true, this.mapRefPath, ref, path);
                }
                // Parameter has Content
                Content content = parameter.getContent();
                if (content!=null){
                    this.checkContent(content, path);
                }

                // Has parameter Schema.?
                Schema schema = parameter.getSchema();
                if(schema!=null){
                    // Send parameter name also
                    String name = parameter.getName();
                    this.checkSchema(schema, name, path);
                }
            }
        }
    }
    public void loopOverOperations(@NotNull List<Operation> operations, String path){
        for (Operation operation : operations) {
            // Loop over Operation Parameters
            List<Parameter> parameters = operation.getParameters();
            if(parameters!=null){
                this.loopOverParameters(parameters, path);
            }

            // Loop over Operation Request Body
            RequestBody requestBody = operation.getRequestBody();
            if(requestBody!=null){
                Content content = requestBody.getContent();
                if(content!=null){
                    this.checkContent(content, path);
                }
            }

            // Loop over Operation Responses
            ApiResponses responses = operation.getResponses();
            if (responses!= null) {
                Collection<ApiResponse> responsesValues = responses.values();
                for (ApiResponse response : responsesValues) {
                    if (response.get$ref() != null) {
                        String ref = response.get$ref();

                        // Adds ref to path
                        this.addRefPath(true, this.mapRefPath, ref, path);
                    }

                    Content content = response.getContent();
                    if (content!=null){
                        this.checkContent(content, path);
                    }
                }
            }

            // loop over Operation CallBacks
            Map<String, Callback> callbackMap = operation.getCallbacks();
            if(callbackMap!=null){
                for(Callback callback : callbackMap.values()){
                    Collection<PathItem> callbackPathItems = callback.values();
                    if(callbackPathItems!=null){
                        for(PathItem callbackPathItemEntry : callbackPathItems){
                            this.checkPathItem(callbackPathItemEntry, path);
                        }
                    }

                }
            }
        }
    }

    public void checkContent(@NotNull Content content, String path) {
        MediaType mediaType = content.get("application/json");
        if (mediaType!=null && mediaType.getSchema()!=null){
            Schema schema = mediaType.getSchema();

            if(schema!=null){
                this.checkSchema(schema, "",path);
            }
        }
    }

    public void checkSchema(@NotNull Schema schema, String name, String path) {
        // schema has ref
        String refSchema = schema.get$ref();
        if (refSchema!= null) {
            // Adds ref to path
            this.addRefPath(true, this.mapRefPath, refSchema, path);
        }

        // Check if is a Definition Object Candidate
        this.isDefinitionCandidate(schema, name, path);

        // schema has properties
        Map<String, Schema> properties = schema.getProperties();
        if(properties!=null){
            for(Map.Entry<String, Schema> entryProperty : properties.entrySet()){
                Schema schemaEntryProperty = entryProperty.getValue();
                String propertyName = entryProperty.getKey();

                String ref = schemaEntryProperty.get$ref();
                if(ref!=null){
                    this.addRefPath(true, this.mapRefPath, ref, path);
                }

                // Check if property item is a Definition Object Candidate
                this.isDefinitionCandidate(schemaEntryProperty, propertyName, path);
            }
        }

        // schema has items
        Schema items = schema.getItems();
        if (items!= null){
            String ref = items.get$ref();      // Consider to check further items type membership
            if (ref!= null) {
                // Adds ref to path
                this.addRefPath(true, this.mapRefPath, ref, path);
            }

            // It has additional Properties.?  Double check this case
            Schema additionalProperties = (Schema) items.getAdditionalProperties();
            if(additionalProperties!=null){
                this.checkSchema(additionalProperties, name, path);
            }

            // Check if item is a Definition Object Candidate
            this.isDefinitionCandidate(items, name, path);
        }
    }

    public void isDefinitionCandidate(@NotNull Schema schema, String name, String path) {
        List enums = schema.getEnum();
        if (enums!=null){
            // Define schema as Definition Object or
            // Register Definition Object ref in path
            this.addRefPath(false, this.mapDefRefPath, name, path);
        }
    }

    // Check where in Paths a BOM is referenced
    // 1 and same Path returns own path
    // 2 and same path returns bomPath/.Common
    // otherwise returns Common/.Common

    public void addBO(Boolean isBOM, String bom, Map.Entry<String,Schema> entry) {


        if(isBOM){
            String key = bom.substring(0, bom.length() - 3);
            this.businessObjects.put(key, new BusinessObject());
            // sets business object $uri
            String bomPath = this.getBOMPath(true, this.mapRefPath, bom)  + ".bomml.boml";
            this.businessObjects.get(key).setUri(bomPath);

            // Adds classDescriptors
            this.businessObjects.get(key).appendClassDescriptor(true, this, entry);
        } else {
            String key = bom;
            key = key + "Def";
            this.definitionObjects.put(key, new BusinessObject());
            // sets definition object $uri
            String bomDefPath = this.getBOMPath(false, this.mapDefRefPath, key)  + ".bomml.boml";
            this.definitionObjects.get(key).setUri(bomDefPath);

            // Adds classDescriptors
            this.definitionObjects.get(key).appendClassDescriptor(false, this, entry);
        }
    }

    @RequestMapping("/openapi")
    public ResponseEntity<String> processOpenApiJson(@org.springframework.web.bind.annotation.RequestBody String openApiJson) throws JsonProcessingException {
        // To proccess OpenAPI JSON


        Paths paths = new Paths();
        Map<String, Schema> schemas = new HashMap<>();

        OpenAPI openAPI = new OpenAPIV3Parser().readContents(openApiJson).getOpenAPI();

        // String target = "User";
        String level = "Automation";
        String rootApiFolderName = "Common";
        String $uri = level + "/Business Objects/" + rootApiFolderName + "/Integration/Camunda";
        //String pathCommon = level + "/Business Objects/Common/Integration/Camunda/";

        // Now you can work with the OpenAPI object
        if (openAPI != null) {
            // Access to its compoments and schemas

            schemas = openAPI.getComponents().getSchemas();
            paths = openAPI.getPaths();

            // Loop over ALL $REF
            // to fill map mapRefPath
            for (String path : paths.keySet()) {
                PathItem pathItem = paths.get(path);

                this.checkPathItem(pathItem, path);
            }
            // ENDS Loop over ALL $REF

            // Build Business Object Array

            for (Map.Entry<String,Schema> entry : schemas.entrySet()){

                String bom = entry.getKey();
                Schema schema = entry.getValue();

                List anEnum = schema.getEnum();
                if(anEnum!=null){
                    // Is a Def
                    this.addBO(false, bom, entry);
                } else {
                    // BOM behavior
                    this.addBO(true, bom, entry);
                }
            }
        }

        ObjectMapper objectMapper = new ObjectMapper();
        // Return JSON string
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(this.definitionObjects));
    }


}
