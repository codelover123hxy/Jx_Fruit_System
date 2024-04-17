package team.CowsAndHorses.service;

import org.springframework.transaction.annotation.Transactional;
import team.CowsAndHorses.domain.Cart;
import team.CowsAndHorses.domain.CartGoods;
import team.CowsAndHorses.dto.GoodsDetail;

import java.util.List;

@Transactional
public interface CartService {
    Integer addGoods(Cart cartInfo);
    Integer removeGoods(Integer id);
    Integer modifyCount(Integer id,Integer count);
    List<CartGoods> getCartDetail(Integer userId);
    List<CartGoods> getCartDetailByGoods(Integer userId, Integer goodsId);
    Integer selectGoods(Integer id);
    Integer selectAllGoods(Boolean selected, Integer userId);
    List<CartGoods> getPreOrderInfo(Integer userId);
    List<CartGoods> getPreOrderInfoByGoods(Integer userId, Integer goodsId);
    List<GoodsDetail> getPreNowOrderInfo(Integer skuId);
}