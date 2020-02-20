package com.ycl.plist;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class BinaryPropertyListWriter {
  OutputStream out;
  
  long count;
  
  public static void write(File file, NSObject root) throws IOException {
    OutputStream out = new FileOutputStream(file);
    write(out, root);
    out.close();
  }
  
  public static void write(OutputStream out, NSObject root) throws IOException {
    BinaryPropertyListWriter w = new BinaryPropertyListWriter(out);
    w.write(root);
  }
  
  public static byte[] writeToArray(NSObject root) throws IOException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    write(bout, root);
    return bout.toByteArray();
  }
  
  Map<NSObject, Integer> idMap = new HashMap<NSObject, Integer>();
  
  int idSizeInBytes;
  
  BinaryPropertyListWriter(OutputStream outStr) throws IOException {
    this.out = new BufferedOutputStream(outStr);
  }
  
  void write(NSObject root) throws IOException {
    write("bplist00".getBytes());
    root.assignIDs(this);
    this.idSizeInBytes = computeIdSizeInBytes(this.idMap.size());
    long[] offsets = new long[this.idMap.size()];
    for (Map.Entry<NSObject, Integer> entry : this.idMap.entrySet()) {
      NSObject obj = entry.getKey();
      int id = ((Integer)entry.getValue()).intValue();
      offsets[id] = this.count;
      if (obj == null) {
        write(0);
        continue;
      } 
      obj.toBinary(this);
    } 
    long offsetTableOffset = this.count;
    int offsetSizeInBytes = computeOffsetSizeInBytes(this.count);
    for (long offset : offsets)
      writeBytes(offset, offsetSizeInBytes); 
    write(new byte[6]);
    write(offsetSizeInBytes);
    write(this.idSizeInBytes);
    writeLong(this.idMap.size());
    writeLong(((Integer)this.idMap.get(root)).intValue());
    writeLong(offsetTableOffset);
    this.out.flush();
  }
  
  void assignID(NSObject obj) {
    if (!this.idMap.containsKey(obj))
      this.idMap.put(obj, Integer.valueOf(this.idMap.size())); 
  }
  
  int getID(NSObject obj) {
    return ((Integer)this.idMap.get(obj)).intValue();
  }
  
  private static int computeIdSizeInBytes(int numberOfIds) {
    if (numberOfIds < 256)
      return 1; 
    if (numberOfIds < 65536)
      return 2; 
    return 4;
  }
  
  private int computeOffsetSizeInBytes(long maxOffset) {
    if (maxOffset < 256L)
      return 1; 
    if (maxOffset < 65536L)
      return 2; 
    if (maxOffset < 4294967296L)
      return 4; 
    return 8;
  }
  
  void writeIntHeader(int kind, int value) throws IOException {
    assert value >= 0;
    if (value < 15) {
      write((kind << 4) + value);
    } else if (value < 256) {
      write((kind << 4) + 15);
      write(16);
      writeBytes(value, 1);
    } else if (value < 65536) {
      write((kind << 4) + 15);
      write(17);
      writeBytes(value, 2);
    } else {
      write((kind << 4) + 15);
      write(18);
      writeBytes(value, 4);
    } 
  }
  
  void write(int b) throws IOException {
    this.out.write(b);
    this.count++;
  }
  
  void write(byte[] bytes) throws IOException {
    this.out.write(bytes);
    this.count += bytes.length;
  }
  
  void writeBytes(long value, int bytes) throws IOException {
    for (int i = bytes - 1; i >= 0; i--)
      write((int)(value >> 8 * i)); 
  }
  
  void writeID(int id) throws IOException {
    writeBytes(id, this.idSizeInBytes);
  }
  
  void writeLong(long value) throws IOException {
    writeBytes(value, 8);
  }
  
  void writeDouble(double value) throws IOException {
    writeLong(Double.doubleToRawLongBits(value));
  }
}


/* Location:              D:\360Downloads\ycl-plist.jar!\com\ycl\plist\BinaryPropertyListWriter.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */
