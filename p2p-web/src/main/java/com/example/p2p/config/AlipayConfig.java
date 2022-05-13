package com.example.p2p.config;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {
	
//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

	// 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号 2021000119691455
    public static String app_id = "2021000119691455";

    // 返回格式
    public static String format = "json";

	// 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCuAAir6tgQAO5xyx0bPuiHK1DH8TbgUmHzF75v5ul/r5sA7nWMf8E8+iSh16SvllphaFXg3aYuRqE/P3cqO872Y5xXUQdEhx73pBDOB14YUjlIHEuMJ2GhDMcSrvr5NU45xVLaMF1CIqQscnjtzUyWe9fgOPC73/Elp79XuLVdmAGq4zFJrK3NH54U03oad32dmdPgVGv8lcf38+VQHKS5A2dFLL0krACu4L+8zhTPSMJXdLY1iyLcfG74h2VRF3efQFvEnBoplSD9rNn+w/p2p2wROa49xC5/t/6VI/RyqjqacRYyUWdLC/a+GKxhlRq6uj1s0e4Yykl5QKdZFlO5AgMBAAECggEAfJ9jlpZmV9gC8Be06h29d4qLmhjm3/XBV9YKvKUbWiuRDjK8mHusEJtsf0ovFt/OLfQHhj3aV0yZEu7Gz8X0br5bbKKJXQiWInIs4xOdzjed9UrmxSQzpaC8OGKBONp4A6KTu9hmAwdCpN78Q/4Fu8ZJkLvXmAhF9F1YC2Es7THSd8N2v7S5aCdUGnwZMLULX/Zm+SimUk9XdxLTxOVqoILEX1KP9Ht3gzMiKd7JpvplTel9mU5kKOyZITgQjADajzcxHjRPeKchKJ+F2PFVHryEDr+746uN6i2Q9UaVEjV+pp+XR4/cekBXkhv+02zNLZElI4gnxCGG7YYNrrvwgQKBgQDbimYzTTxldwNwewV2fgu/nXfFv054E4AL8ShDzuMquH7UKoYi9F7p+/JP5tKl5+V0AgPrEI9ibKvf3sW5EOsvFbZT4b4Gxz65PHtIOpdNPoRUy2yzexotM9Dnb9V/FBv2LIVbOKYHyTF4Ho1rv6XZl7WvxQulVygukHdAePBpCQKBgQDK5YQHwi7c0bDudlCw5f97afpS8Ah1fVb5oD0rtpb8bp/OeBvkX0i4Wp5YNEOGehHnumSmgDuBTmqZKJhyvTN+6udAlSh9hb2qLcxyQbSlq7T4/FwbnMdqKOs5RYUHNn/SGrcKeGUqO6m3TZGmdbSh/YgNua2U+Q855eVx0puxMQKBgHLd/Q9GkB0+ogwOMhOHa7o8N9i9ed8VxN96tGiWLQHDOojkHv7XAXpHmsb3rKulo8aI72fkaEFFPsCJnMZx9bvTDmzhJj4Bre3ZmV93WIbP+QkszpkyBcY04E7XkstEyESLktLh1u0n9bw1sCT6R1qMKEHpRWijK6EBcYPIQ3GRAoGBAIJj726g/0bPx3noNUEJ/h7M459LPNRsgWmY7dba17tQSIkCrUIdbflz2xDqPX79keYu5iIOhK87usvEx/vs1Vw6D/V0JfpM6a+lER9YVISnjXz5iSQBpDH4K/q5T51sC8b1fi5PYww6HX58bh7gw/3algXkDsaPLJbTxJladM3xAoGBALA5tiSKv0qmJojz3SuudckCBT+N8A0MxfEyiURh1egdgIprJmcHNXS4/xJxbp8e6JlYdMeLpRIXd+FAjnUcbGJzaDwNTs2eVLh5HdTEKaziV42LLS4cg+P8gmrU5mJV3VP0M3UUSTi1L0+oIOo2fhIgp/DayLHunNmF3FPN7Lls";
	
	// 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAwJQnEh173L5I7irzzdoEtSBchPen8u/O6P6R4pHZ4twurWufiGc9efGxVWhcU/zmpMQdV+ny+L8xF3MsBege1WBhAPBIXMbynFDJqPwF8NBbufoX3gYTD6H7HcBIrwBklqCXOBcrJnusaOkYqlVBnLZB7j0F9thCy/rgwRVIFDc+2IuG5gfhJtt34uSo6LB6+pM/RLjMsKgYhe2mwDlT/vVaRcirEVyfuliHh9aKfio4kARtPfMuyYAVzg7/NE091DhZwTnXbgw8iumsdUjqealS4svdCN9SXKTeYBQvCFsStS4Nl3u9+zckVueoCJpCxrKslHXJDYZK0frw2ifdvwIDAQAB";

	// 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String notify_url = "http://106.75.236.113:8082/p2p/system/toSaveRechargeRecord.do";

	// 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
	public static String return_url = "http://106.75.236.113:8082/p2p/system/toSaveRechargeRecord.do";

	// 签名方式
	public static String sign_type = "RSA2";
	
	// 请求使用的编码格式p
	public static String charset = "utf-8";
	
	// 支付宝网关
	public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
//    public static String log_path = "https://openapi.alipaydev.com/gateway.do";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /** 
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
/*    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(gatewayUrl + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/
}

