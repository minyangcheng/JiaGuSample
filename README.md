# Android应用安全

## 破解一个app

### 步骤一：脱壳

脱壳简单来讲其实就是获取app dex文件，我这里主要介绍下以下两个方法

#### 脱壳方法一

原理：Android4.4以上开始使用ART虚拟机，在此之前我们一直使用的Dalvik虚拟机，ART虚拟机需要用dex2oat将未加密的dex数据编译成oat，那么加固的壳一般都会在dex2oat的之前进行dex解密，因此通过修改dex2oat的代码，在其中插入一段将解密后的dex写出来的代码，即可dump出解密后的dex。

1. 一个android4.4系统虚拟机
2. 修改代码后的dex2oat(用于替换/system/bin/dex2oat位置的文件)
3. 在开发者工具里面将运行环境选择为：ART运行模式
4. 重启虚拟机，安装app，启动app

#### 脱壳方法二

原理：android中的java.lang.Class类拥有一个方法`public native Dex getDex();`，这意味着我们能通过Class对象的`getDex`方法获取到`Dex`对象，`Dex`类中有一个方法`public byte[] getBytes()`，我们能通过此方法获取获取该class对象关联的dex数据。这里采用的是Xposed去hook应用的`ClassLoader.loadClass`方法区dump解密后的dex数据。

1. 一个root过的手机，系统要求在4.4到6.0之间
2. 安装xposed：该工具的原理是修改系统文件，替换了/system/bin/app_process可执行文件，在启动Zygote时加载额外的jar文件（/data/data/de.robv.android.xposed.installer/bin/XposedBridge.jar），并执行一些初始化操作(执行XposedBridge的main方法)。然后我们就可以在这个Zygote上下文中进行某些hook操作。
3. 安装dumpdex：自己写的一个可以脱壳的app，在xposed开启该模块
4. 安装需要脱壳的app
5. 重启手机，启动app

### 步骤二：从dex文件和apk文件中获取app代码和资源

将脱壳后的dex和源app放在一个统一目录下，运行以下脚本，获取app源码和资源。该脚本其实是调用*jadx工具*来实现反编译，然后用*Intellij*打开生成的dist目录，将sources标记为sources root，将resources标记为resources root

```
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
                    return file.getName().endsWith(".dex") || file.getName().endsWith(".apk");
                }
            });
            Process process = null;
            String s = null;
            System.out.println("......开始解析......");
            for (File dexFile : dexFileArr) {
                String cmd = "jadx -d " + distDirFile.getAbsolutePath() + " " + dexFile.getAbsolutePath();
                process = Runtime.getRuntime().exec(cmd);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while ((s = bufferedReader.readLine()) != null) {
                }
                process.waitFor();
                System.out.println(dexFile.getAbsolutePath() + "...解析完成... code=" + process.exitValue());
            }
            System.out.println("......解析结束......");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
```

### 步骤三：动态分析

如果你是为了爬取app的数据，就去针对app的网络请求层进行分析，通过分析得出这个app的请求网络传输参数和加密逻辑，然后你可以通过xposed在关键方法上设置一个切面，甚至你可以直接将app的网络加解密代码提取出来，通过接口请求去爬去数据。
如果你是为了用app的某个收费功能，就去该功能页面的入口处，寻找判断该收费能否使用的代码，然后通过xposed直接hook掉判断逻辑即可。
这里推荐一个动态分析工具Inspeckage，它是一个基于Xposed开发的一款应用，核心功能有监控Shared Preferences数、加密、哈希、SQLite、HTTP、WebView数据，还能动态添加新钩子


## 了解app加固

对app进行加固，可以有效防止移动应用被破解、盗版、二次打包、注入、反编译等，保障程序的安全性、稳定性。加固技术这几年也是不断在迭代，各厂商所采用的技术也不一样。

| 加固技术  | 加固原理  |
| ------------ | ------------ |
| 类加载技术 | 对原classes.dex文件进行完整加密，放入资源文件夹中或者直接放在壳dex的文件尾部，在app运行时通过壳代码进行解密并加载运行 ; |
| 方法替换技术 | 将原classes.dex中所有的方法代码提取出来，单独加密，运行时动态劫持虚拟机中的解析代码，将解密后的代码交给虚拟机执行引擎 |
| .... | .... |

### 类加载机制

### apk打包机制

#### dex文件格式

### 手动操作一把简单加固

### 原理

# Android破解

1. 签名 jarsigner -verbose -keystore minych.jks -storepass 123456 -signedjar signed.apk unsign.apk minych
2. https://www.jianshu.com/p/a5532ecc8377 Android Application启动流程分析
3. 查看当前运行的最上层activity： adb shell dumpsys activity | grep "mFocusedActivity"
