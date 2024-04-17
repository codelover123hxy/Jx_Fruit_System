package team.CowsAndHorses.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import team.CowsAndHorses.dao.FileDao;
import team.CowsAndHorses.domain.FileEntity;
import team.CowsAndHorses.dto.PageQueryDto;
import team.CowsAndHorses.service.FileService;

import java.io.File;
import java.util.List;

@RequiredArgsConstructor
@CacheConfig(cacheNames = "ExpireOneMin")
@Service
public class FileServiceImpl extends ServiceImpl<FileDao, FileEntity> implements FileService  {
    final FileDao fileDao;

    @Override
    public Integer uploadFile(FileEntity newFile) {
        return fileDao.insert(newFile);
    }
    @Override
    public IPage<FileEntity> getAllFiles(PageQueryDto pageQuery) {
        Page<FileEntity> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        return fileDao.selectPage(page, null);
    }
    @Override
    public Integer deleteFilesByIds(List<Integer> ids) {
        try {
            for (Integer id : ids) {
                FileEntity fileInfo = fileDao.selectById(id);
                String fileUrl = fileInfo.getFilename();
                String relativePath = "/www/imagehost/file/";
                String filename = fileUrl.substring(fileUrl.lastIndexOf("/"));
                File file = new File(relativePath + filename);
                System.out.println(relativePath + filename);
                boolean flag = file.delete();
                if (!flag) {
                    return 0;
                }
            }
            return fileDao.deleteBatchIds(ids);
        } catch(Exception e) {
            return 0;
        }
    }
}
