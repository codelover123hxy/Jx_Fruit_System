package team.CowsAndHorses.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import team.CowsAndHorses.domain.Refund;
@Transactional
public interface RefundService extends IService<Refund> {
}