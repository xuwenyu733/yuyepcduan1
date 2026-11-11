package cn.iocoder.yudao.module.infra.controller.admin.file;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/infra/preview")
public class FilePreviewController {

    private static final Map<String, String> CONTENT_TYPE_MAP = new HashMap<>();

    static {
        CONTENT_TYPE_MAP.put("pdf", "application/pdf");
        CONTENT_TYPE_MAP.put("jpg", "image/jpeg");
        CONTENT_TYPE_MAP.put("jpeg", "image/jpeg");
        CONTENT_TYPE_MAP.put("png", "image/png");
        CONTENT_TYPE_MAP.put("gif", "image/gif");
        CONTENT_TYPE_MAP.put("txt", "text/plain");
        CONTENT_TYPE_MAP.put("html", "text/html");
        CONTENT_TYPE_MAP.put("xml", "application/xml");
        CONTENT_TYPE_MAP.put("json", "application/json");
    }

    /**
     * 传入网盘的url地址，实现浏览器访问预览的效果，不会提示直接下载
     * @param fileUrl 网盘url地址
     * @param response 浏览器响应
     */
    @GetMapping
    @PermitAll
    public void previewFile(@RequestParam String fileUrl, HttpServletResponse response) {
        try {
            // 创建URL对象
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);

            // 设置请求头，模拟浏览器访问
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 获取文件扩展名
                String fileExtension = getFileExtension(fileUrl);
                String contentType = CONTENT_TYPE_MAP.getOrDefault(fileExtension.toLowerCase(),
                        connection.getContentType());

                // 设置响应头，实现预览而非下载
                response.setContentType(contentType);
                response.setHeader("Content-Disposition", "inline; filename=\"preview." + fileExtension + "\"");

                // 使用流式传输文件内容
                try (InputStream inputStream = connection.getInputStream();
                     OutputStream outputStream = response.getOutputStream()) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("File not found or access denied");
            }
        } catch (Exception e) {
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Preview failed: " + e.getMessage());
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getFileInfo(@RequestParam String fileUrl) {
        Map<String, Object> result = new HashMap<>();
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                result.put("url", fileUrl);
                result.put("contentType", connection.getContentType());
                result.put("contentLength", connection.getContentLength());
                result.put("fileExtension", getFileExtension(fileUrl));
                result.put("previewable", isPreviewable(fileUrl));
                return ResponseEntity.ok(result);

            } else {
                result.put("error", "File not accessible");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }

        } catch (Exception e) {
            result.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    private String getFileExtension(String fileUrl) {
        int lastDotIndex = fileUrl.lastIndexOf('.');
        int lastSlashIndex = fileUrl.lastIndexOf('/');

        if (lastDotIndex > lastSlashIndex && lastDotIndex < fileUrl.length() - 1) {
            return fileUrl.substring(lastDotIndex + 1).toLowerCase();
        }
        return "unknown";
    }

    private boolean isPreviewable(String fileUrl) {
        String extension = getFileExtension(fileUrl);
        return CONTENT_TYPE_MAP.containsKey(extension);
    }
}
