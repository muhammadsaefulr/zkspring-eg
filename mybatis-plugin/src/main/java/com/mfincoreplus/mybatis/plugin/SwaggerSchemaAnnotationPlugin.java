package com.mfincoreplus.mybatis.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.List;

public class SwaggerSchemaAnnotationPlugin extends PluginAdapter {
    private static final String DELIMITER = ",";
    private static final String DELIMITER_DESCRIPTION = "@";
    private static final String SWAGGER_SCHEMAS = "swaggerSchema";

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    private String annotateSchema(IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable) {
        String fmts = introspectedTable.getTableConfigurationProperty(SWAGGER_SCHEMAS);
        if (fmts != null && !"".equals(fmts)) {
            String[] formats = fmts.split(DELIMITER_DESCRIPTION);
            String[] columns = formats[0].split(DELIMITER);
            String[] annos = formats[1].split(DELIMITER);
            for (int i = 0; i < columns.length; i++) {
                if (columns[i].trim().equals(introspectedColumn.getActualColumnName())) {
//                    String pps = introspectedTable.getTableConfigurationProperty(JACKSON_PROPERTIES);
                    return annos[i].trim();
                }
            }
/*            for (String column : columns) {
                if (column.equals(introspectedColumn.getActualColumnName())) {
                    return anno;
                }
            }*/
        }
        return null;
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass, IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        String schema = annotateSchema(introspectedColumn, introspectedTable);
        if (schema != null) {
            String ann = "@Schema(description = \"" + schema + "\")";
            field.addAnnotation(ann);
            topLevelClass.addImportedType(new FullyQualifiedJavaType("io.swagger.v3.oas.annotations.media.Schema"));
        }
        return true;
    }
}
