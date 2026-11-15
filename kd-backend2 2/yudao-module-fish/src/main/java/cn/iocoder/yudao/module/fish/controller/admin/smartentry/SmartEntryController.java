package cn.iocoder.yudao.module.fish.controller.admin.smartentry;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.fish.service.smartentry.SmartEntryService;
import jakarta.annotation.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@RequestMapping("/fish/smart-entry")
@PreAuthorize("isAuthenticated()")
public class SmartEntryController {
    @Resource private SmartEntryService service;

    @PostMapping(value="/records", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public CommonResult<Map<String, Object>> create(@RequestParam String title,
            @RequestParam(defaultValue="") String content, @RequestParam String requestId,
            @RequestParam(required=false) List<MultipartFile> images) throws IOException {
        return success(service.create(title, content, requestId, images));
    }

    @GetMapping("/records")
    public CommonResult<Map<String, Object>> list(@RequestParam(defaultValue="1") int pageNo, @RequestParam(defaultValue="10") int pageSize) {
        return success(service.list(pageNo, pageSize));
    }

    @GetMapping("/records/{id}")
    public CommonResult<Map<String, Object>> detail(@PathVariable long id) { return success(service.detail(id)); }

    @GetMapping("/search")
    public CommonResult<Map<String, Object>> search(@RequestParam String q) { return success(service.search(q)); }

    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> image(@PathVariable long id) {
        var image = service.image(id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(image.mime()))
                .cacheControl(CacheControl.noStore()).header("X-Content-Type-Options", "nosniff")
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline().filename(image.name(), StandardCharsets.UTF_8).build().toString())
                .body(image.bytes());
    }
}
