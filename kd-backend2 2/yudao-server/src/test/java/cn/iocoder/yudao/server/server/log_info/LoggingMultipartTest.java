package cn.iocoder.yudao.server.server.log_info;

import cn.iocoder.yudao.framework.security.core.util.TokenUtil;
import cn.iocoder.yudao.framework.web.config.WebProperties;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class LoggingMultipartTest {
    @Test void loggerDoesNotReadAlreadyParsedUploadBody() throws Exception {
        var request = spy(new MockMultipartHttpServletRequest());
        request.setRequestURI("/admin-api/fish/smart-entry/records"); request.setMethod("POST");
        request.setContentType("multipart/form-data; boundary=example");
        doThrow(new IllegalStateException("getInputStream() has already been called")).when(request).getReader();
        var response = new MockHttpServletResponse();
        var interceptor = new LoggingInterceptor();
        try (var token = mockStatic(TokenUtil.class)) {
            token.when(TokenUtil::userInfo).thenReturn("test-user");
            assertTrue(interceptor.preHandle(request, response, new Object()));
            interceptor.afterCompletion(request, response, new Object(), null);
        }
        verify(request, never()).getReader(); verify(request, never()).getInputStream();
    }
    @Test void filterPreservesMultipartRequestAndBinaryStream() throws Exception {
        var request = spy(new MockMultipartHttpServletRequest());
        request.setRequestURI("/admin-api/fish/smart-entry/records");
        request.setContentType("multipart/form-data; boundary=example");
        var response = new MockHttpServletResponse(); var chain = mock(FilterChain.class);
        new MyIMTAFilter(new WebProperties()).doFilterInternal(request, response, chain);
        verify(chain).doFilter(same(request), same(response));
        verify(request, never()).getReader(); verify(request, never()).getInputStream();
    }
}
