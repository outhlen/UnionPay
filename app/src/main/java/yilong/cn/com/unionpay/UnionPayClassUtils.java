package yilong.cn.com.unionpay;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.unionpay.UPPayAssistEx;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 2017\8\2 0002.
 *
 *  调起银行卡支付业务类
 *
 */

public  class UnionPayClassUtils  implements Handler.Callback,Runnable{

    public static final String LOG_TAG = "UnionPayClassUtils";
    private Context mContext;
    private Handler mHandler;
    private ProgressDialog mLoadingDialog = null;
    private String url,mode;
    public UnionPayClassUtils(Context context){
        this.mContext = context;
    }

    public void initPay(String url,String mode){
        this.url  = url;
        this.mode  = mode;
        mHandler  = new Handler(this);
        mLoadingDialog = ProgressDialog.show(mContext, // context
                "提示",
                "努力的获取数据中,请稍候...", true); // 进度是否是不确定的，这只和创建进度条有关
       new Thread(this).start();
    }

    /**
     * 验证支付
     */
    public  boolean verify(String msg, String sign64, String mode) {
        // 此处的verify，商户需送去商户后台做验签
        return true;
    }

    @Override
    public boolean handleMessage(Message msg) {
        Log.e(LOG_TAG, " " + "" + msg.obj);
        if (mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
        String tn = "";
        if (msg.obj == null || ((String) msg.obj).length() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("错误提示");
            builder.setMessage("网络连接失败,请重试!");
            builder.setNegativeButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        } else {
            tn = (String) msg.obj;
            doStartUnionPayPlugin(mContext, tn, mode);
        }
        return false;
    }

    private void doStartUnionPayPlugin(Context mContext, String tn, String curMode) {
        UPPayAssistEx.startPay(mContext, null, null, tn, curMode);
    }


    @Override
    public void run() {
        String tn = null;
        InputStream is;
        try {
            URL myURL = new URL(url);
            URLConnection ucon = myURL.openConnection();
            ucon.setConnectTimeout(120000);
            is = ucon.getInputStream();
            int i = -1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((i = is.read()) != -1) {
                baos.write(i);
            }
            tn = baos.toString();
            is.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Message msg = mHandler.obtainMessage();
        msg.obj = tn;
        mHandler.sendMessage(msg);

    }



}
