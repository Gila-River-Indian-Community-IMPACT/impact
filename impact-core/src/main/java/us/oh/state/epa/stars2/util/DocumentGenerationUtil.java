package us.oh.state.epa.stars2.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import us.oh.state.epa.stars2.webcommon.AppBase;

public class DocumentGenerationUtil extends AppBase {
    XMLUtil xmlUtil = XMLUtil.getInstance();

    static class Variable {
        String variableName;

        List<DocumentGenerationBean> values = new ArrayList<DocumentGenerationBean>();

        int maxValues;
    }

    static enum TagType {
        OPEN_TAG, CLOSE_TAG, OPEN_CLOSE_TAG
    }

    static enum NodeType {
        PROPERTY_ELEMENT, FOREACH_ELEMENT, TEXT, END_DOCUMENT
    }

    static class Node {
        NodeType nodeType;

        TagType tagType;

        Map<String, String> attributes = new HashMap<String, String>();
    }

    private String template;

    private OutputStream output;

    private LinkedList<Variable> varStack = new LinkedList<Variable>();

    public void generate(String temp, OutputStream out,
                         DocumentGenerationBean data) 
        throws IOException, DocumentGenerationException {

        template = temp;
        output = out;

        Variable var = new Variable();
        var.variableName = "root";
        var.values.add(data);
        var.maxValues = 1;
        generateRecursive(0, // start position
                true, // write to output
                var, // variable
                null, // replace text with this string
                NodeType.END_DOCUMENT); // return when this node found
    }

    private void getAttributes(int startPos, int stopPos, Node node)
            throws DocumentGenerationException {

        String attributesStr = template.substring(startPos, stopPos);
        StringTokenizer st = new StringTokenizer(attributesStr, " ");
        while (st.hasMoreTokens()) {

            String attributeStr = st.nextToken();
            int equalPos = attributeStr.indexOf("=");
            if (equalPos < 0) {
                throw new DocumentGenerationException("Equal sign missing for attribute.");
            }

            String attributeName = attributeStr.substring(0, equalPos);
            String attributeValue;
            int startDQ = attributeStr.indexOf("\"", equalPos + 1);
            if (startDQ < 0) {
                throw new DocumentGenerationException("Opening double quote missing for attribute value.");
            }
            int endDQ = attributeStr.indexOf("\"", startDQ + 1);
            if (endDQ < 0) {
                throw new DocumentGenerationException("Closing double quote missing for attribute value.");
            }

            attributeValue = attributeStr.substring(startDQ + 1, endDQ);
            node.attributes.put(attributeName, attributeValue);
        }
    }

    private int getNextNode(int pos, boolean writeToOutput,
                            boolean searchForText, Node retVal)
        throws DocumentGenerationException, IOException {

        boolean afterOpenTag = false;

        for (;;) {
            int startPos;
            int stopPos;
            
            if ((startPos = template.indexOf("<", pos)) < 0
                || (stopPos = template.indexOf(">", startPos)) < 0) {

                retVal.nodeType = NodeType.END_DOCUMENT;
                if (writeToOutput) {
                    output.write(template.substring(pos).getBytes());
                }
                return -1;
            }

            TagType tagType;
            
            if (template.charAt(startPos + 1) == '/') {
                tagType = TagType.CLOSE_TAG;
            }
            else if (template.indexOf("/", startPos) < stopPos) {
                tagType = TagType.OPEN_CLOSE_TAG;
            }
            else {
                tagType = TagType.OPEN_TAG;
            }

            if (searchForText) {
                if (afterOpenTag && tagType == TagType.CLOSE_TAG) {
                    retVal.nodeType = NodeType.TEXT;
                    return startPos;
                }
                else if (tagType == TagType.OPEN_TAG) {
                    afterOpenTag = true;
                }
                else {
                    afterOpenTag = false;
                }
            }

            int tagNamePos 
                = (tagType == TagType.CLOSE_TAG) ? startPos + 2 : startPos + 1;
                    
            int spacePos 
                = (tagType == TagType.OPEN_CLOSE_TAG) ? stopPos - 1 : stopPos;
                    
            for (int idx = startPos; idx < stopPos; idx++) {
                if (template.charAt(idx) == ':') {
                    tagNamePos = idx + 1;
                }
                else if (template.charAt(idx) == ' ') {
                    spacePos = idx;
                    break;
                }
            }

            String tagName;
            if (tagNamePos < spacePos) {
                tagName = template.substring(tagNamePos, spacePos);
            }
            else {
                tagName = "";
            }

            if (tagName.equals("property") || tagName.equals("foreach")) {
                retVal.nodeType 
                    = tagName.equals("property") ? NodeType.PROPERTY_ELEMENT : NodeType.FOREACH_ELEMENT;
                retVal.tagType = tagType;
                getAttributes(spacePos, stopPos, retVal);
                return stopPos + 1;
            }

            if (writeToOutput) {
                output.write(template.substring(pos, stopPos + 1).getBytes());
            }
            pos = stopPos + 1;
        }
    }

