package team.CowsAndHorses.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class Comment {
    @TableId(type = IdType.AUTO)
    private Integer commentId;
    private String content;
    private String publishTime;
    private Double score;
    private Integer publisherId;
    private Integer goodsId;
    private Integer skuId;
    private Integer isShow;
}
