package team.CowsAndHorses.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import team.CowsAndHorses.constant.OrderState;
import team.CowsAndHorses.dao.*;
import team.CowsAndHorses.domain.*;
import team.CowsAndHorses.dto.*;
import team.CowsAndHorses.service.OrderService;
import team.CowsAndHorses.util.DateUtil;
import team.CowsAndHorses.util.PageUtil;
import team.CowsAndHorses.util.WechatUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@CacheConfig(cacheNames = "ExpireOneMin")
@Service
public class OrderServiceImpl extends ServiceImpl<OrderDao, Order> implements OrderService {
    final OrderDao orderDao;
    final OrderInfoDao orderInfoDao;
    final CartDao cartDao;
    final OrderSkuDao orderSkuDao;
    final SkuDao skuDao;
    final OrderDetailDao orderDetailDao;
    final AddressDao addressDao;
    final LoginDao loginDao;
    final CouponDao couponDao;
    final RefundDao refundDao;
    final CommentDao commentDao;
    final DeliverDao deliverDao;
    static Double postFee = 0.0;

    /**
     * 获取商品列表
     * @param orderId
     * @return
     */
    @Override
    public JSONArray getShippingList(Integer orderId) {
        List<SkuInfoDto> orderSkuList = skuDao.selectSkuList(orderId);
        JSONArray jsonArray = new JSONArray();
        IntStream.range(0, orderSkuList.size()).forEach(
                index -> {
                    SkuInfoDto item = orderSkuList.get(index);
                    JSONObject jsonObject = new JSONObject();
                    StringBuffer goods = new StringBuffer();
                    goods.append(item.getGoodsName()).
                            append("(").append(item.getScale()).
                            append(") *").append(item.getNum());
                    if (index < orderSkuList.size() - 1) {
                        goods.append(",");
                    }
                    jsonObject.put("item_desc", goods);
                    jsonArray.add(jsonObject);
                });
        return jsonArray;
    }

    /**
     * 获取商品介绍
     * @param orderId
     * @return
     */
    @Override
    public String getItemDesc(Integer orderId) {
        List<SkuInfoDto> orderSkuList = skuDao.selectSkuList(orderId);
        JSONArray jsonArray = new JSONArray();
        StringBuffer itemDesc = new StringBuffer();
        IntStream.range(0, orderSkuList.size()).forEach(
                index -> {
                    SkuInfoDto item = orderSkuList.get(index);
                    itemDesc.append(item.getGoodsName()).
                            append("(").append(item.getScale()).
                            append(") *").append(item.getNum());
                    if (index < orderSkuList.size() - 1) {
                        itemDesc.append(",");
                    }
                });
        return itemDesc.toString();
    }
    @Override
    public IPage<OrderDetail> getOrderInfo(Integer orderState, PageQueryDto pageQuery){
        Integer pageNum = pageQuery.getPageNum();
        Integer pageSize = pageQuery.getPageSize();
        IPage<OrderDetail> page = new Page<>(pageNum, pageSize);
        if (orderState != 0) {
            QueryWrapper<OrderDetail> qw = new QueryWrapper<>();
            qw.eq("order_state", orderState);
            return orderDetailDao.selectPage(page, qw);
        }
        else {
            return orderDetailDao.selectPage(page, null);
        }
    }

    final OrderAddressDao orderAddressDao;
    @Override
    public IPage<OrderResultDto> getUserOrderListByState(QueryWrapper<OrderAddress> qw, PageQueryDto pageQuery) {
        IPage<OrderAddress> pageOrder = new Page<>(
                pageQuery.getPageNum(),
                pageQuery.getPageSize()
        );
        qw.orderByDesc("submit_time");
        IPage<OrderAddress> orderList = orderAddressDao.selectPage(pageOrder, qw);
        IPage<OrderResultDto> pageOrderResult = PageUtil.pageFormatTransform(orderList, OrderResultDto.class);
        List<OrderResultDto> resultList = new ArrayList<>();
        for (OrderAddress orderAddress: orderList.getRecords()) {
            OrderResultDto result = new OrderResultDto();
            Integer orderId = orderAddress.getOrderId();
            Order order = orderDao.getOrderById(orderId);
            result.setOrder(order);
            result.setNickname(orderAddress.getNickName());
            result.setAddress(addressDao.selectById(order.getAddressId()));
            setCommentStatus(orderId, result);
            resultList.add(result);
        }
        pageOrderResult.setRecords(resultList);
        return pageOrderResult;
    }

