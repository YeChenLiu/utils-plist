package com.ycl.plist;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;

public class BinaryPropertyListParser {
  private byte[] bytes;
  
  private int offsetSize;
  
  private int objectRefSize;
  
  private int numObjects;
  
  private int topObject;
  
  private int offsetTableOffset;
  
  private int[] offsetTable;
  
  public static NSObject parse(byte[] data) throws Exception {
    BinaryPropertyListParser parser = new BinaryPropertyListParser();
    return parser.doParse(data);
  }
  
  public NSObject doParse(byte[] data) throws Exception {
    this.bytes = data;
    String magic = new String(copyOfRange(this.bytes, 0, 8));
    if (!magic.equals("bplist00"))
      throw new Exception("The given data is no binary property list. Wrong magic bytes: " + magic); 
    byte[] trailer = copyOfRange(this.bytes, this.bytes.length - 32, this.bytes.length);
    this.offsetSize = (int)parseUnsignedInt(copyOfRange(trailer, 6, 7));
    this.objectRefSize = (int)parseUnsignedInt(copyOfRange(trailer, 7, 8));
    this.numObjects = (int)parseUnsignedInt(copyOfRange(trailer, 8, 16));
    this.topObject = (int)parseUnsignedInt(copyOfRange(trailer, 16, 24));
    this.offsetTableOffset = (int)parseUnsignedInt(copyOfRange(trailer, 24, 32));
    this.offsetTable = new int[this.numObjects];
    for (int i = 0; i < this.numObjects; i++) {
      byte[] offsetBytes = copyOfRange(this.bytes, this.offsetTableOffset + i * this.offsetSize, this.offsetTableOffset + (i + 1) * this.offsetSize);
      this.offsetTable[i] = (int)parseUnsignedInt(offsetBytes);
    } 
    return parseObject(this.topObject);
  }
  
  public static NSObject parse(InputStream is) throws Exception {
    byte[] buf = PropertyListParser.readAll(is, 2147483647);
    is.close();
    return parse(buf);
  }
  
  public static NSObject parse(File f) throws Exception {
    if (f.length() > Runtime.getRuntime().freeMemory())
      throw new Exception("To little heap space available! Wanted to read " + f.length() + " bytes, but only " + Runtime.getRuntime().freeMemory() + " are available."); 
    return parse(new FileInputStream(f));
  }
  
  private NSObject parseObject(int obj) throws Exception {
    int j, dataoffset, length, k, stroffset, arrayoffset, dictoffset;
    NSArray array;
    NSSet set;
    NSDictionary dict;
    int i, offset = this.offsetTable[obj];
    byte type = this.bytes[offset];
    int objType = (type & 0xF0) >> 4;
    int objInfo = type & 0xF;
    switch (objType) {
      case 0:
        switch (objInfo) {
          case 0:
            return null;
          case 8:
            return new NSNumber(false);
          case 9:
            return new NSNumber(true);
          case 15:
            return null;
        } 
        return null;
      case 1:
        j = (int)Math.pow(2.0D, objInfo);
        if (j < Runtime.getRuntime().freeMemory())
          return new NSNumber(copyOfRange(this.bytes, offset + 1, offset + 1 + j), 0); 
        throw new Exception("To little heap space available! Wanted to read " + j + " bytes, but only " + Runtime.getRuntime().freeMemory() + " are available.");
      case 2:
        j = (int)Math.pow(2.0D, objInfo);
        if (j < Runtime.getRuntime().freeMemory())
          return new NSNumber(copyOfRange(this.bytes, offset + 1, offset + 1 + j), 1); 
        throw new Exception("To little heap space available! Wanted to read " + j + " bytes, but only " + Runtime.getRuntime().freeMemory() + " are available.");
      case 3:
        if (objInfo != 3)
          System.err.println("Unknown date type :" + objInfo + ". Parsing anyway..."); 
        return new NSDate(copyOfRange(this.bytes, offset + 1, offset + 9));
      case 4:
        dataoffset = 1;
        k = objInfo;
        if (objInfo == 15) {
          int int_type = this.bytes[offset + 1];
          int intType = (int_type & 0xF0) / 15;
          if (intType != 1)
            System.err.println("UNEXPECTED LENGTH-INT TYPE! " + intType); 
          int intInfo = int_type & 0xF;
          int intLength = (int)Math.pow(2.0D, intInfo);
          dataoffset = 2 + intLength;
          if (intLength < 3) {
            k = (int)parseUnsignedInt(copyOfRange(this.bytes, offset + 2, offset + 2 + intLength));
          } else {
            k = (new BigInteger(copyOfRange(this.bytes, offset + 2, offset + 2 + intLength))).intValue();
          } 
        } 
        if (k < Runtime.getRuntime().freeMemory())
          return new NSData(copyOfRange(this.bytes, offset + dataoffset, offset + dataoffset + k)); 
        throw new Exception("To little heap space available! Wanted to read " + k + " bytes, but only " + Runtime.getRuntime().freeMemory() + " are available.");
      case 5:
        length = objInfo;
        stroffset = 1;
        if (objInfo == 15) {
          int int_type = this.bytes[offset + 1];
          int intType = (int_type & 0xF0) / 15;
          if (intType != 1)
            System.err.println("UNEXPECTED LENGTH-INT TYPE! " + intType); 
          int intInfo = int_type & 0xF;
          int intLength = (int)Math.pow(2.0D, intInfo);
          stroffset = 2 + intLength;
          if (intLength < 3) {
            length = (int)parseUnsignedInt(copyOfRange(this.bytes, offset + 2, offset + 2 + intLength));
          } else {
            length = (new BigInteger(copyOfRange(this.bytes, offset + 2, offset + 2 + intLength))).intValue();
          } 
        } 
        if (length < Runtime.getRuntime().freeMemory())
          return new NSString(copyOfRange(this.bytes, offset + stroffset, offset + stroffset + length), "ASCII"); 
        throw new Exception("To little heap space available! Wanted to read " + length + " bytes, but only " + Runtime.getRuntime().freeMemory() + " are available.");
      case 6:
        length = objInfo;
        stroffset = 1;
        if (objInfo == 15) {
          int int_type = this.bytes[offset + 1];
          int intType = (int_type & 0xF0) / 15;
          if (intType != 1)
            System.err.println("UNEXPECTED LENGTH-INT TYPE! " + intType); 
          int intInfo = int_type & 0xF;
          int intLength = (int)Math.pow(2.0D, intInfo);
          stroffset = 2 + intLength;
          if (intLength < 3) {
            length = (int)parseUnsignedInt(copyOfRange(this.bytes, offset + 2, offset + 2 + intLength));
          } else {
            length = (new BigInteger(copyOfRange(this.bytes, offset + 2, offset + 2 + intLength))).intValue();
          } 
        } 
        length *= 2;
        if (length < Runtime.getRuntime().freeMemory())
          return new NSString(copyOfRange(this.bytes, offset + stroffset, offset + stroffset + length), "UTF-16BE"); 
        throw new Exception("To little heap space available! Wanted to read " + length + " bytes, but only " + Runtime.getRuntime().freeMemory() + " are available.");
      case 8:
        length = objInfo + 1;
        if (length < Runtime.getRuntime().freeMemory())
          return new UID(String.valueOf(obj), copyOfRange(this.bytes, offset + 1, offset + 1 + length)); 
        throw new Exception("To little heap space available! Wanted to read " + length + " bytes, but only " + Runtime.getRuntime().freeMemory() + " are available.");
      case 10:
        length = objInfo;
        arrayoffset = 1;
        if (objInfo == 15) {
          int int_type = this.bytes[offset + 1];
          int intType = (int_type & 0xF0) / 15;
          if (intType != 1)
            System.err.println("UNEXPECTED LENGTH-INT TYPE! " + intType); 
          int intInfo = int_type & 0xF;
          int intLength = (int)Math.pow(2.0D, intInfo);
          arrayoffset = 2 + intLength;
          if (intLength < 3) {
            length = (int)parseUnsignedInt(copyOfRange(this.bytes, offset + 2, offset + 2 + intLength));
          } else {
            length = (new BigInteger(copyOfRange(this.bytes, offset + 2, offset + 2 + intLength))).intValue();
          } 
        } 
        if ((length * this.objectRefSize) > Runtime.getRuntime().freeMemory())
          throw new Exception("To little heap space available!"); 
        array = new NSArray(length);
        for (i = 0; i < length; i++) {
          int objRef = (int)parseUnsignedInt(copyOfRange(this.bytes, offset + arrayoffset + i * this.objectRefSize, offset + arrayoffset + (i + 1) * this.objectRefSize));
          array.setValue(i, parseObject(objRef));
        } 
        return array;
      case 12:
        length = objInfo;
        arrayoffset = 1;
        if (objInfo == 15) {
          int int_type = this.bytes[offset + 1];
          int intType = (int_type & 0xF0) / 15;
          if (intType != 1)
            System.err.println("UNEXPECTED LENGTH-INT TYPE! " + intType); 
          int intInfo = int_type & 0xF;
          int intLength = (int)Math.pow(2.0D, intInfo);
          arrayoffset = 2 + intLength;
          if (intLength < 3) {
            length = (int)parseUnsignedInt(copyOfRange(this.bytes, offset + 2, offset + 2 + intLength));
          } else {
            length = (new BigInteger(copyOfRange(this.bytes, offset + 2, offset + 2 + intLength))).intValue();
          } 
        } 
        if ((length * this.objectRefSize) > Runtime.getRuntime().freeMemory())
          throw new Exception("To little heap space available!"); 
        set = new NSSet();
        for (i = 0; i < length; i++) {
          int objRef = (int)parseUnsignedInt(copyOfRange(this.bytes, offset + arrayoffset + i * this.objectRefSize, offset + arrayoffset + (i + 1) * this.objectRefSize));
          set.addObject(parseObject(objRef));
        } 
        return set;
      case 13:
        length = objInfo;
        dictoffset = 1;
        if (objInfo == 15) {
          int int_type = this.bytes[offset + 1];
          int intType = (int_type & 0xF0) / 15;
          if (intType != 1)
            System.err.println("UNEXPECTED LENGTH-INT TYPE! " + intType); 
          int intInfo = int_type & 0xF;
          int intLength = (int)Math.pow(2.0D, intInfo);
          dictoffset = 2 + intLength;
          if (intLength < 3) {
            length = (int)parseUnsignedInt(copyOfRange(this.bytes, offset + 2, offset + 2 + intLength));
          } else {
            length = (new BigInteger(copyOfRange(this.bytes, offset + 2, offset + 2 + intLength))).intValue();
          } 
        } 
        if ((length * 2 * this.objectRefSize) > Runtime.getRuntime().freeMemory())
          throw new Exception("To little heap space available!"); 
        dict = new NSDictionary();
        for (i = 0; i < length; i++) {
          int keyRef = (int)parseUnsignedInt(copyOfRange(this.bytes, offset + dictoffset + i * this.objectRefSize, offset + dictoffset + (i + 1) * this.objectRefSize));
          int valRef = (int)parseUnsignedInt(copyOfRange(this.bytes, offset + dictoffset + length * this.objectRefSize + i * this.objectRefSize, offset + dictoffset + length * this.objectRefSize + (i + 1) * this.objectRefSize));
          NSObject key = parseObject(keyRef);
          NSObject val = parseObject(valRef);
          dict.put(key.toString(), val);
        } 
        return dict;
    } 
    System.err.println("Unknown object type: " + objType);
    return null;
  }
  
