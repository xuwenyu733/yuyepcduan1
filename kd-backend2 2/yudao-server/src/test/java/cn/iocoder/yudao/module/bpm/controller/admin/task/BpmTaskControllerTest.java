package cn.iocoder.yudao.module.bpm.controller.admin.task;

import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.bpm.controller.admin.task.vo.task.BpmTaskRespVO;
import cn.iocoder.yudao.server.YudaoServerApplication;
import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.flowable.task.api.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = YudaoServerApplication.class)
@RunWith(SpringRunner.class)
public class BpmTaskControllerTest {

    @Resource
    private BpmTaskController bpmTaskController;

    @Test
    public void notifyTest() {
        // List<BpmTaskRespVO> tasks = bpmTaskController.notifyTodoList(144L);
        // if(EasyListUtil.isEmpty(tasks)) return;
        // BpmTaskRespVO currTask = tasks.get(0);
        // String msg = String.format("%s于%s发起%s流程，请尽快审批！", currTask.getProcessInstance().getStartUser().getNickname(),
        //         EasyDateUtil.format(currTask.getProcessInstance().getCreateTime(), "yyyy-MM-dd HH:mm:ss"),
        //         currTask.getProcessInstance().getName());
        // System.out.println("tasks=======>"+ msg);
    }

}
