package team.CowsAndHorses.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("tbl_sms")
public class QttDto {
    @ExcelProperty("跟团号")
    private String orderNo;
    @ExcelProperty("收货人")
    private String receiverName;
    @ExcelProperty("寝室楼名称")
    private String dormitory;
    @ExcelProperty("寝室楼号")
    private String dormitoryNo;
    @ExcelProperty("联系电话")
    private String receiverPhone;
    @ExcelIgnore
    private String createTime;
    @ExcelIgnore
    private String smsStatus;
}
