package team.CowsAndHorses.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public class ParseUtil {
    public static Integer parseInteger(Map<String,Object> map,String key){
        return Integer.parseInt((String) map.get(key));
    }

    public static Integer parseToken(HttpServletRequest request){
        String token = request.getHeader("token");
        Map<String, Object> info = JwtUtil.getInfo(token);
        Integer userId = null;
        if (null != info)
            userId = (Integer) info.get("userId");
        return userId;
    }
}
