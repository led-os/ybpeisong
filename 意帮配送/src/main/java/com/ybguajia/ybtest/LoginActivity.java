package com.ybguajia.ybtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ybguajia.ybtest.entity.UserMsg;
import com.ybguajia.ybtest.utils.CommonUtils;
import com.ybguajia.ybtest.utils.DataUtils;
import com.ybguajia.ybtest.utils.GlobalConstant;
import com.ybguajia.ybtest.utils.PromptManager;
import com.ybguajia.ybtest.utils.SharePreUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import okhttp3.Call;
import okhttp3.Response;


public class LoginActivity extends Activity {

    private String LOGIN_URL = GlobalConstant.SERVER_IP + "managelog.aspx";
    private EditText edUsername;
    private EditText edPwd;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i(GlobalConstant.LOG, "login");
        //判断是否登录过
        String uid = SharePreUtils.getDataString(GlobalConstant.USER_ID, "", this);
        if (TextUtils.isEmpty(uid)) {
            //没登录，去登录
            findView();
            setListener();
        } else {
            //直接跳到菜单页面
            startMenuActivity();
        }

    }

    /**
     * 跳转到菜单页面
     */
    private void startMenuActivity() {
        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    private void setListener() {

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = edUsername.getText().toString();
                String pwd = edPwd.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(pwd)) {
                    PromptManager.showToast(LoginActivity.this, R.string.input_empty);
                    return;
                }

                String url = LOGIN_URL + "?uid=" + username + "&pwd=" + CommonUtils.getMd5(pwd) + "&key=" + DataUtils
                        .md5ByDay();
                OkHttpUtils.get().url(url).build().execute(new UserMsgCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        PromptManager.showToast(LoginActivity.this, R.string.server_error);
                    }

                    @Override
                    public void onResponse(UserMsg userMsg, int i) {
                        if (userMsg != null) {
                            if ("1".equals(userMsg.result)) {
                                //登录成功，保存用户ID,记录用户登陆名称和密码
                                PromptManager.showToast(LoginActivity.this, R.string.login_success);
                                SharePreUtils.setDataString(GlobalConstant.USER_ID, userMsg.data, LoginActivity.this);
                                SharePreUtils.setDataString(GlobalConstant.USER_NAME, username, LoginActivity.this);
                                startMenuActivity();
                            } else {
                                PromptManager.showToast(LoginActivity.this, userMsg.message);
                            }

                        } else {
                            PromptManager.showToast(LoginActivity.this, R.string.server_error);
                        }
                    }
                });
            }
        });
    }

    /**
     * 查找控件
     */
    private void findView() {
        edUsername = (EditText) findViewById(R.id.ed_username);
        edPwd = (EditText) findViewById(R.id.ed_pwd);
        tvLogin = (TextView) findViewById(R.id.tv_login);
        edUsername.setText(SharePreUtils.getDataString(GlobalConstant.USER_NAME, "", this));
    }

    abstract class UserMsgCallback extends Callback<UserMsg> {
        @Override
        public UserMsg parseNetworkResponse(Response response, int i) throws Exception {
            String result = response.body().string();
            return new Gson().fromJson(result, UserMsg.class);
        }
    }


}
