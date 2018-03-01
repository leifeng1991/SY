package com.xxzlkj.zhaolinshare.base.util;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 描述:设置状态栏、导航栏的工具类，一个页面只能设置一个样式，不能切换样式
 * 依赖于SystemBarTintManager
 *
 * @author zhangrq
 *         2016/11/25 14:43
 */
@SuppressWarnings({"SpellCheckingInspection", "WeakerAccess"})
public class SystemBarUtils {

    /**
     * 设置状态栏颜色
     *
     * @param activity       要设置的activity
     * @param statusBarColor 状态栏颜色
     */
    public static void setStatusBarColor(Activity activity, int statusBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // >= 21
            activity.getWindow().setStatusBarColor(statusBarColor);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // > 19
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(activity);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(statusBarColor);
            ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0)
                    .setFitsSystemWindows(true);
        }
    }

    /**
     * 设置状态栏透明
     *
     * @param activity 要设置的activity
     */
    public static void setStatusBarTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 21
            // 让contentView布局内嵌在状态栏里面
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION// 去掉，否则底部有虚拟键的话，顶不起来
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);// 去掉，否则me滑动有问题
            // 设置状态栏透明
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // > 19
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            // 让contentView布局内嵌在状态栏里面
            View childAt = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            childAt.setFitsSystemWindows(false);/* 默认为false*/
        }
    }

    /**
     * 设置状态栏透明
     *
     * @param activity 要设置的activity
     */
    public static void addStatusBarTranslucentFlags(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 21
            // 让contentView布局内嵌在状态栏里面
            Window window = activity.getWindow();
            window.setStatusBarColor(Color.TRANSPARENT);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // > 19
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 设置状态栏透明
     *
     * @param activity 要设置的activity
     */
    public static void setPaddingTopStatusBarHeight(Activity activity, View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // > 19
            int paddingTop = view.getPaddingTop() + getStatusBarHeight(activity);
            view.setPadding(view.getPaddingLeft(), paddingTop, view.getPaddingRight(), view.getPaddingBottom());
        }
    }

    /**
     * 获取状态栏的高度
     */
    public static int getStatusBarHeight(Activity activity) {
        return new SystemBarTintManager(activity).getConfig().getStatusBarHeight();
    }

    /**
     * 设置状态栏亮模式，即设置状态栏文字深色字体及深色图标
     * 适配4.4以上版本MIUI、Flyme和6.0以上版本其他Android
     *
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return 0为失败，其余成功
     */
    public static int setStatusBarLightMode(Activity activity, boolean dark) {
        int result = 0;
        if (setStatusBarLightMode_MIUI(activity.getWindow(), dark)) {
            //MIUI
            result = 1;
        }
        if (setStatusBarLightMode_Flyme(activity.getWindow(), dark)) {
            //Flyme
            result = 2;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 其它的6.0手机
                result = 3;

                View decor = activity.getWindow().getDecorView();
                int ui = decor.getSystemUiVisibility();
                if (dark) {
                    ui |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    ui &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decor.setSystemUiVisibility(ui);

//                View decorView = activity.getWindow().getDecorView();
//                decorView.setSystemUiVisibility(dark ?
//                        decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR :
//                        decorView.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        return result;
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean成功执行返回true
     */
    public static boolean setStatusBarLightMode_MIUI(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * <p>
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean成功执行返回true
     */

    public static boolean setStatusBarLightMode_Flyme(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
        return result;
    }

}
