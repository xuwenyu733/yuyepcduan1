package cn.iocoder.yudao.framework.common.util.easy;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * 自定义MultipartFile实现类
 * 基于字节数组存储文件内容，避免生成临时文件
 */
public class CustomMultipartFile implements MultipartFile {

    private final String name;               // 表单字段名称
    private final String originalFilename;   // 原始文件名
    private final String contentType;        // 内容类型
    private final byte[] content;            // 文件内容字节数组

    /**
     * 构造函数
     *
     * @param name             表单字段名称
     * @param originalFilename 原始文件名
     * @param contentType      内容类型（MIME类型）
     * @param content          文件内容字节数组
     */
    public CustomMultipartFile(String name, String originalFilename,
                               String contentType, byte[] content) {
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.content = content != null ? content : new byte[0];
    }

    /**
     * 简化构造函数（使用默认内容类型）
     *
     * @param originalFilename 原始文件名
     * @param content          文件内容字节数组
     */
    public CustomMultipartFile(String originalFilename, byte[] content) {
        this("file", originalFilename, "application/octet-stream", content);
    }

    /**
     * 从字符串内容创建MultipartFile
     *
     * @param originalFilename 原始文件名
     * @param content          字符串内容
     * @return CustomMultipartFile实例
     */
    public static CustomMultipartFile fromString(String originalFilename, String content) {
        byte[] bytes = content.getBytes();
        return new CustomMultipartFile(originalFilename, bytes);
    }

    /**
     * 从本地文件创建MultipartFile
     *
     * @param file 本地文件
     * @return CustomMultipartFile实例
     * @throws IOException 文件读取异常
     */
    public static CustomMultipartFile fromFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] content = fis.readAllBytes();
            String contentType = java.nio.file.Files.probeContentType(file.toPath());
            return new CustomMultipartFile("file", file.getName(),
                    contentType, content);
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getOriginalFilename() {
        return this.originalFilename;
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty() {
        return this.content == null || this.content.length == 0;
    }

    @Override
    public long getSize() {
        return this.content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return this.content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        if (dest.exists() && !dest.delete()) {
            throw new IOException("目标文件已存在且无法删除: " + dest.getAbsolutePath());
        }

        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(this.content);
            fos.flush();
        }
    }

    /**
     * 将文件内容写入指定路径
     *
     * @param destPath 目标路径
     * @throws IOException 写入异常
     */
    public void transferTo(String destPath) throws IOException {
        transferTo(new File(destPath));
    }
}

