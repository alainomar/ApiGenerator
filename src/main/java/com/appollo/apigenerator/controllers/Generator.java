package com.appollo.apigenerator.controllers;

import com.appollo.apigenerator.models.BussinessObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/generator")
public class Generator {

    @RequestMapping("/openapi")
    public ResponseEntity<String> processOpenApiJson(@RequestBody String openApiJson) throws JsonProcessingException {
        // Aquí puedes procesar el JSON de OpenAPI como desees
        // y retornar el contenido como JSON en pantalla

        BussinessObject bussinessObject = new BussinessObject();
        Paths paths = new Paths();
        Schema schema = new Schema();
        PathItem user = new PathItem();

        OpenAPI openAPI = new OpenAPIV3Parser().readContents(openApiJson).getOpenAPI();

        String target = "User";
        String level = "Automation";
        String rootApiFolderName = "Common";
        String pathTarget = level + "/Business Objects/" + rootApiFolderName + "/Integration/Camunda/" + target;
        String pathCommon = level + "/Business Objects/Common/Integration/Camunda/";


        // Now you can work with the OpenAPI object
        // OpenAPI openple, you can access the components, schemas, paths, etc.


        if (openAPI != null) {
            // Access to its compoments and schemas

            Map<String, Schema> schemas = openAPI.getComponents().getSchemas();
            paths = openAPI.getPaths();
            user = paths.get("/user");

            schema = schemas.get(target); // openAPI.getComponents().getSchemas().get

            //  bussinessObject.setUri(path + target + "/." + target + ".bomml.boml");
            //  bussinessObject.setClassDescriptors(new ClassDescriptor[]{new ClassDescriptor(schema, path, target)});

            // Processing the paths

            // Processing the schemas

        }

        ObjectMapper objectMapper = new ObjectMapper();

        // Convert Java object to JSON string
        String outJSON = objectMapper.writeValueAsString(new BussinessObject[]{
                bussinessObject,     // target Json
               // bussinessObject      // Common Json
        });
        String outOpenJSON = objectMapper.writeValueAsString(openApiJson);

        // Retorna el string JSON
        return ResponseEntity.ok().body(outJSON);
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
