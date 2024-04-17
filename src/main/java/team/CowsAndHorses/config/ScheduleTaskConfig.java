package team.CowsAndHorses.config;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import team.CowsAndHorses.dto.OrderExportDto;
import team.CowsAndHorses.service.GoodsService;
import team.CowsAndHorses.service.OrderService;
import team.CowsAndHorses.util.EmailUtils;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
public class ScheduleTaskConfig {
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.smtpHost}")
    private String smtpHost;
    private GoodsService goodsService;
    private static OrderService orderService;
    @Autowired
    public void init(OrderService orderService) {
        ScheduleTaskConfig.orderService = orderService;
    }
//    @Scheduled(cron = "0 30 14 * * ?")
//    @Scheduled(cron = "0 00 14 * * ?")
    public void export() throws Exception {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDate = new SimpleDateFormat("MMdd").format(date);
        date = DateUtils.setHours(date, 14);
        date = DateUtils.setMinutes(date, 0);
        date = DateUtils.setSeconds(date, 0);
        Date lastDate = DateUtils.addDays(date, -1);
        String endTime = sdf.format(date);
        String startTime = sdf.format(lastDate);
        System.out.println(startTime);
        System.out.println(endTime);
        List<OrderExportDto> resultList = orderService.getOrderList(startTime, endTime, null, null);
        resultList = resultList.stream().filter(orderExportDto ->
                !"待付款".equals(orderExportDto.getOrderState()) && !"已取消".equals(orderExportDto.getOrderState())).collect(Collectors.toList());
        List<OrderExportDto> ZH = resultList.stream().filter(orderExportDto ->
                "朝晖校区".equals(orderExportDto.getCampus())).collect(Collectors.toList());

        List<OrderExportDto> PF = resultList.stream().filter(orderExportDto ->
                "屏峰校区".equals(orderExportDto.getCampus())).collect(Collectors.toList());

        List<OrderExportDto> MGS = resultList.stream().filter(orderExportDto ->
                "莫干山校区".equals(orderExportDto.getCampus())).collect(Collectors.toList());
        String filePath = "/www/imagehost/file/" + nowDate + ".xlsx";
        File file = new File(filePath);
        ExcelWriter excelWriter = EasyExcel.write(file).build();
        WriteSheet writeSheet;
        try {
            writeSheet = EasyExcel.writerSheet("订单总表").head(OrderExportDto.class).build();
            excelWriter.write(resultList, writeSheet);
            writeSheet = EasyExcel.writerSheet("ZH").head(OrderExportDto.class).build();
            excelWriter.write(ZH, writeSheet);
            writeSheet = EasyExcel.writerSheet("PF").head(OrderExportDto.class).build();
            excelWriter.write(PF, writeSheet);
            writeSheet = EasyExcel.writerSheet("MGS").head(OrderExportDto.class).build();
            excelWriter.write(MGS, writeSheet);
            excelWriter.finish();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        List<String> attachments = List.of(filePath); // 附件的路径，多个附件也不怕
        String to = "274000929@qq.com";
        String cc = "2687057458@qq.com";
        String subject = "发送商品信息";
        String body = "";
        EmailUtils emailUtils = EmailUtils.entity(
                smtpHost, username, password, to, cc, subject, body, attachments);
        emailUtils.send();
        boolean flag = file.delete();
    }

//    @Scheduled(cron = "0 30 14 * * ?")
    @Scheduled(cron="0/10 * * * * ?")
    public void excelExportSendEmail() throws Exception {
//        String userName = "2687057458@qq.com";
//        String password = "etstoxkmwkttdeed";
//        String smtpHost = "smtp.qq.com"; // 邮件服务器
//        String to = "2687057458@qq.com";
//        //163邮箱测试
//        // String userName = "gblfy02@163.com"; // 发件人邮箱
//        // String password = "TBFJUSKCUOPEYOYU"; // 发件人密码，其实不一定是邮箱的登录密码，对于QQ邮箱来说是SMTP服务的授权文本
//        // String smtpHost = "smtp.163.com"; // 邮件服务器
//        String to = "2679533643@qq.com"; // 收件人，多个收件人以半角逗号分隔
////        String cc = "2687057458@qq.com"; // 抄送，多个抄送以半角逗号分隔
//        String subject = "孙欧文傻逼"; // 主题
//        String body = "你爹想骂你，打字太麻烦了"; // 正文，可以用html格式的哟
////        List<String> attachments = Arrays.asList("E:\\桌面\\ZZOnline.rar"); // 附件的路径，多个附件也不怕
//        EmailUtils emailUtils = EmailUtils.entity(smtpHost, userName, password, to, null, subject, body, null);
//        emailUtils.send(); // 发送！

//        List<GoodsDetail> goodsList = goodsService.selectGoodsDetail();
//        String filePath = "/www/imagehost/file/商品列表" + System.currentTimeMillis() + ".xlsx";
//        File file = new File(filePath);
//        EasyExcel.write(file, GoodsDetail.class)
//                .sheet(1)
//                .doWrite(goodsList);
//        List<String> attachments = List.of(filePath); // 附件的路径，多个附件也不怕
//        String to = "274000929@qq.com";
//        String subject = "发送商品信息";
//        String body = "";
//        EmailUtils emailUtils = EmailUtils.entity(
//                smtpHost, username, password, to, null, subject, body, attachments);
//        emailUtils.send();
//        boolean flag = file.delete();
//        System.out.println(flag);
    }
}
