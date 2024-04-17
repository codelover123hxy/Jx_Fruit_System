package team.CowsAndHorses.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.CowsAndHorses.dao.OrderAddressDao;
import team.CowsAndHorses.domain.Order;
import team.CowsAndHorses.dto.*;
import team.CowsAndHorses.service.OrderService;
import team.CowsAndHorses.service.QttService;
import team.CowsAndHorses.util.PageUtil;
import team.CowsAndHorses.util.SmsUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api")
public class SmsController {

    final OrderService orderService;

    final QttService qttService;

    String[] choiceList = {"朝晖", "屏峰", "莫干山"};
    String[] mgsDeliver = {"19957126823", "18874465700", "18868435336", "18874465700", "18874465700", "18868435336", "19957126823"};
//
//    @GetMapping("/sms/unSend/query")
//    public Object getUnSendList() {
//
//
//
//    }


    @GetMapping("/sms/query")
    @ResponseBody
    public Object querySms(@RequestParam String date, PageQueryDto pageQueryDto,
                           @RequestParam(required = false) Integer status,
                           @RequestParam(required = false) String name,
                           @RequestParam(required = false) Integer choice) throws ParseException {
        Integer pageSize = pageQueryDto.getPageSize();
        Integer pageNum = pageQueryDto.getPageNum();
        Date format = new SimpleDateFormat("yyyyMMdd").parse(date);
        String longDate = new SimpleDateFormat("yyyy-MM-dd").format(format);
        String startTime = longDate + " 00:00:00";
        String endTime = longDate + " 23:59:59";
        System.out.println(startTime);
        System.out.println(endTime);

        List<String> phoneNumList = qttService.list(new QueryWrapper<QttDto>().between("create_time", startTime, endTime))
                .stream().map(QttDto::getReceiverPhone).collect(Collectors.toList());
        phoneNumList = phoneNumList.stream().distinct().collect(Collectors.toList());
//        System.out.println(phoneNumList);
        List<SmsResultDto> resultList = new ArrayList<>();
        phoneNumList.forEach(phone -> {
            try {
                resultList.addAll(Objects.requireNonNull(SmsUtil.querySmsCondition(date, phone)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
//        System.out.println("resultList");
//        System.out.println(resultList);
        List<SmsResultDto> result = resultList.stream().distinct().
                sorted(Comparator.comparing(SmsResultDto::getSendDate).reversed()).collect(Collectors.toList());
        if (status != 0) {
            result = result.stream().filter(item -> item.getSendStatus() == Integer.toUnsignedLong(status))
                    .collect(Collectors.toList());
        }
        if (null != name) {
            result = result.stream().filter(item -> item.getContent().contains(name)).collect(Collectors.toList());
        }

        List<QttDto> qttList = smsList;

        if (choice != null) {
            if (choice == 0) {
                result = result.stream().filter(qtt -> qtt.getContent().contains("梦溪村") || qtt.getContent().contains("尚德园")).collect(Collectors.toList());
                qttList = qttList.stream().filter(qtt -> qtt.getDormitory().contains("梦溪村")
                                || qtt.getDormitory().contains("尚德园"))
                        .collect(Collectors.toList());
            } else if (choice == 1) {
                result = result.stream().filter(qtt -> qtt.getContent().contains("家和东苑") || qtt.getContent().contains("家和西苑")).collect(Collectors.toList());
                qttList = qttList.stream().filter(qtt -> qtt.getDormitory().contains("家和东苑")
                                || qtt.getDormitory().contains("家和西苑"))
                        .collect(Collectors.toList());
            } else if (choice == 2) {
                result = result.stream().filter(qtt -> qtt.getContent().contains("德馨苑")).collect(Collectors.toList());
                qttList = qttList.stream().filter(qtt -> qtt.getDormitory().contains("德馨苑"))
                        .collect(Collectors.toList());
            }
        }

//        System.out.println("result");
//        System.out.println(result);
        List<String> dormitoryList = qttList.stream().map(qtt-> qtt.getDormitory() + qtt.getDormitoryNo()).distinct().collect(Collectors.toList());
        StringBuilder contents = new StringBuilder();
        result.forEach(item -> contents.append(item.getContent()));
        System.out.println(result);
        List<String> unSendList = dormitoryList.stream().filter(dormitory -> !contents.toString().contains(dormitory)).collect(Collectors.toList());
        IPage<SmsResultDto> ipage = PageUtil.List2IPage(result, pageNum, pageSize);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", ipage);
        resultMap.put("unSendList", unSendList);
        return AjaxResult.SUCCESS(resultMap);
    }
    static List<QttDto> smsList = new ArrayList<>();

    @GetMapping("/sms/import/query")
    @ResponseBody
    public Object queryFileExist() throws Exception {
        if (smsList == null) {
            return AjaxResult.FAIL(false);
        } else {
            return AjaxResult.SUCCESS(true);
        }
    }

    @GetMapping("/sms/clear")
    @ResponseBody
    public Object clearFile() throws Exception {
        smsList = null;
        return AjaxResult.SUCCESS("已清空", null);
    }

    @PostMapping("/sms/import/qtt")
    @ResponseBody
    public Object sendSmsBatchByQttExcel(@RequestBody MultipartFile file) throws IOException {
        System.out.println(file);
        System.out.println(file.getOriginalFilename());
        String originalName = file.getOriginalFilename();
        List<QttDto> qttList = EasyExcel.read(file.getInputStream())
                .head(QttDto.class)
                .sheet("Sheet1")
                .doReadSync();
        qttList.forEach(item -> {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            item.setCreateTime(sdf.format(date));
        });
        qttService.saveBatch(qttList);
        smsList = qttList;
        System.out.println(smsList);
        return AjaxResult.SUCCESS("上传成功", null);
    }

    @GetMapping("/week/query")
    @ResponseBody
    public Object getDayOfWeek() {
        String[] mgsDeliver = {"18868435336", "18874465700", "18868435336", "18874465700", "18874465700", "18868435336", "18874465700"};
        Calendar calendar = Calendar.getInstance();
        // 获取当前日期所在的星期数（1表示周日，2表示周一...）
        calendar.set(Calendar.DAY_OF_WEEK, 7);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String deliver = mgsDeliver[dayOfWeek-1];
        System.out.println(mgsDeliver[dayOfWeek-1]);
        return AjaxResult.SUCCESS(deliver);
    }

    final OrderAddressDao orderAddressDao;

    @PostMapping("/sms/send/batch")
    public Object sendSmsBatch(@RequestBody Map<String, List<Integer>> map) throws Exception {
        List<OrderAddress> orders = orderAddressDao.selectBatchIds(map.get("ids"));
        List<OrderAddress> distinctList = orders.stream()
                .collect(Collectors.groupingBy(OrderAddress::getReceiverPhone))
                .entrySet().stream().filter(e -> e.getValue().size() == 1)
                .flatMap(e -> e.getValue().stream()).collect(Collectors.toList());
        List<OrderAddress> repeatList = orders.stream()
                .collect(Collectors.groupingBy(OrderAddress::getReceiverPhone))
                .entrySet().stream().filter(e -> e.getValue().size() > 1)
                .flatMap(e -> e.getValue().stream()).collect(Collectors.toList());

        List<String> phoneNumberList = distinctList.stream()
                .map(OrderAddress::getReceiverPhone).collect(Collectors.toList());

        if (!distinctList.isEmpty()) {
            List<SmsTemplateDto> templateParamList = distinctList.stream().map(item ->
                    {
                        SmsTemplateDto sms = new SmsTemplateDto();
                        sms.setName(item.getReceiverName());
                        sms.setCampus(item.getCampus());
                        sms.setDeliverPhone(item.getDeliverPhone());
                        sms.setOrderNo(item.getOrderId().toString());
                        sms.setRoomAddress(item.getRoomAddress());
                        return sms;
                    }
            ).collect(Collectors.toList());
            String phoneNumberJson = JSONArray.toJSONString(phoneNumberList);
            String templateParamJson = JSONArray.toJSONString(templateParamList);
            SmsUtil.sendByQttExcel(phoneNumberJson, templateParamJson);
        }
        /*
         * 处理重复的数据
         **/
        repeatList.forEach(item -> {
            String phoneNum = item.getReceiverPhone();
            String name = item.getReceiverName();
            String orderNo = item.getOrderId().toString();
            String roomAddress = item.getRoomAddress();
            String campus = item.getCampus();
            String deliverPhone = item.getDeliverPhone();
            SmsTemplateDto smsTemplateDto = new SmsTemplateDto(name, orderNo, roomAddress, campus, deliverPhone);
            String templateParam = JSONObject.toJSONString(smsTemplateDto);
            System.out.println(templateParam);
            try {
                SmsUtil.sendBySingle(phoneNum, templateParam);
                System.out.println("发送一条短信");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return AjaxResult.SUCCESS("发送完毕");
    }


    @PostMapping("/sms/send/batch/qtt/{choice}")
    @ResponseBody
    public Object sendSmsBatchByQttExcel(@PathVariable Integer choice,
                                         @RequestParam String dormitoryNo,
                                         @RequestParam String deliverPhone
    ) {
        try {
            if (smsList.isEmpty() || smsList == null) {
                return AjaxResult.FAIL("请上传快团团模版", null);
            }
            List<QttDto> qttList = smsList;
//            Calendar calendar = Calendar.getInstance();
//            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
//            System.out.println(Calendar.DAY_OF_MONTH);
//            System.out.println(choice);
            if (choice == 0) {
                qttList = qttList.stream().filter(qtt -> qtt.getDormitory().contains("梦溪村")
                        || qtt.getDormitory().contains("尚德园"))
                        .collect(Collectors.toList());
            } else if (choice == 1) {
                qttList = qttList.stream().filter(qtt -> qtt.getDormitory().contains("家和东苑")
                                || qtt.getDormitory().contains("家和西苑"))
                        .collect(Collectors.toList());
            } else {
                qttList = qttList.stream().filter(qtt -> qtt.getDormitory().contains("德馨苑"))
                        .collect(Collectors.toList());
            }

            if (dormitoryNo != null) {
                qttList = qttList.stream().filter(qtt -> (
                            (choice == 2) ?(qtt.getDormitory() + qtt.getDormitoryNo()).contains(dormitoryNo):
                            (qtt.getDormitory() + qtt.getDormitoryNo()).equals(dormitoryNo)))
                            .collect(Collectors.toList());
            }

            if (qttList.isEmpty()) {
                return AjaxResult.FAIL("模版中无该校区手机号", null);
            }

            List<QttDto> distinctList = qttList.stream()
                    .collect(Collectors.groupingBy(QttDto::getReceiverPhone))
                    .entrySet().stream().filter(e -> e.getValue().size() == 1)
                    .flatMap(e -> e.getValue().stream()).collect(Collectors.toList());
            List<QttDto> repeatList = qttList.stream()
                    .collect(Collectors.groupingBy(QttDto::getReceiverPhone))
                    .entrySet().stream().filter(e -> e.getValue().size() > 1)
                    .flatMap(e -> e.getValue().stream()).collect(Collectors.toList());
            System.out.println(distinctList);
            System.out.println(repeatList);
            /*
             * 处理不重复的数据
            **/
            String campus = choiceList[choice];
            List<String> phoneNumberList = distinctList.stream()
                    .map(QttDto::getReceiverPhone).collect(Collectors.toList());
            if (!distinctList.isEmpty()) {
                List<SmsTemplateDto> templateParamList = distinctList.stream().map(qtt ->
                        {
                            SmsTemplateDto sms = new SmsTemplateDto();
                            sms.setName(qtt.getReceiverName());
                            sms.setCampus(campus);
                            sms.setDeliverPhone(deliverPhone);
                            sms.setOrderNo(qtt.getOrderNo());
                            sms.setRoomAddress(qtt.getDormitory() + qtt.getDormitoryNo());
                            return sms;
                        }
            ).collect(Collectors.toList());
                String phoneNumberJson = JSONArray.toJSONString(phoneNumberList);
                String templateParamJson = JSONArray.toJSONString(templateParamList);
                SmsUtil.sendByQttExcel(phoneNumberJson, templateParamJson);
            }
            /*
             * 处理重复的数据
             **/
            repeatList.forEach(qtt -> {
                String phoneNum = qtt.getReceiverPhone();
                String name = qtt.getReceiverName();
                String orderNo = qtt.getOrderNo();
                String roomAddress = qtt.getDormitory() + qtt.getDormitoryNo();
                SmsTemplateDto smsTemplateDto = new SmsTemplateDto(name, orderNo, roomAddress, campus, deliverPhone);
                String templateParam = JSONObject.toJSONString(smsTemplateDto);
                System.out.println(templateParam);
                try {
                    SmsUtil.sendBySingle(phoneNum, templateParam);
                    System.out.println("发送一条短信");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            return AjaxResult.SUCCESS();
        } catch(Exception e) {
            e.printStackTrace();
            return AjaxResult.FAIL("发送失败", null);
        }
    }

    @PostMapping("/sms/send/batch/excel/{type}")
    @ResponseBody
    public Object sendSmsBatchByExcel(@RequestBody MultipartFile file, @PathVariable Integer type) {
        try {
            String originalName = file.getOriginalFilename();
            System.out.println(originalName);
            List<SmsDto> list = EasyExcel.read(file.getInputStream())
                    .head(SmsDto.class)
                    .sheet("Sheet1")
                    .doReadSync();
            System.out.println(list);
            List<String> phoneNumList = new ArrayList<>();
            List<Map<String,String>> dataList = new ArrayList<>();
            list.forEach(item -> {
                phoneNumList.add(item.getPhone());
                Map<String, String> map = new HashMap<>();
                map.put("address", item.getAddress());
                map.put("name", item.getName());
                dataList.add(map);
            });
            JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(dataList));
            JSONArray jsonArray1 = JSONArray.parseArray(JSONArray.toJSONString(phoneNumList));
            String dataStringList = jsonArray.toJSONString();
            String phoneStringList = jsonArray1.toJSONString();
            SmsUtil.sendByImportExcel(type, phoneStringList, dataStringList);
            return AjaxResult.SUCCESS("发送成功", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AjaxResult.SUCCESS();
    }
}