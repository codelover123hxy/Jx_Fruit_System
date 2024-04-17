package team.CowsAndHorses.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@TableName("order_info")
public class OrderInfo {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String submitTime;
    private Integer addressId;
    private Integer orderState;
    private String notes;
    private Double payMoney;
    private Double postFee;
    private Double totalMoney;
    private Integer totalNum;
    private Double discount;
    private String cancelReason;
    private String orderTradeNo;
    private Integer deliverId;
    @TableLogic
    private Integer isDeleted;
}