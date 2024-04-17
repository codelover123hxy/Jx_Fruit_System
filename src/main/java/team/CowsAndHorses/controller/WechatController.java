package team.CowsAndHorses.controller;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


import team.CowsAndHorses.config.WechatConfig;
import team.CowsAndHorses.config.WxPayAppConfig;
import team.CowsAndHorses.constant.OrderState;
import team.CowsAndHorses.dao.CouponCodeDao;
import team.CowsAndHorses.domain.CouponCode;
import team.CowsAndHorses.domain.Order;
import team.CowsAndHorses.dto.*;
import team.CowsAndHorses.service.GoodsService;
import team.CowsAndHorses.service.OrderService;
import team.CowsAndHorses.service.UserService;
import team.CowsAndHorses.service.WxAppPayService;
import team.CowsAndHorses.util.ParseUtil;
import team.CowsAndHorses.util.WechatUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/wechat")
public class WechatController {

    final WxAppPayService wxAppPayService;
    final WxPayAppConfig wxPayAppConfig;
    final UserService userService;
    final OrderService orderService;

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;

    @GetMapping("/accessToken")
    public String getAccessToken(HttpServletRequest request) throws Exception {
        return WechatConfig.getAccessToken();
    }

    @PostMapping("/getCustomServiceList")
    public Object getCustomServiceList(HttpServletRequest request) throws Exception {
        JSONObject body = new JSONObject();
        body.put("offset", 0);
        body.put("limit", 100);
        String accessToken = WechatConfig.getAccessToken();
        System.out.println(accessToken);
        String post =  cn.hutool.http.HttpUtil.post("https://qyapi.weixin.qq.com/cgi-bin/kf/account/list?access_token=" + accessToken, body.toString());
        return AjaxResult.SUCCESS(post);
    }

    @PostMapping("/getAccountLink")
    public Object getCustomServiceAccountLink(HttpServletRequest request) throws Exception {
        JSONObject body = new JSONObject();
        String accessToken= WechatConfig.getAccessToken();
        String post = cn.hutool.http.HttpUtil.post("https://qyapi.weixin.qq.com/cgi-bin/kf/add_contact_way?access_token=" + accessToken, body.toString());
        return AjaxResult.SUCCESS();
    }

    @GetMapping("/qrCode")
    public void getQrCode(HttpServletRequest request, HttpServletResponse response) throws Exception{
        JSONObject body = new JSONObject();
        body.put("path", "pages/index/index");
        String accessToken= WechatConfig.getAccessToken();
        byte[] bytes = HttpRequest.post("https://api.weixin.qq.com/wxa/getwxacode?access_token=" + accessToken)
                .body(body.toString()).execute().bodyBytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        System.out.println(inputStream);
        BufferedImage image = ImageIO.read(inputStream);
        File output = new File("output.png");
        ImageIO.write(image, "png", output);
        System.out.println("图片保存成功！");
        response.setContentType("image/png");
        OutputStream stream = response.getOutputStream();
        stream.write(bytes);
        stream.flush();
        stream.close();
    }

    @PostMapping("/shortLink")
    public Object getShortLink(HttpServletRequest request, @RequestBody Map<String, Object> params) throws Exception {
        String pageTitle = (String) params.get("pageTitle");
        String id = (String) params.get("id");
        JSONObject body = new JSONObject();
        body.put("page_url", "/pages/goods/goods" + "?id=" + id);
        body.put("page_title", pageTitle);
        String accessToken= WechatConfig.getAccessToken();
        System.out.println(body);
        String post =  cn.hutool.http.HttpUtil.post("https://api.weixin.qq.com/wxa/genwxashortlink?access_token=" + accessToken, body.toString());
        System.out.println(post);
        return AjaxResult.SUCCESS(post);
    }

    @PostMapping("/subscribe")
    public Object subscribe(HttpServletRequest request,
                            @RequestBody Map<String,List<Integer>> map,
                            @RequestParam Integer type
    ) throws Exception {
        for (Integer id: map.get("ids")) {
            OrderDetail orderDetail = orderService.getOrderDetailById(id);
            Order order = orderService.getById(id);
            SubscribeDto subscribeDto = new SubscribeDto();
            subscribeDto.setAddress(orderDetail.getCampus() + orderDetail.getRoomAddress());
            subscribeDto.setDetail(orderDetail.getGoodsName() + "(" + orderDetail.getScale() + ")");
            subscribeDto.setOpenId(orderDetail.getOpenId());
            subscribeDto.setOrderNo(orderDetail.getOrderTradeNo());
            subscribeDto.setDeliverPhone(orderDetail.getDeliverPhone());
            subscribeDto.setDeliverName(orderDetail.getDeliverName());
            subscribeDto.setReceiverName(orderDetail.getReceiverName());
            WechatUtil.subscribe(WechatConfig.getAccessToken(), subscribeDto, type);
            order.setOrderState(type + 2);
            orderService.updateById(order);
        }
        return AjaxResult.SUCCESS();
    }

    @PostMapping("/pay/unifiedOrder")
    public Object unifiedOrder(PayQueryDto payQueryDto, HttpServletRequest request) throws Exception {
        Integer userId = ParseUtil.parseToken(request);
        System.out.println("userId: " + userId);
        Map<String, String> resultMap = wxAppPayService.wxPay(payQueryDto, userId);
        return AjaxResult.SUCCESS(resultMap);
    }

    @PostMapping("/query/order/{transactionId}")
    public Object queryOrder(@PathVariable String transactionId) throws Exception {
        wxAppPayService.orderQuery(transactionId);
        return AjaxResult.SUCCESS();
    }

