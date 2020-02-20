package com.ycl.plist;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class NSString extends NSObject {
  private String content;
  
  private static CharsetEncoder asciiEncoder;
  
  private static CharsetEncoder utf16beEncoder;
  
  public NSString(byte[] bytes, String encoding) throws UnsupportedEncodingException {
    this.content = new String(bytes, encoding);
  }
  
  public NSString(String string) {
    try {
      this.content = new String(string.getBytes("UTF-8"), "UTF-8");
    } catch (UnsupportedEncodingException ex) {
      ex.printStackTrace();
    } 
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof NSString))
      return false; 
    return this.content.equals(((NSString)obj).content);
  }
  
  public int hashCode() {
    return this.content.hashCode();
  }
  
  public String toString() {
    return this.content;
  }
  
  public void toXML(StringBuilder xml, int level) {
    indent(xml, level);
    xml.append("<string>");
    if (this.content.contains("&") || this.content.contains("<") || this.content.contains(">")) {
      xml.append("<![CDATA[");
      xml.append(this.content.replaceAll("]]>", "]]]]><![CDATA[>"));
      xml.append("]]>");
    } else {
      xml.append(this.content);
    } 
    xml.append("</string>");
  }
  
  public void toBinary(BinaryPropertyListWriter out) throws IOException {
    int kind;
    ByteBuffer byteBuf;
    CharBuffer charBuf = CharBuffer.wrap(this.content);
    synchronized (NSString.class) {
      if (asciiEncoder == null)
        asciiEncoder = Charset.forName("ASCII").newEncoder(); 
      if (asciiEncoder.canEncode(charBuf)) {
        kind = 5;
        byteBuf = asciiEncoder.encode(charBuf);
      } else {
        if (utf16beEncoder == null)
          utf16beEncoder = Charset.forName("UTF-16BE").newEncoder(); 
        kind = 6;
        byteBuf = utf16beEncoder.encode(charBuf);
      } 
    } 
    byte[] bytes = new byte[byteBuf.remaining()];
    byteBuf.get(bytes);
    out.writeIntHeader(kind, this.content.length());
    out.write(bytes);
  }
}


/* Location:              D:\360Downloads\ycl-plist.jar!\com\ycl\plist\NSString.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */
