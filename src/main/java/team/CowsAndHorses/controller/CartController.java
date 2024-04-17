package team.CowsAndHorses.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import team.CowsAndHorses.domain.Cart;
import team.CowsAndHorses.domain.CartGoods;
import team.CowsAndHorses.dto.AjaxResult;
import team.CowsAndHorses.dto.GoodsDetail;
import team.CowsAndHorses.service.CartService;
import java.util.List;
import static team.CowsAndHorses.util.ParseUtil.parseToken;

/**
 * @author LittleHorse
 * @version 1.0
 */

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/cart")
public class CartController {
    final CartService cartService;

    @PostMapping("/add")
    @ResponseBody
    public Object addCart(HttpServletRequest request, @RequestBody Cart cartInfo){
        Integer userId = parseToken(request);
        cartInfo.setUserId(userId);
        cartService.addGoods(cartInfo);
        return AjaxResult.SUCCESS();
    }

    @PostMapping("/select")
    @ResponseBody
    public Object selectGoods(HttpServletRequest request, @RequestParam Integer id){
        cartService.selectGoods(id);
        return AjaxResult.SUCCESS();
    }

    @PostMapping("/select/all")
    @ResponseBody
    public Object selectAllGoods(HttpServletRequest request, @RequestParam Boolean selected){
        Integer userId = parseToken(request);
        return cartService.selectAllGoods(selected, userId) == 1 ?
                AjaxResult.SUCCESS(): AjaxResult.FAIL();
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public Object removeCart(@RequestParam Integer id) {
        cartService.removeGoods(id);
        return AjaxResult.SUCCESS();
    }

    @PostMapping("/update")
    @ResponseBody
    public Object updateCount(@RequestParam Integer id, Integer count){
        cartService.modifyCount(id, count);
        return AjaxResult.SUCCESS();
    }

    @GetMapping("/query")
    @ResponseBody
    public Object getCartInfo(HttpServletRequest request,
                              @RequestParam(required = false) Integer goodsId){
        Integer userId = parseToken(request);
        if (goodsId == null) {
            return AjaxResult.SUCCESS(cartService.getCartDetail(userId));
        } else {
            return AjaxResult.SUCCESS(cartService.getCartDetailByGoods(userId, goodsId));
        }
    }

    @GetMapping("/preOrder")
    @ResponseBody
    public Object getPreOrderInfo(HttpServletRequest request){
        Integer userId = parseToken(request);
        List<CartGoods> res = cartService.getPreOrderInfo(userId);
        return AjaxResult.SUCCESS(res);
    }

    @GetMapping("/preOrder/{goodsId}")
    @ResponseBody
    public Object getPreOrderInfo(HttpServletRequest request, @PathVariable Integer goodsId){
        Integer userId = parseToken(request);
        List<CartGoods> res = cartService.getPreOrderInfoByGoods(userId, goodsId);
        return AjaxResult.SUCCESS(res);
    }

    @GetMapping("/preOrder/now/{scaleId}")
    @ResponseBody
    public Object getPreNowOrderInfo(HttpServletRequest request, @PathVariable Integer scaleId) {
        List<GoodsDetail> res = cartService.getPreNowOrderInfo(scaleId);
        return AjaxResult.SUCCESS(res);
    }
}