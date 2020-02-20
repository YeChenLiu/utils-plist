package com.ycl.plist;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Test {
  public static void main(String[] paramArrayOfString) throws Exception {
    JFrame jFrame = new JFrame();
    jFrame.setDefaultCloseOperation(3);
    FileDialog fileDialog = new FileDialog(jFrame);
    fileDialog.setVisible(true);
    if (fileDialog.getFile() == null)
      System.exit(1); 
    String str = fileDialog.getDirectory() + fileDialog.getFile();
    File file1 = new File(str);
    File file2 = new File(str + ".xml.plist");
    long l1 = System.currentTimeMillis();
    PropertyListParser.convertToXml(file1, file2);
    long l2 = System.currentTimeMillis();
    System.out.println("Conversion took " + (l2 - l1) + " ms");
    System.exit(0);
  }
}


/* Location:              D:\360Downloads\dd-plist.jar!\Test.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */
