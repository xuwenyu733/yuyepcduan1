package cn.iocoder.yudao.module.fish.dal.mysql.zkfishoperationlog;


import cn.iocoder.yudao.server.YudaoServerApplication;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = YudaoServerApplication.class)
@RunWith(SpringRunner.class)
public class ZkFishOperationLogMapperTest {

    @Autowired
    private ZkFishOperationLogMapper zkFishOperationLogMapper;

    @Test
    public void test() {
        List<Map<String, Object>> maps = zkFishOperationLogMapper.staticCost();
        System.out.println(JSON.toJSONString(maps));
    }

}
