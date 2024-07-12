package com.vampon.springbootinit.service.impl;

import cn.hutool.core.io.FileUtil;
import com.vampon.springbootinit.common.ErrorCode;
import com.vampon.springbootinit.constant.FileConstant;
import com.vampon.springbootinit.exception.BusinessException;
import com.vampon.springbootinit.manager.CosManager;
import com.vampon.springbootinit.model.enums.FileUploadBizEnum;
import com.vampon.springbootinit.service.FileService;
import com.vampon.vamapicommon.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;

@Service
@Slf4j
public class FileServiceImpl implements FileService {

    @Resource
    private CosManager cosManager;

    @Override
    public String uploadFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum, User user) {
        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), user.getId(), filename);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            cosManager.putObject(filepath, file);
            // 返回可访问地址
            return FileConstant.COS_HOST + filepath;
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    @Override
    public String uploadFile(File file, FileUploadBizEnum fileUploadBizEnum, User user) {
        // 文件目录：根据业务、用户来划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        // 这里为了保持用户的凭证文件唯一，前面就不加uuid了
        String filename = "credentials_" + user.getId() + ".csv";
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), user.getId(), filename);
        try {
            // 上传文件
            cosManager.putObject(filepath, file);
            // 返回可访问地址
            return FileConstant.COS_HOST + filepath;
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    @Override
    public void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 1024 * 1024L;
        // 根据业务类型不同校验不同
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)||FileUploadBizEnum.INTERFACE_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
        }else if(FileUploadBizEnum.USER_VOUCHER.equals(fileUploadBizEnum)){
            if (fileSize > ONE_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
            }
            if (!Arrays.asList("csv", "txt").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
        }
    }
}
