package team.CowsAndHorses.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ApifoxModel
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MembershipLevel {
    /**
     * 每月可领消费券
     */
    private String availableCoupon;
    /**
     * 生日礼
     */
    private String birthdayGift;
    /**
     * 到下一等级还需消费量
     */
    private String diffToNextLevel;
    /**
     * 折扣
     */
    private String discount;
    private long id;
    /**
     * 级别，0~4为白银、黄金、铂金、钻石、星耀
     */
    private long vipLevel;
    /**
     * 等级名称
     */
    private String name;
    /**
     * 需求
     */
    private String requirement;
    /**
     * 待遇
     */
    private String treatment;
}
