package cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree;

import cn.iocoder.yudao.framework.common.util.easy.EasyListUtil;
import cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.to.KnowLedgeSummaryVo;
import cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.to.KnowledgeNodeTypeEnum;
import cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.to.TreeGetInfoByStr;
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

import cn.iocoder.yudao.module.fish.controller.admin.zkkonwledgetree.vo.*;
import cn.iocoder.yudao.module.fish.dal.dataobject.zkkonwledgetree.ZkKonwledgeTreeDO;
// import cn.iocoder.yudao.module.fish.convert.zkkonwledgetree.ZkKonwledgeTreeConvert;
import cn.iocoder.yudao.module.fish.service.zkkonwledgetree.ZkKonwledgeTreeService;

@Tag(name = "管理后台 - 知识树")
@RestController
@RequestMapping("/fish/zk-konwledge-tree")
@Validated
public class ZkKonwledgeTreeController {

    @Resource
    private ZkKonwledgeTreeService zkKonwledgeTreeService;
    @Resource
    private TreeGetInfoByStr treeGetInfoByStr;

    @PostMapping("/create")
    @Operation(summary = "创建知识树")
    public CommonResult<Long> createZkKonwledgeTree(@Valid @RequestBody ZkKonwledgeTreeCreateReqVO createReqVO) {
        return success(zkKonwledgeTreeService.createZkKonwledgeTree(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新知识树")
    public CommonResult<Boolean> updateZkKonwledgeTree(@Valid @RequestBody ZkKonwledgeTreeUpdateReqVO updateReqVO) {
        zkKonwledgeTreeService.updateZkKonwledgeTree(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除知识树")
    @Parameter(name = "id", description = "编号", required = true)
    public CommonResult<Boolean> deleteZkKonwledgeTree(@RequestParam("id") Long id) {
        zkKonwledgeTreeService.deleteZkKonwledgeTree(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得知识树")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    public CommonResult<ZkKonwledgeTreeRespVO> getZkKonwledgeTree(@RequestParam("id") Long id) {
        ZkKonwledgeTreeDO zkKonwledgeTree = zkKonwledgeTreeService.getZkKonwledgeTree(id);
        return success(BeanUtils.toBean(zkKonwledgeTree, ZkKonwledgeTreeRespVO.class));
    }

    @GetMapping("/getKnowledgeByStr")
    @Operation(summary = "获得知识树通过关键词")
    public CommonResult<String> getKnowledgeByStr(@RequestParam("str") String str) {
        String res = treeGetInfoByStr.exe_find(str);
        return success(res);
    }
    @GetMapping("/getKnowledgeInfo")
    @Operation(summary = "获得知识树通过关键词")
    public CommonResult<KnowLedgeSummaryVo> getKnowledgeInfo() {
        // 1. 获取列表
        List<ZkKonwledgeTreeDO> list = zkKonwledgeTreeService.selectList();
        // 2. 获取 部位
        String bodyInfo = list.stream().filter(obj -> Objects.equals(obj.getNodeType(), KnowledgeNodeTypeEnum.BODY.getTypeValue())).map(
                obj -> obj.getNodeTitle()
        ).collect(Collectors.joining(","));
        // 3. 获取 症状
        String symptomInfo = list.stream().filter(obj -> Objects.equals(obj.getNodeType(), KnowledgeNodeTypeEnum.SYMPTOM.getTypeValue())).map(
                obj -> obj.getNodeTitle()
        ).collect(Collectors.joining(","));
        // 4. 获取 疾病原因列表
        String illInfo = list.stream().filter(obj -> Objects.equals(obj.getNodeType(), KnowledgeNodeTypeEnum.REASON.getTypeValue())).filter(obj -> StringUtils.isNotBlank(obj.getNodeTitle())).map(
                obj -> obj.getNodeTitle()
        ).collect(Collectors.joining(","));
        // 5. 获取 鱼表象
        String appearanceInfo = list.stream().filter(obj -> Objects.equals(obj.getNodeType(), KnowledgeNodeTypeEnum.USE_ROOT.getTypeValue())).filter(obj -> StringUtils.isNotBlank(obj.getNodeTitle())).map(
                obj -> obj.getNodeTitle()
        ).collect(Collectors.joining(","));
        KnowLedgeSummaryVo vo = new KnowLedgeSummaryVo(bodyInfo, symptomInfo, illInfo).build_appearanceInfo(appearanceInfo);
        return success(vo);
    }

    @GetMapping("/list")
    @Operation(summary = "获得知识树列表")
    @Parameter(name = "ids", description = "编号列表", required = true, example = "1024,2048")
    public CommonResult<List<ZkKonwledgeTreeRespVO>> getZkKonwledgeTreeList(@RequestParam("ids") Collection<Long> ids) {
        List<ZkKonwledgeTreeDO> list = zkKonwledgeTreeService.getZkKonwledgeTreeList(ids);
        return success(BeanUtils.toBean(list, ZkKonwledgeTreeRespVO.class));
    }

    @GetMapping("/listKnowledge")
    @Operation(summary = "获得知识树列表")
    public CommonResult<List<ZkKonwledgeTreeRespVO>> listKnowledge(ZkKonwledgeTreePageReqVO pageVO) {
        // 1. 获取分页数据
        PageResult<ZkKonwledgeTreeDO> zkKonwledgeTreePage = zkKonwledgeTreeService.getZkKonwledgeTreePage(pageVO);
        if(EasyListUtil.isEmpty(zkKonwledgeTreePage.getList())) return CommonResult.success(new ArrayList<>());
        // 2. 循环获取
        List<ZkKonwledgeTreeDO> list = new ArrayList<>();
        list.addAll(zkKonwledgeTreePage.getList());
        Long total = zkKonwledgeTreePage.getTotal();
        while (total > Long.parseLong(zkKonwledgeTreePage.getList().size()+"")) {
            Integer curr_pageNo = pageVO.getPageNo()+1;
            pageVO.setPageNo(curr_pageNo);
            zkKonwledgeTreePage = zkKonwledgeTreeService.getZkKonwledgeTreePage(pageVO);
            list.addAll(zkKonwledgeTreePage.getList());
        }
        return success(BeanUtils.toBean(list, ZkKonwledgeTreeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得知识树分页")
    public CommonResult<PageResult<ZkKonwledgeTreeRespVO>> getZkKonwledgeTreePage(@Valid ZkKonwledgeTreePageReqVO pageVO) {
        PageResult<ZkKonwledgeTreeDO> pageResult = zkKonwledgeTreeService.getZkKonwledgeTreePage(pageVO);
        return success(BeanUtils.toBean(pageResult, ZkKonwledgeTreeRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出知识树 Excel")
    // @OperateLog(type = EXPORT)
    public void exportZkKonwledgeTreeExcel(@Valid ZkKonwledgeTreeExportReqVO exportReqVO,
              HttpServletResponse response) throws IOException {
        List<ZkKonwledgeTreeDO> list = zkKonwledgeTreeService.getZkKonwledgeTreeList(exportReqVO);
        // 导出 Excel
        List<ZkKonwledgeTreeExcelVO> datas = BeanUtils.toBean(list, ZkKonwledgeTreeExcelVO.class);
        ExcelUtils.write(response, "知识树.xls", "数据", ZkKonwledgeTreeExcelVO.class, datas);
    }

}
