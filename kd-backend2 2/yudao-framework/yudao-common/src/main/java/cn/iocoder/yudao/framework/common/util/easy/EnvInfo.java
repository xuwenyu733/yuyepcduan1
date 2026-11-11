package cn.iocoder.yudao.framework.common.util.easy;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @创建人 lr
 * @创建时间 2024-04-25
 * @描述
 */
@Component
@Slf4j
@Order(0)
public class EnvInfo {

    public static String profilesActive;
    public static String applicationName;

    public static String tempFilePath;

    public static String datasourceUrl;

    @Value("${spring.profiles.active}")
    public void setProfilesActive(String profilesActive) {
        EnvInfo.profilesActive = profilesActive;
        log.warn("【系统启动】middle,profilesActive:{}",profilesActive);
    }

    @Value("${spring.application.name}")
    public void setApplicationName(String applicationName) {
        EnvInfo.applicationName = applicationName;
        log.warn("【系统启动】middle,applicationName:{}",applicationName);
    }

    @Value("${outPutFilePath}")
    public void setTempFilePath(String tempFilePath) {
        log.warn("【系统启动】middle,setTempFilePath:{}", tempFilePath);
        EnvInfo.tempFilePath = tempFilePath;
    }

    @Value("${spring.datasource.dynamic.datasource.master.url:}")
    public void setDatasourceUrl(String datasourceUrl) {
        EnvInfo.datasourceUrl = datasourceUrl;
        log.warn("【系统启动】middle,datasourceUrl:{}",datasourceUrl);
    }

    public static Boolean isDev() {
        return false;
        // List<String> list = Arrays.asList("lr", "local");
        // return list.contains(profilesActive);
    }

    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        return request;
    }


    /**
     * 获取一个系统缓存文件的路径，直接在当前结果后面加文件名就好了
     * @return eg:   /opt/2025-12-31/37372724747_  所以直接添加你自己的文件名就好了
     */
    public static String genPathPrefix() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String currDay = sdf.format(date);
        return String.format("%s/%s/%s_", tempFilePath, currDay, date.getTime());
    }

    /**
     * 根据文件名生成一个 可以缓存文件的路径
     * @param suffix 文件名， eg: 巡检.pdf
     * @return eg:   /opt/2025-12-31/37372724747_巡检.pdf  所以直接用该地址存储文件就好了
     */
    public static String genPath(String suffix) {
        String prefix = genPathPrefix();
        String format = String.format("%s%s", prefix, suffix);
        File file = new File(format).getParentFile();
        if(!file.exists()) file.mkdirs();
        return format;
    }

}
