package com.ycl.plist;

import java.io.IOException;

public class UID extends NSObject {
    private byte[] bytes;

    private String name;

    public UID(String name, byte[] bytes) {
        this.name = name;
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public String getName() {
        return this.name;
    }

    public void toXML(StringBuilder xml, int level) {
        indent(xml, level);
        xml.append("<string>");
        xml.append(new String(this.bytes));
        xml.append("</string>");
    }

    void toBinary(BinaryPropertyListWriter out) throws IOException {
        out.write(128 + this.bytes.length - 1);
        out.write(this.bytes);
    }
}


/* Location:              D:\360Downloads\ycl-plist.jar!\com\ycl\plist\UID.class
 * Java compiler version: 5 (49.0)
 * JD-Core Version:       1.1.3
 */
