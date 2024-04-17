package team.CowsAndHorses.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Address {
    private Integer id;
    private Integer userId;
    private String receiverName;
    private String receiverPhone;
    private String campus;
    private String roomAddress;
    private Integer isDefault;
}