    @PostMapping("/order/deliver")
    public Object deliverOrderByWechat(@RequestBody Map<String, Integer> map) throws Exception {
        Integer orderId = map.get("orderId");
        String post = WechatUtil.deliverOrder(orderId);
        JSONObject json = JSONObject.parseObject(post);
        if (json.get("errmsg").equals("ok")) {
            Order order = orderService.getById(orderId);
            if (Objects.equals(order.getOrderState(), OrderState.WAIT_FOR_DELIVERY)) {
                order.setOrderState(OrderState.WAIT_FOR_RECEIPT);
                orderService.updateById(order);
            }
            return AjaxResult.SUCCESS(JSON.parse(post));
        }
        else {
            return AjaxResult.FAIL(json);
        }
    }

//    public String deliverOrder(Integer orderId) throws Exception {
//        JSONObject body = new JSONObject();
//        Order order = orderService.getById(orderId);
//        JSONObject orderKey = new JSONObject();
//        orderKey.put("order_number_type", 1);
//        orderKey.put("mchid", wxPayAppConfig.getMchID());
//        orderKey.put("out_trade_no", order.getOrderTradeNo());
//        body.put("order_key", orderKey);
//        body.put("logistics_type", 2);
//        body.put("delivery_mode", 1);
//        JSONArray shippingList = new JSONArray();
//        JSONObject shipping = new JSONObject();
//        String itemDesc = orderService.getItemDesc(orderId);
//        shipping.put("item_desc", itemDesc);
//        shippingList.add(shipping);
//        body.put("shipping_list", shippingList);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSX");
//        String upload_time = sdf.format(new Date());
//        body.put("upload_time", upload_time);
//        JSONObject payer = new JSONObject();
//        payer.put("openid", userService.getById(order.getUserId()).getOpenId());
//        body.put("payer", payer);
//        String accessToken= WechatConfig.getAccessToken();
//        System.out.println(body);
//        return HttpUtil.post("https://api.weixin.qq.com/wxa/sec/order/upload_shipping_info?access_token=" + accessToken, body.toString());
//    }

    @PostMapping("/refund/approve")
    public Object refund(@RequestBody Map<String, Integer> map) throws Exception {
        Object result = wxAppPayService.refund(map.get("refundId"));
        return AjaxResult.SUCCESS(result);
    }

    @ResponseBody
    @PostMapping("/refund/notify")
    public String handleRefundNotify(HttpServletRequest request, HttpServletResponse response) {
        try {
            ServletInputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String result = "";
            while((line = bufferedReader.readLine()) != null){
                result += line;
            }
            System.out.println(result);
            // 通过微信支付通知的返回结果，校验是否支付成功
            JSONObject obj = JSONObject.parseObject(result);
            System.out.println(obj);
            JSONObject resource = (JSONObject) obj.get("resource");
            System.out.println(resource);
            String associatedData = (String) resource.get("associated_data");
            String nonce = (String) resource.get("nonce");
            String ciphertext = (String) resource.get("ciphertext");
            AesUtil aesUtil = new AesUtil("Hsdaq2312421249859hhhfakfblgdois".getBytes());
            String res = aesUtil.decryptToString(associatedData.getBytes(), nonce.getBytes(), ciphertext);
            System.out.println(res);
            return res;
        } catch (Exception e) {
            return null;
        }
    }
    final GoodsService goodsService;
    final CouponCodeDao couponCodeDao;
    /**
     * 微信支付结果通知接口（POST方式）
     * @param request
     * @param response
     * @return
     */
    @ResponseBody
    @PostMapping("/pay/notify")
    public String handleTradeNotify(HttpServletRequest request, HttpServletResponse response) {
        try {
            System.out.println("已支付");
            ServletInputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String result = "";
            while((line = bufferedReader.readLine()) != null){
                result += line;
            }
            System.out.println(result);
            // 通过微信支付通知的返回结果，校验是否支付成功
            JSONObject obj = JSONObject.parseObject(result);
            System.out.println(obj);
            JSONObject resource = (JSONObject) obj.get("resource");
            System.out.println(resource);
            String associatedData = (String) resource.get("associated_data");
            String nonce = (String) resource.get("nonce");
            String ciphertext = (String) resource.get("ciphertext");
            AesUtil aesUtil = new AesUtil("Hsdaq2312421249859hhhfakfblgdois".getBytes());
            String res = aesUtil.decryptToString(associatedData.getBytes(), nonce.getBytes(), ciphertext);
            System.out.println(res);
            JSONObject payInfo = JSONObject.parseObject(res);
            String outTradeNo = (String) payInfo.get("out_trade_no");
            OrderResultDto orderResultDto = orderService.getOrderByOutTradeNo(outTradeNo);
            orderResultDto.getSkus().forEach(item -> {
                if (goodsService.getById(item.getGoodsId()).getIsCoupon() == 1) {
                    CouponCode couponCode = new CouponCode();
                    couponCode.setCodeNo(outTradeNo.substring(0, 10));
                    couponCode.setUserId(orderResultDto.getUserId());
                    couponCode.setOrderSkuId(item.getOrderSkuId());
                    couponCodeDao.insert(couponCode);
                }
            });
//            Order payedOrder = orderService.getOne(new QueryWrapper<Order>().eq("out_trade_no", outTradeNo));
            System.out.println(outTradeNo);
            wxAppPayService.paySuccess(outTradeNo);
            return res;
        } catch (Exception e) {
//            logger.error("微信支付结果回调出现异常：{}", e.getMessage());
            return null;
        }
    }
}
