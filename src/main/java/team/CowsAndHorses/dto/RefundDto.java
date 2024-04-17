package team.CowsAndHorses.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import team.CowsAndHorses.domain.Order;
import team.CowsAndHorses.domain.Refund;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundDto {
    private Integer id;
    private Integer orderId;
    private String outRefundNo;
    private Double amount;
    private String outTradeNo;
    private Integer state;
    private String reason;
    private String imgUrl;
    private String submitTime;
    private Double totalMoney;
    private Integer totalNum;
    private List<SkuInfoDto> skus;


    public void setOrderInfo(Order order) {
        this.submitTime = order.getSubmitTime();
        this.totalMoney = order.getTotalMoney();
        this.totalNum = order.getTotalNum();
    }
    public void setRefundInfo(Refund refund) {
        this.id = refund.getId();
        this.orderId = refund.getOrderId();
        this.outRefundNo = refund.getOutRefundNo();
        this.amount = refund.getAmount();
        this.outTradeNo = refund.getOutTradeNo();
        this.state = refund.getState();
        this.reason = refund.getReason();
        this.imgUrl = refund.getImgUrl();
    }
}
