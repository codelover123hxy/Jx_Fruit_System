package team.CowsAndHorses.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartGoods {
    private Integer id;
    private Integer userId;
    private Integer skuId;
    private Integer goodsId;
    private String scale;
    private Integer num;
    private Integer purchased;
    private Integer selected;
    private Double totalPrice;
    private String goodsName;
    private Double price;
    private String category;
    private String thumbNail;
}