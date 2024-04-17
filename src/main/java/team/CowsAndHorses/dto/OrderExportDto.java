package team.CowsAndHorses.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import team.CowsAndHorses.constant.OrderConstant;
import team.CowsAndHorses.constant.OrderState;
import team.CowsAndHorses.domain.Address;
import team.CowsAndHorses.domain.Order;
import team.CowsAndHorses.domain.OrderInfo;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderExportDto {
    @ExcelProperty("提交时间")
    private String submitTime;
    @ExcelProperty("昵称")
    private String nickname;
    @ExcelProperty("订单状态")
    private String orderState;
    @ExcelProperty("订单号")
    private Integer orderId;
    @ExcelProperty("总数")
    private Integer totalNum;
    @ExcelProperty("备注")
    private String notes;
    @ExcelProperty("商品总价")
    private Double payMoney;
    @ExcelProperty("运费")
    private Double postFee;
    @ExcelProperty("订单金额")
    private Double totalMoney;
    @ExcelProperty("商品")
    private String goods;
    @ExcelProperty("订单号")
    private String orderTradeNo;
    @ExcelProperty("收货人")
    private String receiverName;
    @ExcelProperty("联系电话")
    private String receiverPhone;
    @ExcelProperty("校区")
    private String campus;
    @ExcelProperty("寝室楼")
    private String roomAddress;

    public void setOrderInfo(OrderInfo order) {
        this.notes = order.getNotes();
        this.orderId = order.getId();
        this.submitTime = order.getSubmitTime();
        this.orderState = OrderConstant.OrderStateList[order.getOrderState()];
        this.payMoney = order.getPayMoney();
        this.postFee = order.getPostFee();
        this.totalMoney = order.getTotalMoney();
        this.totalNum = order.getTotalNum();
        this.orderTradeNo = order.getOrderTradeNo();
    }

    public void setAddress(Address address) {
        this.receiverName = address.getReceiverName();
        this.receiverPhone = address.getReceiverPhone();
        this.campus = address.getCampus();
        this.roomAddress = address.getRoomAddress();
    }
}
