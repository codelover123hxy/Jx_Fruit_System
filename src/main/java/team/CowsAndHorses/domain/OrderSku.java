package team.CowsAndHorses.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("tbl_order_sku")
public class OrderSku {
    private Integer id;
    private Integer orderId;
    private Integer skuId;
    private Integer num;
    private String thumbNail;
    private Integer refundState;
    private Integer isCommented;
}
