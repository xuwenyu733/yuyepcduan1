package cn.iocoder.yudao.module.fish.service.zkworkshop;

import cn.iocoder.yudao.framework.common.base_api.BaseDeptService;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkworkshop.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkworkshop.ZkWorkshopDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

// import cn.iocoder.yudao.module.fish.convert.zkworkshop.ZkWorkshopConvert;
import cn.iocoder.yudao.module.fish.dal.mysql.zkworkshop.ZkWorkshopMapper;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.module.fish.enums.ErrorCodeConstants.*;

/**
 * 车间 Service 实现类
 *
 * @author 系统管理员
 */
@Service
@Validated
public class ZkWorkshopServiceImpl implements ZkWorkshopService {

    @Resource
    private ZkWorkshopMapper zkWorkshopMapper;

    @Resource
    private BaseDeptService baseDeptService;

    @Override
    public Long createZkWorkshop(ZkWorkshopCreateReqVO createReqVO) {
        // 插入
        ZkWorkshopDO zkWorkshop = BeanUtils.toBean(createReqVO, ZkWorkshopDO.class);
        zkWorkshopMapper.insert(zkWorkshop);
        // 返回
        return zkWorkshop.getId();
    }

    @Override
    public void updateZkWorkshop(ZkWorkshopUpdateReqVO updateReqVO) {
        // 校验存在
        validateZkWorkshopExists(updateReqVO.getId());
        // 更新
        ZkWorkshopDO updateObj = BeanUtils.toBean(updateReqVO, ZkWorkshopDO.class);
        zkWorkshopMapper.updateById(updateObj);
    }

    @Override
    public void deleteZkWorkshop(Long id) {
        // 校验存在
        validateZkWorkshopExists(id);
        // 删除
        zkWorkshopMapper.deleteById(id);
    }

    private void validateZkWorkshopExists(Long id) {
        if (zkWorkshopMapper.selectById(id) == null) {
            throw exception(ZK_WORKSHOP_NOT_EXISTS);
        }
    }

    @Override
    public ZkWorkshopDO getZkWorkshop(Long id) {
        return zkWorkshopMapper.selectById(id);
    }

    @Override
    public List<ZkWorkshopDO> getZkWorkshopList(Collection<Long> ids) {
        return zkWorkshopMapper.selectBatchIds(ids);
    }

    @Override
    public PageResult<ZkWorkshopDO> getZkWorkshopPage(ZkWorkshopPageReqVO pageReqVO) {
        baseDeptService.enhancerDeptIds(pageReqVO, pageReqVO.getDeptId(), "deptIds");
        return zkWorkshopMapper.selectPage(pageReqVO);
    }

    @Override
    public List<ZkWorkshopDO> getZkWorkshopList(ZkWorkshopExportReqVO exportReqVO) {
        return zkWorkshopMapper.selectList(exportReqVO);
    }

    @Override
    public List<ZkWorkshopDashboardRespVO> getZkWorkshopDashboardList() {
        try {
            return zkWorkshopMapper.selectDashboardList();
        } catch (Exception ex) {
            // 本地库缺 zk_workshop 等表时，大屏不应 500「系统异常」
            return Collections.emptyList();
        }
    }

}
