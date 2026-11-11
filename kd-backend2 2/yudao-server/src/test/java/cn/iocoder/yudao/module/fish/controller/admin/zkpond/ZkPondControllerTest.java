package cn.iocoder.yudao.module.fish.controller.admin.zkpond;

import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondfish.ZkPondFishDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpond.ZkPondMapper;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpondfish.ZkPondFishMapper;
import cn.iocoder.yudao.server.YudaoServerApplication;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = YudaoServerApplication.class)
@RunWith(SpringRunner.class)
public class ZkPondControllerTest {
    @Resource
    private ZkPondMapper zkPondMapper;
    @Resource
    private ZkPondFishMapper zkPondFishMapper;

    @Test
    public void test() {
        QueryWrapper<ZkPondDO> queryWrapper = new QueryWrapper<>();
        List<ZkPondDO> zkPondDOS = zkPondMapper.selectList(queryWrapper);
        zkPondDOS.forEach(obj -> obj.setCreator("1"));
        Boolean b = zkPondMapper.updateBatch(zkPondDOS);
        System.out.println(String.format("size:%s update %s", zkPondDOS.size(), b));
    }

    @Test
    public void test2() {
        QueryWrapper<ZkPondFishMapper> queryWrapper = new QueryWrapper<>();
        List<ZkPondFishDO> zkPondDOS = zkPondFishMapper.selectList();
        zkPondDOS.forEach(obj -> obj.setCreator("1"));
        Boolean b = zkPondFishMapper.updateBatch(zkPondDOS);
        System.out.println(String.format("size:%s update %s", zkPondDOS.size(), b));
    }

}
