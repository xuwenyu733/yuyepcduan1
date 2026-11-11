package cn.iocoder.yudao.module.fish.controller.admin.zkissue;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;

/**
 * @创建人 lr
 * @创建时间 2025-03-07
 * @描述
 */
@Tag(name = "管理后台 - 测试接口")
@RestController
@RequestMapping("/jidi/hello")
@Validated
public class JidiHelloController {

//    @PostMapping("/world")
//    public CommonResult world(@RequestBody RequestVo vo) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
//        String format = vo.getCreateTime().format(formatter);
//        System.out.println("==="+format+"========>vo:"+ JSON.toJSONString(vo));
//        vo.setMsg(vo.getMsg()+":"+format);
//
//        return CommonResult.success(vo);
//    }

}
