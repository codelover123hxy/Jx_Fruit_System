package team.CowsAndHorses.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import team.CowsAndHorses.dao.CartDao;
import team.CowsAndHorses.dao.GoodsDao;
import team.CowsAndHorses.dao.SkuDao;
import team.CowsAndHorses.domain.Cart;
import team.CowsAndHorses.domain.CartGoods;
import team.CowsAndHorses.dto.GoodsDetail;
import team.CowsAndHorses.service.CartService;
import java.util.List;
import static team.CowsAndHorses.util.IntegerUtil.bool2int;

@RequiredArgsConstructor
@CacheConfig(cacheNames = "ExpireOneMin")
@Service
public class CartServiceImpl implements CartService {
    final CartDao cartDao;
    final GoodsDao goodsDao;
    final SkuDao skuDao;
    @Override
    public Integer addGoods(Cart cartInfo){
        Cart cart = cartDao.selectOne(
                new QueryWrapper<Cart>()
                        .eq("user_id", cartInfo.getUserId())
                        .eq("sku_id", cartInfo.getSkuId())
        );
        if (null == cart) {
            Integer skuId = cartInfo.getSkuId();
            Double singlePrice = skuDao.selectById(skuId).getPrice();
            Integer num = cartInfo.getNum();
            cartInfo.setTotalPrice(singlePrice * num);
            return cartDao.insert(cartInfo);
        } else {
            cart.setNum(cart.getNum() + cartInfo.getNum());
            return cartDao.updateById(cart);
        }
    }
    @Override
    public Integer removeGoods(Integer id){
        return cartDao.deleteById(id);
    }
    @Override
    public Integer modifyCount(Integer id, Integer count){
        Cart cartInfo = cartDao.selectById(id);
        if (cartInfo == null)
            return 0;
        Integer skuId = cartInfo.getSkuId();
        Double singlePrice = skuDao.selectById(skuId).getPrice();
        cartInfo.setNum(count);
        cartInfo.setTotalPrice(count * singlePrice);
        return cartDao.updateById(cartInfo);
    }
    @Override
    public Integer selectGoods(Integer id){
        Cart cartInfo = cartDao.selectById(id);
        Integer selected = cartInfo.getSelected();
        cartInfo.setSelected(1 - selected);
        return cartDao.updateById(cartInfo);
    }
    @Override
    public Integer selectAllGoods(Boolean selected, Integer userId){
        QueryWrapper<Cart> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<Cart> cartList = cartDao.selectList(wrapper);
        try {
            for (Cart cart: cartList) {
                cart.setSelected(bool2int(selected));
                cartDao.updateById(cart);
            }
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }
    @Override
    public List<CartGoods> getCartDetail(Integer userId){
        return cartDao.getCartDetail(userId);
    }
    @Override
    public List<CartGoods> getCartDetailByGoods(Integer userId, Integer goodsId) {
        return cartDao.getCartDetailByGoods(userId, goodsId);
    }

    @Override
    public List<CartGoods> getPreOrderInfo(Integer userId) {
        List<CartGoods> res = cartDao.getPreOrderDetail(userId);
        return res;
    }
    @Override
    public List<CartGoods> getPreOrderInfoByGoods(Integer userId, Integer goodsId) {
        List<CartGoods> res = cartDao.getPreOrderDetailByGoods(userId, goodsId);
        return res;
    }

    @Override
    public List<GoodsDetail> getPreNowOrderInfo(Integer skuId) {
        List<GoodsDetail> res = cartDao.getCartDetailByScaleId(skuId);
        return res;
    }
}