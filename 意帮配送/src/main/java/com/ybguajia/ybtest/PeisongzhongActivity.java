package com.ybguajia.ybtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ybguajia.ybtest.entity.OrderDetail;
import com.ybguajia.ybtest.entity.Result;
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

import org.w3c.dom.Text;

import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by yb on 2017/4/6.
 * 配送中
 */

public class PeisongzhongActivity extends Activity {

    private ListView lvPeisongzhong;
    private TextView tvHint;
    private String URL = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peisongzhong);
        tvHint = (TextView) findViewById(R.id.tv_hint);
        lvPeisongzhong = (ListView) findViewById(R.id.lv_peisongzhong_List);
        URL = GlobalConstant.SERVER_IP + "getsendorders.aspx?key=" + DataUtils.md5ByDay() + "&aid=" + SharePreUtils
                .getUserId(this) + "&flag=2";
        getServerData();
    }

    //获取服务端数据
    private void getServerData() {
        PromptManager.showLoadingDialog(this);
        OkHttpUtils.get().url(URL).build().execute(new OrderDetailCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                PromptManager.closeDialog();
            }

            @Override
            public void onResponse(OrderDetail orderDetail, int i) {
                if (orderDetail != null) {
                    if ("1".equals(orderDetail.result)) {
                        //填充页面数据
                        initData(orderDetail.data);
                    } else {
                        PromptManager.showToast(PeisongzhongActivity.this, orderDetail.message);
                        PromptManager.closeDialog();
                    }
                } else {
                    PromptManager.showToast(PeisongzhongActivity.this, R.string.server_error);
                    PromptManager.closeDialog();
                }
            }
        });
    }

    private void initData(final List<OrderDetail.OrderdetaiGoods> data) {

        if (data != null && data.size() <= 0) {
            lvPeisongzhong.setVisibility(View.GONE);
            tvHint.setVisibility(View.VISIBLE);
        } else {
            lvPeisongzhong.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.GONE);


            lvPeisongzhong.setAdapter(new CommonListAdapter<OrderDetail.OrderdetaiGoods>(data, PeisongzhongActivity
                    .this, R.layout.item_psz) {
                @Override
                public void converView(ViewHolder viewHolder, final OrderDetail.OrderdetaiGoods item) {
                    viewHolder.setText(R.id.tv_psz_username, "收货人:" + item.express_name).setText(R.id.tv_psz_phone,
                            item.express_tel).setText(R.id.tv_psz_address, item.express_address);

                    View view = viewHolder.getView(R.id.ll_psz_container);

                    double hungry = item.express_hurry;
                    TextView tvOrderNum = viewHolder.getView(R.id.tv_psz_ordernum);
                    TextView tvCountPrice = viewHolder.getView(R.id.tv_psz_countprice);
                    if (hungry > 0) {
                        view.setBackgroundResource(R.color.hungry_color);

                        tvOrderNum.setTextColor(PeisongzhongActivity.this.getResources().getColor(R.color.white));
                        tvCountPrice.setTextColor(PeisongzhongActivity.this.getResources().getColor(R.color.white));
                    } else {
                        view.setBackgroundResource(R.color.white);
                        tvOrderNum.setTextColor(PeisongzhongActivity.this.getResources().getColor(R.color
                                .main_text_color));
                        tvCountPrice.setTextColor(PeisongzhongActivity.this.getResources().getColor(R.color
                                .main_text_color));
                    }

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //跳转到订单详情页面
                            Intent intent = new Intent(PeisongzhongActivity.this, OrderDetailActivity.class);
                            intent.putExtra(GlobalConstant.ORDER_NUM, item.ordernum);
                            startActivity(intent);
                        }
                    });
                    TextView tvFinish = viewHolder.getView(R.id.tv_psz_ok);
                    tvFinish.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 配送中确认收货
                            //弹框确认操作
                            View view = LayoutInflater.from(PeisongzhongActivity.this).inflate(R.layout
                                    .make_sure_dialog, null);
                            TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
                            tvTitle.setText("确认配送?");
                            TextView tvReceive = (TextView) view.findViewById(R.id.tv_dialog_receive);
                            TextView tvCancel = (TextView) view.findViewById(R.id.tv_dialog_cancel);
                            tvReceive.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String okUrl = GlobalConstant.SERVER_IP + "orderoption" + ".aspx?key=" +
                                            DataUtils.md5ByDay() + "&flag=4&uid=" + SharePreUtils.getUserId
                                            (PeisongzhongActivity.this) + "&opid=" + item.id;
                                    OkHttpUtils.get().url(okUrl).build().execute(new Callback<Result>() {
                                        @Override
                                        public Result parseNetworkResponse(Response response, int i) throws Exception {
                                            String result = response.body().string();
                                            return new Gson().fromJson(result, Result.class);
                                        }

                                        @Override
                                        public void onError(Call call, Exception e, int i) {

                                        }

                                        @Override
                                        public void onResponse(Result result, int i) {
                                            if (result != null && "1".equals(result.result)) {
                                                //重新刷新界面
                                                getServerData();
                                                PromptManager.showToast(PeisongzhongActivity.this, "收货成功");
                                            } else {
                                                PromptManager.showToast(PeisongzhongActivity.this, R.string
                                                        .server_error);

                                            }
                                        }
                                    });
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
                            PromptManager.showCustomAlertDialog(view, PeisongzhongActivity.this);


                        }
                    });
                    //处理订单编号
                    String orderNum = item.ordernum;
                    String finalOrderNum = "订单编号:" + orderNum;

                    tvOrderNum.setText(finalOrderNum);


                    //处理订单金额
                    String countPrice = String.valueOf(item.paymoney);
                    String finalCountPrice = "订单金额:" + countPrice + "元";

                    tvCountPrice.setText(finalCountPrice);

                }
            });
        }
        PromptManager.closeDialog();
    }

    abstract class OrderDetailCallback extends Callback<OrderDetail> {

        @Override
        public OrderDetail parseNetworkResponse(Response response, int i) throws Exception {

            return new Gson().fromJson(response.body().string(), OrderDetail.class);
        }
    }


}
