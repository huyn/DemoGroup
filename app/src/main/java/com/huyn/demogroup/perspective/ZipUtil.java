package com.huyn.demogroup.perspective;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by huyaonan on 2017/10/27.
 */

public class ZipUtil {

    public static String unzip(String filePath) {
        try {
            File file = new File(filePath);
            if(!file.exists())
                return null;
            String parentPath = file.getParent();
            ZipFile zipFile = new ZipFile(filePath);
            String filaName = zipFile.getName();
            String dir = parentPath + "/" + filaName;
            File unzippedFile = new File(dir);
            if(unzippedFile.exists()) {
                System.out.println("+++clear files");
                File[] childFiles = unzippedFile.listFiles();
                if(childFiles != null) {
                    for(int i=childFiles.length-1;i>=0; i--) {
                        childFiles[i].delete();
                    }
                }
            } else {
                System.out.println("+++create file");
                unzippedFile.mkdirs();
            }
            InputStream is;
            Enumeration enumeration = zipFile.entries();
            ZipEntry entry;
            FileOutputStream fos = null;
            int count;
            File dstFile;

            while (enumeration.hasMoreElements()) {
                entry = (ZipEntry) enumeration.nextElement();
                String name = entry.getName();
                if(!name.contains("svg") || name.startsWith("__MACOSX")) {
                    System.out.println("+++ignore : " + name);
                    continue;
                }
                System.out.println("+++release file : " + entry.getName());
                try {
                    is = new BufferedInputStream(zipFile.getInputStream(entry));
                    dstFile = new File(dir + "/" + entry.getName());

                    fos = new FileOutputStream(dstFile);
                    byte[] buffer = new byte[8192];
                    while ((count = is.read(buffer, 0, buffer.length)) != -1) {
                        fos.write(buffer, 0, count);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(fos != null)
                fos.close();
            return dir;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