  public static final long parseUnsignedInt(byte[] bytes) {
    long l = 0L;
    for (byte b : bytes) {
      l <<= 8L;
      l |= (b & 0xFF);
    } 
    l &= 0xFFFFFFFFL;
    return l;
  }
  
  public static final long parseLong(byte[] bytes) {
    long l = 0L;
    for (byte b : bytes) {
      l <<= 8L;
      l |= (b & 0xFF);
    } 
    return l;
  }
  
  public static final double parseDouble(byte[] bytes) {
    if (bytes.length == 8)
      return Double.longBitsToDouble(parseLong(bytes)); 
    if (bytes.length == 4)
      return Float.intBitsToFloat((int)parseLong(bytes)); 
    throw new IllegalArgumentException("bad byte array length " + bytes.length);
  }
  
  public static byte[] copyOfRange(byte[] src, int startIndex, int endIndex) {
    int length = endIndex - startIndex;
    if (length < 0)
      throw new IllegalArgumentException("startIndex (" + startIndex + ")" + " > endIndex (" + endIndex + ")"); 
    byte[] dest = new byte[length];
    System.arraycopy(src, startIndex, dest, 0, length);
    return dest;
  }
}


/* Location:              D:\360Downloads\ycl-plist.jar!\com\ycl\plist\BinaryPropertyListParser.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */
