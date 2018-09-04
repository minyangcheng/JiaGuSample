package com.min.dump;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import dalvik.system.DexClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class DexHook implements IXposedHookLoadPackage {

    private static final String TAG = DexHook.class.getSimpleName();
    private static final String DUMP_PACKAGE_NAME = "com.min.dump";

    private final XSharedPreferences shares = new XSharedPreferences(DUMP_PACKAGE_NAME, Constants.CONFIG_PRE_NAME);

    @Override
    public void handleLoadPackage(final LoadPackageParam lp) throws Throwable {
        if (lp.packageName.equals(DUMP_PACKAGE_NAME) || Build.VERSION.SDK_INT < 19 || Build.VERSION.SDK_INT > 25) {
            return;
        }
        shares.reload();
        String specifyPackageName = shares.getString(Constants.KEY_SELECT_APPLICATION, null);
        String specifyClassName = shares.getString(Constants.KEY_SELECT_CLASS, null);
        if (!TextUtils.isEmpty(specifyPackageName) && lp.packageName.equals(specifyPackageName)) {
            xLog(specifyPackageName, "加载成功.....");
            Log.d(TAG, "Loading " + specifyPackageName + " ...." + specifyClassName);
            if (!TextUtils.isEmpty(specifyClassName)) {
                handleSingleClass(specifyPackageName, specifyClassName);
            } else {
                handleAllPackage(specifyPackageName);
            }
        }
    }

    public void handleSingleClass(final String packageName, final String className) {
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ClassLoader classLoader = ((Context) param.args[0]).getClassLoader();
                try {
                    Class clazz = classLoader.loadClass(className);
                    if (clazz != null) {
                        dumpDexFromClassObj(packageName, clazz);
                    }
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }
            }
        });
    }

    public void handleAllPackage(final String packageName) {
        final ClassLoader systemLoader = ClassLoader.getSystemClassLoader();
        final ClassLoader bootLoader = DexClassLoader.class.getClassLoader();

        findAndHookMethod(ClassLoader.class, "loadClass", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ClassLoader loader = (ClassLoader) param.thisObject;
                if (param.hasThrowable() || loader == bootLoader || loader == systemLoader || loader.getParent() == systemLoader) {
                    return;
                }
                try {
                    Class clazz = (Class) param.getResult();
                    dumpDexFromClassObj(packageName, clazz);
                } catch (Exception e) {
                    Log.e(TAG, Log.getStackTraceString(e));
                }

            }
        });
    }

    public void dumpDexFromClassObj(String packageName, Class clazz) {
        Object dex = XposedHelpers.callMethod(clazz, "getDex");
        byte[] data = (byte[]) XposedHelpers.callMethod(dex, "getBytes");
        File dexFile = new File(getDumpDir(packageName), data.length + ".dex");
        if (!dexFile.exists()) {
            dexFile.setReadable(true, false);
            writeByte(dexFile, data);
        }
    }

    public File getDumpDir(String packageName) {
        String dumpDirPath = "/data/data/" + packageName + "/tuoke";
        File dirFile = new File(dumpDirPath);
        if (!dirFile.exists()) {
            Log.i(TAG, "脱壳目录: " + dirFile.getAbsolutePath());
            dirFile.mkdirs();
            dirFile.setReadable(true, false);
            dirFile.setWritable(true, false);
            dirFile.setExecutable(true, false);
        }
        return dirFile;
    }

    public void writeByte(File file, byte[] bArr) {
        try {
            OutputStream outputStream = new FileOutputStream(file);
            outputStream.write(bArr);
            outputStream.flush();
            outputStream.close();
            Log.i(TAG, "新增脱壳文件: " + file.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG, "write dex file error", e);
        }
    }

    public void xLog(String tag, Object object) {
        if (tag == null || object == null) {
            return;
        }
        XposedBridge.log(tag + " : " + object.toString());
    }

}
