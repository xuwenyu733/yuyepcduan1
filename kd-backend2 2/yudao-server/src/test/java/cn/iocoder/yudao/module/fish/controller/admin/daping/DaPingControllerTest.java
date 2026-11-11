package cn.iocoder.yudao.module.fish.controller.admin.daping;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.fish.controller.admin.daping.fishbatch.FishTree;
import cn.iocoder.yudao.server.YudaoServerApplication;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = YudaoServerApplication.class)
@RunWith(SpringRunner.class)
public class DaPingControllerTest {
    @Resource
    private DaPingController daPingController;

    @Test
    public void getFishBatch() {
        CommonResult<List<FishTree>> fishBatch = daPingController.getFishBatch("7");
        System.out.println("test res is :" + JSON.toJSONString(fishBatch));
    }

}
