package com.min.source;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.View;
import android.widget.Toast;

import com.min.source.util.L;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class App extends Application {

    private static Application context;

    public App() {
        super();
        context = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ApplicationInfo info = this.getApplicationInfo();
        L.d("App onCreate = " + info.sourceDir);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        L.d("App onTerminate");
    }

    public static Application getApplication() {
        return context;
    }

    private void hookNotificationManager(Context context) {
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            // 得到系统的 sService
            Method getService = NotificationManager.class.getDeclaredMethod("getService");
            getService.setAccessible(true);
            final Object sService = getService.invoke(notificationManager);

            Class iNotiMngClz = Class.forName("android.app.INotificationManager");
            // 动态代理 INotificationManager
            Object proxyNotiMng = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{iNotiMngClz}, new InvocationHandler() {

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    L.d("invoke(). method:{}", method);
                    if (args != null && args.length > 0) {
                        for (Object arg : args) {
                            L.d("type:{}, arg:{}", arg != null ? arg.getClass() : null, arg);
                        }
                    }
                    // 操作交由 sService 处理，不拦截通知
                    // return method.invoke(sService, args);
                    // 拦截通知，什么也不做
                    return null;
                    // 或者是根据通知的 Tag 和 ID 进行筛选
                }
            });
            // 替换 sService
            Field sServiceField = NotificationManager.class.getDeclaredField("sService");
            sServiceField.setAccessible(true);
            sServiceField.set(notificationManager, proxyNotiMng);
        } catch (Exception e) {
            L.w("Hook NotificationManager failed!", e);
        }
    }

    private void hookOnClickListener(View view) {
        try {
            // 得到 View 的 ListenerInfo 对象
            Method getListenerInfo = View.class.getDeclaredMethod("getListenerInfo");
            getListenerInfo.setAccessible(true);
            Object listenerInfo = getListenerInfo.invoke(view);
            // 得到 原始的 OnClickListener 对象
            Class<?> listenerInfoClz = Class.forName("android.view.View$ListenerInfo");
            Field mOnClickListener = listenerInfoClz.getDeclaredField("mOnClickListener");
            mOnClickListener.setAccessible(true);
            View.OnClickListener originOnClickListener = (View.OnClickListener) mOnClickListener.get(listenerInfo);
            // 用自定义的 OnClickListener 替换原始的 OnClickListener
            View.OnClickListener hookedOnClickListener = new HookedOnClickListener(originOnClickListener);
            mOnClickListener.set(listenerInfo, hookedOnClickListener);
        } catch (Exception e) {
            L.e("hook clickListener failed!", e);
        }
    }

    static class HookedOnClickListener implements View.OnClickListener {
        private View.OnClickListener origin;

        HookedOnClickListener(View.OnClickListener origin) {
            this.origin = origin;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(App.getApplication(), "hook click", Toast.LENGTH_SHORT).show();
            L.d("Before click, do what you want to to.");
            if (origin != null) {
                origin.onClick(v);
            }
            L.d("After click, do what you want to to.");
        }
    }

}