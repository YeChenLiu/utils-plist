package com.ycl.plist;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

public class PropertyListParser {
  static byte[] readAll(InputStream in, int max) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    while (max > 0) {
      int n = in.read();
      if (n == -1)
        break; 
      buf.write(n);
      max--;
    } 
    return buf.toByteArray();
  }
  
  public static NSObject parse(File f) throws Exception {
    FileInputStream fis = new FileInputStream(f);
    String magicString = new String(readAll(fis, 8), 0, 8);
    fis.close();
    if (magicString.startsWith("bplist00"))
      return BinaryPropertyListParser.parse(f); 
    if (magicString.startsWith("<?xml"))
      return XMLPropertyListParser.parse(f); 
    if (magicString.startsWith("(") || magicString.startsWith("{"))
      return ASCIIPropertyListParser.parse(f); 
    throw new UnsupportedOperationException("The given data is neither a binary nor a XML property list. ASCII property lists are not supported.");
  }
  
  public static NSObject parse(byte[] bytes) throws Exception {
    String magicString = new String(bytes, 0, 8);
    if (magicString.startsWith("bplist00"))
      return BinaryPropertyListParser.parse(bytes); 
    if (magicString.startsWith("<?xml"))
      return XMLPropertyListParser.parse(bytes); 
    if (magicString.startsWith("(") || magicString.startsWith("{"))
      return ASCIIPropertyListParser.parse(bytes); 
    throw new UnsupportedOperationException("The given data is neither a binary nor a XML property list. ASCII property lists are not supported.");
  }
  
  public static NSObject parse(InputStream is) throws Exception {
    if (is.markSupported()) {
      is.mark(10);
      String magicString = new String(readAll(is, 8), 0, 8);
      is.reset();
      if (magicString.startsWith("bplist00"))
        return BinaryPropertyListParser.parse(is); 
      if (magicString.startsWith("<?xml"))
        return XMLPropertyListParser.parse(is); 
      if (magicString.startsWith("(") || magicString.startsWith("{"))
        return ASCIIPropertyListParser.parse(is); 
      throw new UnsupportedOperationException("The given data is neither a binary nor a XML property list. ASCII property lists are not supported.");
    } 
    return parse(readAll(is, 2147483647));
  }
  
  public static void saveAsXML(NSObject root, File out) throws IOException {
    OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(out), "UTF-8");
    w.write(root.toXMLPropertyList());
    w.close();
  }
  
  public static void convertToXml(File in, File out) throws Exception {
    NSObject root = parse(in);
    saveAsXML(root, out);
  }
  
  public static void saveAsBinary(NSObject root, File out) throws IOException {
    BinaryPropertyListWriter.write(out, root);
  }
  
  public static void convertToBinary(File in, File out) throws Exception {
    NSObject root = parse(in);
    saveAsBinary(root, out);
  }
}


/* Location:              D:\360Downloads\ycl-plist.jar!\com\ycl\plist\PropertyListParser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */
