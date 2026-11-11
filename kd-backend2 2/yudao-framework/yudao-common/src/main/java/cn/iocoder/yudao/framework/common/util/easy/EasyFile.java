package cn.iocoder.yudao.framework.common.util.easy;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.iocoder.yudao.framework.common.exception.CommonException;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.framework.common.util.io.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * @创建人 lr
 * @创建时间 2025-03-14
 * @描述
 */
@Component
@Slf4j
public class EasyFile {

    public static String outPutFilePath;

    /**
     * 根据文件URL下载文件，返回文件的绝对路径
     * @param url
     * @return
     */
    public static String downloadByUrl(String url) {
        try {
            String fileName = getNameByUrl(url);
            String download = FileUtils.download(url, outPutFilePath, fileName);
            return download;
        } catch (IOException e) {
            throw new CommonException(500, String.format("下载文件，出现错误URL:%s,具体异常信息:%s", url, ExceptionUtil.getStackTrace(e)));
        }
    }

    /**
     * 获取文件名
     * @param url
     * @return
     */
    public static String getNameByUrl(String url) {
        File file = new File(url);
        String name = file.getName();
        if(StringUtil.isEmpty(name) || name.indexOf(".") == -1)
            throw new CommonException(403, String.format("下载文件，发现错误URL:%s", url));
        String suffix = name.split("\\.")[1];
        String fileName = new StringBuilder(StringUtil.getFileName())
                .append("_gen_.")
                .append(suffix).toString();
        return fileName;
    }

    /**
     * 删除文件
     * @param download
     */
    public static void remove(String download) {
        try {
            FileUtil.del(download);
        } catch (IORuntimeException e) {
            log.warn("【删除文件出现错误】middle,该错误不用管，不删除不会影响正常业务,异常信息:{}", ExceptionUtil.getStackTrace(e));
        }
    }

    @Value("${outPutFilePath}")
    public void setOutPutFilePath(String outPutFilePath) {
        EasyFile.outPutFilePath = outPutFilePath;
    }
}
