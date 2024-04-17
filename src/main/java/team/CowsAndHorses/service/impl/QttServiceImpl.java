package team.CowsAndHorses.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import team.CowsAndHorses.dao.QttDao;
import team.CowsAndHorses.dto.QttDto;
import team.CowsAndHorses.service.QttService;
@RequiredArgsConstructor
@CacheConfig(cacheNames = "ExpireOneMin")
@Service
public class QttServiceImpl extends ServiceImpl<QttDao, QttDto> implements QttService {
    final QttDao qttDao;
}
