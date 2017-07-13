package com.ybguajia.ybtest.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ybguajia.ybtest.R;

/**
 * Created by yb on 2017/4/1.
 */

public class PromptManager {

    private static AlertDialog mAlertDialog;
    private static Dialog mDialog;

    /**
     * 关闭mAlertDialog
     */
    public static void closeAlertDialog() {
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
        }
    }

    /**
     * 关闭mDialog
     */
    public static void closeDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    /**
     * 自定义提示框
     */
    public static void showCustomAlertDialog(View view, Context context) {
        mAlertDialog = new AlertDialog.Builder(context).create();
        mAlertDialog.setCanceledOnTouchOutside(false);
        mAlertDialog.show();
        mAlertDialog.setContentView(view);
    }

    /**
     *
     */
    public static void showEditAlertDialog(View view, Context context) {
        mDialog = new Dialog(context);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mDialog.show();
        mDialog.setContentView(view);

    }

    public static void showLoadingDialog(Context context) {
        mDialog = new AlertDialog.Builder(context).create();
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
        // 注意此处要放在show之后 否则会报异常
        mDialog.setContentView(R.layout.view_loading_process_dialog);
    }


    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, int msgResId) {
        Toast.makeText(context, msgResId, Toast.LENGTH_SHORT).show();
    }

}
