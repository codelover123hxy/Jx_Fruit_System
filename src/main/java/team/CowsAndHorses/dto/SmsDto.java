package team.CowsAndHorses.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmsDto {
    @ExcelProperty("phone")
    private String phone;
    @ExcelProperty("name")
    private String name;
    @ExcelProperty("address")
    private String address;
}
