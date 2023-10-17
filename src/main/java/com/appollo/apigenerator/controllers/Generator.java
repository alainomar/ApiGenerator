package com.appollo.apigenerator.controllers;

import com.appollo.apigenerator.models.BussinessObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tags;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.tags.Tag;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.reference.Reference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/generator")
public class Generator {

    public Map<String, Map<String, Integer>> mapRefPath = new HashMap<>();

    public void addRefPath(String ref, String path){
        // Register BOM 's path
        // Verify if tag exists in  mapRefPath
        // Cleanning ref and turning in tag nad path into pathKey
        String tag1 = ref.substring(ref.lastIndexOf("/") + 1);
        String tag = tag1.substring(0, tag1.length() - 3);
        String pathKey = "";

        if(path.indexOf("/", 1) == -1){
            pathKey = path.substring(0,  path.length());
        } else {
            pathKey = path.substring(0,  path.indexOf("/", 1));
        }

        if (this.mapRefPath.containsKey(tag)) {
            // Adds pathKey to existing tag
            if(this.mapRefPath.get(tag).containsKey(pathKey)) {
                // Update mapPath
                Integer count = this.mapRefPath.get(tag).get(pathKey);
                this.mapRefPath.get(tag).replace(pathKey, count + 1);
            } else {
                // Adds new pathKey to existing tag
                this.mapRefPath.get(tag).put(pathKey, 1);
            }
        } else {
            // Create new mapPathCount for new tag
            Map<String, Integer> mapPathCount = new HashMap<>();
            mapPathCount.put(pathKey, 1);
            this.mapRefPath.put(tag, mapPathCount);
        }
    }

    public void loopOverParameters(List<Parameter> parameters, String path) {
        //List<Parameter> parameters = pathItem.getParameters();
        if (parameters != null) {
            for (Parameter parameter : parameters) {
                if (parameter.get$ref() != null) {
                    String ref = parameter.get$ref();

                    // Adds ref to path
                    this.addRefPath(ref, path);
                }
            }
        }
    }

    public String getBOMPath(String tag){
        String bom = tag.substring(0, tag.length() - 3);

        String bomPath = "";
        if (this.mapRefPath.containsKey(bom)) {
            Map<String, Integer> a = this.mapRefPath.get(bom);
            if(a.size() > 1){
                bomPath =  "/Common/.Common/";
            }

            if (a.size() == 1){
                bomPath = "/Common/." + bom;
            }
        } else {
            bomPath = "/" + bom +  "/." + bom;
        }
        return bomPath;
    }

