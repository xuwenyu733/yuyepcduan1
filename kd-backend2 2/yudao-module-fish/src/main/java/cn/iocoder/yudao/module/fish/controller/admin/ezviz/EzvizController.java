package cn.iocoder.yudao.module.fish.controller.admin.ezviz;


//import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "管理后台 - 萤石云摄像头")
@RestController
@RequestMapping("/ezviz")
@Validated
@Slf4j
public class EzvizController {

    private static final String UPSTREAM_BASE = "http://39.107.190.68:8080/api/ezviz";

    @GetMapping("/liveAddress")
    @Operation(summary = "获取直播地址")
    public Object getLiveAddress(
            @Parameter(description = "设备序列号", required = true, example = "GC1159385")
            @RequestParam("deviceSerial") String deviceSerial,
            @Parameter(description = "通道号", required = true, example = "1")
            @RequestParam("channelNo") Integer channelNo) {
        String url = UPSTREAM_BASE + "/liveAddress?deviceSerial=" + deviceSerial + "&channelNo=" + channelNo;
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
            return response.getBody();
        } catch (Exception e) {
            // 上游不可用时不抛 500「系统异常」，避免其它页面连带弹错
            log.warn("[ezviz] liveAddress upstream unavailable deviceSerial={}, channelNo={}: {}",
                    deviceSerial, channelNo, e.getMessage());
            Map<String, Object> body = new HashMap<>();
            body.put("url", null);
            body.put("msg", "萤石上游服务暂不可用");
            return body;
        }
    }

    @GetMapping("/token")
    @Operation(summary = "获取萤石云token")
    public Object getToken() {
        String url = UPSTREAM_BASE + "/token";
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Object> response = restTemplate.getForEntity(url, Object.class);
            return response.getBody();
        } catch (Exception e) {
            log.warn("[ezviz] token upstream unavailable: {}", e.getMessage());
            return Collections.singletonMap("accessToken", "");
        }
    }
}
