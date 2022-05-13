package com.example.util;

public class ConstantsUtil {
//     historyAvgRate的key
    public static final String HISTORY_AVG_RATE = "historyAvgRate";
//     allUsersCount的key
    public static final String ALL_USERS_COUNT = "allUsersCount";
//    allBidMoney的key
    public static final String ALL_Bid_MONEY = "allBidMoney";
//    产品类型的key
    public static final int NOVICE_TREASURE_TYPE = 0;
    public static final int PRIORITY_TYPE = 1;
    public static final int SCATTERED_TYPE = 2;
//    返回loanInfos的key
    public static final String LOAN_INFO_LIST_NOVICE_TREASURE = "noviceTreasureLoanInfoList";
    public static final String LOAN_INFO_LIST_PRIORITY = "priorityLoanInfoList";
    public static final String LOAN_INFO_LIST_SCATTERED = "scatteredLoanInfoList";
//    商品列表每页返回的总条数
    public static final int PAGE_SIZE = 9;
//    商品详细信息页的投资列表显示的总条数
    public static final int PAGE_SIZE_FOR_BID_DETAIL = 10;
//    返回结果的状态码
    public static final int SUCCESS_CODE = 1;
    public static final int FAIL_CODE = -1;
//    session的key
    public static final String SESSION_USER = "user";
//    生成图片文字的文本集合
    public static final String TEXT_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
//    阿里云AIP的appcode 64ac953e7e79432fb2e6a92324132eb8
    public static final String APP_CODE = "64ac953e7e79432fb2e6a92324132eb8";
//    获取用户登录时图片验证码的key
    public static final String LOGIN_CODE_KEY = "loginCodeKey";
//    产品状态
    public static final int PRODUCT_STATUS_NOT_FULL = 0;
    public static final int PRODUCT_STATUS_FULL = 1;
    public static final int PRODUCT_STATUS_FULL_GENERATE_REVENUE = 2;
//    投资排行的key
    public static final String INVEST_TOP = "invest_top";
//    收益状态
    public static final int INCOME_STATUS_NOT_BACK = 0;
    public static final int INCOME_STATUS_BACK = 1;

    /**
     * 交易状态
     * 0:充值中
     * 1：充值成功
     * 2：充值失败
     */
    public static final String RECHARGE_STATUS_ING = "0";
    public static final String RECHARGE_STATUS_SUCCESS = "1";
    public static final String RECHARGE_STATUS_FAIL = "2";

    //申请支付宝接口的必要参数
    public static final String ORDER_NAME = "充值";
    public static final String DESC_BODY = "支付宝充值";
    public static final String PRODUCT_CODE = "FAST_INSTANT_TRADE_PAY";//FAST_INSTANT_TRADE_PAY：新快捷即时到账产品
    public static final String TIMEOUT_EXPRESS = "11m";//超时时间11分钟
    public static final String CERT_TYPE = "IDENTITY_CARD";//IDENTITY_CARD：身份证
    public static final String NEED_CHECK_INFO = "T";//需要强制校验传：T
}