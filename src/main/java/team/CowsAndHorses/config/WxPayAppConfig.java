package team.CowsAndHorses.config;
import com.github.wxpay.sdk.WXPayConfig;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import team.CowsAndHorses.service.AddressService;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;

/**
 * 配置我们自己的信息
 *
 * @author Lenovo
 */
@Component
@Data
public class WxPayAppConfig {
    /**
     * appID
     */
    @Value("${wechat.appid}")
    private String appID = "appID";

    /**
     * 商户号
     */
    @Value("${wechat.merchantId}")
    private String mchID = "商户号";

    /**
     * AppSecret
     */
    @Value("${wechat.secret}")
    private String appSecret;

    /**
     * API 商户密钥
     */
    @Value("${wechat.apiSecret}")
    private String apiV3key;



    @Value("${wechat.tradeType}")
    private String tradeType;

    /**
     * API证书绝对路径 (本项目放在了 resources/cert/wxpay/apiclient_cert.p12")
     */
    @Value("${wechat.certPath}")
    private String certPath;

    private String certPassword;

    /**
     * HTTP(S) 连接超时时间，单位毫秒
     */
    private int httpConnectTimeoutMs = 8000;

    /**
     * HTTP(S) 读数据超时时间，单位毫秒
     */
    private int httpReadTimeoutMs = 10000;

//    String merchantPrivateKey = "src\\main\\resources\\cert\\wxpay\\apiclient_key.pem";

    String merchantPrivateKey = "/www/cert/wxpay/apiclient_key.pem";
    /**
     * 微信支付异步通知地址
     */
    @Value("${wechat.notifyUrl.trade}")
    private String tradeNotifyUrl;

    @Value("${wechat.notifyUrl.refund}")
    private String refundNotifyUrl;

    @Value("${wechat.privateKey}")
    private String mchSerialNo;
    /**
     * 微信退款异步通知地址
     */

    private WechatPayHttpClientBuilder builder;
    private AutoUpdateCertificatesVerifier verifier;
    private AesUtil aesUtil;

    /**
     * 获取商户证书内容（这里证书需要到微信商户平台进行下载）
     *
     * @return 商户证书内容
     */

}

