package team.CowsAndHorses.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayQueryDto {
    private Integer amount;
    private Integer orderId;
    private Integer userid;
    private String openId;
}
