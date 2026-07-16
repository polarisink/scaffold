package com.scaffold.web.config;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingResponseWrapper;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLogFilterTest {

    @Test
    void doesNotBufferOrCompleteEventStreamResponses() throws Exception {
        RequestLogFilter filter = new RequestLogFilter(new WebProperties(null, null, null));
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/sse/connect");
        request.addHeader(HttpHeaders.ACCEPT, MediaType.TEXT_EVENT_STREAM_VALUE);
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (filteredRequest, filteredResponse) -> {
            assertThat(filteredResponse).isNotInstanceOf(ContentCachingResponseWrapper.class);
            HttpServletResponse servletResponse = (HttpServletResponse) filteredResponse;
            servletResponse.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
            servletResponse.getWriter().write("event:connected\ndata:{}\n\n");
            servletResponse.flushBuffer();
        });

        assertThat(response.getContentAsString()).isEqualTo("event:connected\ndata:{}\n\n");
        assertThat(response.getHeader(HttpHeaders.CONTENT_LENGTH)).isNull();
    }
}
