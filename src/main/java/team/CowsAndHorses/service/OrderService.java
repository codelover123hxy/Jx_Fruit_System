package team.CowsAndHorses.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import team.CowsAndHorses.domain.Deliver;
import team.CowsAndHorses.domain.Order;
import team.CowsAndHorses.domain.PurchaseParam;
import team.CowsAndHorses.dto.*;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

@Transactional
public interface OrderService extends IService<Order> {
    JSONArray getShippingList(Integer orderId);
    String getItemDesc(Integer orderId);
    IPage<OrderDetail> getOrderInfo(Integer orderState, PageQueryDto pageQuery);
    IPage<OrderResultDto> getUserOrderListByState(QueryWrapper<OrderAddress> qw, PageQueryDto pageQuery);
    List<OrderExportDto> getOrderList(String startTime, String endTime, Integer orderState, String campus);
    OrderResultDto getOrderById(Integer id);
    OrderResultDto getOrderByOutTradeNo(String outTradeNo);
    OrderDetail getOrderDetailById(Integer id);
    void deliver(Map<String, Object> map);
    void arrive(Map<String, Object> map);
    IPage<Order> getOrderInfoByUserId(Integer userId, PageQueryDto pageQuery);
    IPage<Order> getOrderInfoByGoodsId(Integer goodsId, PageQueryDto pageQuery);
    Integer submitOrder(Integer userId, PurchaseParam orderInfo);
    Integer withdrawOrder(Integer orderId,String cancelReason);
    Integer deleteOrderByIds(Integer userId, List<Integer> ids);
    Integer deleteOrderById(Integer userId, Integer id);
    Integer setOrderState(Integer orderId, Integer targetState);
    IPage<OrderResultDto> getOrderInfoByCondition(Map<String, Object> condition, PageQueryDto pageQuery) throws ParseException;
    Integer refund(Integer orderId, List<Integer> skuIds, Double amount, String reason);
    List<Deliver> getDelivers();
}
