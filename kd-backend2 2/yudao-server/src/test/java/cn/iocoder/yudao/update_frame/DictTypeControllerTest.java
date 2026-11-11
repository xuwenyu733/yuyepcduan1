package cn.iocoder.yudao.update_frame;

import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictDataDO;
import cn.iocoder.yudao.module.system.dal.dataobject.dict.DictTypeDO;
import cn.iocoder.yudao.module.system.dal.dataobject.permission.MenuDO;
import cn.iocoder.yudao.module.system.dal.mysql.dict.DictDataMapper;
import cn.iocoder.yudao.module.system.dal.mysql.dict.DictTypeMapper;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = YudaoServerApplication.class)
@RunWith(SpringRunner.class)
public class DictTypeControllerTest {
    @Autowired
    private DictTypeMapper dictTypeMapper;
    @Autowired
    private DictDataMapper dictDataMapper;

    @Test
    public void testInsertMenu() {
        String path = "E:\\Use\\t1/dictType.txt";
        List<DictTypeDO> list = new ArrayList<>();
        List<DictTypeDO> needAdd = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {  // 按行读取文件内容
                List<DictTypeDO> menuDOS = JSON.parseArray(line, DictTypeDO.class);
                list.addAll(menuDOS);
                // System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("读取文件时发生错误：" + e.getMessage());
        }
        list.stream().filter(obj -> !StringUtils.equals("存塘记录", obj.getName())).forEach(obj -> {
            obj.setId(null);
            needAdd.add(obj);
        });
        Boolean b = dictTypeMapper.insertBatch(needAdd, needAdd.size());
        System.out.println(String.format("%s insert : %s", needAdd.size(), b));
    }

    @Test
    public void test_saveSon() {
        String path = "E:\\Use\\t1/dictData.txt";
        List<DictDataDO> list = new ArrayList<>();
        List<DictDataDO> needAdd = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {  // 按行读取文件内容
                List<DictDataDO> menuDOS = JSON.parseArray(line, DictDataDO.class);
                list.addAll(menuDOS);
                // System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("读取文件时发生错误：" + e.getMessage());
        }
        list.stream().forEach(obj -> {
            obj.setId(null);
            needAdd.add(obj);
        });
        System.out.println("================>"+JSON.toJSONString(needAdd));
        Boolean b = dictDataMapper.insertBatch(needAdd, needAdd.size());
        System.out.println(String.format("%s insert : %s", needAdd.size(), b));
    }

}
