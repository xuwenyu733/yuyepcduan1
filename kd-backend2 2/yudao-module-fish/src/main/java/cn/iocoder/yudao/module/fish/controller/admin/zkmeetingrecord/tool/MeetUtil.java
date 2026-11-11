package cn.iocoder.yudao.module.fish.controller.admin.zkmeetingrecord.tool;

import cn.hutool.http.HttpUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyMap3;
import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class MeetUtil {

    @Value("${KEDE_COMMON_API_URI}")
    private String KEDE_COMMON_API_URI;

    @Value("${KEDE_COMMON_API_PREUUID}")
    private String KEDE_COMMON_API_PREUUID;

    public static final String GET_VIDEO_URLS_URI = "/meet/selectVideoUrls";

    /**
     * 请求[会议服务器的kede_common_api]获取会议录屏
     * @param meetNum 会议号
     * @param meetDay 会议日期
     * @return 会议录屏url
     */
    @SuppressWarnings("unchecked")
    public String selectVideoUrls(String meetNum, String meetDay) {
        try {
            Map<String, String> value = new EasyMap3<String>().put("meetNum", meetNum)
                    .put("logDay", meetDay)
                    .put("preUUID", KEDE_COMMON_API_PREUUID).value();
            String url = String.format("%s%s", KEDE_COMMON_API_URI, GET_VIDEO_URLS_URI);
            String post = HttpUtil.post(url, JSON.toJSONString(value));
            log.warn("【会议获取video urls-{}】middle 1,会议信息:{}_{}，获取video urls:{}", TokenUtil.userInfo(), meetNum, meetDay, post);
            if(StringUtil.isEmpty(post)) return "";
            JSONObject jsonObject = JSON.parseObject(post);
            Integer code = jsonObject.getInteger("code");
            if(Objects.equals(code, 200)) {
                String data = jsonObject.getString("data");
                log.warn("【会议获取video urls-{}】middle 2,会议信息:{}_{}，获取video urls:{}", TokenUtil.userInfo(), meetNum, meetDay, data);
                return data;
            } else return "";
        } catch (Exception e) {
            String sub = String.format("【请求[会议服务器的kede_common_api]获取会议录屏】异常 meetNum:%s, meetDay:%s", meetNum, meetDay);
            String error_msg = String.format("error: %s", ExceptionUtil.getStackTrace(e));
            EmailUtil.send(sub, error_msg);
            log.error(sub, error_msg);
        }
        return "";
    }

}
