package com.ycl.plist;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class NSSet extends NSObject {
  private Set<NSObject> set;
  
  public NSSet() {
    this.set = new LinkedHashSet<NSObject>();
  }
  
  public NSSet(NSObject... objects) {
    this.set = new LinkedHashSet<NSObject>();
    this.set.addAll(Arrays.asList(objects));
  }
  
  public void addObject(NSObject obj) {
    this.set.add(obj);
  }
  
  public void removeObject(NSObject obj) {
    this.set.remove(obj);
  }
  
  public NSObject[] allObjects() {
    return this.set.<NSObject>toArray(new NSObject[count()]);
  }
  
  public NSObject anyObject() {
    if (this.set.isEmpty())
      return null; 
    return this.set.iterator().next();
  }
  
  public boolean containsObject(NSObject obj) {
    return this.set.contains(obj);
  }
  
  public NSObject member(NSObject obj) {
    for (NSObject o : this.set) {
      if (o.equals(obj))
        return o; 
    } 
    return null;
  }
  
  public boolean intersectsSet(NSSet otherSet) {
    for (NSObject o : this.set) {
      if (otherSet.containsObject(o))
        return true; 
    } 
    return false;
  }
  
  public boolean isSubsetOfSet(NSSet otherSet) {
    for (NSObject o : this.set) {
      if (!otherSet.containsObject(o))
        return false; 
    } 
    return true;
  }
  
  public Iterator<NSObject> objectIterator() {
    return this.set.iterator();
  }
  
  public int hashCode() {
    int hash = 7;
    hash = 29 * hash + ((this.set != null) ? this.set.hashCode() : 0);
    return hash;
  }
  
  public boolean equals(Object obj) {
    if (obj == null)
      return false; 
    if (getClass() != obj.getClass())
      return false; 
    NSSet other = (NSSet)obj;
    if (this.set != other.set && (this.set == null || !this.set.equals(other.set)))
      return false; 
    return true;
  }
  
  public int count() {
    return this.set.size();
  }
  
  public void toXML(StringBuilder xml, int level) {
    indent(xml, level);
    xml.append("<array>");
    xml.append(NSObject.NEWLINE);
    for (NSObject o : this.set) {
      o.toXML(xml, level + 1);
      xml.append(NSObject.NEWLINE);
    } 
    indent(xml, level);
    xml.append("</array>");
  }
  
  void assignIDs(BinaryPropertyListWriter out) {
    super.assignIDs(out);
    for (NSObject obj : this.set)
      obj.assignIDs(out); 
  }
  
  void toBinary(BinaryPropertyListWriter out) throws IOException {
    out.writeIntHeader(12, this.set.size());
    for (NSObject obj : this.set)
      out.writeID(out.getID(obj)); 
  }
}


/* Location:              D:\360Downloads\ycl-plist.jar!\com\ycl\plist\NSSet.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */
