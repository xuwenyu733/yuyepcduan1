package cn.iocoder.yudao.module.fish.controller.admin.zkpondwarn;

import cn.iocoder.yudao.framework.common.base_api.BaseNotifyService;
import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import cn.iocoder.yudao.module.fish.service.dictdata.DictDataService;
import cn.iocoder.yudao.module.fish.service.dictdata.vo.DictBaseVo;
import cn.iocoder.yudao.module.fish.service.dictdata.vo.DictDataVo;
import cn.iocoder.yudao.module.fish.service.zkdevice.ZkDeviceService;
import cn.iocoder.yudao.module.fish.service.zkpond.ZkPondService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkpondwarn.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkpondwarn.ZkPondWarnDO;
// import cn.iocoder.yudao.module.fish.convert.zkpondwarn.ZkPondWarnConvert;
import cn.iocoder.yudao.module.fish.service.zkpondwarn.ZkPondWarnService;

@Tag(name = "管理后台 - 故障报警")
@RestController
@RequestMapping("/fish/zk-pond-warn")
@Validated
@Slf4j
public class ZkPondWarnController {

    @Resource
    private ZkPondWarnService zkPondWarnService;
    @Resource
    private DictDataService dictDataService;
    @Resource
    private ZkPondService zkPondService;
    @Resource
    private ZkDeviceService zkDeviceService;

    @Resource
    private BaseNotifyService baseNotifyService;

    @PostMapping("/judgeParams")
    @Operation(summary = "创建故障报警判断参数是否缺少")
    public CommonResult<String> judgeParams(String params) {
        log.warn("【创建故障报警判断参数是否缺少-{}】middle,接受参数:{}", TokenUtil.userInfo(), params);
        Map<String, String> dictInfo = getDictInfo(Arrays.asList("zk_pond_warn_type"));
        // 槽位0 properties： 属性名， 槽位1 json_serializable： json序列化说明， 槽位2 property_enum： 属性信息枚举， 槽位3 eg： 示例， 槽位4 remark： 参数交互描述
        String t1 = "properties \n json_serializable \n property_enum \n eg \n remark";
        t1 = t1.replace("properties", "添加【故障报警】数据需要收集的属性信息有： 系统编号、告警对象、告警类型、告警描述 ；");
        t1 = t1.replace("json_serializable", "添加【故障报警】数据信息如何转换成对应json对象说明：   物料的 系统编号用pondId替换、告警对象用warnName替换、告警类型用warnType替换、告警描述用warnContent替换 。");
        t1 = t1.replace("property_enum", "" +
                "   系统编号(用户选择后，取id值)：   pondInfo \n".replace("pondInfo", dictInfo.get("pondInfo")) +
                "   告警对象(用户选择后，取id值)：   deviceInfo \n".replace("deviceInfo", dictInfo.get("deviceInfo")) +
                "   告警类型(用户选择后，取value值)：   warnType \n".replace("warnType", dictInfo.get("zk_pond_warn_type")));
        t1 = t1.replace("eg", "添加【故障报警】数据输入示例： {\"pondId\":2,\"warnDeviceId\":1,\"warnType\":\"1\",\"warnContent\":\"摄像头模糊\"}");
        t1 = t1.replace("remark", "添加【故障报警】数据缺少必要属性数据时，作为助手的你可以向用户索取数据，轻易不要使用示例中数据。");
        if(StringUtils.isEmpty(params)) return CommonResult.error(403, t1);
        try {
            ZkPondWarnCreateReqVO vo = JSON.parseObject(params, ZkPondWarnCreateReqVO.class);
            if(
                    vo.getPondId()==null && vo.getWarnDeviceId()==null && StringUtils.isEmpty(vo.getWarnType())
                            && StringUtils.isEmpty(vo.getWarnContent())
            ) {
                return CommonResult.error(403, t1);
            } else if(
                    vo.getPondId()==null || vo.getWarnDeviceId()==null || StringUtils.isEmpty(vo.getWarnType())
                            || StringUtils.isEmpty(vo.getWarnContent())
            ){
                return CommonResult.error(403, String.format(t1));
            }
            return success(String.format("参数：%s，校验合格，可以告诉用户 执行工具add1 ", JSON.toJSONString(vo)));
        } catch (Exception e) {
            return CommonResult.error(403, t1);
        }
    }

    public Map<String, String> getDictInfo(List<String> dictTypes) {
        // 1. 获取字典描述
        List<DictDataVo> dictDataList = dictDataService.getByDictTypes(dictTypes);
        if(EasyListUtil.isEmpty(dictDataList)) return null;
        Map<String, List<DictDataVo>> collect = dictDataList.stream().collect(Collectors.groupingBy(obj -> obj.getDictType()));
        Map<String, String> map = new HashMap<>();
        for(String key : collect.keySet()) {
            List<DictDataVo> dictDataVos = collect.get(key);
            List<DictBaseVo> newList = dictDataVos.stream().map(obj -> (DictBaseVo) obj).collect(Collectors.toList());
            map.put(key, JSON.toJSONString(newList));
        }
        // 2. 获取存塘信息
        String pondInfo = zkPondService.getPondInfo();
        map.put("pondInfo", pondInfo);
        String deviceInfo = zkDeviceService.getDeviceInfo();
        map.put("deviceInfo", deviceInfo);
        return map;
    }

    @PostMapping("/create")
    @Operation(summary = "创建告警")
    public CommonResult<Long> createZkPondWarn(@Valid @RequestBody ZkPondWarnCreateReqVO createReqVO) {
        // 1. 添加告警
        ZkPondWarnDO zkPondWarn3 = zkPondWarnService.createZkPondWarn3(createReqVO);
        // 2. 消息通知
        baseNotifyService.insertNotify(zkPondWarn3.getUserId(), zkPondWarn3.selectMsg());
        return success(zkPondWarn3.getId());
    }

    @PutMapping("/update")
    @Operation(summary = "更新告警")
    public CommonResult<Boolean> updateZkPondWarn(@Valid @RequestBody ZkPondWarnUpdateReqVO updateReqVO) {
        zkPondWarnService.updateZkPondWarn(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除告警")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkPondWarn(@RequestParam("id") Long id) {
        zkPondWarnService.deleteZkPondWarn(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得告警")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkPondWarnRespVO> getZkPondWarn(@RequestParam("id") Long id) {
        ZkPondWarnDO zkPondWarn = zkPondWarnService.getZkPondWarn(id);
        return success(BeanUtils.toBean(zkPondWarn, ZkPondWarnRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得告警列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkPondWarnRespVO>> getZkPondWarnList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkPondWarnDO> list = zkPondWarnService.getZkPondWarnList(ids);
        return success(BeanUtils.toBean(list, ZkPondWarnRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得告警分页")
    public CommonResult<PageResult<ZkPondWarnRespVO>> getZkPondWarnPage(@Valid ZkPondWarnPageReqVO pageVO) {
        PageResult<ZkPondWarnDO> pageResult = zkPondWarnService.getZkPondWarnPage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkPondWarnRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出告警 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkPondWarnExcel(@Valid ZkPondWarnExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkPondWarnDO> list = zkPondWarnService.getZkPondWarnList(exportReqVO);
        // 导出 Excel
        List<ZkPondWarnExcelVO> datas = BeanUtils.toBean(list, ZkPondWarnExcelVO.class);
        ExcelUtils.write(response, "告警.xls", "数据", ZkPondWarnExcelVO.class, datas);
    }

}
