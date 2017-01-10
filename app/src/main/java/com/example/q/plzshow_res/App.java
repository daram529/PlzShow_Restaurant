package com.example.q.plzshow_res;

import android.app.Application;
import android.graphics.Typeface;

import com.facebook.appevents.AppEventsLogger;

public class App extends Application{
    public static Typeface NanumBarunGothic;
    public static Typeface NanumBarunGothicBold;
    public static Typeface NanumBarunGothicLight;
    public static Typeface NanumBarunGothicUltraLight;
    public static Typeface NanumBarunpenBold;
    public static Typeface NanumBarunpenRegular;
    public static Typeface NanumGothic;
    public static Typeface NanumPen;

    @Override
    public void onCreate() {
        AppEventsLogger.activateApp(this);
        NanumBarunGothic = Typeface.createFromAsset(getAssets(), "fonts/NanumBarunGothic.ttf");
        NanumBarunGothicBold = Typeface.createFromAsset(getAssets(), "fonts/NanumBarunGothicBold.ttf");
        NanumBarunGothicLight = Typeface.createFromAsset(getAssets(), "fonts/NanumBarunGothicLight.ttf");
        NanumBarunGothicUltraLight = Typeface.createFromAsset(getAssets(), "fonts/NanumBarunGothicUltraLight.ttf");
        NanumBarunpenBold = Typeface.createFromAsset(getAssets(), "fonts/NanumBarunpenBold.ttf");
        NanumBarunpenRegular = Typeface.createFromAsset(getAssets(), "fonts/NanumBarunpenRegular.ttf");
        NanumGothic = Typeface.createFromAsset(getAssets(), "fonts/NanumGothic.ttf");
        NanumPen = Typeface.createFromAsset(getAssets(), "fonts/NanumPen.ttf");
        super.onCreate();
    }
}
