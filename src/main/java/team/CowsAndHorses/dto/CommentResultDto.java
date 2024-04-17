package team.CowsAndHorses.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import team.CowsAndHorses.domain.Comment;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResultDto {
    private Integer commentId;
    private String content;
    private String publishTime;
    private Double score;
    private Integer publisherId;
    private Integer goodsId;
    private Integer isShow;
    private String avatarUrl;
    private String publisher;
    private List<String> images;
    public CommentResultDto(Comment comment) {
        this.commentId = comment.getCommentId();
        this.content = comment.getContent();
        this.publishTime = comment.getPublishTime();
        this.score = comment.getScore();
        this.publisherId = comment.getPublisherId();
        this.goodsId = comment.getGoodsId();
        this.isShow = comment.getIsShow();
    }
}
