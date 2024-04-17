package team.CowsAndHorses.domain;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class User {
    @ExcelProperty("编号")
    private Integer id;
    @ExcelProperty("昵称")
    private String nickName;
    @ExcelProperty("密码")
    private String password;
    @ExcelProperty("手机号码")
    private String phoneNum;
    @ExcelProperty("vip等级")
    private Integer vipLevel;
    @ExcelProperty("会员积分")
    private Integer memberPoints;
    @ExcelProperty("学号")
    private String stuNo;
    @ExcelProperty("校区")
    private String campus;
    @ExcelProperty("用户名")
    private String username;
    @ExcelProperty("头像")
    private String avatarUrl;
    @ExcelProperty("性别")
    private String gender;
    @ExcelProperty("openId")
    private String openId;

    @ExcelIgnore
    private Integer browseNum;
    @ExcelIgnore
    private Integer purchased;
}