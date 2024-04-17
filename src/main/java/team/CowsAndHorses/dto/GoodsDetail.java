package team.CowsAndHorses.dto;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.*;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsDetail {
    @ExcelProperty("编号")
    private Integer id;
    @ExcelProperty("商品名")
    private String goodsName;
    @ExcelProperty("缩略图")
    private String thumbNail;
    @ExcelProperty("分类")
    private String category;
    @ExcelProperty("库存")
    private Integer inventory;
    @ExcelProperty("时间")
    private String submitTime;
    @ExcelProperty("折扣")
    private Float discount;
    @ExcelProperty("描述")
    private String description;
    @ExcelProperty("生产时间")
    private String productTime;
    @ExcelProperty("产地")
    private String productRegion;
    @ExcelProperty("规格")
    private String scale;
    @ExcelProperty("价格")
    private float price;
    @ExcelProperty("备注")
    private String notes;
    @ExcelIgnore
    private Integer skuId;
}

