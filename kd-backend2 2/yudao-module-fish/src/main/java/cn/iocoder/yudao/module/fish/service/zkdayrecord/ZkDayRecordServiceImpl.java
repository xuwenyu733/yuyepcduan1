package cn.iocoder.yudao.module.fish.service.zkdayrecord;

import cn.iocoder.yudao.framework.common.util.easy.EasyDateUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkdayrecord.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkdayrecord.ZkDayRecordDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkdayrecord.ZkDayRecordConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkdayrecord.ZkDayRecordMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 时间字典 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkDayRecordServiceImpl implements ZkDayRecordService {

    @Resource
    private ZkDayRecordMapper zkDayRecordMapper;

    @Override
    public Long createZkDayRecord(ZkDayRecordCreateReqVO createReqVO) {
        // 插入
        ZkDayRecordDO zkDayRecord = BeanUtils.toBean(createReqVO, ZkDayRecordDO.class);
        zkDayRecordMapper.insert(zkDayRecord);
        // 返回
        return zkDayRecord.getId();
    }

    @Override
    public void updateZkDayRecord(ZkDayRecordUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkDayRecordExists(updateReqVO.getId());
        // 更新
        ZkDayRecordDO updateObj = BeanUtils.toBean(updateReqVO, ZkDayRecordDO.class);
        zkDayRecordMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkDayRecord(Long id) {
        // 校验存在
        validateZkDayRecordExists(id);
        // 删除
        zkDayRecordMapper.deleteById(id);
    }

    private void validateZkDayRecordExists(Long id) {
        if (zkDayRecordMapper.selectById(id) == null) {
            throw exception(ZK_DAY_RECORD_NOT_EXISTS);
        }
    }

    @Override
    public ZkDayRecordDO getZkDayRecord(Long id) {
        return zkDayRecordMapper.selectById(id);
    }

    @Override
    public List<ZkDayRecordDO> getZkDayRecordList(Collection<Long> ids) {
        return zkDayRecordMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkDayRecordDO> getZkDayRecordPage(ZkDayRecordPageReqVO pageReqVO) {
        return zkDayRecordMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkDayRecordDO> getZkDayRecordList(ZkDayRecordExportReqVO exportReqVO) {
        return zkDayRecordMapper.selectList(exportReqVO);
    }

    @Override
    public void customSaveBatch(List<ZkDayRecordDO> days) {
        zkDayRecordMapper.insertBatch(days);
    }

    @Override
    public PageResult<ZkDayRecordDO> myGetZkDayRecordPage(ZkDayRecordPageReqVO pageVO) {
        Date baseDate = pageVO.select_baseDate();
        ZkDayRecordPageReqVO newVo = new ZkDayRecordPageReqVO();
        CommonUtil.copyPropertiesIgnoreNull(pageVO, newVo);
        if(newVo.getDateYear() == null || Objects.equals(newVo.getDateYear(), 0)) {
            newVo.setDateYear(EasyDateUtil.getYear(baseDate));
        }
        if(newVo.getDateMonth() == null || Objects.equals(newVo.getDateMonth(), 0)) {
            Integer currMonth = EasyDateUtil.getMonth(baseDate);
            newVo.setDateMonth(currMonth);
        }
        // if(Objects.equals(0, pageVO.getPlusMonth()) && (newVo.getDateValue() == null || Objects.equals(newVo.getDateValue(), 0))) {
        //     newVo.build_geDateValue(EasyDateUtil.getDay(baseDate));
        // }
        newVo.setIdIsAsc(true);
        return getZkDayRecordPage(newVo);
    }

}
