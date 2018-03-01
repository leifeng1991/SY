package com.xxzlkj.zhaolinshouyin.utils;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * 描述: 条形码生成工具
 *
 * @author zhangrq
 *         2017/12/23 15:47
 */

public class BarcodeUtils {

    private static final int BLACK = 0xff000000;
    private static final int WHITE = 0xFFFFFFFF;
    private static BarcodeFormat barcodeFormat = BarcodeFormat.CODE_128;// 表示高密度数据， 字符串可变长，符号内含校验码

    /**
     * @param content       内容
     * @param desiredWidth  要生成的宽度
     * @param desiredHeight 要生成的高度
     * @return 生成的条码
     */
    public static Bitmap createBarcode(String content, int desiredWidth, int desiredHeight) {
        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            BitMatrix result = writer.encode(content, barcodeFormat, desiredWidth, desiredHeight);
            int width = result.getWidth();
            int height = result.getHeight();
            int[] pixels = new int[width * height];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
