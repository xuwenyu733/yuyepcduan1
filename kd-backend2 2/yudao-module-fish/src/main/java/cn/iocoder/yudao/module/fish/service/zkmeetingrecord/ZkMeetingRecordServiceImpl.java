package cn.iocoder.yudao.module.fish.service.zkmeetingrecord;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.obj.CommonUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkmeetingrecord.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeetingrecord.ZkMeetingRecordDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkmeetingrecord.ZkMeetingRecordConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkmeetingrecord.ZkMeetingRecordMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 会议记录 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkMeetingRecordServiceImpl implements ZkMeetingRecordService {

    @Resource
    private ZkMeetingRecordMapper zkMeetingRecordMapper;

    @Override
    public Long createZkMeetingRecord(ZkMeetingRecordCreateReqVO createReqVO) {
        // 插入
        ZkMeetingRecordDO zkMeetingRecord = BeanUtils.toBean(createReqVO, ZkMeetingRecordDO.class);
        zkMeetingRecordMapper.insert(zkMeetingRecord);
        // 返回
        return zkMeetingRecord.getId();
    }

    @Override
    public Long customSave(ZkMeetingRecordCreateReqVO createReqVO) {
        // 插入
        ZkMeetingRecordDO zkMeetingRecord = BeanUtils.toBean(createReqVO, ZkMeetingRecordDO.class);
        ZkMeetingRecordDO exist = getByMeetingId(zkMeetingRecord.getMeetingId());
        if(exist != null) {
            exist.build_summaryAndMeetToDo(createReqVO.getMeetingPoint(), createReqVO.getMeetToDo())
                    .build_meetingNum(createReqVO.getMeetingNum());
            zkMeetingRecordMapper.updateById(exist);
            return exist.getId();
        } else {
            zkMeetingRecordMapper.insert(zkMeetingRecord);
            // 返回
            return zkMeetingRecord.getId();
        }
    }

    @Override
    public ZkMeetingRecordDO getByMeetingId(Long meetingId) {
        QueryWrapper<ZkMeetingRecordDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("meeting_id", meetingId).orderByDesc("id").last("limit 1");
        return zkMeetingRecordMapper.selectOne(queryWrapper);
    }

    @Override
    public ZkMeetingRecordDO updateOrInsert(ZkMeetingRecordDO meetingRecordDO) {
        ZkMeetingRecordDO byMeetingId = getByMeetingId(meetingRecordDO.getMeetingId());
        if(byMeetingId == null) {
            zkMeetingRecordMapper.insert(meetingRecordDO);
            return meetingRecordDO;
        } else {
            meetingRecordDO.setId(null);
            CommonUtil.copyPropertiesIgnoreNull(meetingRecordDO, byMeetingId);
            zkMeetingRecordMapper.updateById(byMeetingId);
            return byMeetingId;
        }
    }

    @Override
    public void batchUpdateCustom(List<ZkMeetingRecordDO> needUpdate) {
        if(EasyListUtil.isEmpty(needUpdate)) return;
        zkMeetingRecordMapper.updateBatch(needUpdate);
    }

    @Override
    public void updateZkMeetingRecord(ZkMeetingRecordUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkMeetingRecordExists(updateReqVO.getId());
        // 更新
        ZkMeetingRecordDO updateObj = BeanUtils.toBean(updateReqVO, ZkMeetingRecordDO.class);
        zkMeetingRecordMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkMeetingRecord(Long id) {
        // 校验存在
        validateZkMeetingRecordExists(id);
        // 删除
        zkMeetingRecordMapper.deleteById(id);
    }

    private void validateZkMeetingRecordExists(Long id) {
        if (zkMeetingRecordMapper.selectById(id) == null) {
            throw exception(ZK_MEETING_RECORD_NOT_EXISTS);
        }
    }

    @Override
    public ZkMeetingRecordDO getZkMeetingRecord(Long id) {
        return zkMeetingRecordMapper.selectById(id);
    }

    @Override
    public List<ZkMeetingRecordDO> getZkMeetingRecordList(Collection<Long> ids) {
        return zkMeetingRecordMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkMeetingRecordDO> getZkMeetingRecordPage(ZkMeetingRecordPageReqVO pageReqVO) {
        return zkMeetingRecordMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkMeetingRecordDO> getZkMeetingRecordList(ZkMeetingRecordExportReqVO exportReqVO) {
        return zkMeetingRecordMapper.selectList(exportReqVO);
    }


}
