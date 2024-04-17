package team.CowsAndHorses.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;
@Data
@Builder
public class PurchaseParam {
    private Integer addressId;
    private Double payMoney;
    private String notes;
    private List<Map<String, Object>> goods;
    private Double discount;
    private Integer couponId;
}
