package com.Toukui.controller;

import com.Toukui.pojo.Pl;
import com.Toukui.pojo.Result;
import com.Toukui.pojo.Zp;
import com.Toukui.service.ZpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class ZpController {
    private static final Logger log = LoggerFactory.getLogger(ZpController.class);
    @Autowired
    private ZpService zpService;

    /**
     * 发布作品
     */
    @PostMapping("/setzp")
    @CrossOrigin
    public Result setNewZp(@RequestParam("id") String id, @RequestParam("title") String title, @RequestParam("content") String content, @RequestParam("file") MultipartFile file) {
        try {
            log.info("开始处理作品发布请求，用户ID: {}", id);
            
            // 将文件转换为byte[]数组
            byte[] zpimg = file.getBytes();
            log.info("图片文件已读取，大小: {}字节", zpimg.length);
            
            // 获取当前时间
            String zptime = getCurrentTime();
            
            // 添加作品到数据库
            log.info("准备将作品信息保存到数据库");
            int res = zpService.setNewZp(id, title, content, zpimg, zptime);
            
            if (res > 0) {
                log.info("作品发布成功，用户ID: {}", id);
                return Result.success();
            } else {
                log.warn("作品发布失败：数据库操作失败，用户ID: {}", id);
                return Result.error("发布失败：数据库操作失败");
            }
        } catch (Exception e) {
            // 记录错误信息
            log.error("作品发布异常，用户ID: {}", id, e);
            return Result.error("发布失败：" + e.getMessage());
        }
    }

    /**
     * 获取全部作品
     * @return
     */
    @GetMapping("/getallzp")
    @CrossOrigin
    public Result getAllZp() {
        List<Zp> zpList = zpService.getAllZp();
        if (zpList.size() > 0) {
            return Result.success(zpList);
        } else {
            return Result.error("暂无作品");
        }
    }

    /**
     *更新5条数据
     */
    @GetMapping("/getMore")
    @CrossOrigin
    public Result getMoreZp(@RequestParam int start, @RequestParam int limit) {
        List<Zp> zpList = zpService.getMoreZp(start, limit);
        if (zpList.size() > 0) {
            return Result.success(zpList);
        } else {
            return Result.error("暂无更多作品");
        }
    }

    /**
     * 通过作品id获取单个作品信息
     */
    @GetMapping("/getzpinfo")
    @CrossOrigin
    private Result getZpByZpid(String zpid) {
        List<Zp> zpitem = zpService.getZpByZpid(zpid);
        if (zpitem.size() > 0) {
            return Result.success(zpitem.get(0));
        } else {
            return Result.error("暂无作品");
        }
    }

    /**
     * 作品点赞
     */
    @PostMapping("/dz")
    @CrossOrigin
    private Result HandleDz(String id, String zpid) {
        int res = zpService.HandleDz(zpid);
        int res2 = zpService.HandleAddDz(id, zpid);
        if (res > 0 && res2 > 0) {
            return Result.success();
        } else {
            return Result.error("点赞失败");
        }
    }

    /**
     * 获取用户点赞的作品id
     */
    @GetMapping("/getdz")
    @CrossOrigin
    private Result GetDz(String id) {
        List<String> res = zpService.getDz(id);
        if (res.size() > 0) {
            return Result.success(res);
        } else {
            return Result.error("获取失败");
        }
    }

    /**
     * 用户取消点赞
     */
    @PostMapping("/cancledz")
    @CrossOrigin
    private Result CancleDz(String id, String zpid) {
        int res = zpService.CancleDz(zpid);
        int res2 = zpService.DeleteDzInfo(id, zpid);
        if (res > 0 && res2 > 0) {
            return Result.success();
        } else {
            return Result.error("点赞失败");
        }
    }

    /**
     * 添加作品评论
     */
    @PostMapping("/pl")
    @CrossOrigin
    private Result HandlePl(String zpid, String userid, String content) {
        String zptime = getCurrentTime();
        int res = zpService.HandlePl(zpid, userid, content, zptime);
        int res2 = zpService.AddPlNum(zpid);
        if (res > 0 && res2 > 0) {
            return Result.success();
        } else {
            return Result.error("点赞失败");
        }
    }

    /**
     * 获取作品评论
     */
    @GetMapping ("/getpl")
    @CrossOrigin
    private Result getPlByZpid(String zpid) {
        List<Pl> res = zpService.getPlByZpid(zpid);
        if (res.size() > 0) {
            return Result.success(res);
        } else {
            return Result.error("获取失败");
        }
    }



    /**
     * 压缩图片
     * @param originalImageData
     * @return
     * @throws IOException
     */
    private byte[] compressImage(byte[] originalImageData) throws IOException {
    
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(new ByteArrayInputStream(originalImageData))
                .scale(1) // 不改变图片尺寸
                .outputQuality(0.4) // 设置压缩后的图片质量
                .outputFormat("jpg") // 设置输出格式，可根据需要修改
                .toOutputStream(outputStream);

        return outputStream.toByteArray();
    }

    /**
     * 获取当前时间 - 返回数据库兼容的格式
     * @return
     */
    public String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
}