    private int generateRecursive(int pos, boolean writeToOutput, Variable var,
                                  String replaceText, NodeType stopNodeType) 
        throws IOException, DocumentGenerationException {
                                                                                   
        if (var != null) {
            varStack.addFirst(var);
        }

        for (;;) {
            Node node = new Node();
            pos = getNextNode(pos, writeToOutput, replaceText != null, node);

            if (node.nodeType == stopNodeType
                && (stopNodeType == NodeType.END_DOCUMENT || node.tagType == TagType.CLOSE_TAG)) {
                break;
            }
            else if (node.nodeType == NodeType.END_DOCUMENT) {
                throw new DocumentGenerationException("Unexpected end of document.");
            }

            if (node.tagType == TagType.OPEN_CLOSE_TAG) {
                continue;
            }
            else if (node.tagType == TagType.CLOSE_TAG) {
                throw new DocumentGenerationException("Unexpected close tag.");
            }

            if (node.nodeType == NodeType.PROPERTY_ELEMENT) {
                
                String variableName = getNodeAttribute(node, "var", "root");
                String propertyName = getNodeAttribute(node, "property");
                String required = getNodeAttribute(node, "required", "no");

                int subscript = getNodeIntAttribute(node, "subscript", 0);
                String propertyValue = getProperty(variableName, propertyName,
                                                   subscript, required);

                if ((propertyValue == null || propertyValue.length() == 0)
                    && (required != null && (required.equalsIgnoreCase("yes") 
                                             || required.equalsIgnoreCase("true"))))
                    {
                        throw new DocumentGenerationException("A required property was not found: " 
                                                              + propertyName + ".");
                    }
                else if (propertyValue != null && propertyValue.length() == 0) {
                    pos = generateRecursive(pos, // start position.
                                            false, // write to output.
                                            null, // variable.
                                            propertyValue, // replace text with this string.
                                            NodeType.PROPERTY_ELEMENT); // return when this node found.
                }
                else {
                    pos = generateRecursive(pos, // start position
                                            writeToOutput, // write to output
                                            null, // variable
                                            propertyValue, // replace text with this string
                                            NodeType.PROPERTY_ELEMENT); // return when this node found.
                }

            }
            else if (node.nodeType == NodeType.FOREACH_ELEMENT) {

                String variableName = getNodeAttribute(node, "var", "root");
                String collectionName = getNodeAttribute(node, "collection");
                int subscript = getNodeIntAttribute(node, "subscript", 0);
                Variable cursorVar = new Variable();
                cursorVar.variableName = getNodeAttribute(node, "cursorVar");
                cursorVar.maxValues = getNodeIntAttribute(node, "cursorSize", 1);

                String required = getNodeAttribute(node, "required", "no");
                boolean reqd = false;
                if (required.equalsIgnoreCase("yes") || required.equalsIgnoreCase("true")) {
                    reqd = true;
                }

                List<DocumentGenerationBean> childCollection 
                    = getChildCollection(variableName, collectionName, subscript, reqd);

                if (childCollection == null || childCollection.size() == 0) {
                    pos = generateRecursive(pos,   // position.
                                            false, // write to output.
                                            cursorVar,  // variable.
                                            null,  // replace text with this string.
                                            NodeType.FOREACH_ELEMENT); // return when this node found.
                    continue;
                }

                int endPos = -1;
                int idx;
                for (idx = 0; idx < childCollection.size(); idx += cursorVar.maxValues) {
                    cursorVar.values.clear();

                    int xx;
                    for (xx = 0; xx < cursorVar.maxValues; xx++) {
                        if (idx + xx >= childCollection.size()) {
                            break;
                        }
                        cursorVar.values.add(childCollection.get(idx + xx));
                    }
                    endPos = generateRecursive(pos, // position
                            true, // write to output
                            cursorVar, // variable
                            null, // replace text with this string
                            NodeType.FOREACH_ELEMENT); // return when this node
                                                        // found
                }
                pos = endPos;
            }
            else if (node.nodeType == NodeType.TEXT) {
                if (replaceText != null && writeToOutput) {
                    String xmlText = xmlUtil.utf2xml(replaceText);
                    output.write(xmlText.getBytes());
                }
                continue;
            }

        }

        if (var != null) {
            varStack.removeFirst();
        }
        return pos;
    }

    private Variable getVariable(String variableName)
            throws DocumentGenerationException {
        for (Variable var : varStack) {
            if (var.variableName.equals(variableName)) {
                return var;
            }
        }
        throw new DocumentGenerationException("variable not found: "
                + variableName);
    }

