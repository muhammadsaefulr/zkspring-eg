/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfincoreplus.mybatis.plugin;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
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
public class EndDateLimitPlugin extends PluginAdapter {

    public EndDateLimitPlugin() {
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
                endDateIsNullOrGreaterThanOrEqualTo(introspectedTable, innerClass);
            }
        }
        return true;
    }

    private void endDateIsNullOrGreaterThanOrEqualTo(IntrospectedTable introspectedTable, InnerClass innerClass) {
        String endDate = introspectedTable.getTableConfiguration()
                .getProperty("endDateLimitColumns");

        if (endDate != null) {
            String[] lstColumns = StringUtils.split(endDate, ",");
            for (IntrospectedColumn introspectedColumn : introspectedTable.getNonBLOBColumns()) {
                if (ArrayUtils.contains(lstColumns, introspectedColumn.getActualColumnName())) {

                    StringBuilder sb = new StringBuilder();
                    sb.append(introspectedColumn.getJavaProperty());
                    sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
                    sb.insert(0, "and");
                    sb.append("IsNullOrGreaterThanOrEqualTo");

                    Method method = new Method(sb.toString());
                    method.setVisibility(JavaVisibility.PUBLIC);
                    method.addParameter(new Parameter(introspectedColumn.getFullyQualifiedJavaType(),
                            "value"));
//                    method.setName(sb.toString());
                    method.setReturnType(FullyQualifiedJavaType.getCriteriaInstance());

                    sb.setLength(0);
                    sb.append("addCriterionForJDBCDate(\"coalesce(");
                    sb.append(MyBatis3FormattingUtilities.getAliasedActualColumnName(introspectedColumn));
                    sb.append(",current_date+1000) >= \", value,\"");
                    sb.append(MyBatis3FormattingUtilities.getAliasedActualColumnName(introspectedColumn));
                    // sb.append(introspectedColumn.getJavaProperty());
                    sb.append("\");");
                    //method.addBodyLine("if (value == null){ return (Criteria)this; }");
                    method.addBodyLine(sb.toString());
                    method.addBodyLine("return (Criteria)this;");
                    innerClass.addMethod(method);
                }
            }
        }

    }
}
