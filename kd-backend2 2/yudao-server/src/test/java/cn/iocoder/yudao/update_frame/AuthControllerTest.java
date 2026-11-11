package cn.iocoder.yudao.update_frame;


import cn.iocoder.yudao.module.system.dal.dataobject.permission.MenuDO;
import cn.iocoder.yudao.module.system.dal.mysql.permission.MenuMapper;
import cn.iocoder.yudao.server.YudaoServerApplication;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = YudaoServerApplication.class)
@RunWith(SpringRunner.class)
public class AuthControllerTest {

    @Autowired
    private MenuMapper menuMapper;


    @Test
    public void testInsertMenuSon() {
        // String path = "E:\\Use\\t1/menu_all_son_list.txt";
        String path = "E:\\Use\\t1/menu.txt";
        List<MenuDO> list = new ArrayList<>();
        List<MenuDO> needAdd = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {  // 按行读取文件内容
                List<MenuDO> menuDOS = JSON.parseArray(line, MenuDO.class);
                list.addAll(menuDOS);
                // System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("读取文件时发生错误：" + e.getMessage());
        }
        Set<String> collect = list.stream().map(obj -> obj.getParentName()).collect(Collectors.toSet());
        List<MenuDO> parentMenuList = selectParent(collect);
        list.forEach(obj -> enhancerParentName(parentMenuList, obj));
        list.stream().filter(obj -> !StringUtils.equals("存塘记录", obj.getName())).forEach(obj -> {
            // obj.setParentId(5061L);
            obj.setId(null);
            needAdd.add(obj);
        });
        System.out.println("needAdd:"+JSON.toJSONString(needAdd));
        Boolean b = menuMapper.insertBatch(needAdd);
        // int insert = menuMapper.insert(obj);
         System.out.println(String.format("%s insert : %s", needAdd.size()  , b));
    }
    public MenuDO enhancerParentName(List<MenuDO> menuDOS, MenuDO item) {
//        for(MenuDO local : menuDOS) {
//            if(StringUtils.equals(item.getParentName(), local.getName())) {
//                item.setParentId(local.getId());
//                return item;
//            }
//        }
        item.setParentId(5537L);
        return item;
    }
    public List<MenuDO> selectParent(Set<String> nameSet) {
        try {
            QueryWrapper<MenuDO> queryWrapper = new QueryWrapper<>();
            // List<String> nameList = Arrays.asList("知识库", "设备与监控", "任务与通知", "申请与审批", "人事管理");
            // queryWrapper.eq("name", "知识库");
            queryWrapper.in("name", nameSet);
            // queryWrapper.in("id", idSet);
            List<MenuDO> menuDOS = menuMapper.selectList(queryWrapper);
            return menuDOS;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testInsertMenu() {
        String path = "E:\\Use\\t1/menu.txt";
        List<MenuDO> list = new ArrayList<>();
        List<MenuDO> needAdd = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {  // 按行读取文件内容
                List<MenuDO> menuDOS = JSON.parseArray(line, MenuDO.class);
                list.addAll(menuDOS);
                // System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("读取文件时发生错误：" + e.getMessage());
        }
        list.stream().filter(obj -> !StringUtils.equals("存塘记录", obj.getName())).forEach(obj -> {
            obj.setParentId(5061L);
            obj.setId(null);
            String component = obj.getComponent();
            component = component.replaceAll("pl/", "fish/");
            obj.setComponent(component);
            needAdd.add(obj);
            int insert = menuMapper.insert(obj);
            System.out.println(String.format("%s insert : %s", obj.getName(), insert));
        });
    }

    @Test
    public void test_updateMenu() {
        QueryWrapper<MenuDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("id", 5063L);
        List<MenuDO> menuDOS = menuMapper.selectList(queryWrapper);
        for(MenuDO item : menuDOS) {
            String component = item.getComponent();
            component = component.replaceAll("pl/", "fish/");
            item.setComponent(component);
            int i = menuMapper.updateById(item);
            System.out.println(String.format("%s update : %s", item.getName(), i));
        }
    }

}
