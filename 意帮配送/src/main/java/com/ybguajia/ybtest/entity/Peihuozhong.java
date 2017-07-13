package com.ybguajia.ybtest.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yb on 2017/4/5.
 * 配货中
 */

public class Peihuozhong {
    public String result;
    public String message;
    public List<PeihuozhongGoods> data = new ArrayList<PeihuozhongGoods>();

    public class PeihuozhongGoods {
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
