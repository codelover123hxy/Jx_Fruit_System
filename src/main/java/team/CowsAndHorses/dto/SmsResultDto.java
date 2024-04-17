package team.CowsAndHorses.dto;

import com.aliyun.dysmsapi20170525.models.QuerySendDetailsResponseBody;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsResultDto {
    private Long sendStatus;
    private String receiveDate;
    private String sendDate;
    private String templateCode;
    private String content;
    private String errCode;
    private String outId;
    private String phoneNum;

    public SmsResultDto(QuerySendDetailsResponseBody.QuerySendDetailsResponseBodySmsSendDetailDTOsSmsSendDetailDTO item) {
        this.sendStatus = item.getSendStatus();
        this.receiveDate = item.getReceiveDate();
        this.sendDate = item.getSendDate();
        this.templateCode = item.getTemplateCode();
        this.content = item.getContent();
        this.errCode = item.getErrCode();
        this.outId = item.getOutId();
        this.phoneNum = item.getPhoneNum();
    }
}
