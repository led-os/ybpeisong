package com.ybguajia.ybtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ybguajia.ybtest.entity.OrderDetail;
import com.ybguajia.ybtest.entity.PszDetail;
import com.ybguajia.ybtest.entity.Result;
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
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;

import static android.R.attr.data;

/**
 * Created by yb on 2017/4/6.
 */

public class OrderDetailActivity extends Activity {
    String url = GlobalConstant.SERVER_IP + "orderdetail.aspx?key=" + DataUtils.md5ByDay() + "&ordernum=";

    private TextView tvUsername;
    private TextView tvPhone;
    private TextView tvHungry;
    private TextView tvPaymethod;
    private TextView tvAddress;
    private ListView lvGoods;
    private String showWhat = "";
    String orderNum = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        tvUsername = (TextView) findViewById(R.id.tv_psz_username);
        tvPhone = (TextView) findViewById(R.id.tv_psz_phone);
        tvAddress = (TextView) findViewById(R.id.tv_psz_address);
        lvGoods = (ListView) findViewById(R.id.lv_pszgoods_List);
        tvHungry = (TextView) findViewById(R.id.tv_psz_hungry);
        tvPaymethod = (TextView) findViewById(R.id.tv_psz_paymethod);

        Intent intent = getIntent();
        orderNum = intent.getStringExtra(GlobalConstant.ORDER_NUM);
        showWhat = intent.getStringExtra(GlobalConstant.SHOW_WHAT);
        url = url + orderNum;
        getServerData();
    }

    private void getServerData() {
        PromptManager.showLoadingDialog(this);
        OkHttpUtils.get().url(url).build().execute(new PszDetailCallback() {
            @Override
            public void onError(Call call, Exception e, int i) {
                PromptManager.showToast(OrderDetailActivity.this, R.string.server_error);
                PromptManager.closeDialog();
            }

            @Override
            public void onResponse(PszDetail pszDetail, int i) {
                if (pszDetail != null) {
                    if ("1".equals(pszDetail.result)) {
                        initData(pszDetail.data);
                    } else {
                        PromptManager.showToast(OrderDetailActivity.this, pszDetail.message);
                        PromptManager.closeDialog();
                    }
                } else {
                    PromptManager.showToast(OrderDetailActivity.this, R.string.server_error);
                    PromptManager.closeDialog();
                }
            }
        });
    }

    private void initData(PszDetail.PszOrderDetail pszOrderDetail) {

        tvUsername.setText(pszOrderDetail.express_name);
        tvPhone.setText(pszOrderDetail.express_tel);
        tvAddress.setText(pszOrderDetail.express_address);
        //加急信息
        double hungry = Double.parseDouble(pszOrderDetail.express_hurry);
        if (hungry > 0) {
            tvHungry.setText("加急费:￥" + String.valueOf(hungry));

        }
        tvPaymethod.setText("支付方式:" + pszOrderDetail.paytype);
        lvGoods.setAdapter(new CommonListAdapter<PszDetail.PszGoods>(pszOrderDetail.gooddata, OrderDetailActivity
                .this, R.layout.item_orderdetail_goods) {
            @Override
            public void converView(ViewHolder viewHolder, final PszDetail.PszGoods item) {
                viewHolder.setText(R.id.tv_orderdetaigoods_goodsname, item.pname).setText(R.id
                        .tv_orderdetaigoods_price, "￥" + String.valueOf(item.totalmoney)).setText(R.id
                        .tv_orderdetaigoods_num, "x" + item.pcount);
                //处理规格
                TextView tvSku = viewHolder.getView(R.id.tv_orderdetaigoods_sku);
                if (!TextUtils.isEmpty(item.param)) {
                    tvSku.setText("规格:" + item.param);
                }

                //换货
                TextView tvChange = viewHolder.getView(R.id.tv_orderdetail_change);
                //退款
                TextView tvRefund = viewHolder.getView(R.id.tv_orderdetail_refund);
                if (GlobalConstant.PSWC.equals(showWhat)) {
                    tvChange.setVisibility(View.GONE);
                    tvRefund.setVisibility(View.GONE);
                } else {
                    tvChange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OrderDetailActivity.this.showEditDialog("换货", "2", item.pcount, item.opid);
                        }
                    });


                    tvRefund.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OrderDetailActivity.this.showEditDialog("退款", "1", item.pcount, item.opid);
                        }
                    });
                }


            }
        });

        PromptManager.closeDialog();
    }


    /**
     * @param makeType
     * @param flag     退货1，换货2
     */
    public void showEditDialog(String makeType, final String flag, final int count, final int opid) {
        View view = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.makeedit_dialog, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_title);
        tvTitle.setText("确认" + makeType + "吗?");
        TextView tvSure = (TextView) view.findViewById(R.id.tv_edit_receive);
        tvSure.setText(makeType);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_edit_cancel);
        final EditText etReason = (EditText) view.findViewById(R.id.et_edit_reason);
        etReason.setHint("请输入" + makeType + "理由");
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String errorReason = etReason.getText().toString();

                //隐藏键盘
                String finalUrl = GlobalConstant.SERVER_IP + "setorderpro.aspx?key=" + DataUtils.md5ByDay() +
                        "&flag=" + flag + "&opid=" + opid + "&count=" + count + "&reason=" + errorReason;

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
                            PromptManager.showToast(OrderDetailActivity.this, result.message);
                        }
                    }

                });
                PromptManager.closeDialog();

            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PromptManager.closeDialog();
            }
        });

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tvTitle.getLayoutParams();
        layoutParams.width = (int) (ScreenUtils.getScreenWidth(OrderDetailActivity.this) * 0.8);
        tvTitle.setLayoutParams(layoutParams);
        PromptManager.showEditAlertDialog(view, OrderDetailActivity.this);

    }

    abstract class PszDetailCallback extends Callback<PszDetail> {

        @Override
        public PszDetail parseNetworkResponse(Response response, int i) throws Exception {
            return new Gson().fromJson(response.body().string(), PszDetail.class);
        }
    }


}
