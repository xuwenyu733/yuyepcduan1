package cn.iocoder.yudao.module.fish.service.zkdevice;

import cn.iocoder.yudao.framework.common.base_api.BaseDeptService;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyMap;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.fish.controller.admin.zkdevice.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdevice.ZkDeviceDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkdevice.ZkDeviceConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkdevice.ZkDeviceMapper;
import cn.iocoder.yudao.module.fish.utils.DeptScopeSupport;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;
import org.springframework.data.redis.core.RedisTemplate;
import cn.iocoder.yudao.framework.redis.core.PlusRedisService;

/**
 * 设备 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
@Slf4j
public class ZkDeviceServiceImpl implements ZkDeviceService {

//    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ZkDeviceServiceImpl.class);

    @Resource
    private ZkDeviceMapper zkDeviceMapper;
    @Resource
    private BaseDeptService baseDeptService;
    @Resource
    private DeptScopeSupport deptScopeSupport;
    
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    @Resource
    private PlusRedisService plusRedisService;

    @Override
    public Long createZkDevice(ZkDeviceCreateReqVO createReqVO) {
        // 插入
        ZkDeviceDO zkDevice = BeanUtils.toBean(createReqVO, ZkDeviceDO.class);
        zkDeviceMapper.insert(zkDevice);
        // 返回
        return zkDevice.getId();
    }

    @Override
    public void updateZkDevice(ZkDeviceUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkDeviceExists(updateReqVO.getId());
        // 更新
        ZkDeviceDO updateObj = BeanUtils.toBean(updateReqVO, ZkDeviceDO.class);
        zkDeviceMapper.updateById(updateObj);
        
        
        String ruleKey = updateObj.getDeviceSn() + updateObj.getDeviceMetricId();
        

        // 判断ruleStatus等于0时清除缓存
        if (Integer.valueOf(0).equals(updateObj.getRuleStatus())) {

            plusRedisService.deleteStr(ruleKey);

        } 
        // 判断standardStart，startComputer，standardEnd，endComputer至少有两个字段不为空且ruleStatus等于1时，缓存到redis
        else if (Integer.valueOf(1).equals(updateObj.getRuleStatus())) {
            int emptyCount = 0;
            if (updateObj.getStandardStart() != null) emptyCount++;
            if (updateObj.getStartComputer() != null && !updateObj.getStartComputer().isEmpty()) emptyCount++;
            if (updateObj.getStandardEnd() != null) emptyCount++;
            if (updateObj.getEndComputer() != null && !updateObj.getEndComputer().isEmpty()) emptyCount++;
            
            if (emptyCount >= 2) {

                // 使用项目封装的Redis方法进行存储
                plusRedisService.insertStr(ruleKey, JSON.toJSONString(updateObj), 1576800L);
                // 从Redis中读取刚存储的值并打印到日志，验证存储是否成功
                String cachedDeviceStr = plusRedisService.selectStr(ruleKey);
                ZkDeviceDO cachedDevice = JSON.parseObject(cachedDeviceStr, ZkDeviceDO.class);
                log.info("Cached device rule in Redis: key={}, value={}", ruleKey, cachedDevice);
            }
        }
    }

    @Override
    public void deleteZkDevice(Long id) {
        // 校验存在
        validateZkDeviceExists(id);
        // 删除
        zkDeviceMapper.deleteById(id);
    }

    private void validateZkDeviceExists(Long id) {
        if (zkDeviceMapper.selectById(id) == null) {
            throw exception(ZK_DEVICE_NOT_EXISTS);
        }
    }

    @Override
    public ZkDeviceDO getZkDevice(Long id) {
        return zkDeviceMapper.selectById(id);
    }

    @Override
    public List<ZkDeviceDO> getZkDeviceList(Collection<Long> ids) {
        return zkDeviceMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkDeviceDO> getZkDevicePage(ZkDevicePageReqVO pageReqVO) {
        pageReqVO.setDeptIds(deptScopeSupport.resolveDeptIds(pageReqVO.getDeptId()));
        return zkDeviceMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkDeviceDO> getZkDeviceList(ZkDeviceExportReqVO exportReqVO) {
        return zkDeviceMapper.selectList(exportReqVO);
    }

    @Override
    public String getDeviceInfo() {
        List<ZkDeviceDO> list = zkDeviceMapper.selectList();
        if(EasyListUtil.isEmpty(list)) return "";
        List<String> collect = list.stream().map(obj -> new EasyMap()
                .put("id", obj.getId()).put("deviceName", String.format("%s(%s)",obj.getDeviceName(), obj.getPondNum())).toJson()
        ).collect(Collectors.toList());

        return JSON.toJSONString(collect);
    }

}
