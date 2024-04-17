package team.CowsAndHorses.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import team.CowsAndHorses.config.WxPayAppConfig;
import team.CowsAndHorses.constant.OrderConstant;
import team.CowsAndHorses.constant.OrderState;
import team.CowsAndHorses.dao.OrderSkuDao;
import team.CowsAndHorses.dao.RefundDao;
import team.CowsAndHorses.dao.SkuDao;
import team.CowsAndHorses.dao.SkuInfoDao;
import team.CowsAndHorses.domain.Deliver;
import team.CowsAndHorses.domain.Order;
import team.CowsAndHorses.domain.PurchaseParam;
import team.CowsAndHorses.domain.Refund;
import team.CowsAndHorses.dto.*;
import team.CowsAndHorses.service.*;
import team.CowsAndHorses.util.PageUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;

import static team.CowsAndHorses.util.ParseUtil.parseToken;

/**
 * @author LittleHorse
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/order")
public class OrderController {
    final OrderService orderService;
    final AddressService addressService;
    final UserService userService;
    final RefundService refundService;
    final WxAppPayService wxAppPayService;

    @GetMapping("/query")
    @ResponseBody
    public Object getOrderByState(HttpServletRequest request,
                                  @RequestParam Integer orderState,
                                  PageQueryDto pageQuery) {
        Integer userId = parseToken(request);
        QueryWrapper<OrderAddress> qw = new QueryWrapper<>();
        if (orderState != 0) {
            qw.eq("order_state", orderState);
        }
        qw.eq("user_id", userId);
        IPage<OrderResultDto> res = orderService.getUserOrderListByState(qw, pageQuery);
        return AjaxResult.SUCCESS(res);
    }

    @GetMapping("/export")
    @ResponseBody
    public void excelExport(HttpServletResponse response,
                            @RequestParam(required = false) String startTime,
                            @RequestParam(required = false) String endTime,
                            @RequestParam(required = false) Integer orderState,
                            @RequestParam(required = false) String campus
                            ) throws IOException {
        response.setContentType("application/vnd.vnd.ms-excel");
        //设置编码格式
        response.setCharacterEncoding("utf-8");
        //设置导出文件名称（避免乱码）
        String fileName = URLEncoder.encode("订单列表.xlsx", "UTF-8");
        // 设置响应头
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
        OutputStream outputStream = response.getOutputStream();
        List<OrderExportDto> resultList = orderService.getOrderList(startTime, endTime, orderState, campus);
        try {
            EasyExcel.write(outputStream, OrderExportDto.class)//对应的导出实体类
                    .sheet(1)//导出sheet页名称
                    .doWrite(resultList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            outputStream.flush();
            outputStream.close();
        }
    }

    @PostMapping("/deliver/quick")
    public Object deliverOrderQuick(@RequestBody Map<String, Object> map) {
        orderService.deliver(map);
        return AjaxResult.SUCCESS("已发货", null);
    }

    @PostMapping("/arrived/quick")
    public Object orderArriveQuick(HttpServletRequest request, @RequestBody Map<String, Object> map) {
//        for (Integer id: map.get("ids")) {
//            Order order = orderService.getById(id);
//            order.setOrderState(4);
//            orderService.updateById(order);
//        }
        orderService.arrive(map);
        return AjaxResult.SUCCESS("已送达");
    }

    @GetMapping("/query/{id}")
    @ResponseBody
    public Object getOrderById(@PathVariable Integer id) {
        OrderResultDto orderResultDto = orderService.getOrderById(id);
        return AjaxResult.SUCCESS(orderResultDto);
    }

    @GetMapping("/query/user")
    @ResponseBody
    public Object getOrderByUserId(@RequestParam Integer userId, PageQueryDto pageQuery){
        IPage<Order> res = orderService.getOrderInfoByUserId(userId, pageQuery);
        return AjaxResult.SUCCESS(res);
    }

    @GetMapping("/query/self")
    @ResponseBody
    public Object getSelfOrder(HttpServletRequest request, PageQueryDto pageQuery){
        Integer userId = parseToken(request);
        IPage<Order> res = orderService.getOrderInfoByUserId(userId, pageQuery);
        return AjaxResult.SUCCESS(res);
    }

    @GetMapping("/query/goods")
    @ResponseBody
    public Object getOrderByGoodsId(@RequestParam Integer goodsId,
                                    PageQueryDto pageQuery){
        IPage<Order> res = orderService.getOrderInfoByGoodsId(goodsId, pageQuery);
        return AjaxResult.SUCCESS(res);
    }

    @GetMapping("/admin/query")
    @ResponseBody
    public Object getOrderByCondition(@RequestParam Map<String, Object> condition,
                                      PageQueryDto pageQuery) throws ParseException {
        IPage<OrderResultDto> res = orderService.getOrderInfoByCondition(condition, pageQuery);
        return AjaxResult.SUCCESS(res);
    }

    @PostMapping("/submit")
    @ResponseBody
    public Object submitOrder(HttpServletRequest request, @RequestBody PurchaseParam orderInfo){
        Integer userId = parseToken(request);
        Integer orderId = orderService.submitOrder(userId, orderInfo);
        if (orderId > 0) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", orderId);
            return AjaxResult.SUCCESS("提交成功", data);
        }
        else {
            return AjaxResult.FAIL();
        }
    }

    @PostMapping("/cancel/{id}")
    public Object cancelOrderById(HttpServletRequest request,
                                  @PathVariable Integer id,
                                  @RequestBody Map<String, String> data){
        String cancelReason = data.get("cancelReason");
        Integer num = orderService.withdrawOrder(id, cancelReason);
        if (num > 0) {
            return AjaxResult.SUCCESS("已取消订单", 6);
        }
        else {
            return AjaxResult.FAIL("无法取消订单", null);
        }
    }

    @DeleteMapping("/delete")
    public Object deleteOrder(HttpServletRequest request, @RequestBody List<Integer> ids) {
        Integer userId = parseToken(request);
        Integer num = orderService.deleteOrderByIds(userId, ids);
        if (num > 0)
            return AjaxResult.SUCCESS();
        else
            return AjaxResult.FAIL("无法删除订单",null);
    }

    @DeleteMapping("/delete/{id}")
    public Object deleteOrderById(HttpServletRequest request, @PathVariable Integer id) {
        Integer userId = parseToken(request);
        Integer num = orderService.deleteOrderById(userId, id);
        if (num > 0)
            return AjaxResult.SUCCESS();
        else
            return AjaxResult.FAIL("无法删除订单",null);
    }

    @PutMapping("/receive/{id}")
    public Object receiveOrder(HttpServletRequest request, @PathVariable Integer id) {
        Integer num = orderService.setOrderState(id, 4);
        if (num > 0)
            return AjaxResult.SUCCESS("已收货", 4);
        else
            return AjaxResult.FAIL("无法删除订单",null);
    }

    @PostMapping("/deliver/{deliverId}")
    public Object deliverOrder(HttpServletRequest request,
                               @RequestBody Map<String, List<Integer>> map,
                               @PathVariable Integer deliverId) {
        for (Integer id: map.get("ids")) {
            Order order = orderService.getById(id);
            order.setDeliverId(deliverId);
            if (Objects.equals(order.getOrderState(), OrderState.WAIT_FOR_DELIVERY)) {
                order.setOrderState(OrderState.WAIT_FOR_RECEIPT);
                orderService.updateById(order);
            }
        }
        return AjaxResult.SUCCESS("已发货", null);
    }

    @PostMapping("/deliver")
    public Object deliverOrder(@RequestBody Map<String, List<Integer>> map) {
        for (Integer id: map.get("ids")) {
            Order order = orderService.getById(id);
            if (Objects.equals(order.getOrderState(), OrderState.WAIT_FOR_DELIVERY)) {
                order.setOrderState(OrderState.WAIT_FOR_RECEIPT);
                orderService.updateById(order);
            }
        }
        return AjaxResult.SUCCESS("已发货", null);
    }

    @GetMapping("/deliver/query")
    public Object getDelivers() {
        List<Deliver> delivers = orderService.getDelivers();
        return AjaxResult.SUCCESS(delivers);
    }

    @PostMapping("/refund")
    public Object refund(
         @RequestBody  RefundQueryDto refundQuery
    ) throws Exception {
        Integer orderId = refundQuery.getOrderId();
        List<Integer> skuIds = refundQuery.getSkuIds();
        Double amount = refundQuery.getAmount();
        String reason = refundQuery.getReason();
        orderService.refund(orderId, skuIds, amount, reason);
        return AjaxResult.SUCCESS("申请退款成功", null);
    }

    final RefundDao refundDao;
    final SkuDao skuDao;
    final SkuInfoDao skuInfoDao;

    @GetMapping("/refund/query")
    public Object getRefundList(PageQueryDto pageQueryDto) {
        IPage<RefundDto> refundDtoIPage = new Page<>();
        IPage<Refund> page = new Page<>(pageQueryDto.getPageNum(), pageQueryDto.getPageSize());
        QueryWrapper<Refund> refundQueryWrapper = new QueryWrapper<>();
        refundQueryWrapper.orderByDesc("refund_time");
        IPage<Refund> refunds = refundService.page(page, refundQueryWrapper);
        List<RefundDto> resultList = new ArrayList<>();
        refunds.getRecords().forEach(item -> {
            RefundDto refundDto = new RefundDto();
            refundDto.setRefundInfo(item);
            List<Integer> skuIds = refundDao.getSkuIds(item.getId());
            refundDto.setSkus(
                    skuInfoDao.selectList(
                            new QueryWrapper<SkuInfoDto>().eq("order_id", item.getOrderId())
                                    .in("id", skuIds)
                    )
            );
            Order order = orderService.getOne(
                    new QueryWrapper<Order>()
                            .eq("id", item.getOrderId()
                            )
            );

            refundDto.setOrderInfo(order);
            resultList.add(refundDto);
        });
        refundDtoIPage = PageUtil.pageFormatTransform(refunds, RefundDto.class);
        refundDtoIPage.setRecords(resultList);
        return AjaxResult.SUCCESS(refundDtoIPage);
    }
}