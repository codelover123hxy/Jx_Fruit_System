package team.CowsAndHorses.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("tbl_sku")
public class Sku {
    private Integer id;
    private String scale;
    private Double price;
    private Integer goodsId;
    private Integer totalInventory;
    private Integer nowInventory;
    private Integer soldAmount;
    private Integer isDeleted;
    private String attribute;
    @TableField(exist = false)
    private Integer soldOut;
}