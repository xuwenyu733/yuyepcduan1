package cn.iocoder.yudao.module.fish.controller.jidi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RequestMapping("/hello")
@RestController
public class HelloController {

    @GetMapping("/world")
    public String world() {
        String res = String.format("收到hello请求，当前时间:%s", new Date());
        System.out.println(res);
        return res;
    }

}
