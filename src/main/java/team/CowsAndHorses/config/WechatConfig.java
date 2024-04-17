package  team.CowsAndHorses.config;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
/**
 * @author Patrick_Star
 * @version 1.0
 */
@Data
@Component
@ConfigurationProperties("wechat")
public class WechatConfig {

    /**
     * 设置微信小程序的appid
     */
    private static String appid;

    /**
     * 设置微信小程序的Secret
     */
    private static String secret;

    public void setAppid(String appid) {
        WechatConfig.appid = appid;
    }

    public void setSecret(String secret) {
        WechatConfig.secret = secret;
    }

    public static String getAppid() {
        return appid;
    }

    public static String getSecret() {
        return secret;
    }

    public static String getAccessToken() throws Exception{
        HttpClient httpClient = new HttpClient();
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appid
                + "&secret=" + secret;
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
                String token = (String) json.get("access_token");
                return token;
            }
            else {
                throw new Exception();
            }
        } catch (Exception e) {
            throw new Exception();
        }
    }
}
