package team.CowsAndHorses.controller;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.CowsAndHorses.domain.Goods;
import team.CowsAndHorses.domain.GoodsImage;
import team.CowsAndHorses.domain.Sku;
import team.CowsAndHorses.dto.AjaxResult;
import team.CowsAndHorses.dto.GoodsDetail;
import team.CowsAndHorses.dto.PageQueryDto;
import team.CowsAndHorses.service.GoodsService;
import team.CowsAndHorses.util.ImageCompressUtil;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
/**
 * @author LittleHorse
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/goods")
public class GoodsController {
    final GoodsService goodsService;

    @Value("${url.image}")
    private String relativePath;

    @Value("${url.imageHostBaseUrl}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.smtpHost}")
    private String smtpHost;
    final String dir = System.getProperty("user.dir");

    /**
     * user 部分
     * @author 小马
     */
    @PostMapping("/import")
    @ResponseBody
    public Object excelImport(@RequestBody MultipartFile file) throws IOException {
        String originalName = file.getOriginalFilename();
        System.out.println(originalName);
        InputStream is = file.getInputStream();
        List<Goods> list = EasyExcel.read(file.getInputStream())
                .head(Goods.class)
                .sheet("Sheet1")
                .doReadSync();
        return goodsService.excelImport(list) == 1?
                AjaxResult.SUCCESS("导入成功", null): AjaxResult.FAIL("导入失败", null);
    }

    @GetMapping("/export")
    @ResponseBody
    public void excelExport(HttpServletResponse response) throws IOException{
        response.setContentType("application/vnd.vnd.ms-excel");
        //设置编码格式
        response.setCharacterEncoding("utf-8");
        //设置导出文件名称（避免乱码）
        String fileName = URLEncoder.encode("商品列表.xlsx", "UTF-8");
        // 设置响应头
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
        OutputStream outputStream = response.getOutputStream();
        List<GoodsDetail> goodsList = goodsService.selectGoodsDetail();
        try {
            EasyExcel.write(outputStream, GoodsDetail.class)//对应的导出实体类
                    .sheet(1)//导出sheet页名称
                    .doWrite(goodsList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            outputStream.flush();
            outputStream.close();
        }
    }

    @GetMapping("/{category}")
    @ResponseBody
    public Object getGoodsByCategory(@PathVariable("category") String category){
        return AjaxResult.SUCCESS(goodsService.getGoodsByCategory(category));
    }

    @GetMapping("/info")
    @ResponseBody
    public Object getGoodsInfo(@RequestParam Integer id){
        return AjaxResult.SUCCESS(goodsService.getGoodsInfo(id));
    }

    @GetMapping("/search")
    @ResponseBody Object searchGoods(@RequestParam String name) {
        QueryWrapper<Goods> wrapper = new QueryWrapper<>();
        wrapper.like("goods_name", name);
        return AjaxResult.SUCCESS(goodsService.list(wrapper));
    }

    @GetMapping("/category")
    @ResponseBody
    public Object getAllGoodsByCategory(){
        List<Goods> goodsList = goodsService.getAllGoodsByCategory();
        Map<String, List<Goods>> map = new HashMap<>();
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String, Object> onSale = new HashMap<>();
        onSale.put("category", "今日特价");
        onSale.put("list", goodsService.list(
                new QueryWrapper<Goods>().eq("on_sale", 1)
                        .eq("on_shelf", 1)));
        Map<String, Object> activity = new HashMap<>();
        activity.put("category", "活动商品");
        activity.put("list", goodsService.list(
                new QueryWrapper<Goods>().eq("in_activity", 1)
                        .eq("on_shelf", 1)));
        list.add(onSale);
        list.add(activity);
        goodsList.forEach((item)-> {
            String category = item.getCategory();
            if (map.containsKey(category)){
                map.get(category).add(item);
            } else{
                List<Goods> resultList = new ArrayList<>();
                resultList.add(item);
                map.put(category, resultList);
            }
        });
        map.forEach((key, value)->{
            Map<String, Object> each = new HashMap<>();
            each.put("category", key);
            each.put("list", value);
            list.add(each);
        });
        Map<String, Object> underShelf = new HashMap<>();
        underShelf.put("category", "已下架商品");
        underShelf.put("list", goodsService.list(
                new QueryWrapper<Goods>()
                        .eq("on_shelf", 0)));
        list.add(underShelf);
        return AjaxResult.SUCCESS(list);
    }

    @GetMapping("/scale/{goodsId}")
    @ResponseBody
    public Object getGoodsSkus(@PathVariable Integer goodsId) {
        List<Sku> sku = goodsService.getGoodsSkus(goodsId);
        return AjaxResult.SUCCESS(sku);
    }

/**
 * admin 部分
 */
    @GetMapping("/admin/query")
    @ResponseBody
    public Object getAllGoods(PageQueryDto pageQuery) {
        return AjaxResult.SUCCESS(goodsService.getAllGoods(pageQuery));
    }

    @DeleteMapping("/admin/delete")
    @ResponseBody
    public Object deleteGoodsByIds(@RequestBody List<Integer> ids) {
        return goodsService.deleteGoodsByIds(ids) > 0 ?
                AjaxResult.SUCCESS(): AjaxResult.FAIL();
    }

    @DeleteMapping("/admin/delete/{id}")
    @ResponseBody
    public Object deleteGoodsById(@PathVariable Integer id) {
        return goodsService.deleteGoodsById(id) > 0 ?
                AjaxResult.SUCCESS(): AjaxResult.FAIL();
    }

    @PostMapping("/admin/update")
    @ResponseBody
    public Object updateGoods(@RequestBody Goods newGoods) {
        return goodsService.updateGoods(newGoods) > 0 ?
                AjaxResult.SUCCESS(): AjaxResult.FAIL();
    }

    @PostMapping("/admin/add")
    @ResponseBody
    public Object addGoods(Goods goods, @RequestParam(required = false) MultipartFile image) {
        if (goods.getThumbNail() == null) {
            String originalName = image.getOriginalFilename();
            String filePath = "";
            assert originalName != null;
            String prefix = originalName.substring(0, originalName.lastIndexOf("."));
            filePath = relativePath + prefix + ".webp";
            ImageCompressUtil.uploadAsWebp(image, filePath);
            String imgUrl = baseUrl + "/image/jxfruit/" + prefix + ".webp";
            goods.setThumbNail(imgUrl);
        }
        return goodsService.addGoods(goods) > 0 ?
                AjaxResult.SUCCESS(): AjaxResult.FAIL();
    }

    @PostMapping("/admin/add/image/{goodsId}")
    @ResponseBody
    public Object addGoodsImage(@RequestBody MultipartFile[] file, @PathVariable Integer goodsId) {

        try {
            List<GoodsImage> imgs = new ArrayList<>();
            for (MultipartFile fileItem: file) {
                String originalName = fileItem.getOriginalFilename();
                String filePath = "";
                assert originalName != null;
                String prefix = originalName.substring(0, originalName.lastIndexOf("."));
                filePath = relativePath + prefix + ".webp";
                ImageCompressUtil.uploadAsWebp(fileItem, filePath);
                String imgUrl = baseUrl + "/image/jxfruit/" + prefix + ".webp";
                Integer imageId = goodsService.addGoodsImage(goodsId, imgUrl);

                GoodsImage goodsImage = new GoodsImage();
                goodsImage.setId(imageId);
                goodsImage.setImgUrl(imgUrl);
                goodsImage.setGoodsId(goodsId);
                imgs.add(goodsImage);
            }
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("imgs", imgs);
            return AjaxResult.SUCCESS(resultMap);
        }
        catch (IllegalStateException e) {
            e.printStackTrace();
            return AjaxResult.FAIL("上传失败", null);
        }
    }

    @PostMapping("/admin/shelf/{state}")
    @ResponseBody
    public Object setShelfState(@PathVariable Integer state, @RequestBody Map<String, List<Integer>> map) {
        List<Goods> goodsList = goodsService.listByIds(map.get("ids"));
        for (Goods goods: goodsList) {
            goods.setOnShelf(state);
            goodsService.updateById(goods);
        }
        return AjaxResult.SUCCESS();
    }

    @GetMapping("/admin/query/image/{goodsId}")
    @ResponseBody
    public Object getGoodsImages(@PathVariable Integer goodsId) {
        return goodsService.getGoodsImages(goodsId);
    }


    @DeleteMapping("/admin/delete/image/{id}")
    @ResponseBody
    public Object deleteGoodsImage(@PathVariable Integer id) {
        return goodsService.deleteGoodsImage(id) > 0 ?
                AjaxResult.SUCCESS(): AjaxResult.FAIL();
    }

    @PostMapping("/admin/add/scale/{goodsId}")
    @ResponseBody
    public Object addGoodsSku(@PathVariable Integer goodsId, @RequestBody Sku sku) {
        sku.setGoodsId(goodsId);
        sku.setNowInventory(sku.getTotalInventory());
        sku.setSoldAmount(0);
        return goodsService.addGoodsSku(sku) > 0 ?
                AjaxResult.SUCCESS(): AjaxResult.FAIL();
    }
    @PostMapping("/admin/update/scale")
    @ResponseBody
    public Object updateGoodsSku(@RequestBody Sku sku) {
        return goodsService.updateGoodsSku(sku) > 0?
                AjaxResult.SUCCESS(): AjaxResult.FAIL();
    }

    @DeleteMapping("/admin/delete/scale")
    @ResponseBody
    public Object deleteGoodsSku(@RequestParam List<Integer> ids) {
        if (ids == null) {
            return AjaxResult.FAIL();
        }
        return goodsService.deleteGoodsSku(ids) > 0 ?
                AjaxResult.SUCCESS(): AjaxResult.FAIL();
    }
}