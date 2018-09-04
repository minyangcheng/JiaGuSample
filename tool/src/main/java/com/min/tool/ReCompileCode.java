package com.min.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;

/**
 * Created by minych on 18-9-4.
 */

public class ReCompileCode {

    public static void main(String[] args) {
        try {
            if (args == null && args.length == 0) {
                System.out.println("请传递dex目录文件夹");
                return;
            }
            File dexDirFile = new File(args[0]);
            if (!dexDirFile.exists()) {
                System.out.println("dex目录文件夹不存在");
                return;
            }
            File distDirFile = new File(dexDirFile, "dist");
            if (!distDirFile.exists()) {
                distDirFile.mkdirs();
            }
            File[] dexFileArr = dexDirFile.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return file.getName().endsWith(".dex");
                }
            });
            Process process = null;
            String s = null;
            for (File dexFile : dexFileArr) {
                String cmd = "jadx -d " + distDirFile.getAbsolutePath() + " " + dexFile.getAbsolutePath();
                process = Runtime.getRuntime().exec(cmd);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((s = bufferedReader.readLine()) != null) {
//                    System.out.println(s);
                }
                process.waitFor();
                System.out.println(dexFile.getAbsolutePath() + "...解析完成... code=" + process.exitValue());
            }
            System.out.println("所有文件解析完成....");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
