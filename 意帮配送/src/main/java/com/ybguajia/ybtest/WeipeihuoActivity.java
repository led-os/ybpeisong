package com.ybguajia.ybtest;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ybguajia.ybtest.entity.Result;
import com.ybguajia.ybtest.entity.Weipeihuo;
import com.ybguajia.ybtest.utils.CommonUtils;
import com.ybguajia.ybtest.utils.DataUtils;
import com.ybguajia.ybtest.utils.GlobalConstant;
import com.ybguajia.ybtest.utils.PromptManager;
import com.ybguajia.ybtest.utils.ScreenUtils;
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
 * 未配货
 */

public class WeipeihuoActivity extends Activity {
    private ListView lvList;
    private TextView tvHint;
    private final String URL = GlobalConstant.SERVER_IP + "manageorder.aspx?key=" + DataUtils.md5ByDay() + "&flag=1";
    private String OK_URL = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weipeihuo);
        OK_URL = GlobalConstant.SERVER_IP + "orderoption.aspx?key=" + DataUtils.md5ByDay() + "&flag=1&uid=" +
                SharePreUtils.getUserId(this) + "&shopid=";
        lvList = (ListView) findViewById(R.id.lv_weipeihuo_List);
        tvHint = (TextView) findViewById(R.id.tv_hint);
        getDataFromServer();
    }

    private void getDataFromServer() {
        PromptManager.showLoadingDialog(this);
        OkHttpUtils.get().url(URL).build().execute(new WeipeihuoCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                PromptManager.showToast(WeipeihuoActivity.this, R.string.server_error);
                PromptManager.closeDialog();
            }

            @Override
            public void onResponse(Weipeihuo weipeihuo, int i) {
                if (weipeihuo != null) {
                    if ("1".equals(weipeihuo.result)) {

                        initData(weipeihuo);

                    } else {
                        PromptManager.showToast(WeipeihuoActivity.this, weipeihuo.message);
                        PromptManager.closeDialog();
                    }
                } else {
                    PromptManager.showToast(WeipeihuoActivity.this, R.string.server_error);
                    PromptManager.closeDialog();
                }
            }
        });
    }

    /**
     * 初始化未配货界面
     *
     * @param weipeihuo
     */
    private void initData(Weipeihuo weipeihuo) {
        List<Weipeihuo.WeipeihuoDetail> data = weipeihuo.data;
        if (data != null && data.size() <= 0) {
            //没有更多数据
            lvList.setVisibility(View.GONE);
            tvHint.setVisibility(View.VISIBLE);
        } else {
            lvList.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.GONE);

            lvList.setAdapter(new CommonListAdapter<Weipeihuo.WeipeihuoDetail>(weipeihuo.data, WeipeihuoActivity
                    .this, R.layout.item_weipeihuo) {
                @Override
                public void converView(ViewHolder viewHolder, final Weipeihuo.WeipeihuoDetail item) {
                    viewHolder.setText(R.id.tv_itemweipeihuo_shopname, item.shopname);
                    TextView tvReceive = viewHolder.getView(R.id.tv_wph_receive);
                    tvReceive.setText("领取");
                    tvReceive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View view = LayoutInflater.from(WeipeihuoActivity.this).inflate(R.layout
                                    .make_sure_dialog, null);
                            TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
                            tvTitle.setText("确认要领取吗?");
                            TextView tvReceive = (TextView) view.findViewById(R.id.tv_dialog_receive);
                            TextView tvCancel = (TextView) view.findViewById(R.id.tv_dialog_cancel);
                            tvReceive.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String finalUrl = OK_URL + item.shopid;
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
                                                PromptManager.showToast(WeipeihuoActivity.this, result.message);
                                            }
                                        }

                                    });

                                    PromptManager.closeAlertDialog();
                                    //重新刷新界面
                                    getDataFromServer();
                                }
                            });

                            tvCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PromptManager.closeAlertDialog();
                                }
                            });

                            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tvTitle
                                    .getLayoutParams();
                            layoutParams.width = (int) (ScreenUtils.getScreenWidth(context) * 0.8);
                            tvTitle.setLayoutParams(layoutParams);
                            PromptManager.showCustomAlertDialog(view, WeipeihuoActivity.this);


                        }
                    });
                    ListView lvGoodsDetail = viewHolder.getView(R.id.tv_itemweipeihuo_list);
                    lvGoodsDetail.setAdapter(new CommonListAdapter<Weipeihuo.WeipeihuoGoods>(item.data,
                            WeipeihuoActivity
                            .this, R.layout.item_weipeihuo_item) {
                        @Override
                        public void converView(ViewHolder viewHolder, Weipeihuo.WeipeihuoGoods item) {
                            viewHolder.setText(R.id.item_wph_goodsname, item.pname);
                            TextView tvCount = viewHolder.getView(R.id.item_wph_goodsnum);
                            String countNumStr = "共" + item.pcount + "份";
                            tvCount.setText(countNumStr);
                            CommonUtils.changeTvColor(tvCount, 1, countNumStr.length() - 1, R.color.other_text_color,
                                    WeipeihuoActivity.this);

                        }
                    });
                }
            });
        }
        PromptManager.closeDialog();
    }


    abstract class WeipeihuoCallback extends Callback<Weipeihuo> {

        @Override
        public Weipeihuo parseNetworkResponse(Response response, int i) throws Exception {
            String result = response.body().string();
            return new Gson().fromJson(result, Weipeihuo.class);
        }
    }


}
