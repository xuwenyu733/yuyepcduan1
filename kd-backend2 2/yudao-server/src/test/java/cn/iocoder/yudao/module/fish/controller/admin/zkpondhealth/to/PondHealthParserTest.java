package cn.iocoder.yudao.module.fish.controller.admin.zkpondhealth.to;


import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevicerecord.ZkDeviceRecordDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zktask.ZkTaskDO;
import cn.iocoder.yudao.module.fish.service.zkdevicerecord.ZkDeviceRecordService;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import cn.iocoder.yudao.module.fish.service.zktaskdetail.TaskDetailService;
import cn.iocoder.yudao.server.YudaoServerApplication;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = YudaoServerApplication.class)
@RunWith(SpringRunner.class)
public class PondHealthParserTest {

    @Resource
    private PondHealthParser pondHealthParser;
    @Resource
    private ZkPondService zkPondService;
    @Resource
    private ZkDeviceRecordService zkDeviceRecordService;
    @Resource
    private TaskDetailService taskDetailService;


    @Test
    public void test1() {
        Long pondId = 331L;
        ZkPondDO zkPond = zkPondService.getZkPond(pondId);
        ZkDeviceRecordDO zkDeviceRecord = zkDeviceRecordService.getZkDeviceRecord(3L);
        List<ZkTaskDO> taskList = taskDetailService.getByTypesAndPondId(List.of("11", "31"), 329L);
        pondHealthParser.exeParser(zkPond, List.of(zkDeviceRecord), taskList);
    }

}
