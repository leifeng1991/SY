package com.xxzlkj.zhaolinshouyin.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xxzlkj.zhaolinshare.base.util.BitmapUtils;
import com.xxzlkj.zhaolinshare.base.util.PreferencesSaver;
import com.xxzlkj.zhaolinshare.base.util.StringUtil;
import com.xxzlkj.zhaolinshare.base.util.ToastManager;
import com.xxzlkj.zhaolinshouyin.R;
import com.xxzlkj.zhaolinshouyin.config.C;
import com.xxzlkj.zhaolinshouyin.listener.OnConnectDevicesListener;
import com.xxzlkj.zhaolinshouyin.listener.OnPrintEndListener;
import com.xxzlkj.zhaolinshouyin.listener.OnSelectPrintUsbTitleListener;
import com.zrq.spanbuilder.Spans;

import net.posprinter.posprinterface.IMyBinder;
import net.posprinter.posprinterface.UiExecute;
import net.posprinter.service.PosprinterService;
import net.posprinter.utils.BitmapToByteData;
import net.posprinter.utils.DataForSendToPrinterTSC;
import net.posprinter.utils.PosPrinterDev;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 描述:
 *
 * @author zhangrq
 *         2017/12/20 11:04
 */
public class PrintBarCodeHelper {

    //IMyBinder接口，所有可供调用的连接和发送数据的方法都封装在这个接口内
    private IMyBinder binder;
    private final Activity bindServiceActivity;
    private Context bindServiceContext;
    private boolean isConnectUsbPortSuccess;
    private static PrintBarCodeHelper printBarCodeHelper;

