package team.CowsAndHorses.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@TableName("sku_info")
public class SkuInfoDto {
    private String scale;
    private Double price;
    private Integer num;
    private Integer orderId;
    private Integer goodsId;
    private String thumbNail;
    private String goodsName;
    private String submitTime;
    private Integer isCommented;
    private Integer refundState;
    private Integer orderSkuId;
    private Integer id;
}
