package team.CowsAndHorses.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import team.CowsAndHorses.constant.ErrorCode;
import team.CowsAndHorses.domain.User;
import team.CowsAndHorses.dto.AjaxResult;
import team.CowsAndHorses.exception.AppException;
import team.CowsAndHorses.service.LoginService;
import team.CowsAndHorses.util.JwtUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api")
public class LoginController {
    final LoginService loginService;

    @Value("${wechat.appid}")
    private String appid;

    @Value("${wechat.secret}")
    private String secret;


    @PostMapping("/login/{role}")
    @ResponseBody
    public Object loginByPwd(@RequestBody Map<String,Object> userInfo, @PathVariable Integer role){
        String username = (String)userInfo.get("username");
        String password = (String)userInfo.get("password");
        if (username.isEmpty() || password.isEmpty()){
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User res = null;
        if (role == 1 || role == 2) {
            res = loginService.login(username, password, role);
        }
        else {
            return AjaxResult.FAIL("角色选择错误", null);
        }

        if (res == null) {
            throw new AppException(ErrorCode.PASSWORD_OR_STUDENT_ID_ERROR);
        }
        userInfo.put("userId", res.getId());
        String userId = UUID.randomUUID().toString();
        String token = JwtUtil.sign(userId, userInfo);
        Map<String,Object> response = new HashMap<>();
        response.put("userInfo",res);
        response.put("token",token);
        return AjaxResult.SUCCESS("登录成功", response);
    }

    @PostMapping("/login/wechat")
    @ResponseBody
    public Object loginByWechat(@RequestBody Map<String, Object> loginInfo){
        String code = (String) loginInfo.get("code");
        HttpClient httpClient = new HttpClient();
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appid
                + "&secret=" + secret + "&js_code=" + code
                + "&grant_type=authorization_code";
        GetMethod getMethod = new GetMethod(url);
        getMethod.addRequestHeader("accept", "*/*");
        //设置Content-Type，此处根据实际情况确定
        getMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        String result = "";
        try {
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode == 200) {
                result = getMethod.getResponseBodyAsString();
                System.out.println(result);
                JSONObject json = (JSONObject) JSON.parse(result);
                String sessionKey = (String) json.get("session_key");
                String openId = (String) json.get("openid");
                if (openId == null) {
                    return AjaxResult.FAIL("无法获取openId", null);
                }
                User res = loginService.loginByWechat(openId);
                if (res != null) {
                    Map<String, Object> userInfo = new HashMap<String, Object>();
                    userInfo.put("userId", res.getId());
                    String userId = UUID.randomUUID().toString();
                    String token = JwtUtil.sign(userId, userInfo);
                    Map<String,Object> response = new HashMap<String, Object>();
                    response.put("userInfo",res);
                    response.put("token",token);
                    response.put("openId", openId);
                    loginService.addBrowseNum(res.getId());
                    return AjaxResult.SUCCESS("登录成功", response);
                } else {
                    return AjaxResult.FAIL("登录失败", null);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return AjaxResult.FAIL("登录失败", null);
    }

    @PostMapping("/register/wechat")
    @ResponseBody
    public Object registerByWechat (@RequestBody Map<String, Object> registerInfo) {
        String code = (String) registerInfo.get("code");
        System.out.println(code);
        HttpClient httpClient = new HttpClient();
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appid
                + "&secret=" + secret + "&js_code=" + code
                + "&grant_type=authorization_code";
        System.out.println("register   " + url);
        GetMethod getMethod = new GetMethod(url);
        getMethod.addRequestHeader("accept", "*/*");
        //设置Content-Type，此处根据实际情况确定
        getMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        String result = "";
        try {
            int statusCode = httpClient.executeMethod(getMethod);
            if (statusCode == 200) {
                result = getMethod.getResponseBodyAsString();
                JSONObject json = (JSONObject) JSON.parse(result);
                String sessionKey = (String) json.get("session_key");
                String openId = (String) json.get("openid");
                System.out.println("openId: " + openId);
                if (openId == null) {
                    return AjaxResult.FAIL("无法获取openId", null);
                }
                User res = loginService.registerByWechat(openId);
                if (res != null) {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("userId", res.getId());
                    String userId = UUID.randomUUID().toString();
                    String token = JwtUtil.sign(userId, userInfo);
                    Map<String,Object> response = new HashMap<>();
                    response.put("userInfo",res);
                    response.put("token",token);
                    return AjaxResult.SUCCESS("新用户一键注册成功", response);
                } else {
                    return AjaxResult.FAIL("注册失败", null);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return AjaxResult.FAIL("注册失败", null);
    }

    @GetMapping("/logout")
    @ResponseBody
    public Object logout(){
        return AjaxResult.SUCCESS("登出成功");
    }
}
