package cn.iocoder.yudao.module.fish.service.zkworkshop;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkworkshop.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkworkshop.ZkWorkshopDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 车间 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkWorkshopService {

    /**
     * 创建车间
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkWorkshop(@Valid ZkWorkshopCreateReqVO createReqVO);

    /**
     * 更新车间
     *
     * @param updateReqVO 更新信息
     */
    void updateZkWorkshop(@Valid ZkWorkshopUpdateReqVO updateReqVO);

    /**
     * 删除车间
     *
     * @param id 编号
     */
    void deleteZkWorkshop(Long id);

    /**
     * 获得车间
     *
     * @param id 编号
     * @return 车间
     */
    ZkWorkshopDO getZkWorkshop(Long id);

    /**
     * 获得车间列表
     *
     * @param ids 编号
     * @return 车间列表
     */
    List<ZkWorkshopDO> getZkWorkshopList(Collection<Long> ids);

    /**
     * 获得车间分页
     *
     * @param pageReqVO 分页查询
     * @return 车间分页
     */
    PageResult<ZkWorkshopDO> getZkWorkshopPage(ZkWorkshopPageReqVO pageReqVO);

    /**
     * 获得车间列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 车间列表
     */
    List<ZkWorkshopDO> getZkWorkshopList(ZkWorkshopExportReqVO exportReqVO);

    /**
     * 获得车间列表，用于首页大屏
     *
     * @return 车间列表
     */
    List<ZkWorkshopDashboardRespVO> getZkWorkshopDashboardList();

}