    private String getProperty(String variableName, String propertyName,
                               int subscript, String required)
        throws DocumentGenerationException {

        Variable var = getVariable(variableName);

        if (subscript < 0 || subscript >= var.maxValues) {
            throw new DocumentGenerationException("Subscript out of range. ");
        }

        if (subscript >= var.values.size()) {
            return "";
        }

        String retVal = var.values.get(subscript).getProperties().get(propertyName);
                                                                      
        if (retVal == null) {
            retVal = "";
        }
        return retVal;
    }

    private List<DocumentGenerationBean> getChildCollection(String variableName, String collectionName, 
                                                            int subscript, boolean required)
                                                            
        throws DocumentGenerationException {

        Variable var = getVariable(variableName);
        
        if (subscript < 0 || subscript >= var.maxValues) {
            throw new DocumentGenerationException("Subscript out of range.");
        }

        if (subscript >= var.values.size()) {
            List<DocumentGenerationBean> emptyCollection = new ArrayList<DocumentGenerationBean>(0);
            return emptyCollection;
        }

        List<DocumentGenerationBean> retVal 
            = var.values.get(subscript).getChildCollections().get(collectionName);
        
        if (retVal == null && required) {
            throw new DocumentGenerationException("A required collection was not found: " + collectionName + ".");
        }

        return retVal;
    }

    private String getNodeAttribute(Node node, String attributeName)
        throws DocumentGenerationException {

        String retVal = node.attributes.get(attributeName);
        if (retVal == null) {
            throw new DocumentGenerationException("Attribute not found: "
                                                  + attributeName + ".");
        }
        return retVal;
    }

    private String getNodeAttribute(Node node, String attributeName,
                                    String defaultValue) {

        String retVal = node.attributes.get(attributeName);
        if (retVal == null) {
            return defaultValue;
        }
        return retVal;
    }

    private int getNodeIntAttribute(Node node, String attributeName,
            int defaultValue) throws DocumentGenerationException {
        String retStr = node.attributes.get(attributeName);
        if (retStr == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(retStr);
        } catch (NumberFormatException ex) {
            throw new DocumentGenerationException(
                    "attribute value is not a number: " + retStr);
        }
    }

    public static void main(String[] args) throws DocumentGenerationException,
            IOException {
        DocumentGenerationBean eu1 = new DocumentGenerationBean();
        eu1.getProperties().put("eu_id", "B001");
        eu1.getProperties().put("desc", "A big EU");

        DocumentGenerationBean eu2 = new DocumentGenerationBean();
        eu2.getProperties().put("eu_id", "B002");
        eu2.getProperties().put("desc", "A small EU");

        DocumentGenerationBean eu3 = new DocumentGenerationBean();
        eu3.getProperties().put("eu_id", "B003");
        eu3.getProperties().put("desc", "A midsize EU");

        DocumentGenerationBean eu4 = new DocumentGenerationBean();
        eu4.getProperties().put("eu_id", "B004");
        eu4.getProperties().put("desc", "A gigantic EU");

        ArrayList<DocumentGenerationBean> eus;

        eus = new ArrayList<DocumentGenerationBean>();
        eus.add(eu1);
        eus.add(eu2);

        DocumentGenerationBean euGroup1 = new DocumentGenerationBean();
        euGroup1.getProperties().put("eu_group_name", "The EU Group");
        euGroup1.getChildCollections().put("eus", eus);

        eus = new ArrayList<DocumentGenerationBean>();
        eus.add(eu3);
        eus.add(eu4);

        DocumentGenerationBean euGroup2 = new DocumentGenerationBean();
        euGroup2.getProperties().put("eu_group_name", "Another EU Group");
        euGroup2.getChildCollections().put("eus", eus);

        ArrayList<DocumentGenerationBean> euGroups = new ArrayList<DocumentGenerationBean>();
        euGroups.add(euGroup1);
        euGroups.add(euGroup2);

        DocumentGenerationBean data = new DocumentGenerationBean();

        eus = new ArrayList<DocumentGenerationBean>();
        eus.add(eu1);
        eus.add(eu2);
        eus.add(eu3);
        eus.add(eu4);

        data.getProperties().put("facility_name", "The Alex pollution company");
        data.getProperties().put("facility_id", "001-333-444");
        data.getChildCollections().put("eu_groups", euGroups);
        data.getChildCollections().put("eus", eus);

        java.io.FileReader fi = new java.io.FileReader(args[0]);
        StringBuffer template = new StringBuffer();
        int len;
        char[] buf = new char[4096];
        while ((len = fi.read(buf)) > 0) {
            template.append(buf, 0, len);
        }
        fi.close();

        new DocumentGenerationUtil().generate(template.toString(),
                new java.io.FileOutputStream(args[1]), data);
    }
}
