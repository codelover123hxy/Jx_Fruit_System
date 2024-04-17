package team.CowsAndHorses.controller;

import cn.hutool.http.server.HttpServerResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import team.CowsAndHorses.domain.FileEntity;
import team.CowsAndHorses.domain.Goods;
import team.CowsAndHorses.dto.AjaxResult;
import team.CowsAndHorses.dto.PageQueryDto;
import team.CowsAndHorses.service.FileService;
import team.CowsAndHorses.util.ParseUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author LittleHorse
 * @version 1.0
 */
@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/file")
public class FileController {
    final FileService fileService;

    @Value("${url.imageHostBaseUrl}")
    String imageBaseUrl;
    String rootDir = "C:\\Users\\hxy123\\Desktop\\files\\";
    final String dir = System.getProperty("user.dir");

    @PostMapping("/upload")
    @ResponseBody
    public Object uploadFile(@RequestBody MultipartFile file, HttpServletRequest request) throws IOException {
        final String relativePath = "/imagehost/file/";
        Integer userId = ParseUtil.parseToken(request);
        String originalName = file.getOriginalFilename();
        String contentType = file.getContentType();
        System.out.println("type " + contentType);
        file.transferTo(new File(dir + relativePath + originalName));
        String filePath = imageBaseUrl + "/file/" + originalName;
        FileEntity newFile = new FileEntity(null, userId, filePath);
        fileService.uploadFile(newFile);
        return AjaxResult.SUCCESS();
    }

    @GetMapping("/download")
    @ResponseBody
    public void downloadFile(@RequestParam String filename, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/octet-stream;charset=utf-8");
        response.setHeader(
                "Content-disposition",
                "attachment; filename=" + filename);

        BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(Paths.get
               (dir + "/imagehost/file/" + new String(filename.getBytes(), StandardCharsets.UTF_8))));
        System.out.println(rootDir + filename);
        // 输出流
        BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream());
        byte[] buff = new byte[1024];
        int len = 0;
        while ((len = bis.read(buff)) > 0) {
            bos.write(buff, 0, len);
        }
        bos.flush();
    }


    @GetMapping("/query")
    @ResponseBody
    public Object getFiles(HttpServletRequest request, PageQueryDto pageQuery) {
        IPage<FileEntity> res = fileService.getAllFiles(pageQuery);
        return AjaxResult.SUCCESS(res);
    }

    /**
     * admin 部分
     */

    @GetMapping("/admin/query")
    @ResponseBody
    public Object getAllFiles(PageQueryDto pageQuery) {
        IPage<FileEntity> res = fileService.getAllFiles(pageQuery);
        return AjaxResult.SUCCESS(res);
    }

    @DeleteMapping("/admin/delete/{id}")
    @ResponseBody
    public Object deleteFilesByIds(@PathVariable Integer id) {
        fileService.removeById(id);
        return AjaxResult.SUCCESS();
    }
}