package team.CowsAndHorses.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RefundQueryDto {
    private Integer orderId;
    private List<Integer> skuIds;
    private Double amount;
    private String reason;
}
