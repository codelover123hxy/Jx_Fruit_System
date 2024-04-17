package team.CowsAndHorses.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhoneDto {
    @ExcelProperty("联系电话")
    private String receiverPhone;
}