    //bindService的参数connection
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            // 绑定成功
            binder = (IMyBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            binder = null;
        }
    };

    /**
     * 链接服务用
     */
    public static PrintBarCodeHelper getInstance(Activity activity) {
        return printBarCodeHelper == null ? printBarCodeHelper = new PrintBarCodeHelper(activity) : printBarCodeHelper;
    }

    /**
     * 链接服务用
     */
    private PrintBarCodeHelper(Activity activity) {
        this.bindServiceActivity = activity;
        this.bindServiceContext = activity.getApplicationContext();
    }

    /**
     * 绑定service，获取ImyBinder对象
     */
    public void bindService() {
        // 绑定service，获取ImyBinder对象
        Intent intent = new Intent(bindServiceActivity, PosprinterService.class);
        bindServiceActivity.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    /**
     * 解除绑定service
     */
    public void unbindService() {
        if (conn != null)
            bindServiceActivity.unbindService(conn);
    }

    /**
     * 自动链接Usb
     */
    public void autoConnectPrintDevices(Activity activity, OnConnectDevicesListener onConnectDevicesListener) {
        String selectedConnectUsbTitle = PreferencesSaver.getStringAttr(activity.getApplicationContext(), C.S.SELECTED_CONNECT_USB_TITLE);
        if (TextUtils.isEmpty(selectedConnectUsbTitle)) {
            // 之前没选择链接设备,弹框选择，选中后重新链接
            showSelectUsbTitleDialog(activity, "您还没有设置默认打印设备，请选择",
                    selectedPrintUsbTitle -> connectUsbPort(activity, selectedPrintUsbTitle, onConnectDevicesListener));
        } else {
            // 选择了链接设备，判断此列表，是否还有此设备名，有链接，没有，弹框选择
            List<String> strings = PosPrinterDev.GetUsbPathNames(activity.getApplicationContext());
            if (strings != null && strings.contains(selectedConnectUsbTitle)) {
                // 还有此设备名，链接
                connectUsbPort(activity, selectedConnectUsbTitle, onConnectDevicesListener);
            } else {
                // 没有，弹框选择，选中后重新链接
                showSelectUsbTitleDialog(activity, "您的默认设备已拔出，请重新选择",
                        selectedPrintUsbTitle -> connectUsbPort(activity, selectedPrintUsbTitle, onConnectDevicesListener));
            }
        }
    }

    /**
     * 展示选择usb路径的dialog
     */
    public static void showSelectUsbTitleDialogNoConnect(Activity activity) {
        String selectedConnectUsbTitle = PreferencesSaver.getStringAttr(activity.getApplicationContext(), C.S.SELECTED_CONNECT_USB_TITLE);
        showSelectUsbTitleDialog(activity, TextUtils.isEmpty(selectedConnectUsbTitle) ? "您还没有设置默认打印设备，请选择" : "当前默认打印设备：" + selectedConnectUsbTitle, null);
    }

    /**
     * 展示选择usb路径的dialog
     */
    private static void showSelectUsbTitleDialog(Activity activity, String title, OnSelectPrintUsbTitleListener onSelectPrintUsbTitleListener) {
        Context mContext = activity.getApplicationContext();
        List<String> strings = PosPrinterDev.GetUsbPathNames(mContext);
        if (strings == null || strings.size() == 0) {
            ToastManager.showShortToast(mContext, "请插入打印设备");
            return;
        }
        String[] items = strings.toArray(new String[strings.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setItems(items, (dialog, which) -> {
            // 选择完设备，链接此设备，保存到本地，下次进来匹配
            String selectedConnectUsbTitle = items[which];
            // 设置监听
            if (onSelectPrintUsbTitleListener != null) {
                onSelectPrintUsbTitleListener.onSelectPrintUsbTitle(selectedConnectUsbTitle);
            }
            // 保存到本地
            PreferencesSaver.setStringAttr(mContext, C.S.SELECTED_CONNECT_USB_TITLE, selectedConnectUsbTitle);
            // 销毁
            dialog.dismiss();
        });
        builder.create().show();
    }

    /**
     * 连接Usb端口
     */
    private void connectUsbPort(Activity activity, String usbTitle, OnConnectDevicesListener onConnectDevicesListener) {
        if (binder != null) {
            disconnectConnectPrintDevices();
            binder.connectUsbPort(activity.getApplicationContext(), usbTitle, new UiExecute() {
                @Override
                public void onsucess() {
                    // 连接成功后在UI线程中的执行
                    isConnectUsbPortSuccess = true;
                    if (onConnectDevicesListener != null)
                        onConnectDevicesListener.onSuccess();
                }

                @Override
                public void onfailed() {
                    isConnectUsbPortSuccess = false;
                    if (onConnectDevicesListener != null)
                        onConnectDevicesListener.onFailed();
                    // 重新选择链接
                    String selectedConnectUsbTitle = PreferencesSaver.getStringAttr(activity.getApplicationContext(), C.S.SELECTED_CONNECT_USB_TITLE);
                    showSelectUsbTitleDialog(activity, TextUtils.isEmpty(selectedConnectUsbTitle) ? "您还没有设置默认打印设备，请选择" : "当前默认打印设备：" + selectedConnectUsbTitle,
                            selectedPrintUsbTitle -> connectUsbPort(activity, selectedPrintUsbTitle, onConnectDevicesListener));

                }
            });
        } else {
            ToastManager.showShortToast(activity.getApplicationContext(), "链接打印服务失败，请退出app后重新链接");
        }
    }

    /**
     * 断开连接
     */
    public void disconnectConnectPrintDevices() {
        if (binder != null && isConnectUsbPortSuccess)
            binder.disconnectCurrentPort(new UiExecute() {
                @Override
                public void onsucess() {
                    isConnectUsbPortSuccess = false;
                }

                @Override
                public void onfailed() {

                }
            });
    }


    /**
     * 打印商品称重签
     *
     * @param goodsTitle  商品名
     * @param goodsPrice  商品价格
     * @param goodsWeight 商品重量
     * @param number      打印条码数量
     */
    public void printBarCode(Context context, String goodsCode, String goodsTitle, double goodsPrice, double goodsWeight, int number, OnPrintEndListener onPrintEndListener) {
        if (goodsCode != null && goodsCode.length() > 0 && goodsCode.length() <= 6) {
            // 货号没问题
            final ViewGroup rootView = (ViewGroup) View.inflate(context, R.layout.view_print_barcode, null);
            // 初始化
            TextView tv_goods_title = rootView.findViewById(R.id.tv_goods_title);// 商品名
            TextView tv_goods_price = rootView.findViewById(R.id.tv_goods_price);// 商品单价
            TextView tv_goods_weight = rootView.findViewById(R.id.tv_goods_weight);// 商品重量
            TextView tv_goods_all_price = rootView.findViewById(R.id.tv_goods_all_price);// 商品总价
            ImageView iv_bar_code = rootView.findViewById(R.id.iv_bar_code);// 条码图片
            TextView tv_bar_code_num = rootView.findViewById(R.id.tv_bar_code_num);// 条码号
            // 设置值
            tv_goods_title.setText(goodsTitle);// 商品名
            tv_goods_price.setText(StringUtil.saveTwoDecimal(goodsPrice));// 商品单价
            tv_goods_weight.setText(StringUtil.saveThreeDecimal(goodsWeight));// 商品重量
            tv_goods_all_price.setText(StringUtil.saveTwoDecimal(goodsPrice * goodsWeight));// 商品总价
            // 设置条码
            // 22 65001 00030 00184（2 + 5,6 + 5 + 5 = 17,18）
            String devicesNum = ZLUtils.getDevicesNum();// 设备号，保留了两位整数
            int goodsPriceInt = (int) (goodsPrice * 100);// 商品价格,5位，最大 999.99
            int goodsWeightInt = (int) (goodsWeight * 1000);// 商品重量,5位，最大 99.999
            String barCodeStr = String.format(Locale.CHINA, "%s%s%05d%05d", devicesNum, goodsCode, goodsPriceInt, goodsWeightInt);
            tv_bar_code_num.setText(barCodeStr);// 设置条码号
            // 测量
            measureAndSetGoodsBarCodeImageView(goodsCode, rootView, iv_bar_code);
            // 打印图片
            printBarCodeBitmap(BitmapUtils.getBitmapByView(rootView), number > 0 ? number : 1, onPrintEndListener);
        } else {
            // 货号有问题，提示
            ToastManager.showShortToast(context, "非标准商品货号不正确");
            if (onPrintEndListener != null)
                onPrintEndListener.onPrintEnd();
        }
    }

    /**
     * 打印商品码
     *
     * @param goodsCode  商品货号
     * @param goodsTitle 商品名
     * @param number     打印的数量
     */
    public void printGoodsCode(Context context, String goodsCode, String goodsTitle, int number, OnPrintEndListener onPrintEndListener) {
        if (!TextUtils.isEmpty(goodsCode)) {
            // 货号没问题
            final ViewGroup rootView = (ViewGroup) View.inflate(context, R.layout.view_print_goods_code, null);
            // 初始化
            TextView tv_goods_title = rootView.findViewById(R.id.tv_goods_title);// 商品名
            ImageView iv_bar_code = rootView.findViewById(R.id.iv_bar_code);// 条码图片
            TextView tv_bar_code_num = rootView.findViewById(R.id.tv_bar_code_num);// 条码号
            // 设置值
            tv_goods_title.setText(goodsTitle);// 商品名
            // 设置条码
            tv_bar_code_num.setText(goodsCode);// 设置条码号

            // 测量
            measureAndSetGoodsBarCodeImageView(goodsCode, rootView, iv_bar_code);
            // 打印图片
            printBarCodeBitmap(BitmapUtils.getBitmapByView(rootView), number > 0 ? number : 1, onPrintEndListener);
        } else {
            // 货号有问题，提示
            ToastManager.showShortToast(context, "无商品货号");
            if (onPrintEndListener != null)
                onPrintEndListener.onPrintEnd();
        }
    }

    /**
     * 打印图片
     *
     * @param printBmp           打印的图片
     * @param number             打印数量
     * @param onPrintEndListener 打印结束监听
     */
    private void printBarCodeBitmap(Bitmap printBmp, int number, OnPrintEndListener onPrintEndListener) {
        // 打印图片
        if (binder != null) {
            // 链接服务成功
            if (isConnectUsbPortSuccess) {
                // 链接设备成功
                binder.writeDataByYouself(new UiExecute() {
                    @Override
                    public void onsucess() {
                        if (onPrintEndListener != null)
                            onPrintEndListener.onPrintEnd();
                    }

                    @Override
                    public void onfailed() {
                        ToastManager.showShortToast(bindServiceContext, "打印失败，请重新打印");
                        if (onPrintEndListener != null)
                            onPrintEndListener.onPrintEnd();
                    }
                }, () -> {
                    ArrayList<byte[]> list = new ArrayList<>();
                    list.add(DataForSendToPrinterTSC.cls());

                    list.add(DataForSendToPrinterTSC.sizeBymm(40, 30));
                    list.add(DataForSendToPrinterTSC.gapBymm(2, 0));
                    list.add(DataForSendToPrinterTSC.cls());
                    list.add(DataForSendToPrinterTSC.direction(1));
                    list.add(DataForSendToPrinterTSC.bitmap(2, 10, 0, printBmp, BitmapToByteData.BmpType.Dithering));

                    list.add(DataForSendToPrinterTSC.print(number));
                    return list;
                });
            } else {
                // 链接设备失败
                ToastManager.showShortToast(bindServiceContext, "链接打印设备失败，请重新选择");
            }
        } else {
            // 链接服务失败
            ToastManager.showShortToast(bindServiceContext, "链接打印服务失败，请重新链接");
        }

    }

    /**
     * 打印价签
     */
    public void printPriceTag(Context context, String goodsTitle, double goodsPrice, String goodsBarCode) {
        // 货号没问题
        final ViewGroup rootView = (ViewGroup) View.inflate(context, R.layout.view_print_price_tag, null);
        TextView tv_goods_title = rootView.findViewById(R.id.tv_goods_title);// 商品名
        TextView tv_goods_price = rootView.findViewById(R.id.tv_goods_price);// 商品价格
        ImageView iv_bar_code = rootView.findViewById(R.id.iv_bar_code);// 商品条形码
        TextView tv_bar_code_num = rootView.findViewById(R.id.tv_bar_code_num);// 商品条形码
        // 设置值
        tv_goods_title.setText(goodsTitle);
        tv_goods_price.setText(Spans.builder().text("￥").size(30).text(StringUtil.saveTwoDecimal(goodsPrice)).build());
        tv_bar_code_num.setText(goodsBarCode);// 设置条码

        // 测量
        measureAndSetGoodsBarCodeImageView(goodsBarCode, rootView, iv_bar_code);

        // 打印图片
        printPriceTagBitmap(BitmapUtils.getBitmapByView(rootView));
    }

    /**
     * 打印图片
     *
     * @param printBmp 打印的图片
     */
    private void printPriceTagBitmap(Bitmap printBmp) {
        // 打印图片
        if (binder != null) {
            // 链接服务成功
            if (isConnectUsbPortSuccess) {
                // 链接设备成功
                binder.writeDataByYouself(new UiExecute() {
                    @Override
                    public void onsucess() {

                    }

                    @Override
                    public void onfailed() {
                        ToastManager.showShortToast(bindServiceContext, "链接失败，请重新选择");
                    }
                }, () -> {
                    ArrayList<byte[]> list = new ArrayList<>();
                    list.add(DataForSendToPrinterTSC.cls());

                    list.add(DataForSendToPrinterTSC.sizeBymm(90, 31.25));
                    list.add(DataForSendToPrinterTSC.blineBymm(3.38, 0));

                    list.add(DataForSendToPrinterTSC.cls());
                    list.add(DataForSendToPrinterTSC.direction(1));
                    list.add(DataForSendToPrinterTSC.bitmap(90, 18, 0, printBmp, BitmapToByteData.BmpType.Dithering));

                    list.add(DataForSendToPrinterTSC.print(1));
                    return list;
                });
            } else {
                // 链接设备失败
                ToastManager.showShortToast(bindServiceContext, "链接打印设备失败，请重新选择");
            }
        } else {
            // 链接服务失败
            ToastManager.showShortToast(bindServiceContext, "链接打印服务失败，请重新链接");
        }
    }

    /**
     * 测量和设置条形码图片
     */
    private void measureAndSetGoodsBarCodeImageView(String goodsBarCode, ViewGroup rootView, ImageView goodsBarCodeImageView) {
        rootView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        rootView.layout(0, 0, rootView.getMeasuredWidth(), rootView.getMeasuredHeight());
        // 设置条形码，在测量后调用，为了不失真宽度用大值，然后控件fitXY缩放
        goodsBarCodeImageView.setImageBitmap(BarcodeUtils.createBarcode(goodsBarCode, 1280, goodsBarCodeImageView.getMeasuredHeight()));// 设置条码图片
    }
}
