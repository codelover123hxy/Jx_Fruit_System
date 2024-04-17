package team.CowsAndHorses.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import team.CowsAndHorses.config.WechatConfig;
import team.CowsAndHorses.config.WxPayAppConfig;
import team.CowsAndHorses.domain.Order;
import team.CowsAndHorses.dto.SubscribeDto;
import team.CowsAndHorses.service.OrderService;
import team.CowsAndHorses.service.UserService;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Component
//@Data
public class WechatUtil {
    private static WxPayAppConfig wxPayAppConfig;
    private static Sign sign;
    private static CertificatesManager certificatesManager;
    private static OrderService orderService;
    private static UserService userService;

    @Autowired
    public void setOrderService(OrderService orderService) {
        WechatUtil.orderService = orderService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        WechatUtil.userService = userService;
    }
    @Autowired
    public void setWxPayAppConfig(WxPayAppConfig wxPayAppConfig) {
        WechatUtil.wxPayAppConfig = wxPayAppConfig;
    }

    public static CloseableHttpClient buildClientWithAuthorization(WxPayAppConfig wxPayAppConfig) throws Exception {
        PrivateKey merchantPrivateKey = PemUtil.loadPrivateKey(Files.newInputStream(Paths.get(wxPayAppConfig.getMerchantPrivateKey())));
        certificatesManager = CertificatesManager.getInstance();
        sign = SecureUtil.sign(SignAlgorithm.SHA256withRSA, merchantPrivateKey.getEncoded(), null);
        certificatesManager.putMerchant(wxPayAppConfig.getMchID(),
                new WechatPay2Credentials(wxPayAppConfig.getMchID(), new PrivateKeySigner(wxPayAppConfig.getMchSerialNo(),
                        merchantPrivateKey)), wxPayAppConfig.getApiV3key().getBytes(StandardCharsets.UTF_8));
        Verifier verifier = certificatesManager.getVerifier(wxPayAppConfig.getMchID());
        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                .withMerchant(wxPayAppConfig.getMchID(), wxPayAppConfig.getMchSerialNo(), merchantPrivateKey)
                .withValidator(new WechatPay2Validator(verifier));
        return builder.build();
    }

    public static Object subscribe(String accessToken, SubscribeDto subscribeDto, Integer type) throws Exception{
        JSONObject body = new JSONObject();
        body.put("touser", subscribeDto.getOpenId());
        JSONObject json = new JSONObject();
        if (type == 1) {
            body.put("template_id","kxAwLKc6Nn3_uC4RL7DLUof6WqqiqZwfiXvAkE1B3F8");
            JSONObject receiver = new JSONObject();
            receiver.put("value", subscribeDto.getReceiverName());
            JSONObject deliverTime = new JSONObject();
            deliverTime.put("value", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            JSONObject detail = new JSONObject();
            detail.put("value", subscribeDto.getDetail());
            JSONObject predictTime = new JSONObject();
            Date date = new Date();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR, 1);
            date = calendar.getTime();
            predictTime.put("value", DateUtil.format(date, "yyyy-MM-dd HH:mm:ss"));
            JSONObject deliver = new JSONObject();
            deliver.put("value", subscribeDto.getDeliverName());
            json.put("name2", receiver);
            json.put("date4", deliverTime);
            json.put("thing1", detail);
            json.put("time17", predictTime);
            json.put("thing16", deliver);
        }
        else if (type == 2) {
            body.put("template_id","JmwYKZ9RgZr4y1nAp_eDjgUrEt_mMIsSPOaEZMLqKsk");
            JSONObject thing1 = new JSONObject();
            thing1.put("value", subscribeDto.getOrderNo());
            JSONObject thing2 = new JSONObject();
            thing2.put("value", DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
            JSONObject thing3 = new JSONObject();
            thing3.put("value", subscribeDto.getDeliverPhone());
            JSONObject thing4 = new JSONObject();
            thing4.put("value", subscribeDto.getDetail());
            JSONObject thing5 = new JSONObject();
            thing5.put("value", subscribeDto.getAddress());
            json.put("character_string1", thing1);
            json.put("time3", thing2);
            json.put("phone_number7", thing3);
            json.put("thing6", thing4);
            json.put("thing5", thing5);
            System.out.println(json);               //发送
        }
        body.put("data", json);
        String post =  cn.hutool.http.HttpUtil.post("https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token=" + accessToken, body.toString());
        System.out.println(post);
        return post;
    }

    public static String deliverOrder(Integer orderId) throws Exception {
        JSONObject body = new JSONObject();
        Order order = orderService.getById(orderId);
        JSONObject orderKey = new JSONObject();
        orderKey.put("order_number_type", 1);
        orderKey.put("mchid", wxPayAppConfig.getMchID());
        orderKey.put("out_trade_no", order.getOrderTradeNo());
        body.put("order_key", orderKey);
        body.put("logistics_type", 2);
        body.put("delivery_mode", 1);
        JSONArray shippingList = new JSONArray();
        JSONObject shipping = new JSONObject();
        String itemDesc = orderService.getItemDesc(orderId);
        shipping.put("item_desc", itemDesc);
        shippingList.add(shipping);
        body.put("shipping_list", shippingList);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSX");
        String upload_time = sdf.format(new Date());
        body.put("upload_time", upload_time);
        JSONObject payer = new JSONObject();
        payer.put("openid", userService.getById(order.getUserId()).getOpenId());
        body.put("payer", payer);
        String accessToken= WechatConfig.getAccessToken();
        System.out.println(body);
        return HttpUtil.post("https://api.weixin.qq.com/wxa/sec/order/upload_shipping_info?access_token=" + accessToken, body.toString());
    }
}
