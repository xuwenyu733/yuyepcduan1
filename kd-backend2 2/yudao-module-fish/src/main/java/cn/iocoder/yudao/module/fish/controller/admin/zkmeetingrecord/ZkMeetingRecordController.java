package cn.iocoder.yudao.module.fish.controller.admin.zkmeetingrecord;

import cn.hutool.http.HttpUtil;
import cn.iocoder.yudao.framework.common.exception.CommonException;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.common.util.easy.StringUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkmeetingrecord.tool.MeetUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
// import static cn.iocoder.yudao.framework.operatelog.core.enums.OperateTypeEnum.*;
// import cn.iocoder.yudao.framework.datapermission.core.annotation.DataPermission;

import cn.iocoder.yudao.module.fish.controller.admin.zkmeetingrecord.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkmeetingrecord.ZkMeetingRecordDO;
// import cn.iocoder.yudao.module.fish.convert.zkmeetingrecord.ZkMeetingRecordConvert;
import cn.iocoder.yudao.module.fish.service.zkmeetingrecord.ZkMeetingRecordService;

@Tag(name = "管理后台 - 会议记录")
@RestController
@RequestMapping("/fish/zk-meeting-record")
@Validated
public class ZkMeetingRecordController {

    @Resource
    private ZkMeetingRecordService zkMeetingRecordService;

    @Resource
    private MeetUtil meetUtil;

    @PostMapping("/create")
    @Operation(summary = "创建会议记录")
    public CommonResult<Long> createZkMeetingRecord(@Valid @RequestBody ZkMeetingRecordCreateReqVO createReqVO) {
        if(createReqVO.getMeetingId() == null) {
            throw new CommonException(403, "会议id不能为空");
        }
        return success(zkMeetingRecordService.customSave(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新会议记录")
    public CommonResult<Boolean> updateZkMeetingRecord(@Valid @RequestBody ZkMeetingRecordUpdateReqVO updateReqVO) {
        zkMeetingRecordService.updateZkMeetingRecord(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除会议记录")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkMeetingRecord(@RequestParam("id") Long id) {
        zkMeetingRecordService.deleteZkMeetingRecord(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得会议记录")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkMeetingRecordRespVO> getZkMeetingRecord(@RequestParam("id") Long id) {
        ZkMeetingRecordDO zkMeetingRecord = zkMeetingRecordService.getZkMeetingRecord(id);
        return success(BeanUtils.toBean(zkMeetingRecord, ZkMeetingRecordRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得会议记录列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkMeetingRecordRespVO>> getZkMeetingRecordList(@RequestParam("ids") Collection<Long> ids) {
        // 1. 获取会议列表
        List<ZkMeetingRecordDO> list = zkMeetingRecordService.getZkMeetingRecordList(ids);
        enhancerList(list);

        return success(BeanUtils.toBean(list, ZkMeetingRecordRespVO.class));
    }

    /**
     * 会议更新videoUrls（同步块降低多人离开并发写同一行时的丢更新概率）
     * @param list 需要更新的会议
     */
    public synchronized void enhancerList(List<ZkMeetingRecordDO> list) {
        if(EasyListUtil.isEmpty(list)) return;
        List<ZkMeetingRecordDO> needUpdateUrls = list.stream().filter(obj -> {
            Boolean flag = StringUtil.isEmpty(obj.getLocalVideoUrls()) && obj.getVideoGetCount() < 119 && StringUtil.isNotEmptyWith0(obj.getMeetDay());
            return flag;
        }).toList();
        if(EasyListUtil.isEmpty(needUpdateUrls)) return;
        List<ZkMeetingRecordDO> needUpdate = new ArrayList<>();
        for(ZkMeetingRecordDO item : needUpdateUrls) {
            // 再读一次最新行，避免并发下用过期 videoGetCount 覆盖已写回的 URL
            ZkMeetingRecordDO latest = item.getId() != null
                    ? zkMeetingRecordService.getZkMeetingRecord(item.getId())
                    : item;
            if (latest == null) continue;
            if (StringUtil.isNotEmpty(latest.getLocalVideoUrls())) {
                item.setLocalVideoUrls(latest.getLocalVideoUrls());
                continue;
            }
            String videoUrls = meetUtil.selectVideoUrls(latest.getMeetingNum(), latest.getMeetDay());
            if(StringUtil.isEmpty(videoUrls)) {
                latest.add_videoGetCount();
            } else {
                latest.setLocalVideoUrls(videoUrls);
            }
            needUpdate.add(latest);
        }
        zkMeetingRecordService.batchUpdateCustom(needUpdate);
    }



    @GetMapping("/page")
    @Operation(summary = "获得会议记录分页")
    public CommonResult<PageResult<ZkMeetingRecordRespVO>> getZkMeetingRecordPage(@Valid ZkMeetingRecordPageReqVO pageVO) {
        PageResult<ZkMeetingRecordDO> pageResult = zkMeetingRecordService.getZkMeetingRecordPage(pageVO);
        enhancerList(pageResult.getList());
        return success(BeanUtils.toBean(pageResult, ZkMeetingRecordRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出会议记录 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkMeetingRecordExcel(@Valid ZkMeetingRecordExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkMeetingRecordDO> list = zkMeetingRecordService.getZkMeetingRecordList(exportReqVO);
        // 导出 Excel
        List<ZkMeetingRecordExcelVO> datas = BeanUtils.toBean(list, ZkMeetingRecordExcelVO.class);
        ExcelUtils.write(response, "会议记录.xls", "数据", ZkMeetingRecordExcelVO.class, datas);
    }

}
