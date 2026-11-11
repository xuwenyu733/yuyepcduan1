package cn.iocoder.yudao.update_frame;

import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictTypeDO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.MenuDO;
import cn.iocoder.yudao.module.system.dal.mysql.dict.DictDataMapper;
import cn.iocoder.yudao.server.YudaoServerApplication;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = YudaoServerApplication.class)
@RunWith(SpringRunner.class)
public class DictDataControllerTest {
    @Autowired
    private DictDataMapper dictDataMapper;

//    @Test
//    public void test_save() {
//        String path = "E:\\Use\\t1/menu_all_son_list.txt";
//        List<DictDataDO> list = new ArrayList<>();
//        List<DictDataDO> needAdd = new ArrayList<>();
//        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
//            String line;
//            while ((line = br.readLine()) != null) {  // 按行读取文件内容
//                List<DictDataDO> tempList = JSON.parseArray(line, DictDataDO.class);
//                list.addAll(tempList);
//                // System.out.println(line);
//            }
//        } catch (Exception e) {
//            System.out.println("读取文件时发生错误：" + e.getMessage());
//        }
//        Set<String> collect = list.stream().map(obj -> obj.getDictType()).collect(Collectors.toSet());
//        List<DictDataDO> parentList = selectParent(collect);
//        list.forEach(obj -> enhancerParentName(parentList, obj));
//        list.stream().forEach(obj -> {
//            // obj.setParentId(5061L);
//            obj.setId(null);
//            needAdd.add(obj);
//        });
//        System.out.println("needAdd:"+JSON.toJSONString(needAdd));
//        Boolean b = dictDataMapper.insertBatch(needAdd);
//        // int insert = menuMapper.insert(obj);
//        System.out.println(String.format("%s insert : %s", needAdd.size()  , b));
//    }
//    public DictDataDO enhancerParentName(List<DictTypeDO> parentList, DictDataDO item) {
//        for(DictTypeDO local : parentList) {
//            if(StringUtils.equals(item.getDictType(), local.getType())) {
//                // item.setParentId(local.getId());
//                return item;
//            }
//        }
//        return item;
//    }
//    public List<DictDataDO> selectParent(Set<String> nameSet) {
//        try {
//            QueryWrapper<DictDataDO> queryWrapper = new QueryWrapper<>();
//            // List<String> nameList = Arrays.asList("知识库", "设备与监控", "任务与通知", "申请与审批", "人事管理");
//            // queryWrapper.eq("name", "知识库");
//            queryWrapper.in("name", nameSet);
//            // queryWrapper.in("id", idSet);
//            List<DictDataDO> menuDOS = dictDataMapper.selectList(queryWrapper);
//            return menuDOS;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

}
