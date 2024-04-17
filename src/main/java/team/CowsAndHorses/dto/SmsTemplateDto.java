package team.CowsAndHorses.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsTemplateDto {
    private String name;
    private String orderNo;
    private String roomAddress;
    private String campus;
    private String deliverPhone;
}
