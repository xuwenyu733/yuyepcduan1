package cn.iocoder.yudao.module.fish.controller.admin.zkdocument;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.fish.controller.admin.zkdocument.vo.*;
import cn.iocoder.yudao.module.fish.service.zkdocument.ZkDocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 文档协同")
@RestController
@RequestMapping("/fish/zk-document")
@Validated
public class ZkDocumentController {

    @Resource
    private ZkDocumentService documentService;

    @PostMapping("/upload")
    @Operation(summary = "上传文档")
    public CommonResult<Long> uploadDocument(@Valid @RequestBody ZkDocumentCreateReqVO createReqVO) {
        return success(documentService.createDocument(createReqVO));
    }

    @GetMapping("/page")
    @Operation(summary = "获得文档分页")
    public CommonResult<PageResult<ZkDocumentRespVO>> getDocumentPage(@Valid ZkDocumentPageReqVO pageReqVO) {
        PageResult<ZkDocumentRespVO> pageResult = documentService.getDocumentPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获得文档详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    public CommonResult<ZkDocumentRespVO> getDocument(@PathVariable("id") Long id) {
        return success(documentService.getDocument(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文档")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    public CommonResult<Boolean> deleteDocument(@PathVariable("id") Long id) {
        documentService.deleteDocument(id);
        return success(true);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新文档状态（共享/私有）")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    public CommonResult<Boolean> updateDocumentStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") String status) {
        documentService.updateDocumentStatus(id, status);
        return success(true);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获得文档统计")
    public CommonResult<ZkDocumentStatisticsVO> getDocumentStatistics() {
        return success(documentService.getDocumentStatistics());
    }

    @PostMapping("/{id}/wps-link")
    @Operation(summary = "获得WPS链接（查看或编辑）")
    @Parameter(name = "id", description = "文档编号", required = true, example = "1")
    public CommonResult<ZkDocumentWPSLinkVO> getWPSLink(
            @PathVariable("id") Long id,
            @RequestParam(value = "mode", defaultValue = "edit") String mode) {
        // 使用Service的getWPSLink方法
        if (documentService instanceof cn.iocoder.yudao.module.fish.service.zkdocument.ZkDocumentServiceImpl) {
            cn.iocoder.yudao.module.fish.service.zkdocument.ZkDocumentServiceImpl impl = 
                (cn.iocoder.yudao.module.fish.service.zkdocument.ZkDocumentServiceImpl) documentService;
            return success(impl.getWPSLink(id, mode));
        }
        // 降级：如果无法获取实现类，使用原有方法
        return success(documentService.getWPSEditLink(id));
    }
}

