package cn.iocoder.yudao.module.fish.service.zkattendance;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.fish.controller.admin.zkattendance.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkattendance.ZkAttendanceDO;
import jakarta.validation.Valid;

import java.time.LocalDate;

/**
 * 考勤 Service 接口
 *
 * @author 系统管理员
 */
public interface ZkAttendanceService {

    /**
     * 打卡
     *
     * @param createReqVO 打卡信息
     * @return 编号
     */
    Long punch(@Valid ZkAttendanceCreateReqVO createReqVO);

    /**
     * 获得今日考勤信息
     *
     * @return 今日考勤信息
     */
    ZkAttendanceTodayVO getTodayAttendance();

    /**
     * 获得考勤分页
     *
     * @param pageReqVO 分页查询
     * @return 考勤分页
     */
    PageResult<ZkAttendanceRespVO> getAttendancePage(ZkAttendancePageReqVO pageReqVO);

    /**
     * 获得考勤统计
     *
     * @param month 月份，格式：yyyy-MM，为空则查询当前月
     * @return 考勤统计
     */
    ZkAttendanceStatisticsVO getAttendanceStatistics(String month);
}





