package team.CowsAndHorses.pursebuddy;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import jakarta.annotation.Resource;
import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;
import team.CowsAndHorses.config.ProfileConfig;
import team.CowsAndHorses.controller.PrivilegeController;
import team.CowsAndHorses.domain.Order;
import team.CowsAndHorses.dto.*;
import team.CowsAndHorses.service.CouponService;
import team.CowsAndHorses.service.OrderService;
import team.CowsAndHorses.util.*;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


import java.util.Date;
import java.util.stream.Collectors;

@SpringBootTest
class JxxApplicationTests {
    @Autowired
    private PrivilegeController privilegeController;
    @Autowired
    private CouponService couponService;
    @Autowired
    private ApplicationContext context;

    @Test
    public void test() {
        String env = context.getEnvironment().getActiveProfiles()[0];
        System.out.println(env);
//        PageQueryDto pageQueryDto = new PageQueryDto();
//        pageQueryDto.setPageNum(1);
//        pageQueryDto.setPageSize(10);
//        privilegeController.getCouponList(pageQueryDto);
    }

    @Test
    public void getDate() throws ParseException {
        String today = "2024-02-23";

        String deadline = "14:30:00";


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(today + " " + deadline);
        Date newDate = DateUtil.calculateDate(date, -1);
        System.out.println("date " + sdf.format(date));
        System.out.println("newDate " + sdf.format(newDate));

    }

    @Autowired
    private OrderService orderService;

    @Test
    public void generateQrCode() {
        QrCodeUtil.generateQrCode("");
    }

    @Test
    public void pageTransform() {
        IPage<Order> page = new Page<>();
        page.setPages(1);
        page.setCurrent(1);
        page.setTotal(10);
        IPage<OrderAddress> orderAddressIPage = PageUtil.pageFormatTransform(page, OrderAddress.class);
        System.out.println(page.getPages());
        System.out.println(orderAddressIPage.getPages());
    }

    @Test
    public void importQttExcel () {
        File file = new File("E:\\桌面\\快团团测试.xlsx");
        List<QttDto> list = EasyExcel.read(file)
                .head(QttDto.class)
                .sheet("Sheet1")
                .doReadSync();
        System.out.println(list);
    }

    @Test
    public void parseDate() throws ParseException {
        String str = "20231210";
        Date format1 = new SimpleDateFormat("yyyyMMdd").parse(str);
        String longDate = new SimpleDateFormat("yyyy-MM-dd").format(format1);
        String startTime = longDate + " 00:00:00";
        String endTime = longDate + " 23:59:59";
        System.out.println(startTime);
        System.out.println(endTime);
    }

    @Test
    public void sendSmsPhone() throws Exception {
        File file = new File("E:\\桌面\\快团团测试.xlsx");
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        Integer choice = 2;
        String[] choiceList = {"朝晖", "屏峰", "莫干山"};
        String[] mgsDeliver = {"18874465700", "18868435336", "18874465700", "18874465700", "18868435336", "19957126823", "19957126823"};
        String deliverPhone;
        if (choice == 0) {
            deliverPhone = "15933744600";
        } else if (choice == 1) {
            deliverPhone = "18835791245";
        } else {
            deliverPhone = mgsDeliver[dayOfWeek - 1];
        }
        String campus = choiceList[choice];
        List<QttDto> qttList = EasyExcel.read(file)
                .head(QttDto.class)
                .sheet("Sheet1")
                .doReadSync();
        List<String> phoneNumberList = qttList.stream().map(QttDto::getReceiverPhone).collect(Collectors.toList());
        List<SmsTemplateDto> templateParamList = qttList.stream().map(qtt -> {
            String name = qtt.getReceiverName();
            String orderNo = qtt.getOrderNo();
            String roomAddress = qtt.getDormitory() + qtt.getDormitoryNo();

            return new SmsTemplateDto(name, orderNo, roomAddress, campus, deliverPhone);
        }).collect(Collectors.toList());
        String phoneNumberJson = JSONArray.parseArray(JSONArray.toJSONString(phoneNumberList)).toJSONString();
        String templateParamJson = JSONArray.parseArray(JSONArray.toJSONString(templateParamList)).toJSONString();
        SmsUtil.sendByQttExcel(phoneNumberJson, templateParamJson);
    }
    @Autowired
    private RedisUtil redisUtil;

