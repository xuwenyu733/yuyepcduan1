package cn.iocoder.yudao.module.fish.service.zkpondwarn;

import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.exception.ExceptionUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevice.iot.DeviceWarnType4IOTEnum;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevice.iot.warn.MessageDetail4IOT;
import cn.iocoder.yudao.module.fish.controller.admin.zkdevice.iot.warn.WarnMessage4IOT;
import cn.iocoder.yudao.module.fish.controller.admin.zkpondwarn.to.WarnTypeEnum;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpond.ZkPondDO;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpond.ZkPondMapper;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import cn.iocoder.yudao.module.fish.controller.admin.zkpondwarn.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondwarn.ZkPondWarnDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkpondwarn.ZkPondWarnConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkpondwarn.ZkPondWarnMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 告警 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
@Slf4j
public class ZkPondWarnServiceImpl implements ZkPondWarnService {

    @Resource
    private ZkPondWarnMapper zkPondWarnMapper;
    @Resource
    private ZkPondService zkPondService;

    @Override
    public Long createZkPondWarn(ZkPondWarnCreateReqVO createReqVO) {
        return createZkPondWarn3(createReqVO).getId();
    }
    @Override
    public ZkPondWarnDO createZkPondWarn3(ZkPondWarnCreateReqVO createReqVO) {
        // 插入
        ZkPondWarnDO zkPondWarn = BeanUtils.toBean(createReqVO, ZkPondWarnDO.class);
        zkPondWarnMapper.insert(zkPondWarn);
        // 返回
        return zkPondWarn;
    }

    @Override
    public void updateZkPondWarn(ZkPondWarnUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkPondWarnExists(updateReqVO.getId());
        // 更新
        ZkPondWarnDO updateObj = BeanUtils.toBean(updateReqVO, ZkPondWarnDO.class);
        zkPondWarnMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkPondWarn(Long id) {
        // 校验存在
        validateZkPondWarnExists(id);
        // 删除
        zkPondWarnMapper.deleteById(id);
    }

    private void validateZkPondWarnExists(Long id) {
        if (zkPondWarnMapper.selectById(id) == null) {
            throw exception(ZK_POND_WARN_NOT_EXISTS);
        }
    }

    @Override
    public ZkPondWarnDO getZkPondWarn(Long id) {
        return zkPondWarnMapper.selectById(id);
    }

    @Override
    public List<ZkPondWarnDO> getZkPondWarnList(Collection<Long> ids) {
        return zkPondWarnMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkPondWarnDO> getZkPondWarnPage(ZkPondWarnPageReqVO pageReqVO) {
        return zkPondWarnMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkPondWarnDO> getZkPondWarnList(ZkPondWarnExportReqVO exportReqVO) {
        return zkPondWarnMapper.selectList(exportReqVO);
    }

    @Override
    public Integer customInsert(WarnMessage4IOT warnMessage4IOT) {
        // 1. 获取告警信息
        if(warnMessage4IOT == null || warnMessage4IOT.getData() == null) return 0;
        MessageDetail4IOT data = warnMessage4IOT.getData();
        List<String> builder = new ArrayList<>();
        String[] notNullPropertyNames = CommonUtil.getNotNullPropertyNames(data);
        if(notNullPropertyNames == null || notNullPropertyNames.length == 0) {
            return 0;
        }
        String[] notNullValues = CommonUtil.getNotNullValues(data);
        for(String item : notNullValues) {
            for(DeviceWarnType4IOTEnum e : DeviceWarnType4IOTEnum.values()) {
                if(StringUtils.equals(item, e.getWarnType())) {
                    builder.add(e.getWarnInfo());
                    break;
                }
            }
        }
        if(EasyListUtil.isEmpty(builder)) return 0;
        // 2. 添加数据
        ZkPondDO pond = zkPondService.selectFirst(); // 目前物联网iot传输过来没有那个池号，先写成1号存塘 TODO
        ZkPondWarnDO zkPondWarnDO = new ZkPondWarnDO(pond.getWorkshopName(), pond.getWorkshopId(), pond.getPondSystemId(), pond.getPondSystemName(),
                pond.getPondNum(), pond.getPondName(), pond.getId(), pond.getPondDetailNum());
        String warnContent = builder.stream().collect(Collectors.joining(","));
        zkPondWarnDO.build_info("物联网设备告警", WarnTypeEnum.ENV.getWarnType(), warnContent, warnMessage4IOT.getTimestamp()+"");
        try {
            zkPondWarnMapper.insert(zkPondWarnDO);
        } catch (Exception e) {
            log.warn(String.format("【添加警告出现异常】middle，大概率是触发唯一警告，可以不用管:%s,异常：%s", JSON.toJSONString(zkPondWarnDO), ExceptionUtil.getStackTrace(e)));
        }
        return 1;
    }

    @Override
    public List<ZkPondWarnDO> selectRecentInfo(Integer lastDays) {
        LocalDateTime localDateTime = EasyDateUtil.plusDay(LocalDateTime.now(), -lastDays);
        QueryWrapper<ZkPondWarnDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("create_time", localDateTime);
        List<ZkPondWarnDO> zkPondWarnDOS = zkPondWarnMapper.selectList(queryWrapper);

        return zkPondWarnDOS;
    }

}
