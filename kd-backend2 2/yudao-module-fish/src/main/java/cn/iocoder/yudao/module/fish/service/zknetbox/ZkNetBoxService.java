package cn.iocoder.yudao.module.fish.service.zknetbox;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zknetbox.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zknetbox.ZkNetBoxDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * 网盘资源 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkNetBoxService {

    /**
     * 创建网盘资源
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkNetBox(@Valid ZkNetBoxCreateReqVO createReqVO);

    /**
     * 更新网盘资源
     *
     * @param updateReqVO 更新信息
     */
    void updateZkNetBox(@Valid ZkNetBoxUpdateReqVO updateReqVO);

    /**
     * 删除网盘资源
     *
     * @param id 编号
     */
    void deleteZkNetBox(Long id);

    /**
     * 获得网盘资源
     *
     * @param id 编号
     * @return 网盘资源
     */
    ZkNetBoxDO getZkNetBox(Long id);

    /**
     * 获得网盘资源列表
     *
     * @param ids 编号
     * @return 网盘资源列表
     */
    List<ZkNetBoxDO> getZkNetBoxList(Collection<Long> ids);

    /**
     * 获得网盘资源分页
     *
     * @param pageReqVO 分页查询
     * @return 网盘资源分页
     */
    PageResult<ZkNetBoxDO> getZkNetBoxPage(ZkNetBoxPageReqVO pageReqVO);

    /**
     * 获得网盘资源列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return 网盘资源列表
     */
    List<ZkNetBoxDO> getZkNetBoxList(ZkNetBoxExportReqVO exportReqVO);

}
