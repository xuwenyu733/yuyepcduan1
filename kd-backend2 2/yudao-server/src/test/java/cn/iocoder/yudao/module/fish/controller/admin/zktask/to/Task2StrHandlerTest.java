package cn.iocoder.yudao.module.fish.controller.admin.zktask.to;

import cn.iocoder.yudao.module.fish.dal.dataobject.zktaskdetail.TaskDetailDO;
import cn.iocoder.yudao.module.fish.service.zktaskdetail.TaskDetailService;
import cn.iocoder.yudao.server.YudaoServerApplication;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = YudaoServerApplication.class)
@RunWith(SpringRunner.class)
public class Task2StrHandlerTest {

    @Resource
    private Task2StrHandler task2StrHandler;

    @Resource
    private TaskDetailService taskDetailService;

    @Test
    public void parseTaskDetail_test() {
        TaskDetailDO taskDetail = taskDetailService.getTaskDetail(162L);
        // String str = task2StrHandler.parseTaskDetail(taskDetail);
        // System.out.println("str======>"+str);
        Map<String, Object> stringObjectMap = task2StrHandler.parseTaskDetail3(taskDetail);
        System.out.println("map==============>"+ JSON.toJSONString(stringObjectMap));
    }

}
