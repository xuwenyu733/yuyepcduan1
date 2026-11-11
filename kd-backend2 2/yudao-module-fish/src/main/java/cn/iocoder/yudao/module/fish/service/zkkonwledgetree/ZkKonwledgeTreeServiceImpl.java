package cn.iocoder.yudao.module.fish.service.zkkonwledgetree;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.fish.controller.admin.aiagent.vo.ReceiveOperateVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkkonwledgetree.ZkKonwledgeTreeDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkkonwledgetree.ZkKonwledgeTreeConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkkonwledgetree.ZkKonwledgeTreeMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 知识树 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkKonwledgeTreeServiceImpl implements ZkKonwledgeTreeService {

    @Resource
    private ZkKonwledgeTreeMapper zkKonwledgeTreeMapper;

    @Override
    public Long createZkKonwledgeTree(ZkKonwledgeTreeCreateReqVO createReqVO) {
        // 插入
        ZkKonwledgeTreeDO zkKonwledgeTree = BeanUtils.toBean(createReqVO, ZkKonwledgeTreeDO.class);
        zkKonwledgeTreeMapper.insert(zkKonwledgeTree);
        // 返回
        return zkKonwledgeTree.getId();
    }

    @Override
    public void updateZkKonwledgeTree(ZkKonwledgeTreeUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkKonwledgeTreeExists(updateReqVO.getId());
        // 更新
        ZkKonwledgeTreeDO updateObj = BeanUtils.toBean(updateReqVO, ZkKonwledgeTreeDO.class);
        zkKonwledgeTreeMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkKonwledgeTree(Long id) {
        // 校验存在
        validateZkKonwledgeTreeExists(id);
        // 删除
        zkKonwledgeTreeMapper.deleteById(id);
    }

    private void validateZkKonwledgeTreeExists(Long id) {
        if (zkKonwledgeTreeMapper.selectById(id) == null) {
            throw exception(ZK_KONWLEDGE_TREE_NOT_EXISTS);
        }
    }

    @Override
    public ZkKonwledgeTreeDO getZkKonwledgeTree(Long id) {
        return zkKonwledgeTreeMapper.selectById(id);
    }

    @Override
    public List<ZkKonwledgeTreeDO> getZkKonwledgeTreeList(Collection<Long> ids) {
        return zkKonwledgeTreeMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkKonwledgeTreeDO> getZkKonwledgeTreePage(ZkKonwledgeTreePageReqVO pageReqVO) {
        return zkKonwledgeTreeMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkKonwledgeTreeDO> getZkKonwledgeTreeList(ZkKonwledgeTreeExportReqVO exportReqVO) {
        return zkKonwledgeTreeMapper.selectList(exportReqVO);
    }

    @Override
    public List<ZkKonwledgeTreeDO> selectList() {
        QueryWrapper<ZkKonwledgeTreeDO> queryWrapper = new QueryWrapper<>();
        List<ZkKonwledgeTreeDO> list = zkKonwledgeTreeMapper.selectList(queryWrapper);
        return list;
    }

    @Override
    public CommonResult<String> getInfoByVo(ReceiveOperateVo vo) {

        return null;
    }

}
