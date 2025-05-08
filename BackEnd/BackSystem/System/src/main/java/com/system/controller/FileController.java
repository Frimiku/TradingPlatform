package com.system.controller;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import com.system.common.AuthAccess;
import com.system.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

@RestController
@RequestMapping("/file")
public class FileController {
    //文件根目录
    // D:\Engineering\warehouse\SystemFrameworkUI\BackSystem\BackManagement\files
    private static final String ROOT_PATH = System.getProperty("user.dir") + File.separator + "files";

    //上传文件
   @PostMapping("/upload")
   public Result upload(MultipartFile file) throws IOException {
       String originalFilename = file.getOriginalFilename();//文件的原始名称.实际存储到磁盘的文件名称

       //1.获取文件名和后缀【文件名：student.png】
       String mainName = FileUtil.mainName(originalFilename);//不包含后缀的文件名称 => student
       String fileName = file.getOriginalFilename().toString();
       String extName = fileName.substring(fileName.lastIndexOf("."));//获取文件后缀名 => .png
       String filePath = System.getProperty("user.dir");

       //2.传过来的file转换为路径
       if (!FileUtil.exist(ROOT_PATH)){//文件存储的父级目录
           //文件存储的父级不存在，就要创建
           FileUtil.mkdir(ROOT_PATH);
       }
       //判断当前上传的文件是否已经存在了，若存在则要重命名一个文件名称
       //File.separator:分隔符(//)
       if (FileUtil.exist(ROOT_PATH + File.separator + originalFilename)){
           originalFilename = System.currentTimeMillis() + "_" + mainName + extName;
       }
       //D:\Engineering\warehouse\SystemFrameworkUI\BackSystem\BackManagement\files\12315312_student.png
       File saveFile = new File(ROOT_PATH + File.separator + originalFilename);
       file.transferTo(saveFile); // 存储文件到本地磁盘
       //返回文件的链接
       String url = "http://localhost:8080/file/download/" + originalFilename;

       //3.返回文件的链接，这个链接就是文件下载地址，这个文件下载地址就是从后台提供出来的
       return Result.success(url);
   }

    //下载文件
    @AuthAccess
    @GetMapping("/download/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) throws IOException {
       // 附件下载
       //response.addHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(fileName,"UTF-8"));
       // 预览
        response.addHeader("Content-Disposition","inline;filename="+ URLEncoder.encode(fileName,"UTF-8"));
       //字节流数组获取
        String filePath = ROOT_PATH + File.separator + fileName;
        if (!FileUtil.exist(filePath)){
            return;
        }
        byte[] bytes = FileUtil.readBytes(filePath);
        ServletOutputStream outputStream = response.getOutputStream();//io流
        outputStream.write(bytes); //是一个字节数组，也就是文件的字节流数组
        outputStream.flush();//刷新
        outputStream.close();//关闭文件流
    }

    // 富文本框上传文件接口
    @PostMapping("/editor/upload")
    public Dict editorUpload(@RequestParam MultipartFile file,@RequestParam String type) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String mainName = FileUtil.mainName(originalFilename);//不包含后缀的文件名称 => student
        String extName = FileUtil.extName(originalFilename);//获取文件后缀名 => .png
        String filePath = System.getProperty("user.dir");

        if (!FileUtil.exist(ROOT_PATH)){
            FileUtil.mkdir(ROOT_PATH);
        }
        if (FileUtil.exist(ROOT_PATH + File.separator + originalFilename)){
            originalFilename = System.currentTimeMillis() + "_" + mainName + extName;
        }
        //D:\Engineering\warehouse\SystemFrameworkUI\BackSystem\BackManagement\files\12315312_student.png
        File saveFile = new File(ROOT_PATH + File.separator + originalFilename);
        file.transferTo(saveFile); // 存储文件到本地磁盘
        //返回文件的链接
        String url = "http://localhost:8080/file/download/" + originalFilename;
        if ("img".equals(type)){
            // 图片
            return  Dict.create().set("errno",0).set("data", CollUtil.newArrayList(Dict.create().set("url",url)));
        }else if("video".equals(type)) {
            // 视频
            return Dict.create().set("errno",0).set("data",Dict.create().set("url",url));
        }
        // 都不是，则返回一个没有数据的信息
        return Dict.create().set("errno",0);
    }
}