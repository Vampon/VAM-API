package com.vampon.springbootinit.service;

import com.vampon.springbootinit.model.dto.file.UploadFileRequest;
import com.vampon.springbootinit.model.enums.FileUploadBizEnum;
import com.vampon.vamapicommon.model.entity.User;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * 文件上传服务
 */
public interface FileService {

    /**
     * 文件上传
     *
     * @param multipartFile
     * @param fileUploadBizEnum
     * @param user
     * @return
     */
    String uploadFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum, User user);

    /**
     * 文件上传重载
     * @param file
     * @param fileUploadBizEnum
     * @param user
     * @return
     */
    String uploadFile(File file, FileUploadBizEnum fileUploadBizEnum, User user);

    /**
     * 校验文件
     *
     * @param multipartFile
     * @param fileUploadBizEnum 业务类型
     */
    void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum);

}
