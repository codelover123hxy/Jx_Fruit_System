package team.CowsAndHorses.service.impl;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.Sign;
import cn.hutool.crypto.asymmetric.SignAlgorithm;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.*;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import team.CowsAndHorses.config.WxPayAppConfig;
import team.CowsAndHorses.constant.OrderState;
import team.CowsAndHorses.dao.*;
import team.CowsAndHorses.domain.*;
import team.CowsAndHorses.dto.PayQueryDto;
import team.CowsAndHorses.service.WxAppPayService;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@CacheConfig(cacheNames = "ExpireOneMin")
@Service
public class WxAppPayServiceImpl implements WxAppPayService {
    @Resource
    private WxPayAppConfig wxPayAppConfig;
    private Sign sign;
    private static CertificatesManager certificatesManager;
    final OrderDao orderDao;
    final LoginDao loginDao;
    final RefundDao refundDao;
    final SkuDao skuDao;
    final OrderSkuDao orderSkuDao;
    private static CloseableHttpClient httpClient;

    public void init() throws Exception {

    }
    private InputStream getFileInputStream(String path) {
        InputStream in = this.getClass().getResourceAsStream(path);
        return in;
    }
    public Map<String, String> wxPay(PayQueryDto dto, Integer userId) throws Exception{
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
        httpClient = builder.build();
        JSONObject requestMap = new JSONObject();
        requestMap.put("mchid", wxPayAppConfig.getMchID());
        requestMap.put("appid", wxPayAppConfig.getAppID());
        requestMap.put("description", "测试订单");
        String orderTradeNo = orderDao.selectById(dto.getOrderId()).getOrderTradeNo();
        requestMap.put("out_trade_no", orderTradeNo);
        requestMap.put("notify_url", wxPayAppConfig.getTradeNotifyUrl());
        Map<String, String> attach = new HashMap<>();
        attach.put("sn", orderTradeNo);
        requestMap.put("attach", JSON.toJSONString(attach));
        JSONObject amount = new JSONObject();
        amount.put("total", dto.getAmount());
        requestMap.put("amount", amount);
        JSONObject payer = new JSONObject();
        String openId = loginDao.selectById(userId).getOpenId();
        payer.put("openid", openId);
        requestMap.put("payer", payer);
        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/jsapi");
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Content-type", "application/json; charset=utf-8");
        httpPost.setEntity(new StringEntity(requestMap.toJSONString(), "UTF-8"));
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String bodyAsString = EntityUtils.toString(response.getEntity());
            String prePayId = JSONObject.parseObject(bodyAsString).getString("prepay_id");
            if (prePayId == null) {
                String message = JSONObject.parseObject(bodyAsString).getString("message");
                System.out.println(message);
            }
            Map<String, String> map = new HashMap<>(6);
            map.put("appId", wxPayAppConfig.getAppID());
            String timeStamp = String.valueOf(System.currentTimeMillis() / 1000);
            map.put("timeStamp", timeStamp);
            String nonceStr = IdUtil.fastSimpleUUID();
            map.put("nonceStr", nonceStr);
            String packageStr = "prepay_id=" + prePayId;
            map.put("package", packageStr);
            map.put("signType", "RSA");
            String signString =  wxPayAppConfig.getAppID() + "\n" + timeStamp + "\n" + nonceStr + "\n" + packageStr + "\n";
            map.put("paySign", Base64.encode(sign.sign(signString.getBytes())));
            return map;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void paySuccess(String orderTradeNo) {
        Order order = orderDao.selectOne(
                new QueryWrapper<Order>()
                        .eq("order_trade_no", orderTradeNo
                        )
        );
        List<OrderSku> orderSkuList = orderSkuDao.selectList(
                new QueryWrapper<OrderSku>()
                        .eq("order_id", order.getId())
        );
        orderSkuList.forEach(item -> {
            Integer skuId = item.getSkuId();
            Sku sku = skuDao.selectById(skuId);
            sku.setSoldAmount(sku.getSoldAmount() + item.getNum());
            sku.setNowInventory(sku.getTotalInventory() - item.getNum());
            skuDao.updateById(sku);
        });
        order.setOrderState(OrderState.WAIT_FOR_DELIVERY);
        orderDao.updateById(order);
        User user = loginDao.selectById(order.getUserId());
        if (user.getPurchased() == 0) {
            user.setPurchased(1);
            loginDao.updateById(user);
        }
    }
    @Override
    public Object refund(Integer refundId) throws Exception {
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
        httpClient = builder.build();
        JSONObject requestMap = new JSONObject();
        JSONObject amountItem = new JSONObject();
        Refund refund = refundDao.selectById(refundId);
        Integer amount = Math.toIntExact(Math.round(refund.getAmount() * 100));
        amountItem.put("refund", amount);
        Integer total = Math.toIntExact(Math.round(orderDao.selectById(refund.getOrderId()).getPayMoney() * 100));
        amountItem.put("total", total);
        amountItem.put("currency", "CNY");
        requestMap.put("amount", amountItem);
        String outTradeNo = orderDao.selectById(refund.getOrderId()).getOrderTradeNo();
        requestMap.put("out_trade_no", outTradeNo);
        String outRefundNo = UUID.randomUUID().toString().replace("-","");
        requestMap.put("out_refund_no", refund.getOutRefundNo());
        requestMap.put("notify_url", wxPayAppConfig.getRefundNotifyUrl());
        HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/v3/refund/domestic/refunds");
        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Content-type", "application/json; charset=utf-8");
        httpPost.setEntity(new StringEntity(requestMap.toJSONString(), "UTF-8"));
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            String bodyAsString = EntityUtils.toString(response.getEntity());
            return JSONObject.parseObject(bodyAsString);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Object orderQuery(String transactionId) throws Exception{
        //请求URL
        URIBuilder uriBuilder = new URIBuilder("https://api.mch.weixin.qq.com/v3/pay/transactions/id/" + transactionId);
        uriBuilder.setParameter("mchid", wxPayAppConfig.getMchID());
        //完成签名并执行请求
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        httpGet.addHeader("Accept", "application/json");
        CloseableHttpResponse response = httpClient.execute(httpGet);
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                System.out.println("success,return body = " + EntityUtils.toString(response.getEntity()));
            } else if (statusCode == 204) {
                System.out.println("success");
            } else {
                System.out.println("failed,resp code = " + statusCode+ ",return body = " + EntityUtils.toString(response.getEntity()));
                throw new IOException("request failed");
            }
        } finally {
            response.close();
        }
        return null;
    }
}
