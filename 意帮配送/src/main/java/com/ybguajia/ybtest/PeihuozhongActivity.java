package com.ybguajia.ybtest;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ybguajia.ybtest.entity.Peihuozhong;
import com.ybguajia.ybtest.entity.Result;
import com.ybguajia.ybtest.entity.Weipeihuo;
import com.ybguajia.ybtest.utils.CommonUtils;
import com.ybguajia.ybtest.utils.DataUtils;
import com.ybguajia.ybtest.utils.GlobalConstant;
import com.ybguajia.ybtest.utils.PromptManager;
import com.ybguajia.ybtest.utils.SharePreUtils;
import com.ybguajia.ybtest.utils.adapter.CommonListAdapter;
import com.ybguajia.ybtest.utils.adapter.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by yb on 2017/4/1.
 * <p>
 * 配货中
 */

public class PeihuozhongActivity extends Activity {
    private ListView lvList;
    private TextView tvHint;
    private String URL = "";
    private String OK_URL = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peihuozhong);
        tvHint = (TextView) findViewById(R.id.tv_hint);
        URL = GlobalConstant.SERVER_IP + "manageorder.aspx?key=" + DataUtils.md5ByDay() + "&flag=3&uid=" +
                SharePreUtils.getUserId(this);
        lvList = (ListView) findViewById(R.id.lv_peihuozhong_List);
        OK_URL = GlobalConstant.SERVER_IP + "orderoption.aspx?key=" + DataUtils.md5ByDay() + "&flag=3&uid=" +
                SharePreUtils.getUserId(this) + "&opid=";
        getDataFromServer();
    }

    private void getDataFromServer() {
        PromptManager.showLoadingDialog(this);
        String url = URL;
        OkHttpUtils.get().url(URL).build().execute(new PeihuozhongCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                PromptManager.showToast(PeihuozhongActivity.this, R.string.server_error);
                PromptManager.closeDialog();
            }

            @Override
            public void onResponse(Peihuozhong peihuozhong, int i) {
                if (peihuozhong != null) {
                    if ("1".equals(peihuozhong.result)) {
                        initData(peihuozhong);
                    } else {
                        PromptManager.closeDialog();
                        PromptManager.showToast(PeihuozhongActivity.this, peihuozhong.message);
                    }
                } else {
                    PromptManager.showToast(PeihuozhongActivity.this, R.string.server_error);
                    PromptManager.closeDialog();
                }
            }
        });
    }

    /**
     * 初始化未配货界面
     *
     * @param peihuozhong
     */
    private void initData(Peihuozhong peihuozhong) {
        List<Peihuozhong.PeihuozhongGoods> data = peihuozhong.data;
        if (data != null && data.size() <= 0) {
            lvList.setVisibility(View.GONE);
            tvHint.setVisibility(View.VISIBLE);
        } else {
            lvList.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.GONE);

            lvList.setAdapter(new CommonListAdapter<Peihuozhong.PeihuozhongGoods>(peihuozhong.data, PeihuozhongActivity
                    .this, R.layout.item_peihuozhong) {
                @Override
                public void converView(ViewHolder viewHolder, final Peihuozhong.PeihuozhongGoods item) {
                    viewHolder.setText(R.id.tv_phz_username, item.express_name).setText(R.id.tv_phz_goodsname, item
                            .pname).setText(R.id.tv_phz_userphone, item.express_tel);
                    TextView tvCount = viewHolder.getView(R.id.tv_phz_goodsnum);
                    String countNum = String.valueOf(item.pcount) + "份";
                    tvCount.setText(countNum);
                    CommonUtils.changeTvColor(tvCount, 0, countNum.length() - 1, R.color.other_text_color,
                            PeihuozhongActivity
                            .this);

                    TextView tvOk = viewHolder.getView(R.id.tv_phz_ok);
                    tvOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //配货
                            String finalUrl = OK_URL + item.id;
                            OkHttpUtils.get().url(finalUrl).build().execute(new Callback<Result>() {
                                @Override
                                public Result parseNetworkResponse(Response response, int i) throws Exception {
                                    return new Gson().fromJson(response.body().string(), Result.class);
                                }

                                @Override
                                public void onError(Call call, Exception e, int i) {

                                }

                                @Override
                                public void onResponse(Result result, int i) {
                                    if (result != null) {
                                        //PromptManager.showToast(PeihuozhongActivity.this, result.message);
                                        getDataFromServer();
                                    }
                                }

                            });
                        }
                    });

                }
            });
        }

        PromptManager.closeDialog();
    }


    abstract class PeihuozhongCallback extends Callback<Peihuozhong> {

        @Override
        public Peihuozhong parseNetworkResponse(Response response, int i) throws Exception {
            String result = response.body().string();
            return new Gson().fromJson(result, Peihuozhong.class);
        }
    }


}
