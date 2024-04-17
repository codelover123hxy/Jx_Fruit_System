package team.CowsAndHorses.dto;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("order_coupon")
public class CouponOrderDto {
    private Integer couponCodeId;
    private String codeNo;
    private Integer userId;
    private Integer verified;
    private String scale;
    private Double price;
    private String goodsName;
    private String description;
}
