package com.xxzlkj.zhaolinshouyin.utils;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.xxzlkj.zhaolinshouyin.ViceScreenDisplay;

/**
 * 描述:
 *
 * @author zhangrq
 *         2018/1/9 9:17
 */

public class DisplayUtils {
    /**
     * 展示副屏
     */
    public static void showViceScreen(Context context) {
        DisplayManager mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        if (mDisplayManager != null) {
            Display[] displays = mDisplayManager.getDisplays();
            if (displays.length > 1) {
                // 有超过两个屏
                ViceScreenDisplay viceScreenDisplay = new ViceScreenDisplay(context, displays[1]);// displays[1]是副屏
                if (Build.VERSION.SDK_INT < 25) {
                    Window window = viceScreenDisplay.getWindow();
                    if (window != null) {
                        window.setType(WindowManager.LayoutParams.TYPE_TOAST);
                        window.setLayout(1280, 800);

                    }
                }
                viceScreenDisplay.show();
            }
        }
    }
}