    @Override
    public List<OrderExportDto> getOrderList(String startTime, String endTime, Integer orderState, String campus) {
        QueryWrapper<OrderInfo> qw = new QueryWrapper<>();
        if (orderState != null && orderState != 0) {
            qw.eq("order_state", orderState);
        }
        if (startTime != null) {
            qw.ge("submit_time", startTime);
        }
        if (endTime != null) {
            qw.le("submit_time", endTime);
        }
        if (campus != null) {
            qw.eq("campus", campus);
        }
        qw.orderByAsc("room_address");
        List<OrderInfo> orderList = orderInfoDao.selectList(qw);
        List<OrderExportDto> resultList = new ArrayList<>();
        orderList.forEach(order -> {
            OrderExportDto result = new OrderExportDto();
            result.setOrderInfo(order);
            Integer orderId = order.getId();
            result.setNickname(loginDao.selectById(order.getUserId()).getNickName());
            result.setAddress(addressDao.selectById(order.getAddressId()));
            List<SkuInfoDto> orderSkuList = skuDao.selectSkuList(orderId);
            StringBuffer goods = new StringBuffer();
            IntStream.range(0, orderSkuList.size()).forEach(
                    index -> {
                        SkuInfoDto item = orderSkuList.get(index);
                        goods.append(item.getGoodsName()).
                                append("(").append(item.getScale()).
                                append(") +").append(item.getNum());
                        if (index < orderSkuList.size() - 1) {
                            goods.append(";");
                        }
                    });
            result.setGoods(goods.toString());
            resultList.add(result);
        });
        return resultList;
    }

    @Override
    public OrderResultDto getOrderById(Integer id) {
        OrderResultDto result = new OrderResultDto();
        Order order = orderDao.selectById(id);
        result.setOrder(order);
        setCommentStatus(id, result);
        result.setAddress(
            addressDao.selectById(
                    order.getAddressId()
            )
        );
        return result;
    }

    @Override
    public OrderResultDto getOrderByOutTradeNo(String outTradeNo) {
        OrderResultDto result = new OrderResultDto();
        Order order = orderDao.selectOne(
                new QueryWrapper<Order>()
                        .eq("order_trade_no", outTradeNo)
        );
        result.setOrder(order);
        setCommentStatus(order.getId(), result);
        result.setAddress(
                addressDao.selectById(
                        order.getAddressId()
                )
        );
        return result;
    }

    private void setCommentStatus(Integer id, OrderResultDto result) {
        List<SkuInfoDto> orderSkuList = skuDao.selectSkuList(id);
        orderSkuList.forEach(item -> {
            OrderSku orderSku = orderSkuDao.selectOne(new QueryWrapper<OrderSku>()
                    .eq("order_id", item.getOrderId())
                    .eq("sku_id", item.getId()));
            item.setIsCommented(orderSku.getIsCommented());
            item.setRefundState(orderSku.getRefundState());
        });
        result.setSkus(orderSkuList);
    }

    @Override
    public OrderDetail getOrderDetailById(Integer id) {
        return orderDao.getOrderDetailById(id);
    }

    @Override
    public void deliver(Map<String, Object> map) {
        String startTime = (String) map.get("startTime");
        String endTime = (String) map.get("endTime");
        String campus = (String) map.get("campus");
        QueryWrapper<OrderInfo> orderInfoQueryWrapper = new QueryWrapper<>();
        if (startTime != null) {
            orderInfoQueryWrapper.ge("submit_time", startTime);
        }
        if (endTime != null) {
            orderInfoQueryWrapper.le("submit_time", endTime);
        }
        if (null != campus) {
            orderInfoQueryWrapper.eq("campus", campus);
        }
        List<OrderInfo> orderInfoList = orderInfoDao.selectList(orderInfoQueryWrapper);
        orderInfoList.forEach(item -> {
            Integer orderId = item.getId();
            try {
                WechatUtil.deliverOrder(orderId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Order order = orderDao.selectById(orderId);
            if (Objects.equals(item.getOrderState(), OrderState.WAIT_FOR_DELIVERY)) {
                order.setOrderState(OrderState.WAIT_FOR_RECEIPT);
            }
            orderDao.updateById(order);
        });
    }
    @Override
    public void arrive(Map<String, Object> map) {
        String startTime = (String) map.get("startTime");
        String endTime = (String) map.get("endTime");
        String campus = (String) map.get("campus");
        QueryWrapper<OrderInfo> orderInfoQueryWrapper = new QueryWrapper<>();
        if (startTime != null) {
            orderInfoQueryWrapper.ge("submit_time", startTime);
        }
        if (endTime != null) {
            orderInfoQueryWrapper.le("submit_time", endTime);
        }
        if (null != campus) {
            orderInfoQueryWrapper.eq("campus", campus);
        }
        List<OrderInfo> orderInfoList = orderInfoDao.selectList(orderInfoQueryWrapper);

    }

    @Override
    public IPage<Order> getOrderInfoByUserId(Integer userId, PageQueryDto pageQuery){
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        Integer pageNum = pageQuery.getPageNum();
        Integer pageSize = pageQuery.getPageSize();
        IPage<Order> page = new Page<>(pageNum, pageSize);
        wrapper.eq("user_id", userId);
        return orderDao.selectPage(page, wrapper);
    }
    @Override
    public IPage<Order> getOrderInfoByGoodsId(Integer goodsId, PageQueryDto pageQuery){
        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        Integer pageNum = pageQuery.getPageNum();
        Integer pageSize = pageQuery.getPageSize();
        IPage<Order> page = new Page<>(pageNum, pageSize);
        wrapper.eq("goods_id", goodsId);
        return orderDao.selectPage(page, wrapper);
    }
    @Override
    public Integer submitOrder(Integer userId, PurchaseParam params){
        try {
            Order order = new Order();
            order.setUserId(userId);
            order.setAddressId(params.getAddressId());
            order.setOrderState(OrderState.WAIT_FOR_PAYMENT); // 待支付
            Integer totalNum = 0;
            for (Map<String, Object> goodsItem: params.getGoods()) {
                totalNum += (Integer) goodsItem.get("num");
            }
            // 更新消费券状态
            if (params.getCouponId() != null) {
                Coupon selectedCoupon = couponDao.selectById(params.getCouponId());
                selectedCoupon.setIsUsed(1);
                couponDao.updateById(selectedCoupon);
            }
            // 设置订单信息
            order.setTotalNum(totalNum);
            order.setPayMoney(params.getPayMoney());
            order.setPostFee(postFee);
            order.setTotalMoney(params.getPayMoney() - postFee);
            order.setNotes(params.getNotes());
            order.setDiscount(params.getDiscount());
            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            order.setSubmitTime(df.format(day));
            String orderTradeNo = UUID.randomUUID().toString().replace("-","");
            order.setOrderTradeNo(orderTradeNo);
            orderDao.insert(order);
            Integer orderId = order.getId();
            for (Map<String, Object> goodsItem: params.getGoods()) {
                OrderSku orderSku = new OrderSku();
                Integer skuId = (Integer) goodsItem.get("id");
                orderSku.setSkuId(skuId);
                orderSku.setNum((Integer) goodsItem.get("num"));
                orderSku.setOrderId(orderId);
                orderSkuDao.insert(orderSku);
                QueryWrapper<Cart> wrapper = new QueryWrapper<>();
                wrapper.eq("sku_id", skuId)
                        .eq("selected", 1)
                        .eq("user_id", userId);
                cartDao.delete(wrapper);
            }
            return orderId;
        } catch (Exception e) {
            return 0;
        }
    }
    @Override
    public Integer withdrawOrder(Integer orderId, String cancelReason){
        Order order = orderDao.selectById(orderId);
        order.setOrderState(OrderState.CANCELED);
        order.setCancelReason(cancelReason);
        return orderDao.updateById(order);
    }
    @Override
    public Integer deleteOrderByIds(Integer userId, List<Integer> ids) {
        try {
            for (Integer id : ids) {
                orderDao.deleteOrderById(id);
            }
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }
    @Override
    public Integer deleteOrderById(Integer userId, Integer id) {
        try {
            orderDao.deleteOrderById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Integer setOrderState(Integer orderId, Integer targetState) {
        Order order = orderDao.selectById(orderId);
        order.setOrderState(targetState);
        orderDao.updateById(order);
        return 1;
    }


    @Value("${order.deadline}")
    private String deadline;

    @Override
    public IPage<OrderResultDto> getOrderInfoByCondition(Map<String, Object> condition, PageQueryDto pageQuery) throws ParseException {
        String phone = (String) condition.get("receiverPhone");
        String orderId = (String) condition.get("orderId");
        String orderState = (String) condition.get("orderState");
        String campus = (String) condition.get("campus");
        String roomAddress = (String) condition.get("roomAddress");
        String date = (String) condition.get("date");
        QueryWrapper<OrderAddress> qw = new QueryWrapper<>();
        System.out.println(condition);
        if (null != phone) {
            qw.like("receiver_phone", phone);
        }
        if (null != orderId) {
            qw.eq("order_id", orderId);
        }
        System.out.println(orderState);
        if (null != orderState && !"0".equals(orderState)) {
            qw.eq("order_state", orderState);
        }
        if (null != campus) {
            qw.eq("campus", campus);
        }
        if (null != roomAddress) {
            qw.eq("room_address", roomAddress);
        }
        if (null != date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d = sdf.parse(date + " " + deadline);
            Date newDate = DateUtil.calculateDate(d, -1);
            qw.le("submit_time", sdf.format(d));
            qw.ge("submit_time", sdf.format(newDate));
        }
        return getUserOrderListByState(qw, pageQuery);
    }

    @Override
    public Integer refund(Integer orderId, List<Integer> skuIds, Double amount, String reason) {
        Refund refund = new Refund();
        refund.setAmount(amount);
        refund.setReason(reason);
        refund.setOrderId(orderId);
        String outRefundNo = UUID.randomUUID().toString().replace("-","");
        refund.setOutRefundNo(outRefundNo);
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        refund.setRefundTime(df.format(day));
        refundDao.insert(refund);
        skuIds.forEach(id -> {
            refundDao.addRefundSku(refund.getId(), id);
            OrderSku orderSku = orderSkuDao.selectOne(
                    new QueryWrapper<OrderSku>()
                            .eq("sku_id", id)
                            .eq("order_id", orderId)
            );
            orderSku.setRefundState(1);
            orderSkuDao.updateById(orderSku);
        });
        return 1;
    }
    @Override
    public List<Deliver> getDelivers() {
        return deliverDao.selectList(null);
    }
}