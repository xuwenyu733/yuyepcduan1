package cn.iocoder.yudao.module.fish.service.userrating;

import java.util.*;
import jakarta.validation.*;
import cn.iocoder.yudao.module.fish.controller.admin.userrating.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.userrating.OaUserRatingDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

/**
 * OA他人打分 Service 接口
 *
 * @author 系统管理员
 */
public interface OaUserRatingService {

  /**
   * 创建OA他人打分
   *
   * @param createReqVO 创建信息
   * @return 编号
   */
  Long createOaUserRating(@Valid OaUserRatingCreateReqVO createReqVO);

  /**
   * 更新OA他人打分
   *
   * @param updateReqVO 更新信息
   */
  void updateOaUserRating(@Valid OaUserRatingUpdateReqVO updateReqVO);

  /**
   * 删除OA他人打分
   *
   * @param id 编号
   */
  void deleteOaUserRating(Long id);

  /**
   * 获得OA他人打分
   *
   * @param id 编号
   * @return OA他人打分
   */
  OaUserRatingDO getOaUserRating(Long id);

  /**
   * 获得OA他人打分分页（带权限控制）
   *
   * @param pageReqVO 分页查询
   * @return OA他人打分分页
   */
  PageResult<OaUserRatingDO> getOaUserRatingPage(OaUserRatingPageReqVO pageReqVO);

  /**
   * 获得OA他人打分列表, 用于 Excel 导出
   *
   * @param exportReqVO 查询条件
   * @return OA他人打分列表
   */
  List<OaUserRatingDO> getOaUserRatingList(OaUserRatingExportReqVO exportReqVO);

  /**
   * 获取可评分人员列表
   *
   * @return 可评分人员列表
   */
  List<Map<String, Object>> getRateableUsers();

}


