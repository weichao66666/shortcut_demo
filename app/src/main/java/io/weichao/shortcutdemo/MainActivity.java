package io.weichao.shortcutdemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.pm.ShortcutInfoCompat;
import android.support.v4.content.pm.ShortcutManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("SHORTCUT", Context.MODE_PRIVATE);
        boolean setShortcut = sharedPreferences.getBoolean("SET_SHORTCUT", false);
        if (!setShortcut) {
            addPinnedShortcut(this, getString(R.string.app_name), R.mipmap.ic_launcher);
            addDynamicShortcut(this);
            sharedPreferences.edit().putBoolean("SET_SHORTCUT", true).commit();
        }
    }

    public static void addPinnedShortcut(Context context, String name, int iconResId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                if (intent == null) {
                    Log.d(TAG, "intent == null");
                } else {
                    ShortcutInfoCompat shortcutInfo = new ShortcutInfoCompat.Builder(context, "shortcutInfo0")
                            .setShortLabel(name)
                            .setIcon(IconCompat.createWithResource(context, iconResId))
                            .setIntent(intent)
                            .build();

                    ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, null);
                }
            }
        } else {
            Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

            // 不允许重复创建（不一定有效）
            shortcut.putExtra("duplicate", false);
            // 快捷方式的名称
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
            // 快捷方式的图标
            Parcelable iconResource = Intent.ShortcutIconResource.fromContext(context, iconResId);
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconResource);
            // 可以进入应用的栈队列顶部的 activity；在桌面长按时，会出现卸载、删除两个选项
            Intent shortcutIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

            context.sendBroadcast(shortcut);
        }
    }

    public static void addDynamicShortcut(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
            if (shortcutManager == null) {
                Log.d(TAG, "shortcutManager == null");
            } else {
                Intent intent = new Intent(context, MainActivity.class);
                intent.setAction(Intent.ACTION_VIEW);
                ShortcutInfo shortcutInfo1 = new ShortcutInfo.Builder(context, "shortcutInfo1")
                        .setShortLabel("ShortLabel1")
                        .setLongLabel("LongLabel1")
                        .setIcon(Icon.createWithResource(context, R.mipmap.ic_launcher))
                        .setIntent(intent)
                        .build();

                ShortcutInfo shortcutInfo2 = new ShortcutInfo.Builder(context, "shortcutInfo2")
                        .setShortLabel("ShortLabel2")
                        .setLongLabel("LongLabel2")
                        .setIcon(Icon.createWithResource(context, R.mipmap.ic_launcher))
                        .setIntent(intent)
                        .build();

                shortcutManager.setDynamicShortcuts(Arrays.asList(shortcutInfo1, shortcutInfo2));
            }
        }
    }
}