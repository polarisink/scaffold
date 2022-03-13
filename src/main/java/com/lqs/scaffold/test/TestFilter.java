package com.lqs.scaffold.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author lqs
 * @describe
 * @date 2021/11/16
 */

@Slf4j
@Component
@WebFilter(urlPatterns = {"/**"}, filterName = "tokenAuthorFilter")
public class TestFilter implements Filter {
	@Override
	public void init(FilterConfig filterConfig) {
		log.info("TokenFilter init {}", filterConfig.getFilterName());
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		log.info("TokenFilter doFilter 我拦截到了请求");
		log.info("TokenFilter doFilter", ((HttpServletRequest) request).getHeader("token"));
		//到下一个链
		chain.doFilter(request, response);

	}

	@Override
	public void destroy() {
		log.info("TokenFilter destroy");
	}
}
