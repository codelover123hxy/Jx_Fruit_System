package team.CowsAndHorses.dto;

import lombok.*;
import team.CowsAndHorses.domain.Comment;
import team.CowsAndHorses.domain.Goods;
import team.CowsAndHorses.domain.GoodsImage;
import team.CowsAndHorses.domain.Sku;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class GoodsDto {
    private Goods goodsInfo;
    private List<GoodsImage> images;
    private List<Sku> skus;
    private Integer commentNum;
    private List<CommentResultDto> comments;
}
