package team.CowsAndHorses.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class PageQueryDto {
    private Integer pageNum;
    private Integer pageSize;
}
