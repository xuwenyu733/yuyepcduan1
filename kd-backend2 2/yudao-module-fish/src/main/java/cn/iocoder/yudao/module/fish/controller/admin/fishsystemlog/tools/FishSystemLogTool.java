package cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.tools;

import cn.iocoder.yudao.module.fish.dal.dataobject.fishsystemlog.FishSystemLogDO;
import cn.iocoder.yudao.module.fish.dal.mysql.fishsystemlog.FishSystemLogMapper;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FishSystemLogTool {

    private static FishSystemLogMapper fishSystemLogMapper;

    public static Integer gen(String pondDetailNum, String logTitle, String originData, String targetData, String originRes, String targetRes) {
        FishSystemLogDO item = new FishSystemLogDO(pondDetailNum, logTitle, originData, targetData, originRes, targetRes);
        return gen(item);
    }
    public static Integer gen(FishSystemLogDO item) {
        int insert = fishSystemLogMapper.insert(item);
        log.warn("【添加fish操作】middle,insert:{},data:{}", insert, JSON.toJSONString(item));
        return insert;
    }

    @Autowired
    public void setFishSystemLogMapper(FishSystemLogMapper fishSystemLogMapper) {
        FishSystemLogTool.fishSystemLogMapper = fishSystemLogMapper;
    }
}
