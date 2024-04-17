package team.CowsAndHorses.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.CowsAndHorses.constant.ErrorCode;
import team.CowsAndHorses.dao.ImageDao;
import team.CowsAndHorses.domain.Comment;
import team.CowsAndHorses.domain.CommentImage;
import team.CowsAndHorses.domain.User;
import team.CowsAndHorses.dto.AjaxResult;
import team.CowsAndHorses.dto.CommentResultDto;
import team.CowsAndHorses.service.CommentImageService;
import team.CowsAndHorses.service.CommentService;
import team.CowsAndHorses.service.OrderService;
import team.CowsAndHorses.util.ImageCompressUtil;
import team.CowsAndHorses.util.JwtUtil;
import team.CowsAndHorses.util.ParseUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static team.CowsAndHorses.util.ParseUtil.parseToken;

/**
 * @author LittleHorse
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/comment")
public class CommentController {
    final CommentService commentService;
    final OrderService orderService;
    final CommentImageService commentImageService;
    @Value("${url.image}")
    private String relativePath;

    @PostMapping("/add/{orderId}")
    @ResponseBody
    public Object addComment(@RequestBody Comment comment, HttpServletRequest request,
                             @PathVariable Integer orderId) {
        Integer userId = parseToken(request);
        comment.setPublisherId(userId);
        Date nowDate = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        comment.setPublishTime(df.format(nowDate));
        commentService.addComment(comment, orderId);
        Integer commentId =  comment.getCommentId();
        Map<String, Integer> resultMap = new HashMap<>();
        resultMap.put("commentId", commentId);
        return AjaxResult.SUCCESS(resultMap);
    }

    @PostMapping("/image/upload/{commentId}")
    @ResponseBody
    public Object uploadProfile(@RequestBody MultipartFile image, @PathVariable Integer commentId){
        String originalName = image.getOriginalFilename();
        String filePath = "";
        assert originalName != null;
        String prefix = originalName.substring(0, originalName.lastIndexOf("."));
        filePath = relativePath + prefix + ".webp";
        ImageCompressUtil.uploadAsWebp(image, filePath);
        CommentImage commentImage = new CommentImage();
        commentImage.setCommentId(commentId);
        String imgUrl = "https://image.familystudy.cn/image/jxfruit/" + prefix + ".webp";
        commentImage.setImgUrl(imgUrl);
        commentImageService.save(commentImage);
        return AjaxResult.SUCCESS();
    }

    @RequestMapping("/query")
    @ResponseBody
    public Object getCommentsByGoodsId(@RequestParam Map<String,Object> requestInfo) {
        Integer goodsId = Integer.parseInt((String)requestInfo.get("goodsId"));
        Integer pageNum = Integer.parseInt((String) requestInfo.get("pageNum"));
        Integer pageSize = Integer.parseInt((String) requestInfo.get("pageSize"));
        IPage<CommentResultDto> res = commentService.getCommentsByGoodsId(goodsId,pageNum,pageSize);
        return AjaxResult.SUCCESS(res);
    }

    @PostMapping("/admin/approve")
    @ResponseBody
    public Object approveCommentByIds(@RequestBody Map<String, List<Integer>> map) {
        for (Integer id: map.get("ids")) {
            Comment comment = commentService.getById(id);
            comment.setIsShow(1);
            commentService.updateById(comment);
        }
        return AjaxResult.SUCCESS("审批成功", null);
    }


    @RequestMapping("/query/self")
    @ResponseBody
    public Object getSelfComments(HttpServletRequest request, @RequestParam Map<String,Object> requestInfo) {
        Integer userId = parseToken(request);
        Integer pageNum = 1;
        Integer pageSize = 10; // 默认页数和每页帖子数
        if (requestInfo.get("pageNum") != null)
            pageNum = ParseUtil.parseInteger(requestInfo,"pageNum");
        if (requestInfo.get("pageSize") != null)
            pageSize = ParseUtil.parseInteger(requestInfo,"pageSize");
        IPage<CommentResultDto> res = commentService.getSelfComments(userId, pageNum, pageSize);
        return AjaxResult.SUCCESS(res);
    }

    @DeleteMapping("/delete")
    @ResponseBody
    public Object deleteSelfComment(HttpServletRequest request, @RequestParam Map<String,Object> requestInfo){
        Integer userId = parseToken(request);
        Integer commentId = ParseUtil.parseInteger(requestInfo,"commentId");
        Integer num =  commentService.deleteSelfComment(userId, commentId);
        if (num > 0)
            return AjaxResult.SUCCESS();
        else
            return AjaxResult.FAIL("无法删除");
    }
}