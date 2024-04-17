package team.CowsAndHorses.service;

import org.springframework.transaction.annotation.Transactional;
import team.CowsAndHorses.dto.PayQueryDto;

import java.util.Map;

@Transactional
public interface WxAppPayService {
    Object refund(Integer refundId) throws Exception;
    Object orderQuery(String transactionId) throws Exception;
    Map<String, String> wxPay(PayQueryDto dto, Integer userId) throws Exception;
    void paySuccess(String orderTradeNo);
}
