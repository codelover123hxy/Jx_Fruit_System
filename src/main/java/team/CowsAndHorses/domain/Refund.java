package team.CowsAndHorses.domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Refund {
    private Integer id;
    private Integer orderId;
    private String outRefundNo;
    private Double amount;
    private String outTradeNo;
    private Integer state;
    private String reason;
    private String imgUrl;
    private String refundTime;
}