    @RequestMapping("/openapi")
    public ResponseEntity<String> processOpenApiJson(@RequestBody String openApiJson) throws JsonProcessingException {
        // To proccess OpenAPI JSON

        Map<String, BussinessObject> bussinessObjects = new HashMap<String, BussinessObject>();
        Paths paths = new Paths();
        Map<String, Schema> schemas = new HashMap<>();
        // Schema schema = new Schema();
        // PathItem user = new PathItem();

        OpenAPI openAPI = new OpenAPIV3Parser().readContents(openApiJson).getOpenAPI();

        // String target = "User";
        String level = "Automation";
        String rootApiFolderName = "Common";
        String $uri = level + "/Business Objects/" + rootApiFolderName + "/Integration/Camunda";
        //String pathCommon = level + "/Business Objects/Common/Integration/Camunda/";

        // Now you can work with the OpenAPI object
        if (openAPI != null) {
            // Access to its compoments and schemas

            List<Tag> tags = openAPI.getTags();
            schemas = openAPI.getComponents().getSchemas();
            paths = openAPI.getPaths();

            // Loop over ALL $REF
            // to fill map mapRefPath
            for (String path : paths.keySet()) {
                PathItem pathItem = paths.get(path);

                // Loop over $ref in Parameters fill mapRefPath
                this.loopOverParameters(pathItem.getParameters(), path);

                // Loop over $ref in Operation fill mapRefPath
                // Operations has tags !!!
                List<Operation> operations = pathItem.readOperations();
                if (operations!= null) {
                    for (Operation operation : operations) {
                        // Loop over Operations Parameters
                        this.loopOverParameters(operation.getParameters(), path);

                        // Loop over Operations Responses
                        ApiResponses responses = operation.getResponses();
                        if (responses!= null) {
                            Collection<ApiResponse> responsesValues = responses.values();
                            for (ApiResponse response : responsesValues) {
                                if (response.get$ref() != null) {
                                    String ref = response.get$ref();

                                    // Adds ref to path
                                    this.addRefPath(ref, path);
                                }

                                Content content = response.getContent();
                                if (content!=null){
                                    MediaType mediaType = content.get("application/json");
                                    if (mediaType!=null && mediaType.getSchema()!=null){
                                        String refContentSchema = mediaType.getSchema().get$ref();
                                        if (refContentSchema!= null) {
                                            // Adds ref to path
                                            this.addRefPath(refContentSchema, path);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
            // ENDS Loop over ALL $REF

            // Build Bussiness Object Array
            // BussinessObject[] appolloAPI = new BussinessObject[];
            for (Map.Entry<String,Schema> entry : schemas.entrySet()){

                String bom = entry.getKey();
                String key = bom.substring(0, bom.length() - 3);

                bussinessObjects.put(key, new BussinessObject());
                String bomPath = this.getBOMPath(bom)  + ".bomml.boml";
                bussinessObjects.get(key).setUri(bomPath);

            }

            // REVISAR TAGS
            for (Tag tag : tags) {
            //    String path = pathTarget + tag.getName().replace(" ", "");
                BussinessObject bOject = new BussinessObject();
            //    bOject.setUri(path);
              //  bussinessObject.put(tag.getName(), bOject);
            }

            // user = paths.get("/user");

            // schema = schemas.get(target); // openAPI.getComponents().getSchemas().get

            //  bussinessObject.setUri(path + target + "/." + target + ".bomml.boml");
            //  bussinessObject.setClassDescriptors(new ClassDescriptor[]{new ClassDescriptor(schema, path, target)});

            // Processing the paths

            // Processing the schemas

        }

        ObjectMapper objectMapper = new ObjectMapper();

        // Convert Java object to JSON string
       /* String outJSON = objectMapper.writeValueAsString(new BussinessObject[]{
                bussinessObject,     // target Json
               // bussinessObject      // Common Json
        }); */
        String outOpenJSON = objectMapper.writeValueAsString(openApiJson);

        // Retorna el string JSON
        return ResponseEntity.ok().body(objectMapper.writeValueAsString(mapRefPath));
    }

}


/*
public ResponseEntity<String> processOpenApiJson(@RequestBody String openApiJson) throws JsonProcessingException {
        // Aquí puedes procesar el JSON de OpenAPI como desees
        // y retornar el contenido como JSON en pantalla

        // Load tAPI = new OpenAPIV3Parser().readContents(openApiJson).getOpenAPI();

        // Now you can work with the OpenAPI object
        // For examhe OpenAPI JSON specification into an OpenAPI object
        //        OpenAPI openple, you can access the components, schemas, paths, etc.

        // Access to its compoments and schemas
        Map<String, Schema> schemas =  openAPI.getComponents().getSchemas(); // openAPI.getPaths();
        Paths paths =  openAPI.getPaths();





          por cada Schema  de sus componentes iterar
               Si item es Object
                   - Crear directorio con nombre title
                   - Crear dentro del directorio
                        bomml.metadata ..........  {"artifactType":"BOM", "artifactSubtype":"DEFAULT"}
                        bomml.i18.properties.....  {"title.userId":{"Description":{"en_US":""}, "Label":{"en_US":"User Id"}, "Placeholder":{"en_US":""}, "ToolTip":{"en_US":""}, "ErrorMessage":{"en_US":""}}}
                        Si sus properties no contienen ref
                            hacer file {
                                   "$uri" = title/.title.bomml.boml
                                   "classDescriptors":[
                                        {
                                            "objectClass": "true",
                                            "$uri": "$uri" + #UserDto",
                                            "title": title,
                                            "properties": {
                                                "profile": {
                                                    "type": "ref",
                                                    "order": 1,
                                                    "ref": "title" + "/.Common.bomml.boml# + UserProfileDto" // capturar UserProfileDto
                                                },
                                                "credentials": {
                                                    "type": "ref",
                                                    "order": 2,
                                                    "ref": "title" + "/.Common.bomml.boml# + UserCredentialsDto"
                                                }
                                            }
                                        }
                                   ]
                                }
                        Else
                            - Crear directorio con nombre Common
                              bomml.metadata ..........  {"artifactType":"BOM", "artifactSubtype":"DEFAULT"}
                              bomml.i18.properties.....  {"title.userId":{"Description":{"en_US":""}, "Label":{"en_US":"User Id"}, "Placeholder":{"en_US":""}, "ToolTip":{"en_US":""}, "ErrorMessage":{"en_US":""}}}

                            - Crear file Common.bomml.boml

                            {
                                "$uri": "Automation/Business Objects/Common/Integration/Camunda/User/.Common.bomml.boml",
                                "classDescriptors": [
                                    {
                                        "objectClass": "true",
                                        "$uri": "Automation/Business Objects/Common/Integration/Camunda/User/.Common.bomml.boml#UserCredentialsDto",
                                        "title": "UserCredentialsDto",
                                        "properties": {
                                            "authenticatedUserPassword": {
                                                "error": "",
                                                "label": "Authentication User Password",
                                                "icon": "{icon:'', color:'', before: false}",
                                                "placeHolder": "",
                                                "tooltip": "",
                                                "type": "string",
                                                "order": 1,
                                                "access": "Input",
                                                "formatDesc": {
                                                    "formatType": "text"
                                                }
                                            },
                                            "password": {
                                                "error": "",
                                                "label": "Password",
                                                "icon": "{icon:'', color:'', before: false}",
                                                "placeHolder": "",
                                                "tooltip": "",
                                                "type": "string",
                                                "order": 0,
                                                "access": "Input",
                                                "formatDesc": {
                                                    "formatType": "text"
                                                }
                                            }
                                        }
                                    },
                                    {
                                        "objectClass": "true",
                                        "$uri": "Automation/Business Objects/Common/Integration/Camunda/User/.Common.bomml.boml#UserProfileDto",
                                        "title": "UserProfileDto",
                                        "properties": {
                                            "id": {
                                                "error": "",
                                                "label": "ID",
                                                "icon": "{icon:'', color:'', before: false}",
                                                "placeHolder": "",
                                                "tooltip": "",
                                                "type": "string",
                                                "order": 0,
                                                "access": "Input",
                                                "formatDesc": {
                                                    "formatType": "text"
                                                }
                                            },
                                            "firstName": {
                                                "error": "",
                                                "label": "First Name",
                                                "icon": "{icon:'', color:'', before: false}",
                                                "placeHolder": "",
                                                "tooltip": "",
                                                "type": "string",
                                                "order": 1,
                                                "access": "Input",
                                                "formatDesc": {
                                                    "formatType": "text"
                                                }
                                            },
                                            "lastName": {
                                                "error": "",
                                                "label": "Last Name",
                                                "icon": "{icon:'', color:'', before: false}",
                                                "placeHolder": "",
                                                "tooltip": "",
                                                "type": "string",
                                                "order": 2,
                                                "access": "Input",
                                                "formatDesc": {
                                                    "formatType": "text"
                                                }
                                            },
                                            "email": {
                                                "error": "",
                                                "label": "Email",
                                                "icon": "{icon:'', color:'', before: false}",
                                                "placeHolder": "",
                                                "tooltip": "",
                                                "type": "string",
                                                "order": 3,
                                                "access": "Input",
                                                "formatDesc": {
                                                    "formatType": "text"
                                                }
                                            }
                                        }
                                    }
                                ]
                            }





    ObjectMapper objectMapper = new ObjectMapper();
    String response = objectMapper.writeValueAsString(); //openAPI.getComponents().getSchemas()
        return ResponseEntity.ok().body(response);
                }
       */
