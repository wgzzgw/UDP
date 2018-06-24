package application;

import android.app.Application;
import android.content.Context;

/**
 * Created by yy on 2018/6/22.
 */

public class MyApplication extends Application {
    private static MyApplication app;
    private static Context appContext;
    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        appContext = getApplicationContext();
    }
    public static MyApplication getApplication() {
        return app;
    }
    public static Context getContext() {
        return appContext;
    }
}
