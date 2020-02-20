package com.ycl.plist;

import java.io.IOException;

public class NSNumber extends NSObject {
  public static final int INTEGER = 0;
  
  public static final int REAL = 1;
  
  public static final int BOOLEAN = 2;
  
  private int type;
  
  private long longValue;
  
  private double doubleValue;
  
  private boolean boolValue;
  
  public NSNumber(byte[] bytes, int type) {
    switch (type) {
      case 0:
        this.doubleValue = (this.longValue = BinaryPropertyListParser.parseLong(bytes));
      case 1:
        this.doubleValue = BinaryPropertyListParser.parseDouble(bytes);
        this.longValue = (long)this.doubleValue;
        break;
      default:
        throw new IllegalArgumentException("Type argument is not valid.");
    } 
    this.type = type;
  }
  
  public NSNumber(String text) {
    try {
      long l = Long.parseLong(text);
      this.doubleValue = (this.longValue = l);
      this.type = 0;
    } catch (Exception ex) {
      try {
        double d = Double.parseDouble(text);
        this.longValue = (long)(this.doubleValue = d);
        this.type = 1;
      } catch (Exception ex2) {
        try {
          this.boolValue = Boolean.parseBoolean(text);
          this.type = 2;
          this.doubleValue = (this.longValue = this.boolValue ? 1L : 0L);
        } catch (Exception ex3) {
          throw new IllegalArgumentException("Given text neither represents a double, int nor boolean value.");
        } 
      } 
    } 
  }
  
  public NSNumber(int i) {
    this.doubleValue = (this.longValue = i);
    this.type = 0;
  }
  
  public NSNumber(double d) {
    this.longValue = (long)(this.doubleValue = d);
    this.type = 1;
  }
  
  public NSNumber(boolean b) {
    this.boolValue = b;
    this.doubleValue = (this.longValue = b ? 1L : 0L);
    this.type = 2;
  }
  
  public int type() {
    return this.type;
  }
  
  public boolean boolValue() {
    if (this.type == 2)
      return this.boolValue; 
    return (this.longValue != 0L);
  }
  
  public long longValue() {
    return this.longValue;
  }
  
  public int intValue() {
    return (int)this.longValue;
  }
  
  public double doubleValue() {
    return this.doubleValue;
  }
  
  public float floatValue() {
    return (float)this.doubleValue;
  }
  
  public boolean equals(Object obj) {
    if (!(obj instanceof NSNumber))
      return false; 
    NSNumber n = (NSNumber)obj;
    return (this.type == n.type && this.longValue == n.longValue && this.doubleValue == n.doubleValue && this.boolValue == n.boolValue);
  }
  
  public int hashCode() {
    int hash = this.type;
    hash = 37 * hash + (int)(this.longValue ^ this.longValue >>> 32L);
    hash = 37 * hash + (int)(Double.doubleToLongBits(this.doubleValue) ^ Double.doubleToLongBits(this.doubleValue) >>> 32L);
    hash = 37 * hash + (boolValue() ? 1 : 0);
    return hash;
  }
  
  public String toString() {
    switch (this.type) {
      case 0:
        return String.valueOf(longValue());
      case 1:
        return String.valueOf(doubleValue());
      case 2:
        return String.valueOf(boolValue());
    } 
    return super.toString();
  }
  
  public void toXML(StringBuilder xml, int level) {
    indent(xml, level);
    switch (this.type) {
      case 0:
        xml.append("<integer>");
        xml.append(longValue());
        xml.append("</integer>");
        break;
      case 1:
        xml.append("<real>");
        xml.append(doubleValue());
        xml.append("</real>");
        break;
      case 2:
        if (boolValue()) {
          xml.append("<true/>");
          break;
        } 
        xml.append("<false/>");
        break;
    } 
  }
  
  void toBinary(BinaryPropertyListWriter out) throws IOException {
    switch (type()) {
      case 0:
        if (longValue() < 0L) {
          out.write(19);
          out.writeBytes(longValue(), 8);
          break;
        } 
        if (longValue() <= 255L) {
          out.write(16);
          out.writeBytes(longValue(), 1);
          break;
        } 
        if (longValue() <= 65535L) {
          out.write(17);
          out.writeBytes(longValue(), 2);
          break;
        } 
        if (longValue() <= 4294967295L) {
          out.write(18);
          out.writeBytes(longValue(), 4);
          break;
        } 
        out.write(19);
        out.writeBytes(longValue(), 8);
        break;
      case 1:
        out.write(35);
        out.writeDouble(doubleValue());
        break;
      case 2:
        out.write(boolValue() ? 9 : 8);
        break;
    } 
  }
}


/* Location:              D:\360Downloads\ycl-plist.jar!\com\ycl\plist\NSNumber.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */
