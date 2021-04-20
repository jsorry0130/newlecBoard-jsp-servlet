package com.newlecture.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

@WebFilter("/*")
public class CharacterEncodingFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8"); //모든 서블릿이실행되기 전에 필터가 실행되므로 UTF-8의 설정완료
		//System.out.println("before filter"); //모든 서블릿의 환경설정을 대신해주는 느낌
		chain.doFilter(request, response);  //흐름을 가져온다
		//System.out.println("after filter");

	}

}
