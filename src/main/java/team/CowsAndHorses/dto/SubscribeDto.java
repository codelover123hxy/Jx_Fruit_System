package team.CowsAndHorses.dto;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeDto {
    private String openId;
    private String orderNo;
    private String deliverPhone;
    private String deliverName;
    private String detail;
    private String address;
    private String receiverName;
}
