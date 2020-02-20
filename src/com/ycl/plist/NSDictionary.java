package com.ycl.plist;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class NSDictionary extends NSObject {
  private HashMap<String, NSObject> dict = new LinkedHashMap<String, NSObject>();
  
  public NSObject objectForKey(String key) {
    return this.dict.get(key);
  }
  
  public void put(String key, NSObject obj) {
    this.dict.put(key, obj);
  }
  
  public void put(String key, String obj) {
    put(key, new NSString(obj));
  }
  
  public void put(String key, int obj) {
    put(key, new NSNumber(obj));
  }
  
  public void put(String key, long obj) {
    put(key, new NSNumber(obj));
  }
  
  public void put(String key, double obj) {
    put(key, new NSNumber(obj));
  }
  
  public void put(String key, boolean obj) {
    put(key, new NSNumber(obj));
  }
  
  public void put(String key, Date obj) {
    put(key, new NSDate(obj));
  }
  
  public void put(String key, byte[] obj) {
    put(key, new NSData(obj));
  }
  
  public int count() {
    return this.dict.size();
  }
  
  public boolean equals(Object obj) {
    return (obj.getClass().equals(getClass()) && ((NSDictionary)obj).dict.equals(this.dict));
  }
  
  public String[] allKeys() {
    return (String[])this.dict.keySet().toArray((Object[])new String[0]);
  }
  
  public int hashCode() {
    int hash = 7;
    hash = 83 * hash + ((this.dict != null) ? this.dict.hashCode() : 0);
    return hash;
  }
  
  public void toXML(StringBuilder xml, int level) {
    indent(xml, level);
    xml.append("<dict>");
    xml.append(NSObject.NEWLINE);
    for (String key : this.dict.keySet()) {
      NSObject val = objectForKey(key);
      indent(xml, level + 1);
      xml.append("<key>");
      if (key.contains("&") || key.contains("<") || key.contains(">")) {
        xml.append("<![CDATA[");
        xml.append(key.replaceAll("]]>", "]]]]><![CDATA[>"));
        xml.append("]]>");
      } else {
        xml.append(key);
      } 
      xml.append("</key>");
      xml.append(NSObject.NEWLINE);
      val.toXML(xml, level + 1);
      xml.append(NSObject.NEWLINE);
    } 
    indent(xml, level);
    xml.append("</dict>");
  }
  
  void assignIDs(BinaryPropertyListWriter out) {
    super.assignIDs(out);
    for (Map.Entry<String, NSObject> entry : this.dict.entrySet()) {
      (new NSString(entry.getKey())).assignIDs(out);
      ((NSObject)entry.getValue()).assignIDs(out);
    } 
  }
  
  public void toBinary(BinaryPropertyListWriter out) throws IOException {
    out.writeIntHeader(13, this.dict.size());
    Set<Map.Entry<String, NSObject>> entries = this.dict.entrySet();
    for (Map.Entry<String, NSObject> entry : entries)
      out.writeID(out.getID(new NSString(entry.getKey()))); 
    for (Map.Entry<String, NSObject> entry : entries)
      out.writeID(out.getID(entry.getValue())); 
  }
}


/* Location:              D:\360Downloads\ycl-plist.jar!\com\ycl\plist\NSDictionary.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */
