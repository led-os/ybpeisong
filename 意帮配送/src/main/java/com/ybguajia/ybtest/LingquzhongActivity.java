package com.ybguajia.ybtest;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ybguajia.ybtest.entity.Result;
import com.ybguajia.ybtest.entity.Weipeihuo;
import com.ybguajia.ybtest.utils.DataUtils;
import com.ybguajia.ybtest.utils.GlobalConstant;
import com.ybguajia.ybtest.utils.PromptManager;
import com.ybguajia.ybtest.utils.ScreenUtils;
import com.ybguajia.ybtest.utils.SharePreUtils;
import com.ybguajia.ybtest.utils.adapter.CommonListAdapter;
import com.ybguajia.ybtest.utils.adapter.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.util.Arrays;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static com.ybguajia.ybtest.utils.SharePreUtils.getDataString;

/**
 * Created by yb on 2017/4/1.
 * <p>
 * 领取中
 */

public class LingquzhongActivity extends Activity {
    private ListView lvList;
    private String URL = "";
    private String OK_URL = "";
    private TextView tvHint;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lingquzhong);
        OK_URL = GlobalConstant.SERVER_IP + "orderoption.aspx?key=" + DataUtils.md5ByDay() + "&flag=2&uid=" +
                SharePreUtils.getUserId(this) + "&shopid=";
        URL = GlobalConstant.SERVER_IP + "manageorder.aspx?key=" + DataUtils.md5ByDay() + "&flag=2&uid=" +
                getDataString(GlobalConstant.USER_ID, "", this);
        lvList = (ListView) findViewById(R.id.lv_lingquzhong_List);
        tvHint = (TextView) findViewById(R.id.tv_hint);
        getDataFromServer();
    }

    private void getDataFromServer() {
        PromptManager.showLoadingDialog(this);
        OkHttpUtils.get().url(URL).build().execute(new WeipeihuoCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                PromptManager.showToast(LingquzhongActivity.this, R.string.server_error);
                PromptManager.closeDialog();
            }

            @Override
            public void onResponse(Weipeihuo weipeihuo, int i) {
                if (weipeihuo != null) {
                    if ("1".equals(weipeihuo.result)) {
                        initData(weipeihuo);
                    } else {
                        PromptManager.showToast(LingquzhongActivity.this, weipeihuo.message);
                        PromptManager.closeDialog();
                    }
                } else {
                    PromptManager.showToast(LingquzhongActivity.this, R.string.server_error);
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
            lvList.setVisibility(View.GONE);
            tvHint.setVisibility(View.VISIBLE);
        } else {
            lvList.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.GONE);

            lvList.setAdapter(new CommonListAdapter<Weipeihuo.WeipeihuoDetail>(weipeihuo.data, LingquzhongActivity
                    .this, R.layout.item_weipeihuo) {
                @Override
                public void converView(ViewHolder viewHolder, final Weipeihuo.WeipeihuoDetail item) {
                    viewHolder.setText(R.id.tv_itemweipeihuo_shopname, item.shopname);
                    TextView tvSure = viewHolder.getView(R.id.tv_wph_receive);
                    tvSure.setText("确认");
                    tvSure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View view = LayoutInflater.from(LingquzhongActivity.this).inflate(R.layout
                                    .make_sure_dialog, null);
                            TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
                            tvTitle.setText("确认提货完毕吗?");
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
                                                PromptManager.showToast(LingquzhongActivity.this, result.message);
                                            }
                                        }

                                    });
                                    //重新刷新界面
                                    getDataFromServer();
                                    PromptManager.closeAlertDialog();

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
                            PromptManager.showCustomAlertDialog(view, LingquzhongActivity.this);
                        }
                    });
                    ListView lvGoodsDetail = viewHolder.getView(R.id.tv_itemweipeihuo_list);
                    lvGoodsDetail.setAdapter(new CommonListAdapter<Weipeihuo.WeipeihuoGoods>(item.data,
                            LingquzhongActivity
                            .this, R.layout.item_weipeihuo_item) {
                        @Override
                        public void converView(ViewHolder viewHolder, final Weipeihuo.WeipeihuoGoods item) {
                            viewHolder.setText(R.id.item_wph_goodsname, item.pname).setText(R.id.item_wph_goodsnum,
                                    item.pcount + "份");

                            final ImageView ivSign = viewHolder.getView(R.id.iv_lqz_sure);
                            final RelativeLayout rlContainer = viewHolder.getView(R.id.rl_lqz_container);
                            rlContainer.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (ivSign.getVisibility() == View.GONE) {
                                        //点击标记
                                        ivSign.setVisibility(View.VISIBLE);
                                        rlContainer.setBackgroundResource(R.color.grey_line_color);
                                        String oldIds = SharePreUtils.getDataString(GlobalConstant.LQZ_SIGN, "",
                                                LingquzhongActivity
                                                .this);


                                        SharePreUtils.setDataString(GlobalConstant.LQZ_SIGN, oldIds + "," + item.id,
                                                LingquzhongActivity
                                                .this);
                                    } else {
                                        //解除标记
                                        ivSign.setVisibility(View.GONE);
                                        rlContainer.setBackgroundResource(R.color.white);
                                        String id = getDataString(GlobalConstant.LQZ_SIGN, "", LingquzhongActivity
                                                .this);
                                        if (!TextUtils.isEmpty(id)) {
                                            String[] ids = id.split(",");
                                            List<String> idList = Arrays.asList(ids);
                                            idList.remove(new Integer(item.id));
                                            for (String targetId : idList) {
                                                SharePreUtils.setDataString(GlobalConstant.LQZ_SIGN, targetId + ",",
                                                        LingquzhongActivity
                                                        .this);
                                            }

                                        }
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

    abstract class WeipeihuoCallback extends Callback<Weipeihuo> {

        @Override
        public Weipeihuo parseNetworkResponse(Response response, int i) throws Exception {
            String result = response.body().string();
            return new Gson().fromJson(result, Weipeihuo.class);
        }
    }


}
