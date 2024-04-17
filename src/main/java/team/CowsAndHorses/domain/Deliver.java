package team.CowsAndHorses.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Deliver {
    private Integer id;
    private String name;
    private Integer campusId;
    private String phone;
}
