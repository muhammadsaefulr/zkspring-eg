/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfincoreplus.mybatis.plugin;

import org.mybatis.generator.api.FullyQualifiedTable;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.*;

/**
 *
 * @author user
 */
public class ForUpdatePlugin extends PluginAdapter {

    private Map<FullyQualifiedTable, List<XmlElement>> elementsToAdd = new HashMap<>();

    @Override
    public boolean validate(List<String> list) {
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        List<XmlElement> elements = elementsToAdd.get(introspectedTable.getFullyQualifiedTable());
        if (elements != null) {
            for (XmlElement element : elements) {
                document.getRootElement().addElement(element);
            }
        }
        return true;
    }

    @Override
    public boolean sqlMapSelectByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        //System.out.println("sqlMapSelectByPrimaryKeyElementGenerated " + introspectedTable.getFullyQualifiedTable().getIntrospectedTableName());
        copyAndSaveElement(element, introspectedTable.getFullyQualifiedTable());
        return true;
    }

    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        copyAndAddMethod(method, interfaze);
        return true;
    }

    private void copyAndAddMethod(Method method, Interface interfaze) {
        Method newMethod = new Method(method);
        newMethod.setName(method.getName() + "ForUpdateNowait"); //$NON-NLS-1$
        interfaze.addMethod(newMethod);
    }

    private void copyAndSaveElement(XmlElement element, FullyQualifiedTable fqt) {
        //if (!ada) {
        Attribute newAttribute;
        List<XmlElement> elements = elementsToAdd.get(fqt);
        if (elements == null) {
            elements = new ArrayList<>();
            elementsToAdd.put(fqt, elements);
        }
        XmlElement newElement = new XmlElement(element);

        // save the new element locally.   We'll add it to the document
        // later
        for (Iterator<Attribute> iterator = newElement.getAttributes().iterator(); iterator.hasNext();) {
            Attribute attribute = iterator.next();
            //  System.out.println(fqt.getIntrospectedTableName() + " " + attribute.getName() + " " + attribute.getName() + " "
            //         + attribute.getValue() + " " + sudah);
            if ("id".equals(attribute.getName())) { //$NON-NLS-1$
                //      if (!sudah) {
                iterator.remove();
                newAttribute = new Attribute("id", attribute.getValue() + "ForUpdateNowait"); //$NON-NLS-1$ //$NON-NLS-2$
                newElement.addAttribute(newAttribute);
                newElement.addElement(new TextElement("for update nowait"));
                elements.add(newElement);
                //          sudah = true;
                //     }
                break;
            }
        }
        //}
    }
}
