package com.ycl.plist;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class NSData extends NSObject {
  private byte[] bytes;
  
  public NSData(byte[] bytes) {
    this.bytes = bytes;
  }
  
  public NSData(String base64) {
    String data = base64.replaceAll("\\s+", "");
    this.bytes = Base64.decode(data);
  }
  
  public NSData(File file) throws FileNotFoundException, IOException {
    this.bytes = new byte[(int)file.length()];
    RandomAccessFile raf = new RandomAccessFile(file, "r");
    raf.read(this.bytes);
    raf.close();
  }
  
  public byte[] bytes() {
    return this.bytes;
  }
  
  public int length() {
    return this.bytes.length;
  }
  
  public void getBytes(ByteBuffer buf, int length) {
    buf.put(this.bytes, 0, Math.min(this.bytes.length, length));
  }
  
  public void getBytes(ByteBuffer buf, int rangeStart, int rangeStop) {
    buf.put(this.bytes, rangeStart, Math.min(this.bytes.length, rangeStop));
  }
  
  public String getBase64EncodedData() {
    return Base64.encodeBytes(this.bytes);
  }
  
  public boolean equals(Object obj) {
    return (obj.getClass().equals(getClass()) && Arrays.equals(((NSData)obj).bytes, this.bytes));
  }
  
  public int hashCode() {
    int hash = 5;
    hash = 67 * hash + Arrays.hashCode(this.bytes);
    return hash;
  }
  
  public void toXML(StringBuilder xml, int level) {
    indent(xml, level);
    xml.append("<data>");
    xml.append(NSObject.NEWLINE);
    String base64 = getBase64EncodedData();
    for (String line : base64.split("\n")) {
      indent(xml, level + 1);
      xml.append(line);
      xml.append(NSObject.NEWLINE);
    } 
    indent(xml, level);
    xml.append("</data>");
  }
  
  void toBinary(BinaryPropertyListWriter out) throws IOException {
    out.writeIntHeader(4, this.bytes.length);
    out.write(this.bytes);
  }
}


/* Location:              D:\360Downloads\ycl-plist.jar!\com\ycl\plist\NSData.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */
