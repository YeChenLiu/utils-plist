package com.ycl.plist;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class NSDate extends NSObject {
  private Date date;
  
  private static final long EPOCH = 978307200000L;
  
  private static final SimpleDateFormat sdfDefault = new SimpleDateFormat("yyyy-MM-ycl'T'HH:mm:ss'Z'");
  
  private static final SimpleDateFormat sdfGnuStep = new SimpleDateFormat("yyyy-MM-ycl HH:mm:ss Z");
  
  static {
    sdfDefault.setTimeZone(TimeZone.getTimeZone("GMT"));
    sdfGnuStep.setTimeZone(TimeZone.getTimeZone("GMT"));
  }
  
  private static synchronized Date parseDateString(String textRepresentation) throws ParseException {
    try {
      return sdfDefault.parse(textRepresentation);
    } catch (ParseException ex) {
      return sdfGnuStep.parse(textRepresentation);
    } 
  }
  
  private static synchronized String makeDateString(Date date) {
    return sdfDefault.format(date);
  }
  
  public NSDate(byte[] bytes) {
    this.date = new Date(978307200000L + (long)(1000.0D * BinaryPropertyListParser.parseDouble(bytes)));
  }
  
  public NSDate(String textRepresentation) throws ParseException {
    this.date = parseDateString(textRepresentation);
  }
  
  public NSDate(Date d) {
    if (d == null)
      throw new IllegalArgumentException("Date cannot be null"); 
    this.date = d;
  }
  
  public Date getDate() {
    return this.date;
  }
  
  public boolean equals(Object obj) {
    return (obj.getClass().equals(getClass()) && this.date.equals(((NSDate)obj).getDate()));
  }
  
  public int hashCode() {
    return this.date.hashCode();
  }
  
  public void toXML(StringBuilder xml, int level) {
    indent(xml, level);
    xml.append("<date>");
    xml.append(makeDateString(this.date));
    xml.append("</date>");
  }
  
  public void toBinary(BinaryPropertyListWriter out) throws IOException {
    out.write(51);
    out.writeDouble((this.date.getTime() - 978307200000L) / 1000.0D);
  }
  
  public String toString() {
    return this.date.toString();
  }
}


/* Location:              D:\360Downloads\ycl-plist.jar!\com\ycl\plist\NSDate.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */
