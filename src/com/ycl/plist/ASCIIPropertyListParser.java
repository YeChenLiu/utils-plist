package com.ycl.plist;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.text.ParseException;
import java.text.StringCharacterIterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ASCIIPropertyListParser {
  private static final Pattern scannerDelimiterPattern = Pattern.compile("(\\s*\\=\\s*)|(\\s*\\;\\s+)|(\\s*\\,\\s+)|(\\s+)");

  private static final Pattern arrayBeginToken = Pattern.compile("\\(");
  
  private static final Pattern arrayEndToken = Pattern.compile("\\)");
  
  private static final Pattern dictionaryBeginToken = Pattern.compile("\\{");
  
  private static final Pattern dictionaryEndToken = Pattern.compile("\\}");
  
  private static final Pattern simpleStringPattern = Pattern.compile("[\\x00-\\x7F&&[^\" ,;\\(\\)\\{\\}\\<\\>]]+");
  
  private static final Pattern quotedStringPattern = Pattern.compile("\"[\\x00-\\x7F]+\"");
  
  private static final Pattern dataBeginToken = Pattern.compile("<[0-9A-Fa-f ]*");
  
  private static final Pattern dataContentPattern = Pattern.compile("[0-9A-Fa-f ]+");
  
  private static final Pattern dataEndToken = Pattern.compile("[0-9A-Fa-f ]*>");
  
  private static final Pattern realPattern = Pattern.compile("[0-9]+.[0-9]+");
  
  private static final Pattern gnuStepDateBeginPattern = Pattern.compile("<\\*D[0-9]{4}-[0-1][0-9]-[0-3][0-9]");
  
  private static final Pattern appleDatePattern = Pattern.compile("\"[0-9]{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-5][0-9]:[0-5][0-9]Z\"");
  
  private static final Pattern appleBooleanPattern = Pattern.compile("(YES)|(NO)");
  
  private static final Pattern gnuStepBooleanPattern = Pattern.compile("(\\<\\*BY\\>)|(\\<\\*BN\\>)");
  
  private static final Pattern gnuStepIntPattern = Pattern.compile("\\<\\*I[0-9]+\\>");
  
  private static final Pattern gnuStepRealPattern = Pattern.compile("\\<\\*R[0-9]+(.[0-9]+)?\\>");
  
  private static CharsetEncoder asciiEncoder;
  
  public static NSObject parse(File f) throws Exception {
    return parse(new Scanner(f));
  }
  
  public static NSObject parse(InputStream in) throws Exception {
    return parse(new Scanner(in));
  }
  
  public static NSObject parse(byte[] bytes) throws Exception {
    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
    return parse(bis);
  }
  
  private static NSObject parse(Scanner s) throws Exception {
    s.useDelimiter(scannerDelimiterPattern);
    if (s.hasNext(arrayBeginToken) || s.hasNext(dictionaryBeginToken))
      return parseObject(s); 
    throw new ParseException("Expected '" + arrayBeginToken + "' or '" + dictionaryBeginToken + "' but found " + s.next(), 0);
  }
  
  private static NSObject parseObject(Scanner s) throws Exception {
    if (s.hasNext(arrayBeginToken)) {
      s.next();
      List<NSObject> arrayObjects = new LinkedList<NSObject>();
      int len = 0;
      while (!s.hasNext(arrayEndToken)) {
        NSObject o = parseObject(s);
        arrayObjects.add(o);
        len++;
      } 
      if (!s.hasNext(arrayEndToken))
        throw new ParseException("Expected '" + arrayEndToken + "' but found " + s.next(), 0); 
      s.next();
      NSArray array = new NSArray(arrayObjects.<NSObject>toArray(new NSObject[len]));
      return array;
    } 
    if (s.hasNext(dictionaryBeginToken)) {
      s.next();
      NSDictionary dict = new NSDictionary();
      while (!s.hasNext(dictionaryEndToken)) {
        String key = "";
        if (s.hasNext(simpleStringPattern)) {
          key = s.next(simpleStringPattern);
        } else if (s.hasNext(quotedStringPattern)) {
          key = parseQuotedString(s.next(quotedStringPattern));
        } else {
          throw new ParseException("Expected String but found " + s.next(), 0);
        } 
        NSObject value = parseObject(s);
        dict.put(key, value);
      } 
      if (!s.hasNext(dictionaryEndToken))
        throw new ParseException("Expected '" + dictionaryEndToken + "' but found " + s.next(), 0); 
      s.next();
      return dict;
    } 
    if (s.hasNext(gnuStepDateBeginPattern)) {
      String dateString = s.next();
      dateString = dateString + " " + s.next();
      dateString = dateString + " " + s.next();
      return new NSDate(dateString.substring(3, dateString.length() - 1));
    } 
    if (s.hasNext(appleDatePattern))
      return new NSDate(s.next().replaceAll("\"", "")); 
    if (s.hasNextInt())
      return new NSNumber(s.nextInt()); 
    if (s.hasNext(realPattern))
      return new NSNumber(Double.parseDouble(s.next())); 
    if (s.hasNext(appleBooleanPattern))
      return new NSNumber(s.next().equals("YES")); 
    if (s.hasNext(gnuStepBooleanPattern))
      return new NSNumber(s.next().equals("<*BY>")); 
    if (s.hasNext(gnuStepIntPattern)) {
      String token = s.next();
      return new NSNumber(Integer.parseInt(token.substring(3, token.length() - 1)));
    } 
    if (s.hasNext(gnuStepRealPattern)) {
      String token = s.next();
      return new NSNumber(Double.parseDouble(token.substring(3, token.length() - 1)));
    } 
    if (s.hasNext(dataBeginToken)) {
      String data = s.next().replaceFirst("<", "");
      while (!s.hasNext(dataEndToken))
        data = data + s.next(dataContentPattern); 
      data = data + s.next().replaceAll(">", "");
      int numBytes = data.length() / 2;
      byte[] bytes = new byte[numBytes];
      for (int i = 0; i < bytes.length; i++) {
        String byteString = data.substring(i * 2, i * 2 + 2);
        int byteValue = Integer.parseInt(byteString, 16);
        bytes[i] = (byte)byteValue;
      } 
      return new NSData(bytes);
    } 
    if (s.hasNext(quotedStringPattern)) {
      String str = parseQuotedString(s.next());
      return new NSString(str);
    } 
    if (s.hasNext(simpleStringPattern)) {
      String str = s.next();
      return new NSString(str);
    } 
    throw new ParseException("Expected a NSObject but found " + s.next(), 0);
  }
  
  public static synchronized String parseQuotedString(String s) throws Exception {
    s = s.substring(1, s.length() - 1);
    List<Byte> strBytes = new LinkedList<Byte>();
    StringCharacterIterator iterator = new StringCharacterIterator(s);
    char c = iterator.current();
    while (iterator.getIndex() < iterator.getEndIndex()) {
      byte[] bts;
      switch (c) {
        case '\\':
          bts = parseEscapedSequence(iterator).getBytes("UTF-8");
          for (byte b : bts)
            strBytes.add(Byte.valueOf(b)); 
          break;
        default:
          strBytes.add(Byte.valueOf((byte)0));
          strBytes.add(Byte.valueOf((byte)c));
          break;
      } 
      c = iterator.next();
    } 
    byte[] bytArr = new byte[strBytes.size()];
    for (int i = 0; i < bytArr.length; i++)
      bytArr[i] = ((Byte)strBytes.get(i)).byteValue(); 
    String result = new String(bytArr, "UTF-8");
    CharBuffer charBuf = CharBuffer.wrap(result);
    if (asciiEncoder == null)
      asciiEncoder = Charset.forName("ASCII").newEncoder(); 
    if (asciiEncoder.canEncode(charBuf))
      return asciiEncoder.encode(charBuf).asCharBuffer().toString(); 
    return result;
  }
  
  private static String parseEscapedSequence(StringCharacterIterator iterator) throws UnsupportedEncodingException {
    char c = iterator.next();
    if (c == '\\')
      return new String(new byte[] { 0, 92 }, "UTF-8"); 
    if (c == '"')
      return new String(new byte[] { 0, 34 }, "UTF-8"); 
    if (c == 'b')
      return new String(new byte[] { 0, 8 }, "UTF-8"); 
    if (c == 'n')
      return new String(new byte[] { 0, 10 }, "UTF-8"); 
    if (c == 'r')
      return new String(new byte[] { 0, 13 }, "UTF-8"); 
    if (c == 't')
      return new String(new byte[] { 0, 9 }, "UTF-8"); 
    if (c == 'U' || c == 'u') {
      String byte1 = "";
      byte1 = byte1 + iterator.next();
      byte1 = byte1 + iterator.next();
      String byte2 = "";
      byte2 = byte2 + iterator.next();
      byte2 = byte2 + iterator.next();
      byte[] arrayOfByte = { (byte)Integer.parseInt(byte1, 16), (byte)Integer.parseInt(byte2, 16) };
      return new String(arrayOfByte, "UTF-8");
    } 
    String num = "";
    num = num + c;
    num = num + iterator.next();
    num = num + iterator.next();
    int asciiCode = Integer.parseInt(num, 8);
    byte[] stringBytes = { 0, (byte)asciiCode };
    return new String(stringBytes, "UTF-8");
  }
}


/* Location:              D:\360Downloads\ycl-plist.jar!\com\ycl\plist\ASCIIPropertyListParser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */
