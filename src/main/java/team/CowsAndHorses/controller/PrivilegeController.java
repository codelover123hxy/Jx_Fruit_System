package team.CowsAndHorses.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.*;
import team.CowsAndHorses.constant.ErrorCode;
import team.CowsAndHorses.dao.CouponOrderDao;
import team.CowsAndHorses.dao.LoginDao;
import team.CowsAndHorses.dao.MembershipLevelDao;
import team.CowsAndHorses.domain.Coupon;
import team.CowsAndHorses.domain.CouponCode;
import team.CowsAndHorses.domain.MembershipLevel;
import team.CowsAndHorses.domain.User;
import team.CowsAndHorses.dto.AjaxResult;
import team.CowsAndHorses.dto.CouponOrderDto;
import team.CowsAndHorses.dto.PageQueryDto;
import team.CowsAndHorses.exception.AppException;
import team.CowsAndHorses.service.*;
import team.CowsAndHorses.util.PageUtil;
import team.CowsAndHorses.util.ParseUtil;
import team.CowsAndHorses.util.QrCodeUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api")
public class PrivilegeController {
    final CouponService couponService;
    final LoginDao loginDao;
    final GoodsService goodsService;
    final AddressService addressService;
    final OrderService orderService;

    @PostMapping("/coupon/add")
    public Object addCoupon(@RequestBody Coupon coupon) {
        Integer number = coupon.getNumber();
        if (number == null) {
            number = 1;
        }
        coupon.setIsUsed(0);
        String username = coupon.getUsername();
        coupon.setUserId(loginDao.selectOne(
                new QueryWrapper<User>().eq("username", username)
        ).getId());

        for (int i=0;i<number;i++) {
            couponService.save(coupon);
        }

        return AjaxResult.SUCCESS();
    }
    
    @NotNull
    private Object handleList(PageQueryDto pageQueryDto, @RequestParam(required = false) Integer subType, List<Coupon> coupons) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        coupons.forEach(item -> {
            Integer expired = null;
            try {
                expired = new Date().compareTo(sdf.parse(item.getEffectiveTime())) > 0? 1: 0;
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            item.setExpired(expired);
            System.out.println(expired);
        });
        if (subType == null) {
            return AjaxResult.SUCCESS("查询成功", PageUtil.List2IPage(coupons, pageQueryDto.getPageNum(), pageQueryDto.getPageSize()));
        }
        if (subType == 0) {
            coupons = coupons.stream().filter(item -> item.getExpired() == 0 && item.getIsUsed() == 0)
                    .collect(Collectors.toList());
        } else if (subType == 1) {
            coupons = coupons.stream().filter(item -> item.getExpired() == 1).collect(Collectors.toList());
        } else if (subType == 2) {
            coupons = coupons.stream().filter(item -> item.getIsUsed() == 1).collect(Collectors.toList());
        }
        return AjaxResult.SUCCESS("查询成功", PageUtil.List2IPage(coupons, pageQueryDto.getPageNum(), pageQueryDto.getPageSize()));
    }

    @GetMapping("/coupon/admin/query")
    public Object getCouponList(PageQueryDto pageQueryDto,
                                @RequestParam(required = false) Integer subType) {
        List<Coupon> couponList = couponService.list();
        return handleList(pageQueryDto, subType, couponList);
    }

    @DeleteMapping("/coupon/admin/delete/batch")
    public Object deleteCouponByBatchAdmin(@RequestBody List<Integer> ids) {
        return AjaxResult.SUCCESS(couponService.removeBatchByIds(ids));
    }

    @GetMapping("/coupon/lottery")
    public Object lotteryForCoupon(HttpServletRequest request) {
        Integer userId = ParseUtil.parseToken(request);
        Date date = new Date();
        Coupon coupon = new Coupon();
        coupon.setType("折扣券");
        double min = 0.0;  // 生成随机浮点数的最小值
        double max = 20.0;  // 生成随机浮点数的最大值
        int randomPrice = (int) Math.round(Math.random() * (max - min) + min);
        coupon.setPrice(randomPrice);
        int effectivePrice = (int) Math.round(randomPrice * 1.5);
        coupon.setEffectivePrice(effectivePrice);
        coupon.setUserId(userId);
        coupon.setNumber(1);
        coupon.setExpired(0);
        coupon.setIsUsed(0);
        coupon.setUsername(loginDao.selectById(userId).getUsername());
        LocalDate today = LocalDate.now(); // 获取当前日期
        LocalDate tenDaysLater = today.plusDays(10);
        coupon.setEffectiveTime(tenDaysLater.toString());

        if (randomPrice < 5) {
            return AjaxResult.SUCCESS("谢谢惠顾", null);
        }

        couponService.save(coupon);
        return AjaxResult.SUCCESS(coupon);
    }

    @GetMapping("/coupon/query/self")
    public Object getSelfCouponList(HttpServletRequest request,
                                    PageQueryDto pageQueryDto,
                                    @RequestParam(required = false) Integer subType) throws ParseException {
        Integer userId = ParseUtil.parseToken(request);
        List<Coupon> coupons = couponService.list(
                new QueryWrapper<Coupon>()
                        .eq("user_id", userId)
                        .orderByAsc("is_used")
                        .orderByDesc("price")
        );
        return handleList(pageQueryDto, subType, coupons);
    }

    @DeleteMapping("/coupon/admin/delete/{id}")
    public Object deleteCouponById(@PathVariable Integer id) {
        couponService.removeById(id);
        return AjaxResult.SUCCESS("删除成功", null);
    }

    @GetMapping("/coupon/query/default")
    public Object getDefaultCouponList(HttpServletRequest request) {
        Integer userId = ParseUtil.parseToken(request);
        Coupon coupon = couponService.getOne(
                new QueryWrapper<Coupon>()
                        .eq("user_id", userId)
                        .ge("effective_time", new Date())
                        .eq("is_used", 0)
                        .orderByDesc("price")
                        .last("limit 1"));
        return AjaxResult.SUCCESS(coupon);
    }

    final CouponCodeService couponCodeService;

    @GetMapping("/coupon/qrCode/{couponCodeId}")
    public void getQrcode(HttpServletResponse resp, @PathVariable Integer couponCodeId) throws IOException {
        String text = couponCodeService.getById(couponCodeId).getCodeNo();
        byte[] buff = QrCodeUtil.generateQrCode(text);
        // 设置响应内容类型
        resp.setContentType("image/jpeg");
        // 设置Content-Disposition头，以附件形式下载
        resp.setHeader("Content-Disposition", "attachment; filename=\"image.jpg\"");
        // 设置响应体长度
        resp.setContentLength(buff.length);
        // 获取输出流并写入字节流
        try (OutputStream os = resp.getOutputStream()) {
            os.write(buff);
            os.flush();
        }
    }

    @PostMapping("/coupon/qrCode/verify")
    public Object verifyQrCode(@RequestBody Map<String, String> map) {
        CouponCode couponCode = couponCodeService.getOne(
                new QueryWrapper<CouponCode>()
                        .eq("code_no", map.get("codeNo"))
        );
        Integer verified = couponCode.getVerified();
        if (verified == 1) {
            throw new AppException(ErrorCode.PROCESSED);
        } else {
            couponCode.setVerified(1);
            couponCodeService.updateById(couponCode);
            return AjaxResult.SUCCESS("核销成功", null);
        }
    }

    final CouponOrderDao couponOrderDao;

    @GetMapping("/coupon/qrCode")
    public Object getQrcodeList(PageQueryDto pageQueryDto, HttpServletRequest request) {
        Integer userId = ParseUtil.parseToken(request);
        IPage<CouponOrderDto> resultList = couponOrderDao.selectPage(
                new Page<>(pageQueryDto.getPageNum(), pageQueryDto.getPageSize()),
                new QueryWrapper<CouponOrderDto>()
                        .eq("user_id", userId)
        );
        return AjaxResult.SUCCESS(resultList);
    }

    @DeleteMapping("/coupon/delete/{id}")
    public Object deleteCoupon(@PathVariable Integer id) {
        return AjaxResult.SUCCESS(couponService.removeById(id));
    }


    @DeleteMapping("/coupon/delete/batch")
    public Object deleteCouponByBatch(@RequestBody List<Integer> ids) {
        return AjaxResult.SUCCESS(couponService.removeBatchByIds(ids));
    }

    final MembershipLevelDao membershipLevelDao;

    @GetMapping("/membership/query")
    public Object getMemberShip(HttpServletRequest request) {
        Integer userId = ParseUtil.parseToken(request);
        User user = loginDao.selectById(userId);
        Integer vipLevel = user.getVipLevel();
        MembershipLevel membershipLevel = membershipLevelDao.selectOne(
                new QueryWrapper<MembershipLevel>()
                        .eq("vip_level", vipLevel)
        );
        Integer couponNum = Math.toIntExact(
                couponService.count(
                        new QueryWrapper<Coupon>()
                                .eq("user_id", userId)
                                .eq("is_used", 0)
                                .ge("effective_time", new Date())
                )
        );
        Integer couponCodeNum = Math.toIntExact(
                couponCodeService.count(
                        new QueryWrapper<CouponCode>()
                                .eq("user_id", userId)
                                .eq("verified", 0)
                )
        );
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("membership", membershipLevel);
        resultMap.put("vipLevel", vipLevel);
        resultMap.put("couponNum", couponNum);
        resultMap.put("couponCodeNum", couponCodeNum);
        resultMap.put("userId", userId);
        return AjaxResult.SUCCESS(resultMap);
    }
}