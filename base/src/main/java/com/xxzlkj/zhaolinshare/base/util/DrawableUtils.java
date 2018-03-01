package com.xxzlkj.zhaolinshare.base.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

/**
 * Drawable、bitmap的工具类
 * 
 * @author zhangrq
 * 
 */
public class DrawableUtils {
	/**
	 * 获取带xx状态对应着xx图片的drawable,如果后期添加其它状态对应的图片,获取此返回参数,调用addState即可
	 * 状态模板：android.R.attr.state_selected
	 * 
	 * @param stateSet
	 * @param stateSetDrawable
	 * @param normalDrawable
	 * @return
	 */
	public static StateListDrawable getStateListDrawable(int[] stateSet,
			Drawable stateSetDrawable, Drawable normalDrawable) {
		StateListDrawable stateListDrawable = new StateListDrawable();
		stateListDrawable.addState(stateSet, stateSetDrawable);// 参数1，stateSet全部为true，才会显示参数2图片
		stateListDrawable.addState(new int[] {}, normalDrawable);
		return stateListDrawable;
	}

	/**
	 * 根据图片id获取图片
	 * 
	 * @param context
	 * @param selectedId
	 * @return
	 */
	public static BitmapDrawable getBitmapDrawable(Context context,
			int selectedId) {
		Bitmap bitmapSelected = BitmapFactory.decodeResource(
				context.getResources(), selectedId);
		return getBitmapDrawable(context, bitmapSelected);
	}

	/**
	 * 根据bitmap获取图片
	 * 
	 * @param context
	 * @param bitmap
	 * @return
	 */
	public static BitmapDrawable getBitmapDrawable(Context context,
			Bitmap bitmap) {
		return new BitmapDrawable(context.getResources(), bitmap);
	}

	/**
	 * 产生阴影Bitmap
	 * 
	 * @param shadowBitmapWidth
	 *            创建bitmap的宽
	 * @param shadowBitmapHeight
	 *            创建bitmap的高
	 * @param fillColor
	 *            填充颜色
	 * @param cornerRadius
	 *            设置bitmap的边角弧度
	 * @param shadowColor
	 *            阴影颜色
	 * @param shadowRadius
	 *            设置阴影的边距
	 * @param shadowDx
	 *            阴影X轴偏移
	 * @param shadowDy
	 *            阴影Y轴偏移
	 * @return
	 */
	public static Bitmap getShadowBitmap(int shadowBitmapWidth,
			int shadowBitmapHeight, int fillColor, float cornerRadius,
			int shadowColor, float shadowRadius, float shadowDx, float shadowDy) {
		if (shadowBitmapWidth <= 0 || shadowBitmapHeight <= 0)
			return null;

		Bitmap bitmap = Bitmap.createBitmap(shadowBitmapWidth,
				shadowBitmapHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		RectF shadowRect = new RectF(shadowRadius, shadowRadius,
				shadowBitmapWidth - shadowRadius, shadowBitmapHeight
						- shadowRadius);

		shadowRect.top -= shadowDy;
		shadowRect.bottom -= shadowDy;

		shadowRect.left -= shadowDx;
		shadowRect.right -= shadowDx;

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(fillColor);
		paint.setStyle(Paint.Style.FILL);
		// 画阴影
		paint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor);

		canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, paint);
		return bitmap;
	}
}
