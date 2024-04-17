package team.CowsAndHorses.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import team.CowsAndHorses.domain.FileEntity;
import team.CowsAndHorses.dto.PageQueryDto;

import java.util.List;

@Transactional
public interface FileService extends IService<FileEntity> {
    Integer uploadFile(FileEntity newFile);
    IPage<FileEntity> getAllFiles(PageQueryDto pageQuery);
    Integer deleteFilesByIds(List<Integer> ids);
}
