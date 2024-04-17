package team.CowsAndHorses.constant;

import lombok.Data;

@Data
public class CouponType {
    public static final Integer UNUSED = 0;
    public static final Integer EXPIRED = 1;
    public static final Integer USED = 2;
}
