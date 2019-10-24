package com.wljs.util.constant;

/**
 * 获取元素的标识定义
 */
public class LabelConstant {

    //获取【我的】按钮元素
    public static final String mineBtn = "//*[@resource-id='com.sibu.futurebazaar:id/ll_mine'][@class='android.widget.RelativeLayout']";
    //获取【测试】按钮元素
    public static final String testBtn = "//*[@resource-id='com.sibu.futurebazaar:id/rbtn_test'][@class='android.widget.RadioButton']";
    //获取【手机登錄】按钮元素
    public static final String phoneLoginBtn = "//*[@resource-id='com.sibu.futurebazaar:id/new_phone_login'][@class='android.widget.RelativeLayout']";
    //获取【手机验证码】栏位元素
    public static final String verifiyCodeField = "//*[@resource-id='com.sibu.futurebazaar:id/et_code'][@class='android.widget.EditText']";
    //获取【登录】按钮元素
    public static final String loginBtn = "//*[@resource-id='com.sibu.futurebazaar:id/bt_login'][@class='android.widget.TextView']";
    //引导页1
    public static final String slidePageOne = "//*[@class='android.widget.ImageView'][@index='0']";
    //引导页2
    public static final String slidePageTwo = "//*[@class='android.widget.ImageView'][@index='1']";


    //获取第一个列表商品名称
    public static final String productNameForList = "//*[@resource-id='com.sibu.futurebazaar:id/tv_name'][@index='0']";
    //获取第一个列表商品价格
    public static final String productPriceForList = "//*[@resource-id='com.sibu.futurebazaar:id/tv_price'][@index='1']";
    //点击商品进入详情
    public static final String productBtn = "//*[@resource-id='com.sibu.futurebazaar:id/iv_goods'][@index='0']";
    //获取商品详情名称
    public static final String productNameForDetail = "//*[@resource-id='com.sibu.futurebazaar:id/tv_goods_name'][@index='0']";
    //获取商品详情价格（普通商品）
    public static final String CommonProPriceForDetail = "//*[@resource-id='com.sibu.futurebazaar:id/tv_price'][@index='1']";

    public static final String allowBtnName = "允许";
    public static final String experienceBtnName = "立即体验";
    public static final String otherLoginBtnName = "其他方式登录";
    public static final String findNewVersionName = "发现新版本";
    public static final String mineBtnName = "我的";
    public static final String loginModeBtnName = "手机登录";
    public static final String loginBtnName = "登录";
    public static final String textBtnName = "测试";
    public static final String smsVerifiyCodeBtnName = "获取短信验证码";
    public static final String confirmInviteCodeBtnName = "确认邀请码";
    public static final String confirmAndBindLoginBtnName = "确认绑定并登录";
    public static final String myOrderBtnName = "我的订单";

}
