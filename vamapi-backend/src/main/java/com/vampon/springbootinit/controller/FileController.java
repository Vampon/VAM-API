package com.vampon.springbootinit.controller;

import cn.hutool.core.io.FileUtil;
import com.vampon.springbootinit.common.BaseResponse;
import com.vampon.springbootinit.common.ErrorCode;
import com.vampon.springbootinit.common.ResultUtils;
import com.vampon.springbootinit.constant.FileConstant;
import com.vampon.springbootinit.exception.BusinessException;
import com.vampon.springbootinit.manager.CosManager;
import com.vampon.springbootinit.model.dto.file.UploadFileRequest;
import com.vampon.springbootinit.model.enums.FileUploadBizEnum;
import com.vampon.springbootinit.service.FileService;
import com.vampon.springbootinit.service.UserService;
import java.io.File;
import java.util.Arrays;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.vampon.vamapicommon.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件接口
 *
 * @author vampon
 * 
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private UserService userService;

    @Resource
    private FileService fileService;

    /**
     * 文件上传
     *
     * @param multipartFile
     * @param uploadFileRequest
     * @param request
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
            UploadFileRequest uploadFileRequest, HttpServletRequest request) {
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        fileService.validFile(multipartFile, fileUploadBizEnum);
        User loginUser = userService.getLoginUser(request);
        if(loginUser == null){
             throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return ResultUtils.success(fileService.uploadFile(multipartFile, fileUploadBizEnum, loginUser));
    }


}
