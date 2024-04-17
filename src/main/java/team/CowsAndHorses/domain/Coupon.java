package team.CowsAndHorses.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {
    private Integer id;
    private String type;
    private Integer price;
    private Integer effectivePrice;
    private String effectiveTime;
    private Integer userId;
    private Integer isUsed;
    @TableField(exist = false)
    private Integer number;
    @TableField(exist = false)
    private Integer expired;
    @TableField(exist = false)
    private String username;
}
