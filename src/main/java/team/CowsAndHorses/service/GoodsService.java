package team.CowsAndHorses.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import team.CowsAndHorses.domain.Goods;
import team.CowsAndHorses.domain.GoodsImage;
import team.CowsAndHorses.domain.Sku;
import team.CowsAndHorses.dto.GoodsDetail;
import team.CowsAndHorses.dto.GoodsDto;
import team.CowsAndHorses.dto.PageQueryDto;

import java.util.List;

@Transactional
public interface GoodsService extends IService<Goods> {
    List<Goods> getGoodsByCategory(String category);
    IPage<GoodsDto> getAllGoods(PageQueryDto pageQuery);
    List<Goods>getAllGoodsByCategory();
    GoodsDto getGoodsInfo(Integer id);
    Integer deleteGoodsById(Integer id);
    Integer deleteGoodsByIds(List<Integer> ids);
    Integer updateGoods(Goods newGoods);
    Integer addGoods(Goods goods);
    Integer addGoodsSku(Sku goodsSku);
    Integer updateGoodsSku(Sku sku);
    Integer deleteGoodsSku(List<Integer> ids);
    Integer addGoodsImage(Integer goodsId, String imgUrl);
    List<GoodsImage>

    getGoodsImages(Integer goodsId);
    Integer deleteGoodsImage(Integer id);
    Integer excelImport(List<Goods> list);
    List<GoodsDetail> selectGoodsDetail();
    List<Sku> getGoodsSkus(Integer goodsId);
}
