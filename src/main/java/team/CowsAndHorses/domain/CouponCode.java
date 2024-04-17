package team.CowsAndHorses.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponCode {
    private Integer id;
    private String codeNo;
    private Integer userId;
    private Integer verified;
    private Integer orderSkuId;
}