    @Resource
    RedisTemplate<String,Object> redisTemplate ;

    @Test
    void testRedis() {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
//        System.out.println(jedis.ping());
        String phone = "12345678901";
//        redisTemplate.opsForValue().set("test:code:"+ phone,"123456");
        System.out.println(redisTemplate.opsForValue().get("test:code:"+phone));
    }

    @Test
    public void saveValue() {
//        redisUtil.set("username", "hxy");
//        String username = (String) redisUtil.get("username");
//        System.out.println("username="+ username);
    }

    @Test
    public void querySmsCondition() throws Exception {
        List<SmsResultDto> list = SmsUtil.querySmsCondition("20231209", "13216183189");
        System.out.println(list);
    }
    @Test
    public void export() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date = DateUtils.setHours(date, 14);
        date = DateUtils.setMinutes(date, 0);
        date = DateUtils.setSeconds(date, 0);
        Date lastDate = DateUtils.addDays(date, -1);
//        String endTime = sdf.format(date);
//        String startTime = sdf.format(lastDate);
        String startTime = "2023-12-06 00:00:00";
        String endTime = "2023-12-06 23:59:00";
        System.out.println(startTime);
        System.out.println(endTime);
        List<OrderExportDto> resultList = orderService.getOrderList(startTime, endTime, null, null);

        resultList = resultList.stream().filter(orderExportDto ->
                !orderExportDto.getOrderState().equals("待付款") && !orderExportDto.getOrderState().equals("已取消")).collect(Collectors.toList());

        List<OrderExportDto> ZH = resultList.stream().filter(orderExportDto ->
                orderExportDto.getCampus().equals("朝晖校区")).collect(Collectors.toList());

        List<OrderExportDto> PF = resultList.stream().filter(orderExportDto ->
                orderExportDto.getCampus().equals("屏峰校区")).collect(Collectors.toList());

        List<OrderExportDto> MGS = resultList.stream().filter(orderExportDto ->
                orderExportDto.getCampus().equals("莫干山校区")).collect(Collectors.toList());

        File file = new File("1208.xlsx");
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
    }

    @Test
    public void exportOrderList() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String startTime = "2023-12-03 14:30:00";
        String endTime = "2023-12-04 14:30:00";
        List<OrderExportDto> resultList =  orderService.getOrderList(startTime, endTime, null, "屏峰校区");
        File file = new File("order.xlsx");
        EasyExcel.write(file,OrderExportDto.class)//对应的导出实体类
                .sheet(1)//导出sheet页名称
                .doWrite(resultList);
    }
    public void sendEmail() throws Exception {
        //QQ邮箱测试
//        String userName = "1159521080@qq.com"; // 发件人邮箱
//        String password = "okljaxdwgsqtheei"; // 发件人密码，其实不一定是邮箱的登录密码，对于QQ邮箱来说是SMTP服务的授权文本
        String userName = "2687057458@qq.com";
        String password = "etstoxkmwkttdeed";
        String smtpHost = "smtp.qq.com"; // 邮件服务器
        //163邮箱测试
        // String userName = "gblfy02@163.com"; // 发件人邮箱
        // String password = "TBFJUSKCUOPEYOYU"; // 发件人密码，其实不一定是邮箱的登录密码，对于QQ邮箱来说是SMTP服务的授权文本
        // String smtpHost = "smtp.163.com"; // 邮件服务器
        String to = "2679533643@qq.com"; // 收件人，多个收件人以半角逗号分隔
//        String cc = "2687057458@qq.com"; // 抄送，多个抄送以半角逗号分隔
        String subject = "孙欧文傻逼"; // 主题
        String body = "你爹想骂你，打字太麻烦了"; // 正文，可以用html格式的哟
//        List<String> attachments = Arrays.asList("E:\\桌面\\ZZOnline.rar"); // 附件的路径，多个附件也不怕
        EmailUtils emailUtils = EmailUtils.entity(smtpHost, userName, password, to, null, subject, body, null);
        emailUtils.send(); // 发送！
    }

//    @Test
    public void send() throws Exception {
        String emailString = "ABCDE";
        HttpSession session = new HttpSession() {
            @Override
            public long getCreationTime() {
                return 0;
            }

            @Override
            public String getId() {
                return null;
            }

            @Override
            public long getLastAccessedTime() {
                return 0;
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public void setMaxInactiveInterval(int i) {

            }

            @Override
            public int getMaxInactiveInterval() {
                return 0;
            }

            @Override
            public Object getAttribute(String s) {
                return null;
            }

            @Override
            public Enumeration<String> getAttributeNames() {
                return null;
            }

            @Override
            public void setAttribute(String s, Object o) {

            }

            @Override
            public void removeAttribute(String s) {

            }

            @Override
            public void invalidate() {

            }

            @Override
            public boolean isNew() {
                return false;
            }
        };

        String email = "2687057458@qq.com";

        session.setAttribute("email", email);
        session.setAttribute("emailcode", emailString);
        //创建Properties类用于记录邮箱的一些属性
        Properties props = new Properties();
        //表示SMTP发送邮件，必须进行身份验证
        props.put("mail.smtp.auth", "true");
        //此处填写SMTP服务器
        props.put("mail.smtp.host", "smtp.qq.com");
        //端口号，QQ邮箱端口465或587
        props.put( "mail.smtp.port","587");
        //此处填写，写信人的账号
        props.put( "mail.user", "1159521080@qq.com");
        //此处填写16位STMP口令
        props.put( "mail.password" ,"okljaxdwgsqtheei");
        //构建授权信息，用于进行SMTP进行身份验证
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                //用户名、密码
                String userName = props.getProperty( "mail.user" );
                String password = props.getProperty( "mail.password");
                return new PasswordAuthentication(userName,password);
            }
        };
        //使用环境属性和授权信息，创建邮件会话
        Session mailSession = Session.getInstance(props, authenticator);
        //创建邮件消息
        MimeMessage message = new MimeMessage(mailSession);
        try {
            //设置发件人
            InternetAddress form = new InternetAddress(props.getProperty("mail.user"));
            message.setFrom(form);
            //设置收件人的邮箱
            InternetAddress to = new InternetAddress(email);
            message.setRecipient(MimeMessage.RecipientType.TO, to);
            //设置邮件标题
            message.setSubject("教科学院创新创业管理系统注册验证码");
            //设置邮件内容体
            message.setContent("验证码：" + emailString,"text/html;charset = UTF-8");

            System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
            System.out.println(message);
            //发送邮件
            Transport.send(message);
        } catch (AddressException e) {
            session.setAttribute("messagesigh", "邮箱输入有误");
            e.printStackTrace();
        } catch (MessagingException e) {
            session.setAttribute("messagesigh", "邮箱输入有误");
            e.printStackTrace();
        }
    }

    @Test
    void deliverOrderBatch() throws Exception {
//        System.out.println(Calendar.DAY_OF_WEEK);
////        System.out.println(Calendar.DATE);
//        Calendar calendar = Calendar.getInstance();
//
//        // 获取当前日期所在的星期数（1表示周日，2表示周一...）
//        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
//        System.out.println(dayOfWeek);
    }

    @Test
    void contextLoads() throws Exception {
//        Integer userId = 43;
//        PageQueryDto pageQueryDto = new PageQueryDto();
//        pageQueryDto.setPageNum(1);
//        pageQueryDto.setPageSize(1000000);
//        IPage<OrderResultDto> result = orderService.getOrderByState(userId, 2, pageQueryDto);
//        System.out.println("----------------------------------------------------------");
//        System.out.println(result.getRecords());
//        File file = new File(filePath);
//        EasyExcel.write(file, GoodsDetail.class)
//                .sheet(1)
//                .doWrite(goodsList);
//        List<OrderResultDto> resultDtos =
//        List<GoodsDetail> goodsList = goodsService.selectGoodsDetail();
//        String filePath = "/www/imagehost/file/商品列表" + System.currentTimeMillis() + ".xlsx";
//        File file = new File(filePath);
//        EasyExcel.write(file, GoodsDetail.class)
//                .sheet(1)
//                .doWrite(goodsList);
    }
}
