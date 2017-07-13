package com.ybguajia.ybtest.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yb on 2017/4/9.
 */

public class PszDetail {
    public String result;
    public String message;
    public PszOrderDetail data;

    public class PszOrderDetail {
        public String oid;
        public String used_hb_money;
        public String paymoney;
        public String send_point;
        public String paytype;
        public String totalprice_express;
        public String express_name;
        public String express_address;
        public String express_tel;
        public String edittime;
        public String paytime;
        public String sendtime;
        public String overtime;
        public String express_hurry;

        public List<PszGoods> gooddata = new ArrayList<>();
    }

    public class PszGoods {
        public String pname;
        public int opid;
        public String param;
        public String picture;
        public double price;
        public double totalmoney;
        public int pcount;
    }
}

