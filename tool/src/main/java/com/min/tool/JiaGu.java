package com.min.tool;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;

public class JiaGu {

    private static final String dir = "/home/minych/project/android/JiaGuSample/tool/src/main/apks/";
    private static String sourceApkPath = dir + "source.apk";
    private static String shellDexPath = dir + "shell.dex";
    private static String newDexFile = dir + "classes.dex";

    public static void main(String[] args) {
        try {
            File apkFile = new File(sourceApkPath);
            File shellDexFile = new File(shellDexPath);
            if (!apkFile.exists()) {
                System.out.println("APK不存在");
                return;
            }
            if (!shellDexFile.exists()) {
                System.out.println("加壳程序的dex不存在");
                return;
            }

            byte[] apkByteArray = encrypt(readFileBytes(apkFile));
            byte[] shellDexArray = readFileBytes(shellDexFile);
            int apkByteArrayLen = apkByteArray.length;
            int shellDexLen = shellDexArray.length;
            int totalLen = apkByteArrayLen + shellDexLen + 4;
            byte[] newDex = new byte[totalLen];

            // 添加壳应用数据
            System.arraycopy(shellDexArray, 0, newDex, 0, shellDexLen);
            // 添加源应用数据
            System.arraycopy(apkByteArray, 0, newDex, shellDexLen, apkByteArrayLen);
            // 添加源应用数据长度
            System.arraycopy(intToByte(apkByteArrayLen), 0, newDex, totalLen - 4, 4);
            // 修改DEX file size文件头
            fixFileSizeHeader(newDex);
            // 修改DEX SHA1 文件头
            fixSHA1Header(newDex);
            // 修改DEX CheckSum文件头
            fixCheckSumHeader(newDex);
            // 把内容写到 newDexFile
            File file = new File(newDexFile);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream localFileOutputStream = new FileOutputStream(newDexFile);
            localFileOutputStream.write(newDex);
            localFileOutputStream.flush();
            localFileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] encrypt(byte[] srcdata) {
        for (int i = 0; i < srcdata.length; i++) {
            srcdata[i] = (byte) (srcdata[i] ^ 3);
        }
        return srcdata;
    }

    /**
     * 修改dex头，CheckSum 校验码
     *
     * @param dexBytes
     */
    private static void fixCheckSumHeader(byte[] dexBytes) {
        Adler32 adler = new Adler32();
        adler.update(dexBytes, 12, dexBytes.length - 12);// 从12到文件末尾计算校验码
        long value = adler.getValue();
        int va = (int) value;
        byte[] newcs = intToByte(va);
        // 高位在前，低位在前掉个个
        byte[] recs = new byte[4];
        for (int i = 0; i < 4; i++) {
            recs[i] = newcs[newcs.length - 1 - i];

        }
        System.arraycopy(recs, 0, dexBytes, 8, 4);// 效验码赋值（8-11）
        System.out.println("较验码字节码数组长度：" + newcs.length);
    }

    /**
     * int 转byte[]
     *
     * @param number
     * @return
     */
    public static byte[] intToByte(int number) {
        byte[] b = new byte[4];
        for (int i = 3; i >= 0; i--) {
            b[i] = (byte) (number % 256);
            number >>= 8;
        }
        return b;
    }

    /**
     * 修改dex头 sha1值
     *
     * @param dexBytes
     * @throws NoSuchAlgorithmException
     */
    private static void fixSHA1Header(byte[] dexBytes)
            throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(dexBytes, 32, dexBytes.length - 32);// 从32为到结束计算sha--1
        byte[] newdt = md.digest();
        System.arraycopy(newdt, 0, dexBytes, 12, 20);// 修改sha-1值（12-31）
        // 输出sha-1值，可有可无
        String hexstr = "";
        for (int i = 0; i < newdt.length; i++) {
            hexstr += Integer.toString((newdt[i] & 0xff) + 0x100, 16)
                    .substring(1);
        }
        System.out.println("sha-1 值：" + hexstr);

    }

    /**
     * 修改dex头 file_size值
     *
     * @param dexBytes
     */
    private static void fixFileSizeHeader(byte[] dexBytes) {
        // 新文件长度
        byte[] newfs = intToByte(dexBytes.length);

        byte[] refs = new byte[4];
        // 高位在前，低位在前掉个个
        for (int i = 0; i < 4; i++) {
            refs[i] = newfs[newfs.length - 1 - i];

        }
        System.arraycopy(refs, 0, dexBytes, 32, 4);// 修改（32-35）
    }

    /**
     * 以二进制读出文件内容
     *
     * @param file
     * @return
     * @throws IOException
     */
    private static byte[] readFileBytes(File file) throws IOException {
        byte[] arrayOfByte = new byte[1024];
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        FileInputStream fis = new FileInputStream(file);
        while (true) {
            int i = fis.read(arrayOfByte);
            if (i != -1) {
                localByteArrayOutputStream.write(arrayOfByte, 0, i);
            } else {
                return localByteArrayOutputStream.toByteArray();
            }
        }
    }
}
