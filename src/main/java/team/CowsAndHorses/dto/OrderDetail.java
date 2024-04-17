package team.CowsAndHorses.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 对应order_detail视图
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {
    private Integer orderId;
    private String username;
    private String submitTime;
    private Integer orderState;
    private String receiverName;
    private String receiverPhone;
    private String goodsName;
    private Double discount;
    private String scale;
    private Double price;
    private Integer num;
    private String openId;
    private String roomAddress;
    private String deliverPhone;
    private String campus;
    private String deliverName;
    private String orderTradeNo;
}