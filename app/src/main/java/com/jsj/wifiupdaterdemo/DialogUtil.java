package com.jsj.wifiupdaterdemo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 自定义对话框样式类，
 */
public class DialogUtil {

    private OnButtonListener onButtonListener;
    private Dialog dialog;

//    public Dialog createDialog(Activity context, String title, String msg, boolean isShowCancel, final OnButtonListener onButtonListener) {
//
//        if (dialog == null) {
//            dialog = new Dialog(context, R.style.CommonDialog);
//        }
//
//        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_common, null);
//        dialog.setContentView(dialogView);
//        TextView tv_title = (TextView) dialogView.findViewById(R.id.tv_title);
//        TextView tv_msg = (TextView) dialogView.findViewById(R.id.tv_msg);
//        Button btn_ok = (Button) dialogView.findViewById(R.id.btn_ok);
//        Button btn_cancel = (Button) dialogView.findViewById(R.id.btn_cancel);
//        tv_title.setText(title);
//        tv_msg.setText(msg);
//        if (!isShowCancel) {
//            btn_cancel.setVisibility(View.GONE);
//        }
//        btn_ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                if (onButtonListener != null) {
//                    onButtonListener.enterButton();
//                }
//            }
//        });
//        btn_cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                if (onButtonListener != null) {
//                    onButtonListener.cancelButton();
//                }
//            }
//        });
//        dialog.show();
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//        return dialog;
//    }

    /**
     * 可设置取消，确定文案
     * @param context
     * @param title
     * @param msg
     * @param isShowCancel
     * @param onButtonListener
     * @return
     */
    public Dialog createSetHintDialog(Activity context, String title, String msg, boolean isShowCancel,String cancelText,String okText, final OnButtonListener onButtonListener) {

        if (dialog == null) {
            dialog = new Dialog(context, R.style.CommonDialog);
        }

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_common, null);
        dialog.setContentView(dialogView);
        TextView tv_title = (TextView) dialogView.findViewById(R.id.tv_title);
        TextView tv_msg = (TextView) dialogView.findViewById(R.id.tv_msg);
        Button btn_ok = (Button) dialogView.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        tv_title.setText(title);
        btn_ok.setText(okText);
        tv_msg.setText(msg);
        if (!isShowCancel) {
            btn_cancel.setVisibility(View.GONE);
        }
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (onButtonListener != null) {
                    onButtonListener.enterButton();
                }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (onButtonListener != null) {
                    onButtonListener.cancelButton();
                }
            }
        });
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

//    /**
//     * 登陆中--加载文字提示
//     *
//     * @param mContext
//     */
//    public static Dialog createLoadingHint(Context mContext, String msg,boolean isCancelable) {
//        final Dialog dialog = new Dialog(mContext, R.style.CommonDialog);
//        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_login_hint, null);
//        TextView tv_msg = (TextView) dialogView.findViewById(R.id.tv_msg);
//        tv_msg.setText(msg);
//        dialog.setContentView(dialogView);
//        dialog.show();
//        dialog.setCancelable(isCancelable);
//        dialog.setCanceledOnTouchOutside(isCancelable);
//        return dialog;
//    }



    /**
     * 输入密码对话框
     *
     * @param mContext
     * @return
     */
    public Dialog createPasswordDialog(Context mContext, String title,String title1,String ok,String cancel, final OnButtonViewListener onButtonViewListener) {
        final Dialog dialog = new Dialog(mContext, R.style.CommonDialog);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_password, null);
        TextView tv_title = (TextView)dialogView.findViewById(R.id.tv_title);
        TextView tv_title1 = (TextView) dialogView.findViewById(R.id.tv_title1);
        final EditText et_password = (EditText) dialogView.findViewById(R.id.et_password);
        Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_cancel);
        final Button bt_add = (Button) dialogView.findViewById(R.id.bt_add);
        tv_title.setText(title);
        tv_title1.setText(title1);
        bt_add.setText(ok);
        bt_cancel.setText(cancel);
        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onButtonViewListener != null) {
                    onButtonViewListener.enterButton(et_password);
                }
                dialog.dismiss();
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (onButtonListener != null) {
                    onButtonListener.cancelButton();
                }
            }
        });

        dialog.setContentView(dialogView);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    /**
     * 确定和取消的监听回调
     */
    public interface OnButtonListener {
        /**
         * 确定回调
         */
        void enterButton();

        /**
         * 取消的回调
         */
        void cancelButton();
    }

    /**
     * 确定和取消的监听回调,返回View
     */
    public interface OnButtonViewListener {
        /**
         * 确定回调
         */
        void enterButton(View view);

        /**
         * 取消的回调
         */
        void cancelButton();
    }
}
