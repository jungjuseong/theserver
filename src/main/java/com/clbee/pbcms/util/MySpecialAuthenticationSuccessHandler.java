package com.clbee.pbcms.util;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

@Service
public class MySpecialAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication arg2) throws IOException,
			ServletException {
		// TODO Auto-generated method stub
		Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>)SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		GrantedAuthority element = authorities.iterator().next();
		String authority = element.getAuthority();

		Map<String, String> map = new HashMap<String, String>();
		Enumeration headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			map.put(key, value);
		}

		System.out.println(map);
		System.out.println("웹인지 모바일인지 구분하기 = " +  map.get("user-agent"));
		String userAgent = map.get("user-agent");

		//iPhone Android iPad일경우 바로 다운로드 페이지로
		if(userAgent.contains("Android")){

			response.sendRedirect("/down/list.html?currentPage=1&isMobile=ADD");
			return;
		}else if( userAgent.contains("iPad") || userAgent.contains("iPhone")){
			response.sendRedirect("/down/list.html?currentPage=1&isMobile=IPHD");
			return;
		}

		if(ConditionCompile.bookFeature) {
			if("bookUser".equals(activeUser.getMemberVO().getUserId())) {
				response.sendRedirect("/book/list.html?currentPage=1");
				return;
			}
		}

		if("ROLE_COMPANY_USER".equals(authority)){
			response.sendRedirect("/down/list.html?currentPage=1&isMobile=NOMB");
		}else{
			response.sendRedirect("/app/list.html?currentPage=1&appSeq=&searchValue=&isAvailable=true");
		}
	}
}
