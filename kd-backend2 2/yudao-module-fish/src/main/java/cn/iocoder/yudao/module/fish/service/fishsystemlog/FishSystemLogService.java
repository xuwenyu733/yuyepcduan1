package cn.iocoder.yudao.module.fish.service.fishsystemlog;

import java.util.*;

import cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.vo.FishSystemLogCreateReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.vo.FishSystemLogExportReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.vo.FishSystemLogPageReqVO;
import cn.iocoder.yudao.module.fish.controller.admin.fishsystemlog.vo.FishSystemLogUpdateReqVO;
import cn.iocoder.yudao.module.fish.dal.dataobject.fishsystemlog.FishSystemLogDO;
import jakarta.validation.*;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * fish系统日志 Service 接口
 *
 * @author 系统管理员
 */
public interface FishSystemLogService {

    /**
     * 创建fish系统日志
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createFishSystemLog(@Valid FishSystemLogCreateReqVO createReqVO);

    /**
     * 更新fish系统日志
     *
     * @param updateReqVO 更新信息
     */
    void updateFishSystemLog(@Valid FishSystemLogUpdateReqVO updateReqVO);

    /**
     * 删除fish系统日志
     *
     * @param id 编号
     */
    void deleteFishSystemLog(Long id);

    /**
     * 获得fish系统日志
     *
     * @param id 编号
     * @return fish系统日志
     */
    FishSystemLogDO getFishSystemLog(Long id);

    /**
     * 获得fish系统日志列表
     *
     * @param ids 编号
     * @return fish系统日志列表
     */
    List<FishSystemLogDO> getFishSystemLogList(Collection<Long> ids);

    /**
     * 获得fish系统日志分页
     *
     * @param pageReqVO 分页查询
     * @return fish系统日志分页
     */
    PageResult<FishSystemLogDO> getFishSystemLogPage(FishSystemLogPageReqVO pageReqVO);

    /**
     * 获得fish系统日志列表, 用于 Excel 导出
     *
     * @param exportReqVO 查询条件
     * @return fish系统日志列表
     */
    List<FishSystemLogDO> getFishSystemLogList(FishSystemLogExportReqVO exportReqVO);

}
