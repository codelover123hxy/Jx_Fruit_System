package team.CowsAndHorses.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import team.CowsAndHorses.domain.Address;
import team.CowsAndHorses.domain.Order;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderResultDto {
    private String submitTime;
    private String nickname;
    private Integer orderState;
    private Integer orderId;
    private Integer userId;
    private Integer totalNum;
    private String notes;
    private Double payMoney;
    private Double postFee;
    private Double totalMoney;
    private List<SkuInfoDto> skus;
    private String orderTradeNo;
    private Address address;

    public void setOrder(Order order) {
        this.notes = order.getNotes();
        this.orderId = order.getId();
        this.submitTime = order.getSubmitTime();
        this.orderState = order.getOrderState();
        this.payMoney = order.getPayMoney();
        this.postFee = order.getPostFee();
        this.totalMoney = order.getTotalMoney();
        this.totalNum = order.getTotalNum();
        this.orderTradeNo = order.getOrderTradeNo();
        this.userId = order.getUserId();
    }
}
