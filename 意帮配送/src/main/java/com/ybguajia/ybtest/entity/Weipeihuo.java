package com.ybguajia.ybtest.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yb on 2017/4/5.
 */

public class Weipeihuo {
    public String result;
    public String message;
    public List<WeipeihuoDetail> data = new ArrayList<WeipeihuoDetail>();


    /**
     * 每个店的信息
     */
    public class WeipeihuoDetail {
        public String shopid;
        public String shopname;
        public List<WeipeihuoGoods> data = new ArrayList<WeipeihuoGoods>();
    }

    /**
     * 商品相信信息
     */
    public class WeipeihuoGoods {
        public String id;
        public String pid;
        public String pname;
        public String param;
        public int pcount;
        public String picture;
        public String ordernum;
        public String express_name;
        public String express_tel;
        public String express_address;
        public int express_hurry;
        public String paytime;
    }
}
