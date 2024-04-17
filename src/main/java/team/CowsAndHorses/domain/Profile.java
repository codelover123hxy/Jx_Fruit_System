package team.CowsAndHorses.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("tbl_user")
public class Profile {
    private String nickName;
    private String gender;
    private String username;
    private String campus;
    private String avatarUrl;
    private String stuNo;
}
