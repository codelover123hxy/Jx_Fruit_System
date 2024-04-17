package team.CowsAndHorses.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Cart {
    private Integer id;
    private Integer userId;
    private Integer skuId;
    private Integer num;
    private Integer purchased;
    private Integer selected;
    private Double totalPrice;
}
