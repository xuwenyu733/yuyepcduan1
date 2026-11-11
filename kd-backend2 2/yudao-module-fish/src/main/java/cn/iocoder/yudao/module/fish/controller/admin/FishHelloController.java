package cn.iocoder.yudao.module.fish.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.easy.EmailUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - Test")
@RestController
@RequestMapping("/fish/hello")
@Validated
public class FishHelloController {

    // 这个构造方法，只是方便大家，验证 Controller 有生效
    public FishHelloController() {
        System.out.println(getClass() + "生效啦！！！");
    }

    @GetMapping("/world")
    @Operation(summary = "获取 test 信息")
    public CommonResult<String> world() {
        EmailUtil.send("test", "test123");
        System.out.println(getClass() + "生效啦！！！");
        return success("true");
    }

}

