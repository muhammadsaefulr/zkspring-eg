/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfincoreplus.mybatis.plugin;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.List;

/**
 *
 * @author user
 */
public class CaseInsensitiveLikePlugin extends PluginAdapter {

    public CaseInsensitiveLikePlugin() {
        super();
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass,
            IntrospectedTable introspectedTable) {
        for (InnerClass innerClass : topLevelClass.getInnerClasses()) {
            if ("GeneratedCriteria".equals(innerClass.getType().getShortName())) {
                modifyGeneratedCriteria(introspectedTable, innerClass);
            }
        }
        return true;
    }

    private void modifyGeneratedCriteria(IntrospectedTable introspectedTable,
            InnerClass innerClass) {
        for (IntrospectedColumn introspectedColumn : introspectedTable.getNonBLOBColumns()) {
            if (!introspectedColumn.isJdbcCharacterColumn()
                    || !introspectedColumn.isStringColumn()) {
                continue;
            }

            StringBuilder sb = new StringBuilder();
            sb.append(introspectedColumn.getJavaProperty());
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            sb.insert(0, "and");
            sb.append("LikeInsensitive");

            Method method = new Method(sb.toString());
            method.setVisibility(JavaVisibility.PUBLIC);
            method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(),
                    "value"));
//            method.setName(sb.toString());
            method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());
            sb.setLength(0);
            //sb.append("addCriterion(\"upper(");
            sb.append("addCriterion(\"");
            sb.append(MyBatis3FormattingUtilities.getAliasedActualColumnName(introspectedColumn));
            //sb.append(") like\", value.toUpperCase(), \"");
            sb.append(" ilike \", value, \"");
            sb.append(introspectedColumn.getJavaProperty());
            sb.append("\");");
            method.addBodyLine("if (value == null){ return (Criteria)this; }");
            method.addBodyLine(sb.toString());
            method.addBodyLine("return (Criteria)this;");

            innerClass.addMethod(method);
        }
    }

}
