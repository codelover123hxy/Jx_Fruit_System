package team.CowsAndHorses.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import team.CowsAndHorses.dao.RefundDao;
import team.CowsAndHorses.domain.Refund;
import team.CowsAndHorses.service.RefundService;

@Service
public class RefundServiceImpl extends ServiceImpl<RefundDao, Refund> implements RefundService{
}
