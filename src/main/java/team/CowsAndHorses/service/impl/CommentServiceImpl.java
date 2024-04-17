package team.CowsAndHorses.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import team.CowsAndHorses.dao.*;
import team.CowsAndHorses.domain.Comment;
import team.CowsAndHorses.domain.Order;
import team.CowsAndHorses.domain.OrderSku;
import team.CowsAndHorses.dto.CommentResultDto;
import team.CowsAndHorses.service.CommentService;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@CacheConfig(cacheNames = "ExpireOneMin")
@Service
public class CommentServiceImpl extends ServiceImpl<CommentDao, Comment> implements CommentService {
    final CommentDao commentDao;
    final LoginDao loginDao;
    final ImageDao imageDao;
    final OrderSkuDao orderSkuDao;
    final OrderDao orderDao;
    @Override
    public Integer addComment(Comment commentInfo, Integer orderId){
        commentDao.insert(commentInfo);
        OrderSku orderSku = orderSkuDao.selectOne(
                new QueryWrapper<OrderSku>()
                        .eq("order_id", orderId)
                        .eq("sku_id", commentInfo.getSkuId())
        );
        orderSku.setIsCommented(1);
        orderSkuDao.updateById(orderSku);

        if (null == orderSkuDao.selectList(
                new QueryWrapper<OrderSku>()
                        .eq("order_id", orderId)
                        .eq("is_commented", 0)
        )) {
            Order order = orderDao.selectById(orderId);
            order.setOrderState(5);
            orderDao.updateById(order);
        }
        return 1;
    }
    @Override
    public IPage<CommentResultDto> getCommentsByGoodsId(Integer goodsId, Integer pageNum, Integer pageSize){
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        Page<Comment> page = new Page<>(pageNum,pageSize);
        wrapper.eq("goods_id",goodsId)
                .orderByDesc("publish_time");
        return getCommentResultDtoIPage(wrapper, page);
    }

    @Override
    public IPage<CommentResultDto> getSelfComments(Integer userId, Integer pageNum, Integer pageSize){
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("publisher_id", userId)
                .orderByDesc("publish_time");
        Page<Comment> page = new Page<>(pageNum, pageSize);
        return getCommentResultDtoIPage(wrapper, page);
    }

    @NotNull
    public IPage<CommentResultDto> getCommentResultDtoIPage(QueryWrapper<Comment> wrapper, Page<Comment> page) {
        IPage<CommentResultDto> commentResult = new Page<>();
        IPage<Comment> comments = commentDao.selectPage(page,wrapper);
        commentResult.setCurrent(comments.getCurrent());
        commentResult.setSize(comments.getSize());
        commentResult.setTotal(comments.getTotal());
        commentResult.setPages(comments.getPages());
        List<CommentResultDto> commentList = new ArrayList<>();
        for (Comment comment: comments.getRecords()) {
            CommentResultDto commentDto = new CommentResultDto(comment);
            if (comment.getIsShow() == 1) {
                commentDto.setPublisher(
                        loginDao.selectById(comment.getPublisherId())
                                .getNickName());
            } else {
                commentDto.setPublisher("匿名用户");
            }
            commentDto.setAvatarUrl(
                    loginDao.selectById(comment.getPublisherId())
                            .getAvatarUrl()
            );
            commentDto.setImages(
                    imageDao.getImagesByCommentId(
                            comment.getCommentId()
                    )
            );
            commentList.add(commentDto);
        }
        commentResult.setRecords(commentList);
        return commentResult;
    }


    @Override
    public Integer deleteSelfComment(Integer userId,Integer commentId){
        Integer id = commentDao.selectId(commentId);
        if (id == null || !id.equals(userId)){
            return 0;
        }
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("comment_id",commentId);
        return commentDao.delete(wrapper);
    }
}
