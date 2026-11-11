package cn.iocoder.yudao.module.fish.controller.admin.zkfishoperationlog;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkfishoperationlog.ZkFishOperationLogDO;
import cn.iocoder.yudao.server.YudaoServerApplication;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = YudaoServerApplication.class)
@RunWith(SpringRunner.class)
public class ZkFishOperationLogControllerTest {
    @Autowired
    private ZkFishOperationLogController zkFishOperationLogController;

    @Test
    public void genLog() {
        List<ZkFishOperationLogDO> zkFishOperationLogDOS = zkFishOperationLogController.genLog();
        System.out.println("genLog=====>"+ JSON.toJSONString(zkFishOperationLogDOS));
    }

}
