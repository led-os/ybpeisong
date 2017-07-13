package com.ybguajia.ybtest.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yb on 2017/4/6.
 * 订单详情
 */

public class OrderDetail {

    public String result;
    public String message;
    public List<OrderdetaiGoods> data = new ArrayList<>();

    public class OrderdetaiGoods {
        public int id;
        public String ordernum;
        public String express_name;
        public String express_tel;
        public String express_address;
        public String pname;
        public double price;
        public int pcount;
        public double totalmoney;
        public String picture;
        public double express_hurry;
        public double paymoney;
        public String paytype;
        public String param;
        public String type;
        public String reason;
        public String totalprice;

    }

}
