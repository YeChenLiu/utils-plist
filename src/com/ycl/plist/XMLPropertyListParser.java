package com.ycl.plist;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class XMLPropertyListParser {
  private static DocumentBuilderFactory docBuilderFactory = null;
  
  private static synchronized void initDocBuilderFactory() throws ParserConfigurationException {
    docBuilderFactory = DocumentBuilderFactory.newInstance();
    docBuilderFactory.setIgnoringComments(true);
    docBuilderFactory.setCoalescing(true);
  }
  
  private static synchronized DocumentBuilder getDocBuilder() throws ParserConfigurationException {
    if (docBuilderFactory == null)
      initDocBuilderFactory(); 
    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    docBuilder.setEntityResolver(new EntityResolver() {
          public InputSource resolveEntity(String publicId, String systemId) {
            if (publicId.equals("-//Apple Computer//DTD PLIST 1.0//EN") || publicId.equals("-//Apple//DTD PLIST 1.0//EN"))
              return new InputSource(new ByteArrayInputStream(new byte[0])); 
            return null;
          }
        });
    return docBuilder;
  }
  
  public static NSObject parse(File f) throws Exception {
    DocumentBuilder docBuilder = getDocBuilder();
    Document doc = docBuilder.parse(f);
    return parseDocument(doc);
  }
  
  public static NSObject parse(byte[] bytes) throws Exception {
    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
    return parse(bis);
  }
  
  public static NSObject parse(InputStream is) throws Exception {
    DocumentBuilder docBuilder = getDocBuilder();
    Document doc = docBuilder.parse(is);
    return parseDocument(doc);
  }
  
  private static NSObject parseDocument(Document doc) throws Exception {
    if (!doc.getDoctype().getName().equals("plist"))
      throw new UnsupportedOperationException("The given XML document is not a property list."); 
    List<Node> rootNodes = filterElementNodes(doc.getDocumentElement().getChildNodes());
    if (rootNodes.size() > 0)
      return parseObject(rootNodes.get(0)); 
    throw new Exception("No root node found!");
  }
  
  private static NSObject parseObject(Node n) throws Exception {
    String type = n.getNodeName();
    if (type.equals("dict")) {
      NSDictionary dict = new NSDictionary();
      List<Node> children = filterElementNodes(n.getChildNodes());
      for (int i = 0; i < children.size(); i += 2) {
        Node key = children.get(i);
        Node val = children.get(i + 1);
        String keyString = key.getChildNodes().item(0).getNodeValue();
        for (int j = 1; j < key.getChildNodes().getLength(); j++)
          keyString = keyString + key.getChildNodes().item(j).getNodeValue(); 
        dict.put(keyString, parseObject(val));
      } 
      return dict;
    } 
    if (type.equals("array")) {
      List<Node> children = filterElementNodes(n.getChildNodes());
      NSArray array = new NSArray(children.size());
      for (int i = 0; i < children.size(); i++)
        array.setValue(i, parseObject(children.get(i))); 
      return array;
    } 
    if (type.equals("true"))
      return new NSNumber(true); 
    if (type.equals("false"))
      return new NSNumber(false); 
    if (type.equals("integer"))
      return new NSNumber(n.getChildNodes().item(0).getNodeValue()); 
    if (type.equals("real"))
      return new NSNumber(n.getChildNodes().item(0).getNodeValue()); 
    if (type.equals("string")) {
      NodeList children = n.getChildNodes();
      if (children.getLength() == 0)
        return new NSString(""); 
      String string = children.item(0).getNodeValue();
      for (int i = 1; i < children.getLength(); i++)
        string = string + children.item(i).getNodeValue(); 
      return new NSString(string);
    } 
    if (type.equals("data"))
      return new NSData(n.getChildNodes().item(0).getNodeValue()); 
    if (type.equals("date"))
      return new NSDate(n.getChildNodes().item(0).getNodeValue()); 
    return null;
  }
  
  private static List<Node> filterElementNodes(NodeList list) {
    List<Node> result = new ArrayList<Node>(list.getLength());
    for (int i = 0; i < list.getLength(); i++) {
      if (list.item(i).getNodeType() == 1)
        result.add(list.item(i)); 
    } 
    return result;
  }
}


/* Location:              D:\360Downloads\ycl-plist.jar!\com\ycl\plist\XMLPropertyListParser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */
