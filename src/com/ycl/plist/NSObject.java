package com.ycl.plist;

import java.io.IOException;

public abstract class NSObject {
  public static final String NEWLINE = System.getProperty("line.separator");
  
  public static final String INDENT = "\t";
  
  public abstract void toXML(StringBuilder paramStringBuilder, int paramInt);
  
  void assignIDs(BinaryPropertyListWriter out) {
    out.assignID(this);
  }
  
  abstract void toBinary(BinaryPropertyListWriter paramBinaryPropertyListWriter) throws IOException;
  
  public String toXMLPropertyList() {
    StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    xml.append(NEWLINE);
    xml.append("<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
    xml.append(NEWLINE);
    xml.append("<plist version=\"1.0\">");
    xml.append(NEWLINE);
    toXML(xml, 0);
    xml.append(NEWLINE);
    xml.append("</plist>");
    return xml.toString();
  }
  
  protected void indent(StringBuilder xml, int level) {
    for (int i = 0; i < level; i++)
      xml.append("\t"); 
  }
}


/* Location:              D:\360Downloads\ycl-plist.jar!\com\ycl\plist\NSObject.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */
