package team.CowsAndHorses.domain;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.*;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Goods {
    @ExcelProperty("编号")
    private Integer id;
    @ExcelProperty("商品名")
    private String goodsName;
    @ExcelProperty("价格")
    private String price;
    @ExcelProperty("缩略图")
    private String thumbNail;
    @ExcelProperty("分类")
    private String category;
    @ExcelProperty("提交时间")
    private String submitTime;
    @ExcelProperty("折扣")
    private Double discount;
    @ExcelProperty("产品描述")
    private String description;
    @ExcelProperty("产品简介")
    private String introduction;
    @ExcelProperty("产品优势")
    private String advantage;
    @ExcelProperty("生产时间")
    private String productTime;
    @ExcelProperty("产地")
    private String productRegion;
    @ExcelProperty("备注")
    private String notes;
    @ExcelIgnore
    private Integer onShelf;
    @TableLogic
    private Integer isDeleted;
    private Integer onSale;
    private Integer inActivity;
    private Integer isCoupon;
}
