package cn.iocoder.yudao.module.fish.service.zkissue;

import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkissue.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkissue.ZkIssueDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkissue.ZkIssueConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkissue.ZkIssueMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 问题总结 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkIssueServiceImpl implements ZkIssueService {

    @Resource
    private ZkIssueMapper zkIssueMapper;

    @Override
    public Long createZkIssue(ZkIssueCreateReqVO createReqVO) {
        // 插入
        ZkIssueDO zkIssue = BeanUtils.toBean(createReqVO, ZkIssueDO.class);
        zkIssueMapper.insert(zkIssue);
        // 返回
        return zkIssue.getId();
    }

    @Override
    public void updateZkIssue(ZkIssueUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkIssueExists(updateReqVO.getId());
        // 更新
        ZkIssueDO updateObj = BeanUtils.toBean(updateReqVO, ZkIssueDO.class);
        zkIssueMapper.updateById(updateObj);
    }

    @Override
    public ZkIssueDO deleteZkIssue(Long id) {
        // 校验存在
        ZkIssueDO zkIssueDO = validateZkIssueExists(id);
        // 删除
        zkIssueMapper.deleteById(id);
        return zkIssueDO;
    }

    private ZkIssueDO validateZkIssueExists(Long id) {
        ZkIssueDO zkIssueDO = zkIssueMapper.selectById(id);
        if (zkIssueDO == null) {
            throw exception(ZK_ISSUE_NOT_EXISTS);
        }
        return zkIssueDO;
    }

    @Override
    public ZkIssueDO getZkIssue(Long id) {
        return zkIssueMapper.selectById(id);
    }

    @Override
    public List<ZkIssueDO> getZkIssueList(Collection<Long> ids) {
        return zkIssueMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkIssueDO> getZkIssuePage(ZkIssuePageReqVO pageReqVO) {
        return zkIssueMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkIssueDO> getZkIssueList(ZkIssueExportReqVO exportReqVO) {
        return zkIssueMapper.selectList(exportReqVO);
    }

}
