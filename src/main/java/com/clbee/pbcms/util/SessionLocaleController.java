package com.clbee.pbcms.util;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Controller
public class SessionLocaleController {
	 @RequestMapping(value = "setChangeLocale.html")
	 public String changeLocale(@RequestParam(required = false) String locale,
			  ModelMap map, HttpServletRequest request, HttpServletResponse response) {
			  HttpSession session = request.getSession();
			    Locale locales = null;
			  // 넘어온 파라미터에 ko가 있으면 Locale의 언어를 한국어로 바꿔준다.
			  // 그렇지 않을 경우 영어로 설정한다.
			   if(ConditionCompile.isJapan){
				  if (locale.matches("ko")) {
				   locales = Locale.JAPAN;
				  } else {
				  //locales = Locale.ENGLISH;
				   locales = Locale.JAPAN;
				  }
			   }else{
				  if(locale.matches("ko")) {
					locales = Locale.KOREAN;
				  } else {
					//locales = Locale.ENGLISH;
					locales = Locale.KOREAN;
				  } 
			   }
			  
			  // 세션에 존재하는 Locale을 새로운 언어로 변경해준다.
			  session.setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME, locales);
			  // 해당 컨트롤러에게 요청을 보낸 주소로 돌아간다.
			  return "redirect:/index.html";
	}
}
