package com.appollo.apigenerator.models;

import io.swagger.v3.oas.models.media.Schema;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Setter
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClassDescriptorPrimaryProperty extends  ClassDescriptorProperty{
    private String error;
    private String label;
    private PropertyIcon icon;
    private String placeholder;
    private String tooltip;
    private String access;
    private FormatDescription formatDesc;

    public ClassDescriptorPrimaryProperty(String label, Schema schema, int order) {
        super(schema, order);

        this.error = "";
        this.label = StringUtils.capitalize(label).replaceAll("(.)([A-Z])", "$1 $2");
        this.icon = new PropertyIcon("", "", false);
        this.placeholder = "";
        this.tooltip = "";
        this.access = "Input";
        this.formatDesc = new FormatDescription("text");

    }
}
