package cn.iocoder.yudao.module.fish.service.zkapplog;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.zkapplog.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkapplog.ZkAppLogDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * app记录 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkAppLogService {

    /**
     * 创建app记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createZkAppLog(@Valid ZkAppLogCreateReqVO createReqVO);

    /**
     * 更新app记录
     *
     * @param updateReqVO 更新信息
     */
    void updateZkAppLog(@Valid ZkAppLogUpdateReqVO updateReqVO);

    /**
     * 删除app记录
     *
     * @param id 编号
     */
    void deleteZkAppLog(Long id);

    /**
     * 获得app记录
     *
     * @param id 编号
     * @return app记录
     */
    ZkAppLogDO getZkAppLog(Long id);

    /**
     * 获得app记录列表
     *
     * @param ids 编号
     * @return app记录列表
     */
    List<ZkAppLogDO> getZkAppLogList(Collection<Long> ids);

    /**
     * 获得app记录分页
     *
     * @param pageReqVO 分页查询
     * @return app记录分页
     */
    PageResult<ZkAppLogDO> getZkAppLogPage(ZkAppLogPageReqVO pageReqVO);

    /**
     * 获得app记录列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return app记录列表
     */
    List<ZkAppLogDO> getZkAppLogList(ZkAppLogExportReqVO exportReqVO);

}
