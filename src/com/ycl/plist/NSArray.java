package com.ycl.plist;

import java.io.IOException;
import java.util.Arrays;

public class NSArray extends NSObject {
  private NSObject[] array;
  
  public NSArray(int length) {
    this.array = new NSObject[length];
  }
  
  public NSArray(NSObject... a) {
    this.array = a;
  }
  
  public NSObject objectAtIndex(int i) {
    return this.array[i];
  }
  
  public void setValue(int key, NSObject value) {
    this.array[key] = value;
  }
  
  public NSObject[] getArray() {
    return this.array;
  }
  
  public int count() {
    return this.array.length;
  }
  
  public boolean containsObject(NSObject obj) {
    for (NSObject o : this.array) {
      if (o.equals(obj))
        return true; 
    } 
    return false;
  }
  
  public int indexOfObject(NSObject obj) {
    for (int i = 0; i < this.array.length; i++) {
      if (this.array[i].equals(obj))
        return i; 
    } 
    return -1;
  }
  
  public int indexOfIdenticalObject(NSObject obj) {
    for (int i = 0; i < this.array.length; i++) {
      if (this.array[i] == obj)
        return i; 
    } 
    return -1;
  }
  
  public NSObject lastObject() {
    return this.array[this.array.length - 1];
  }
  
  public NSObject[] objectsAtIndexes(int... indexes) {
    NSObject[] result = new NSObject[indexes.length];
    Arrays.sort(indexes);
    for (int i = 0; i < indexes.length; ) {
      result[i] = this.array[indexes[0]];
      i++;
    } 
    return result;
  }
  
  public boolean equals(Object obj) {
    return (obj.getClass().equals(getClass()) && Arrays.equals((Object[])((NSArray)obj).getArray(), (Object[])this.array));
  }
  
  public int hashCode() {
    int hash = 7;
    hash = 89 * hash + Arrays.deepHashCode((Object[])this.array);
    return hash;
  }
  
  public void toXML(StringBuilder xml, int level) {
    indent(xml, level);
    xml.append("<array>");
    xml.append(NSObject.NEWLINE);
    for (NSObject o : this.array) {
      o.toXML(xml, level + 1);
      xml.append(NSObject.NEWLINE);
    } 
    indent(xml, level);
    xml.append("</array>");
  }
  
  void assignIDs(BinaryPropertyListWriter out) {
    super.assignIDs(out);
    for (NSObject obj : this.array)
      obj.assignIDs(out); 
  }
  
  void toBinary(BinaryPropertyListWriter out) throws IOException {
    out.writeIntHeader(10, this.array.length);
    for (NSObject obj : this.array)
      out.writeID(out.getID(obj)); 
  }
}


/* Location:              D:\360Downloads\ycl-plist.jar!\com\ycl\plist\NSArray.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */
