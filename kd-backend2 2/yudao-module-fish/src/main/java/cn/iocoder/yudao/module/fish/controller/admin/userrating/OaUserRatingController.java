package cn.iocoder.yudao.module.fish.controller.admin.userrating;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.fish.controller.admin.userrating.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.userrating.OaUserRatingDO;
import cn.iocoder.yudao.module.fish.service.userrating.OaUserRatingService;

@Tag(name = "管理后台 - OA他人打分")
@RestController
@RequestMapping("/fish/oa-user-rating")
@Validated
public class OaUserRatingController {

  @Resource
  private OaUserRatingService oaUserRatingService;

  @PostMapping("/create")
  @Operation(summary = "创建OA他人打分")
  public CommonResult<Long> createOaUserRating(@Valid @RequestBody OaUserRatingCreateReqVO createReqVO) {
    return success(oaUserRatingService.createOaUserRating(createReqVO));
  }

  @PutMapping("/update")
  @Operation(summary = "更新OA他人打分")
  public CommonResult<Boolean> updateOaUserRating(@Valid @RequestBody OaUserRatingUpdateReqVO updateReqVO) {
    oaUserRatingService.updateOaUserRating(updateReqVO);
    return success(true);
  }

  @DeleteMapping("/delete")
  @Operation(summary = "删除OA他人打分")
  @Parameter(name = "id", description = "编号", required = true)
  public CommonResult<Boolean> deleteOaUserRating(@RequestParam("id") Long id) {
    oaUserRatingService.deleteOaUserRating(id);
    return success(true);
  }

  @GetMapping("/get")
  @Operation(summary = "获得OA他人打分")
  @Parameter(name = "id", description = "编号", required = true, example = "1024")
  public CommonResult<OaUserRatingRespVO> getOaUserRating(@RequestParam("id") Long id) {
    OaUserRatingDO rating = oaUserRatingService.getOaUserRating(id);
    return success(BeanUtils.toBean(rating, OaUserRatingRespVO.class));
  }

  @GetMapping("/page")
  @Operation(summary = "获得OA他人打分分页")
  public CommonResult<PageResult<OaUserRatingRespVO>> getOaUserRatingPage(@Valid OaUserRatingPageReqVO pageReqVO) {
    PageResult<OaUserRatingDO> pageResult = oaUserRatingService.getOaUserRatingPage(pageReqVO);
    return success(BeanUtils.toBean(pageResult, OaUserRatingRespVO.class));
  }

  @GetMapping("/rateable-users")
  @Operation(summary = "获取可评分人员列表")
  public CommonResult<List<Map<String, Object>>> getRateableUsers() {
    return success(oaUserRatingService.getRateableUsers());
  }

  @GetMapping("/export-excel")
  @Operation(summary = "导出OA他人打分 Excel")
  public void exportOaUserRatingExcel(@Valid OaUserRatingExportReqVO exportReqVO,
      HttpServletResponse response) throws IOException {
    List<OaUserRatingDO> list = oaUserRatingService.getOaUserRatingList(exportReqVO);
    // 导出 Excel
    List<OaUserRatingRespVO> datas = BeanUtils.toBean(list, OaUserRatingRespVO.class);
    ExcelUtils.write(response, "OA他人打分.xls", "数据", OaUserRatingRespVO.class,
        datas);
  }

}
