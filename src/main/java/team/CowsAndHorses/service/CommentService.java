package team.CowsAndHorses.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import team.CowsAndHorses.domain.Comment;
import team.CowsAndHorses.dto.CommentResultDto;

@Transactional
public interface CommentService extends IService<Comment> {
    Integer addComment(Comment commentInfo, Integer orderId);
    IPage<CommentResultDto> getCommentsByGoodsId(Integer goodsId,Integer pageNum, Integer pageSize);
    IPage<CommentResultDto> getSelfComments(Integer userId, Integer pageNum, Integer pageSize);
    Integer deleteSelfComment(Integer userId,Integer commentId);
}