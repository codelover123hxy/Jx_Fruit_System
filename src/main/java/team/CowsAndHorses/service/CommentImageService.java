package team.CowsAndHorses.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import team.CowsAndHorses.domain.CommentImage;
@Transactional
public interface CommentImageService extends IService<CommentImage> {
}
