package team.CowsAndHorses.controller;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.CowsAndHorses.constant.ErrorCode;
import team.CowsAndHorses.dao.LoginDao;
import team.CowsAndHorses.domain.*;
import team.CowsAndHorses.dto.AjaxResult;
import team.CowsAndHorses.dto.GoodsDetail;
import team.CowsAndHorses.dto.PageQueryDto;
import team.CowsAndHorses.exception.AppException;
import team.CowsAndHorses.service.LoginService;
import team.CowsAndHorses.service.UserService;
import team.CowsAndHorses.util.JwtUtil;
import team.CowsAndHorses.util.ParseUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author LittleHorse
 * @version 1.0
 */

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api")
public class UserController {
    final UserService userService;
    final LoginService loginService;
    final String dir = System.getProperty("user.dir");

    @Value("${url.image}")
    private String relativePath;

    @GetMapping("/profile")
    @ResponseBody
    public Object getProfileInfo(HttpServletRequest request){
        String token = request.getHeader("token");
        Map<String, Object> info = JwtUtil.getInfo(token);
        Integer userId = null;
        if (null != info) {
            userId = (Integer) info.get("userId");
        }
        Profile profile = userService.getProfileInfo(userId);
        return AjaxResult.SUCCESS(profile);
    }

    @PostMapping("/profile/update")
    @ResponseBody
    public Object updateProfileInfo(HttpServletRequest request, @RequestBody Profile profile){
        String token = request.getHeader("token");
        Map<String, Object> info = JwtUtil.getInfo(token);
        Integer userId = null;
        if (null != info)
            userId = (Integer) info.get("userId");
        Integer num = userService.updateProfileInfo(userId, profile);
        return num > 0 ? AjaxResult.SUCCESS(): AjaxResult.FAIL();
    }

    @PostMapping("/profile/upload")
    @ResponseBody
    public Object uploadProfile(HttpServletRequest request, @RequestBody MultipartFile file){
        Integer userId = ParseUtil.parseToken(request);
        String filePath = "";
        String imgUrl = null;
        String originalName = file.getOriginalFilename();
        try {
            String newName = UUID.randomUUID() + originalName;
            System.out.println(newName);
            filePath = relativePath + newName;
            file.transferTo(new File(filePath));
            //压缩图片 分辨率 效果好的
            imgUrl = "https://image.familystudy.cn/image/jxfruit/" + newName;
            userService.updateProfile(userId, imgUrl);
        }
        catch (IllegalStateException e) {
            e.printStackTrace();
            return AjaxResult.FAIL();
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResult.FAIL();
        }
        Map<String, Object> responseInfo = new HashMap<>();
        responseInfo.put("avatarUrl", imgUrl);
        return AjaxResult.SUCCESS(responseInfo);
    }

    @GetMapping("/user/admin/query")
    @ResponseBody
    public Object getAllUser(PageQueryDto pageQuery,
                             @RequestParam(required = false) String username) {
        return AjaxResult.SUCCESS(userService.getAllUsers(pageQuery));
    }

    @GetMapping("/user/admin/query/condition")
    @ResponseBody
    public Object getUserByCondition(@RequestParam String username,
                                     @RequestParam String phoneNum,
                                     @RequestParam String stuNo) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("username", username)
                .like("phone_num", phoneNum)
                .like("stu_no", stuNo);
        List<User> userList = loginService.list(queryWrapper);
        return AjaxResult.SUCCESS(userList);
    }

    @DeleteMapping("/user/admin/delete")
    @ResponseBody
    public Object deleteUserById(@RequestParam List<Integer> ids) {
        return userService.deleteUserByIds(ids) > 0 ?
                AjaxResult.SUCCESS(): AjaxResult.FAIL();
    }

    @PostMapping("/user/admin/update")
    @ResponseBody
    public Object updateUser(@RequestBody User newUser) {
        return userService.updateUser(newUser) > 0 ?
                AjaxResult.SUCCESS(): AjaxResult.FAIL();
    }

    @PostMapping("/user/admin/add")
    @ResponseBody
    public Object addUser(@RequestBody User user) {
        return userService.addUser(user) > 0 ?
                AjaxResult.SUCCESS(): AjaxResult.FAIL();
    }

    @PostMapping("/user/import")
    @ResponseBody
    public Object excelImport(@RequestBody MultipartFile file) throws IOException {
        List<User> list = EasyExcel.read(file.getInputStream())
                .head(User.class)
                .sheet("Sheet1")
                .doReadSync();
        System.out.println(list);
        return userService.excelImport(list) == 1?
                AjaxResult.SUCCESS("导入成功", null): AjaxResult.FAIL("导入失败", null);
    }

    @GetMapping("/user/export")
    @ResponseBody
    public Object excelExport(HttpServletResponse response) throws IOException{
        response.setContentType("application/vnd.vnd.ms-excel");
        //设置编码格式
        response.setCharacterEncoding("utf-8");
        //设置导出文件名称（避免乱码）
        String fileName = URLEncoder.encode("用户列表.xlsx", "UTF-8");
        // 设置响应头
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
        OutputStream outputStream = response.getOutputStream();
        PageQueryDto pageQuery = new PageQueryDto();
        Integer MAX_SIZE = 1000000;
        pageQuery.setPageSize(MAX_SIZE);
        pageQuery.setPageNum(1);
        List<User> userList = userService.getAllUsers(pageQuery).getRecords();
        try {
            EasyExcel.write(outputStream, User.class)//对应的导出实体类
                    .sheet("Sheet1")//导出sheet页名称
                    .doWrite(userList);
            return AjaxResult.SUCCESS();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            outputStream.flush();
            outputStream.close();
        }
    }
}