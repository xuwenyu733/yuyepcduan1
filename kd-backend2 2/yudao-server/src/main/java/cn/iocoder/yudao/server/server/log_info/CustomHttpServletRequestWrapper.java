package cn.iocoder.yudao.server.server.log_info;

/**
 * @创建人 lr
 * @创建时间 2024-05-02
 * @描述
 */
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author zhoule
 * @description: 自定义request wrapper用于对请求体中的报文进行处理
 */
public class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private byte[] body;

    public CustomHttpServletRequestWrapper(HttpServletRequest request) throws IOException
    {
        super(request);

        BufferedReader reader = request.getReader();
        byte[] bytes = convertReaderToByteArray(reader);
        this.body = bytes;

//        try (StringWriter writer = new StringWriter()) {
//            int read;
//            byte[] buf = new byte[1024 * 8];
//            while ((read = reader.read(buf)) != -1) {
//                writer.write(buf, 0, read);
//            }
//            this.body = writer.getBuffer().toString().getBytes();
//        }
    }

    private static byte[] convertReaderToByteArray(BufferedReader reader) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        String line;
        while ((line = reader.readLine()) != null) {
            byteArrayOutputStream.write(line.getBytes(StandardCharsets.UTF_8));
            byteArrayOutputStream.write("\n".getBytes(StandardCharsets.UTF_8)); // 添加换行符
        }
        return byteArrayOutputStream.toByteArray();
    }

    public String getBody(){
        return new String(body, StandardCharsets.UTF_8);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
        return new ServletInputStream()
        {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            public int read() throws IOException
            {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException
    {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}

