package team.CowsAndHorses.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import team.CowsAndHorses.dao.*;
import team.CowsAndHorses.domain.Comment;
import team.CowsAndHorses.domain.Goods;
import team.CowsAndHorses.domain.GoodsImage;
import team.CowsAndHorses.domain.Sku;
import team.CowsAndHorses.dto.GoodsDetail;
import team.CowsAndHorses.dto.GoodsDto;
import team.CowsAndHorses.dto.PageQueryDto;
import team.CowsAndHorses.service.CommentService;
import team.CowsAndHorses.service.GoodsService;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@CacheConfig(cacheNames = "ExpireOneMin")
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsDao, Goods> implements GoodsService {
    final GoodsDao goodsDao;
    final GoodsImageDao goodsImageDao;
    final SkuDao skuDao;
    final CommentDao commentDao;
    final CommentService commentService;
    final LoginDao loginDao;
    final ImageDao imageDao;
    @Value("${url.image}")
    private String relativePath;
    @Override
    public List<Goods> getGoodsByCategory(String category){
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.eq("category",category);
        System.out.println(goodsDao.selectList(wrapper));
        return goodsDao.selectList(wrapper);
    }
    @Override
    public IPage<GoodsDto> getAllGoods(PageQueryDto pageQuery){
        Page<Goods> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        IPage<Goods> goodsList =  goodsDao.selectPage(page, null);
        IPage<GoodsDto> result = new Page<>();
        List<Goods> records = goodsList.getRecords();
        List<GoodsDto> res = new ArrayList<>();
        for (Goods item: records) {
            Integer id = item.getId();
            QueryWrapper<GoodsImage> imageWrapper = new QueryWrapper<>();
            imageWrapper.eq("goods_id", id);
            List<GoodsImage> images = goodsImageDao.selectList(imageWrapper);

            QueryWrapper<Sku> skuWrapper = new QueryWrapper<>();
            skuWrapper.eq("goods_id", id);
            List<Sku> skus = skuDao.selectList(skuWrapper);
            skus.forEach(sku -> {
                sku.setSoldOut(sku.getNowInventory() > 0? 1: 0);
            });
            GoodsDto goodsDto = new GoodsDto();
            goodsDto.setGoodsInfo(item);
            goodsDto.setImages(images);
            goodsDto.setSkus(skus);
            res.add(goodsDto);
        }
        result.setRecords(res);
        result.setPages(goodsList.getPages());
        result.setSize(goodsList.getSize());
        result.setTotal(goodsList.getTotal());
        return result;
    }
    @Override
    public List<Goods> getAllGoodsByCategory(){
        QueryWrapper<Goods> qw = new QueryWrapper<>();
        qw.orderByAsc("category")
                .eq("on_shelf", 1);
        return goodsDao.selectList(qw);
    }
    @Override
    public GoodsDto getGoodsInfo(Integer id) {
        Goods goods = goodsDao.selectById(id);
        GoodsDto goodsDto = new GoodsDto();
        goodsDto.setGoodsInfo(goods);
        QueryWrapper<GoodsImage> imageWrapper = new QueryWrapper<>();
        imageWrapper.eq("goods_id", id);
        List<GoodsImage> images = goodsImageDao.selectList(imageWrapper);
        QueryWrapper<Sku> skuWrapper = new QueryWrapper<>();
        skuWrapper.eq("goods_id", id);
        List<Sku> skus = skuDao.selectList(skuWrapper);
        skus.forEach(item -> {
            item.setSoldOut(item.getNowInventory() > 0? 0: 1);
        });
        goodsDto.setImages(images);
        goodsDto.setSkus(skus);
        goodsDto.setCommentNum(
                Math.toIntExact(commentDao.selectCount(new QueryWrapper<Comment>()
                        .eq("goods_id", id)))
        );

        goodsDto.setComments(
                commentService.getCommentsByGoodsId(id, 1, 5).getRecords()
        );

//        List<Comment> comments = commentDao.selectList(new QueryWrapper<Comment>().eq("goods_id", id));
//        List<CommentResultDto> commentResultDtos = new ArrayList<>();
//        for (Comment comment: comments) {
//            Integer userId = comment.getPublisherId();
//            String avatarUrl = loginDao.selectById(userId).getAvatarUrl();
//            String nickname = loginDao.selectById(userId).getNickName();
//            CommentResultDto commentResultDto = new CommentResultDto(comment);
//            commentResultDto.setAvatarUrl(avatarUrl);
//            commentResultDto.setPublisher(nickname);
//            commentResultDto.setImages(
//                    imageDao.getImagesByCommentId(comment.getCommentId())
//            );
//            commentResultDtos.add(commentResultDto);
//        }
//        goodsDto.setComments(commentResultDtos);
        return goodsDto;
    }
    @Override
    public Integer deleteGoodsByIds(List<Integer> ids) {
        for (Integer id: ids) {
            goodsDao.deleteLogicById(id);
        }
        return 1;
    }
    @Override
    public Integer deleteGoodsById(Integer id) {
//        QueryWrapper<GoodsImage> wrapperImage = new QueryWrapper<>();
//        wrapperImage.eq("goods_id", id);
//        goodsImageDao.delete(wrapperImage);
//        QueryWrapper<Sku> wrapperSku = new QueryWrapper<>();
//        wrapperSku.eq("goods_id", id);
//        skuDao.delete(wrapperSku);
        return goodsDao.deleteLogicById(id);
//        return goodsDao.deleteById(id);
    }
    @Override
    public Integer updateGoods(Goods newGoods) {
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        newGoods.setSubmitTime(df.format(day));
        return goodsDao.updateById(newGoods);
    }
    @Override
    public Integer addGoods(Goods goods) {
        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        goods.setSubmitTime(df.format(day));
        if (goods.getProductTime().isEmpty()) {
            goods.setProductTime(null);
        }
        return goodsDao.insert(goods);
    }
    @Override
    public Integer addGoodsSku(Sku sku) {
        return skuDao.insert(sku);
    }
    @Override
    public Integer updateGoodsSku(Sku sku) {
        return skuDao.updateById(sku);
    }
    @Override
    public Integer deleteGoodsSku(List<Integer> ids) {
        return skuDao.deleteBatchIds(ids);
    }
    @Override
    public Integer addGoodsImage(Integer goodsId, String imgUrl) {
        GoodsImage goodsImage = new GoodsImage();
        goodsImage.setGoodsId(goodsId);
        goodsImage.setImgUrl(imgUrl);
        goodsImageDao.insert(goodsImage);
        return goodsImage.getId();
    }

    @Override
    public List<GoodsImage> getGoodsImages(Integer goodsId) {
        List<GoodsImage> result = goodsImageDao.selectList(
                new QueryWrapper<GoodsImage>().eq("goods_id", goodsId));
        return result;
    }

    @Override
    public Integer deleteGoodsImage(Integer id) {
        try {
            String imgUrl = goodsImageDao.selectById(id).getImgUrl();
            String filename = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
            File file = new File(relativePath + filename);
            boolean flag = file.delete();
            if (!flag) {
                return 0;
            }
            return goodsImageDao.deleteById(id);
        } catch(Exception e) {
            return 0;
        }
    }
    @Override
    public Integer excelImport(List<Goods> list) {
        System.out.println(list);
        try {
            for (Goods goods : list) {
                System.out.println(goods);
                Date day = new Date();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                goods.setId(null);
                goods.setSubmitTime(df.format(day));
                goodsDao.insert(goods);
            }
            return 1;
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("导入失败");
            return 0;
        }
    }

    @Override
    public List<GoodsDetail> selectGoodsDetail() {
        return goodsDao.selectGoodsDetail();
    }
    @Override
    public List<Sku> getGoodsSkus(Integer goodsId) {
        List<Sku> resultList = skuDao.selectList(new QueryWrapper<Sku>().eq("goods_id", goodsId));
        resultList.forEach(item -> {
            item.setSoldOut(item.getNowInventory() > 0? 0: 1);
        });
        return resultList;
    }
}
