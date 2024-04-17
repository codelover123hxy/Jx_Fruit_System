package team.CowsAndHorses.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("tbl_goods_image")
public class GoodsImage {
    private Integer id;
    private String imgUrl;
    private Integer goodsId;
}
