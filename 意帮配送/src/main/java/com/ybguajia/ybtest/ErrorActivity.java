package com.ybguajia.ybtest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ybguajia.ybtest.entity.OrderDetail;
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
 * Created by yb on 2017/4/6.
 */

public class ErrorActivity extends Activity {

    public String URL = "";
    private ListView lvPswc;
    private TextView tvHint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pswc);
        URL = GlobalConstant.SERVER_IP + "getquestionorders.aspx?key=" + DataUtils.md5ByDay();
        lvPswc = (ListView) findViewById(R.id.lv_pswc_List);
        tvHint = (TextView) findViewById(R.id.tv_hint);
        OkHttpUtils.get().url(URL).build().execute(new PswcCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                PromptManager.showToast(ErrorActivity.this, R.string.server_error);
            }

            @Override
            public void onResponse(OrderDetail orderDetail, int i) {
                if (orderDetail != null) {
                    if ("1".equals(orderDetail.result)) {
                        initData(orderDetail.data);
                    } else {
                        PromptManager.showToast(ErrorActivity.this, orderDetail.message);
                    }
                } else {
                    PromptManager.showToast(ErrorActivity.this, R.string.server_error);
                }
            }
        });
    }

    private void initData(List<OrderDetail.OrderdetaiGoods> data) {

        if (data != null && data.size() == 0) {
            lvPswc.setVisibility(View.GONE);
            tvHint.setVisibility(View.VISIBLE);
        } else {

            lvPswc.setVisibility(View.VISIBLE);
            tvHint.setVisibility(View.GONE);
            lvPswc.setAdapter(new CommonListAdapter<OrderDetail.OrderdetaiGoods>(data, ErrorActivity.this, R.layout
                    .item_pswc) {
                @Override
                public void converView(ViewHolder viewHolder, final OrderDetail.OrderdetaiGoods item) {
                    viewHolder.setText(R.id.tv_pswc_ordernum, item.ordernum).setText(R.id.tv_pswc_username, "问题原因:" +
                            item.reason).setText(R.id.tv_pswc_address, "商品名称:" + String.valueOf(item.pname));

                    View view = viewHolder.getView(R.id.ll_pswc_container);

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //跳转到订单详情页面
                            Intent intent = new Intent(ErrorActivity.this, OrderDetailActivity.class);
                            intent.putExtra(GlobalConstant.ORDER_NUM, item.ordernum);
                            intent.putExtra(GlobalConstant.SHOW_WHAT, GlobalConstant.PSWC);
                            startActivity(intent);
                        }
                    });

                    //处理订单编号
                    String orderNum = item.ordernum;
                    String finalOrderNum = "订单编号:" + orderNum;
                    TextView tvOrderNum = viewHolder.getView(R.id.tv_pswc_ordernum);
                    tvOrderNum.setText(finalOrderNum);
                    CommonUtils.changeTvColor(tvOrderNum, finalOrderNum.length() - orderNum.length(), finalOrderNum
                            .length(), R.color.other_text_color, ErrorActivity.this);

                    //处理订单金额
                    String countPrice = String.valueOf(item.totalprice);
                    String finalCountPrice = "订单金额:" + countPrice + "元";
                    TextView tvCountPrice = viewHolder.getView(R.id.tv_pswc_countprice);
                    tvCountPrice.setText(finalCountPrice);
                    CommonUtils.changeTvColor(tvCountPrice, finalCountPrice.length() - countPrice.length() - 1,
                            finalCountPrice.length(), R.color.other_text_color, ErrorActivity.this);
                }
            });
        }

    }

    abstract class PswcCallback extends Callback<OrderDetail> {

        @Override
        public OrderDetail parseNetworkResponse(Response response, int i) throws Exception {
            return new Gson().fromJson(response.body().string(), OrderDetail.class);
        }
    }
}
