// This file is auto-generated, don't edit it. Thanks.
package team.CowsAndHorses.util;
import com.alibaba.fastjson.JSONArray;
import com.aliyun.dysmsapi20170525.models.QuerySendDetailsResponse;
import com.aliyun.dysmsapi20170525.models.SendBatchSmsResponse;
import com.aliyun.tea.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import team.CowsAndHorses.constant.TemplateConstant;
import team.CowsAndHorses.dto.PageQueryDto;
import team.CowsAndHorses.dto.SmsResultDto;

import java.util.ArrayList;
import java.util.List;
@Component
public class SmsUtil {
    /**
     * 使用AK&SK初始化账号Client
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */

    public static com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    /**
     * 使用STS鉴权方式初始化账号Client，推荐此方式。
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @param securityToken
     * @return Client
     * @throws Exception
     */

    public static com.aliyun.dysmsapi20170525.Client createClientWithSTS(String accessKeyId, String accessKeySecret, String securityToken) throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret)
                // 必填，您的 Security Token
                .setSecurityToken(securityToken)
                // 必填，表明使用 STS 方式
                .setType("sts");
        // Endpoint 请参考 https://api.aliyun.com/product/Dysmsapi
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    /**
     * @param accessKeyId
     * @param accessKeySecret
     * 从yml中获取值
     */
    private static String accessKeyId;
    private static String accessKeySecret;

    @Value("${sms.accessKeyId}")
    public void setAccessKeyId(String accessKeyId) {
        SmsUtil.accessKeyId = accessKeyId;
    }

    @Value("${sms.accessKeySecret}")
    private void setAccessKeySecret(String accessKeySecret) {
        SmsUtil.accessKeySecret = accessKeySecret;
    }

    public static void send(String phoneNum) throws Exception {
        // 请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID 和 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
        // 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html
        com.aliyun.dysmsapi20170525.Client client = SmsUtil.createClient(accessKeyId, accessKeySecret);
        com.aliyun.dysmsapi20170525.models.SendBatchSmsRequest sendBatchSmsRequest = new com.aliyun.dysmsapi20170525.models.SendBatchSmsRequest()
                .setPhoneNumberJson("[\"" + phoneNum + "\"]")
                .setSignNameJson("[\"践行鲜生\"]")
                .setTemplateCode("SMS_463895048");
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        com.aliyun.dysmsapi20170525.models.SendBatchSmsResponse resp = client.sendBatchSmsWithOptions(sendBatchSmsRequest, runtime);
    }


    public static void sendBySingle(String phoneNum, String templateParam) throws Exception {
        com.aliyun.dysmsapi20170525.Client client = SmsUtil.createClient(accessKeyId, accessKeySecret);
        com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                .setPhoneNumbers(phoneNum)
                .setSignName("践行鲜生")
                .setTemplateCode("SMS_464260289")
                .setTemplateParam(templateParam);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            client.sendSmsWithOptions(sendSmsRequest, runtime);
        } catch (TeaException error) {
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
    }
    public static void sendByQttExcel(String phoneNumberJson, String templateParamJson) throws Exception {
        System.out.println(accessKeyId);
        com.aliyun.dysmsapi20170525.Client client = SmsUtil.createClient(accessKeyId, accessKeySecret);
        List<String> templateJsonList = new ArrayList<>();
        int size = JSONArray.parseArray(phoneNumberJson).size();
        for (int i = 0; i < size; i++) {
            templateJsonList.add("践行鲜生");
        }
        String signNameJson = JSONArray.parseArray(JSONArray.toJSONString(templateJsonList)).toString();
        System.out.println(phoneNumberJson);
        System.out.println(templateParamJson);
        System.out.println(signNameJson);
        com.aliyun.dysmsapi20170525.models.SendBatchSmsRequest sendBatchSmsRequest = new com.aliyun.dysmsapi20170525.models.SendBatchSmsRequest()
                .setTemplateCode("SMS_464260289")
                .setTemplateParamJson(templateParamJson)
                .setSignNameJson(signNameJson)
                .setPhoneNumberJson(phoneNumberJson);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            SendBatchSmsResponse response = client.sendBatchSmsWithOptions(sendBatchSmsRequest, runtime);
            System.out.println(response.getBody().getMessage());
        } catch (TeaException error) {
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
    }

    public static List<SmsResultDto> querySmsCondition(String date, String phoneNum) throws Exception {
        com.aliyun.dysmsapi20170525.Client client = SmsUtil.createClient(accessKeyId, accessKeySecret);
        com.aliyun.dysmsapi20170525.models.QuerySendDetailsRequest querySendDetailsRequest = new com.aliyun.dysmsapi20170525.models.QuerySendDetailsRequest()
                .setSendDate(date)
                .setPhoneNumber(phoneNum)
                .setPageSize(1000000L)
                .setCurrentPage(1L);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            QuerySendDetailsResponse response = client.querySendDetailsWithOptions(querySendDetailsRequest, runtime);
            List<SmsResultDto> result = new ArrayList<>();
            response.getBody().getSmsSendDetailDTOs().getSmsSendDetailDTO().forEach(item -> {
                result.add(new SmsResultDto(item));
            });
            return result;
        } catch (TeaException error) {
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
        return null;
    }

    public static void sendByImportExcel(Integer type, String phoneNumberJson, String templateParamJson) throws Exception {
        // 请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID 和 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
        // 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html
        System.out.println(phoneNumberJson);
        System.out.println(templateParamJson);
        com.aliyun.dysmsapi20170525.Client client = SmsUtil.createClient(accessKeyId, accessKeySecret);
        com.aliyun.dysmsapi20170525.models.SendBatchSmsRequest sendBatchSmsRequest = new com.aliyun.dysmsapi20170525.models.SendBatchSmsRequest();
        List<String> templateJsonList = new ArrayList<>();
        JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(templateJsonList));
        String signNameJson = jsonArray.toJSONString();
        int size = JSONArray.parseArray(phoneNumberJson).size();
        for (int i = 0; i < size; i++) {
            templateJsonList.add("践行鲜生");
        }
        if (type == 1) {
            sendBatchSmsRequest
                    .setPhoneNumberJson(phoneNumberJson)
                    .setSignNameJson(signNameJson)
                    .setTemplateCode(TemplateConstant.PINGFENG.getTemplateCode());
        } else if (type == 2) {
            sendBatchSmsRequest
                    .setPhoneNumberJson(phoneNumberJson)
                    .setSignNameJson(signNameJson)
                    .setTemplateCode(TemplateConstant.ZHAOHUI.getTemplateCode())
                    .setTemplateParamJson(templateParamJson);
        }
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            client.sendBatchSmsWithOptions(sendBatchSmsRequest, runtime);
        } catch (TeaException error) {
            System.out.println(error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            System.out.println(error);
            // 如有需要，请打印 error
            com.aliyun.teautil.Common.assertAsString(error.message);
        }
    }
}