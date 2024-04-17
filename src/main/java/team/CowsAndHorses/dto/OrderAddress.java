package team.CowsAndHorses.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("order_address")
public class OrderAddress {
    @TableId
    private Integer orderId;
    private Integer userId;
    private String orderTradeNo;
    private String username;
    private String openId;
    private String submitTime;
    private Integer orderState;
    private String receiverName;
    private String receiverPhone;
    private String roomAddress;
    private String deliverPhone;
    private String deliverName;
    private String nickName;
    private String campus;
}
