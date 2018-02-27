package com.clbee.pbcms.controller;


import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javassist.expr.NewArray;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;


import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import com.clbee.pbcms.service.AppService;
import com.clbee.pbcms.service.CompanyService;
import com.clbee.pbcms.service.DepartmentService;
import com.clbee.pbcms.service.DeviceService;
import com.clbee.pbcms.service.LogService;
import com.clbee.pbcms.service.MemberService;
import com.clbee.pbcms.service.NoticeService;
import com.clbee.pbcms.service.ProvisionService;
import com.clbee.pbcms.util.ConditionCompile;
import com.clbee.pbcms.util.DateUtil;
import com.clbee.pbcms.util.FileUtil;
import com.clbee.pbcms.util.Formatter;
import com.clbee.pbcms.util.myUserDetails;
import com.clbee.pbcms.vo.CompanyVO;
import com.clbee.pbcms.vo.DepartmentVO;
import com.clbee.pbcms.vo.DeviceList;
import com.clbee.pbcms.vo.DeviceVO;
import com.clbee.pbcms.vo.InappcategoryVO;
import com.clbee.pbcms.vo.LogList;
import com.clbee.pbcms.vo.MemberList;
import com.clbee.pbcms.vo.MemberVO;
import com.clbee.pbcms.vo.NoticeList;
import com.clbee.pbcms.vo.NoticeSubVO;
import com.clbee.pbcms.vo.NoticeVO;
import com.clbee.pbcms.vo.NoticeappSubVO;
import com.clbee.pbcms.vo.ProvisionVO;

@Controller
public class ManController {
	
	@Autowired
	AppService appService;
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	ProvisionService provisionService;
	
	@Autowired
	MessageSource messageSource;

	@Autowired
	CompanyService companyService;
	
	@Autowired
	JavaMailSender javaMailSender;
	
	@Autowired
	LocaleResolver localeResolver;
	
	@Autowired
	DepartmentService departmentService;
	
	@Autowired
	DeviceService deviceService;
	
	@Autowired
	NoticeService noticeService;
	
	@Autowired
	LogService logService;
	
	@InitBinder
	private void initBinder(WebDataBinder binder) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//edit for the    format you need
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	    binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class,  true));
	}

	@RequestMapping(value = "man/provision/list.html", method = RequestMethod.GET)
	public ModelAndView man_provision_list(HttpServletRequest request, HttpSession session){

		ModelAndView modelAndView = new ModelAndView();
		try{
			String provSeq = request.getParameter("provSeq");
			myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();		
			System.out.println(activeUser.getMemberVO().getUserSeq());
			//for member seq
			int regUserSeq = activeUser.getMemberVO().getUserSeq();
			//for company seq
			int regCompanySeq = activeUser.getMemberVO().getCompanySeq();
			List provitionList = null;
			provitionList = provisionService.selectList(regUserSeq, regCompanySeq);
			System.out.println("provitionList========================================================"+provitionList.toString());
			modelAndView.addObject("provSeq", provSeq);
			modelAndView.addObject("provisionList", provitionList);
			modelAndView.setViewName("05_admin/distribution_list");
		}catch(Exception e){
			e.printStackTrace();
		}
		return modelAndView;
	}

	@RequestMapping(value = "man/provision/delete.html", method = RequestMethod.POST)
	public ModelAndView man_provision_delete(HttpServletRequest request, HttpSession session){
		ModelAndView modelAndView = new ModelAndView();
		try {
			System.out.println("In man_provision_delete!!");		
			myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();		
			System.out.println(activeUser.getMemberVO().getUserSeq());
			//for member seq
			int regUserSeq = activeUser.getMemberVO().getUserSeq();
			//for company seq
			int regCompanySeq = activeUser.getMemberVO().getCompanySeq();
			//provSeq
			int provSeq = Integer.parseInt(request.getParameter("provSeq"));

			ProvisionVO vo;
			vo = provisionService.selectRow(provSeq, regUserSeq, regCompanySeq);
			provisionService.delete(provSeq, regUserSeq, regCompanySeq);

			String provisionFilePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.path.provision.file",null, localeResolver.resolveLocale(request))+vo.getDistrProfileSaveName()+"."+vo.getDistrProfile();
			System.out.println("provisionFilePath==="+provisionFilePath);
			deleteFile(provisionFilePath);

			modelAndView.setViewName("redirect:/man/provision/list.html");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			HashMap mv = new HashMap();
			//message : ���� �����Ͽ����ϴ�.
			mv.put("msg", messageSource.getMessage("man.control.001", null, localeResolver.resolveLocale(request)));
			mv.put("type", "href");
			mv.put("url", "/man/provision/list.html");
			mv.put("paramName", "provSeq");
			mv.put("paramValue", request.getParameter("provSeq"));
			modelAndView.addAllObjects(mv);
			modelAndView.setViewName("inc/dummy");
		}
		return modelAndView;
	}

	private void deleteFile(String provisionFilePath) {
		// TODO Auto-generated method stub
		File file = new File(provisionFilePath);
		if(!FileUtil.delete(file)){
			System.out.println("���ϻ�������");
		}
	}

	//ȸ������������
	@RequestMapping(value = "man/provision/modify.html", method = RequestMethod.GET)
	public @ResponseBody ProvisionVO man_provision_modify(HttpServletRequest request, HttpSession session) throws Exception{
		
		int provSeq = Integer.parseInt(request.getParameter("provSeq") );
		
		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();		
		System.out.println(activeUser.getMemberVO().getUserSeq());
		//for member seq
		int regUserSeq = activeUser.getMemberVO().getUserSeq();
		//for company seq
		int regCompanySeq = activeUser.getMemberVO().getCompanySeq();

		ProvisionVO vo = new ProvisionVO();
		vo = provisionService.selectRow(provSeq, regUserSeq, regCompanySeq);

		return vo;
	}

	//��������
	@RequestMapping(value = "man/provision/modify.html", method = RequestMethod.POST)
	public ModelAndView man_provision_modify_impl(ProvisionVO vo, HttpServletRequest request, HttpSession session, @RequestParam("provFile") MultipartFile file) throws Exception{  	
		// ���� ó��
		ModelAndView modelAndView = new ModelAndView();
		HashMap<String, String> map = new HashMap<String, String>();
		//meessage : �� �� ���� ���� �߻�
		String msg = messageSource.getMessage("man.control.003", null, localeResolver.resolveLocale(request));
		try{
			if(!file.isEmpty()){
				map = uploadFile(map, file, request);	
				vo.setDistrProfileName(map.get("orgFileName"));//�������ϸ�
				vo.setDistrProfile(map.get("fileExt"));//����Ȯ����
				vo.setDistrProfileSaveName(map.get("saveFileName"));//�������ϸ�			
			}
			if(map.get("error")!=null&&!"none".equals(map.get("error"))){
				msg = map.get("error");
				throw new Exception();
			}
	
			myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();		
			System.out.println(activeUser.getMemberVO().getUserSeq());
			//for member seq
			int regUserSeq = activeUser.getMemberVO().getUserSeq();
			//for company seq
			String regUserId = activeUser.getMemberVO().getUserId();
			int regCompanySeq = activeUser.getMemberVO().getCompanySeq();
			String regUserGb = activeUser.getMemberVO().getUserGb(); 
			
			//vo.setProvSeq(provSeq);	
			vo.setChgUserSeq(regUserSeq);
			vo.setChgUserId(regUserId);
			vo.setRegUserGb(regUserGb);
			vo.setRegUserSeq(regUserSeq);
			vo.setRegUserId(regUserId);
			vo.setChgUserGb(regUserGb);
			vo.setRegCompanySeq(regCompanySeq);
			
			int result = provisionService.update( vo);
			if(result<1){
				String type = "alertandredirect";
				String redirectUrl = "";
				HashMap mv = new HashMap();
				mv.put("msg", msg);
				mv.put("type", "href");
				mv.put("url", "/man/provision/list.html");
				mv.put("paramName", "provSeq");
				mv.put("paramValue", vo.getProvSeq());
				modelAndView.addAllObjects(mv);
				modelAndView.setViewName("inc/dummy");
			}else{
				modelAndView.setViewName("redirect:/man/provision/list.html");			
			}
		}catch(Exception e){
			e.printStackTrace();
			String provisionFilePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) +messageSource.getMessage("file.path.provision.file",null, localeResolver.resolveLocale(request))+vo.getDistrProfileSaveName()+"."+vo.getDistrProfile();
			System.out.println("provisionFilePath==="+provisionFilePath);
			deleteFile(provisionFilePath);
			HashMap mv = new HashMap();
			//message : �Է� ������ �߸��Ǿ����ϴ�.
			mv.put("msg", messageSource.getMessage("man.control.004", null, localeResolver.resolveLocale(request)));
			mv.put("type", "href");
			mv.put("url", "/man/provision/list.html");
			mv.put("paramName", "provSeq");
			mv.put("paramValue", vo.getProvSeq());
			modelAndView.addAllObjects(mv);
			modelAndView.setViewName("inc/dummy");
		}
		return modelAndView;
	}

	//������μ���
	@RequestMapping(value = "man/provision/regist.html", method = RequestMethod.POST)
	public ModelAndView man_provision_regist_impl(ProvisionVO vo, HttpServletRequest request, HttpSession session, @RequestParam("provFile") MultipartFile file) throws Exception
	{
		// ���� ó��
		ModelAndView modelAndView = new ModelAndView();
		HashMap<String, String> map = new HashMap<String, String>();
		//message : �˼� ���� ���� �߻�
		String msg = messageSource.getMessage("man.control.003", null, localeResolver.resolveLocale(request));
		try{
			map = uploadFile(map, file, request);
			if(map.get("error")!=null && !"none".equals(map.get("error"))){
				msg = map.get("error");
				throw new Exception();
			}
			vo.setDistrProfileName(map.get("orgFileName"));//�������ϸ�
			vo.setDistrProfile(map.get("fileExt"));//����Ȯ����
			vo.setDistrProfileSaveName(map.get("saveFileName"));//�������ϸ�
			System.out.println(vo);
			myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			System.out.println(activeUser.getMemberVO().getUserSeq());
			//for member seq
			int regUserSeq = activeUser.getMemberVO().getUserSeq();
			//for company seq
			String regUserId = activeUser.getMemberVO().getUserId();
			int regCompanySeq = activeUser.getMemberVO().getCompanySeq();
			String regUserGb = activeUser.getMemberVO().getUserGb();

			//vo.setProvSeq(provSeq);
			vo.setChgUserSeq(regUserSeq);
			vo.setChgUserId(regUserId);
			vo.setRegUserGb(regUserGb);
			vo.setRegUserSeq(regUserSeq);
			vo.setRegUserId(regUserId);
			vo.setChgUserGb(regUserGb);
			vo.setRegCompanySeq(regCompanySeq);
			provisionService.insert(vo);

			modelAndView.setViewName("redirect:/man/provision/list.html");		
		}catch(Exception e){
			e.printStackTrace();
			String provisionFilePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) +messageSource.getMessage("file.path.provision.file",null, localeResolver.resolveLocale(request))+vo.getDistrProfileSaveName()+"."+vo.getDistrProfile();
			System.out.println("provisionFilePath==="+provisionFilePath);
			deleteFile(provisionFilePath);
			HashMap mv = new HashMap();
			mv.put("msg", msg);
			mv.put("type", "href");
			mv.put("url", "/man/provision/list.html");
			mv.put("paramName", "provSeq");
			mv.put("paramValue", "reg");
			modelAndView.addAllObjects(mv);
			modelAndView.setViewName("inc/dummy");
		}
		return modelAndView;
	}

	public HashMap<String, String> uploadFile(HashMap map, MultipartFile upLoadFile, HttpServletRequest request) {

		InputStream inputStream = null;
		OutputStream outputStream = null;
		String error = "none";
		try{
			if (upLoadFile != null) {
				if (upLoadFile.getSize() > 0) {

					String orgFileName = upLoadFile.getOriginalFilename();
					String orgFileOnlyName = orgFileName.substring(0, orgFileName.lastIndexOf("."));
					 
					String fileExt = orgFileName.substring(orgFileName.lastIndexOf(".") + 1, orgFileName.length());
					fileExt = fileExt.toLowerCase();
					
					String saveFileOnlyName = DateUtil.getDate("yyyyMMdd_hhmmss") + "_" + Formatter.getRandomNumber(100);
					String saveFileName = saveFileOnlyName + "." + fileExt;
					inputStream = upLoadFile.getInputStream();				
					map.put("orgFileName", orgFileOnlyName);
					map.put("saveFileName", saveFileOnlyName);
					map.put("fileSize", upLoadFile.getSize());
					map.put("fileExt", fileExt);
					System.out.println("fileExt====="+fileExt);
					System.out.println("contentsType====="+upLoadFile.getContentType());
					if (!("mobileprovision".equals(fileExt.toLowerCase())||"keystore".equals(fileExt.toLowerCase()))) {
						//message : ����� �� ���� ���������Դϴ�. ��� �Ǿ���ϴµ�,
						//message : �Է� ������ �߸��Ǿ����ϴ� ��� ��.
						error = messageSource.getMessage("man.control.004", null, localeResolver.resolveLocale(request));
						throw new Exception(messageSource.getMessage("man.control.004", null, localeResolver.resolveLocale(request)));
					}
					String savePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) +messageSource.getMessage("file.path.provision.file", null, localeResolver.resolveLocale(request));
					outputStream = new FileOutputStream(savePath + saveFileName);

					int readBytes = 0;
					byte[] buffer = new byte[8192];
					while ((readBytes = inputStream.read(buffer, 0 , 8192)) != -1) {
						outputStream.write(buffer, 0, readBytes);
					}

					outputStream.close();
					inputStream.close();
					error = "none";
				}
			}else{
				//message : ��� ���� �ҷ����µ� �����߽��ϴ�.
				error = messageSource.getMessage("man.control.005", null, localeResolver.resolveLocale(request));
				throw new Exception(messageSource.getMessage("man.control.005", null, localeResolver.resolveLocale(request)));
			}
		}catch(Exception e){
			//messsage : ���� ���ε� �� �� �� ���� ���� �߻�
			if("none".equals(error))error=messageSource.getMessage("man.control.006", null, localeResolver.resolveLocale(request));
			//map.put("error", error);
		}finally{
			map.put("error", error);
			return map;			
		}
	}	

	@RequestMapping(value = "man/user/list.html", method = RequestMethod.GET)
	public ModelAndView manUserList( String page, String searchType, String searchValue, String isAvailable, HttpServletRequest request, HttpSession session){
		ModelAndView modelAndView = new ModelAndView();

		String[] formattedDate;
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

		//System.out.println("searchType = "+searchType);
		//System.out.println("searchValue = "+searchValue);

		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>)SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		String authority = null;

		GrantedAuthority element = authorities.iterator().next();
		authority = element.getAuthority();

		//System.out.println("[AFTER]Authority = " + authority);
		if("ROLE_COMPANY_MEMBER".equals(authority)) {
			MemberList memberList =  memberService.getListMember(Integer.parseInt(page), activeUser.getMemberVO().getCompanySeq(), 10, searchType, searchValue, isAvailable, true);
			CompanyVO companyVO = companyService.findByCustomInfo("companySeq", activeUser.getMemberVO().getCompanySeq());
			
			formattedDate = new String[memberList.getList().size()];
			  
			for( int i = 0; i < memberList.getList().size() ; i++){
				formattedDate[i] = format.format(memberList.getList().get(i).getRegDt());
			}
			
			modelAndView.addObject("currentPage", page);
			modelAndView.addObject("formattedDate",formattedDate);
			modelAndView.addObject("companyName", companyVO.getCompanyName());
			modelAndView.addObject("memberList", memberList);
			modelAndView.setViewName("05_admin/user_list");
		}else if("ROLE_ADMIN_SERVICE".equals(authority)) {
			MemberList memberList =  memberService.getListMember(Integer.parseInt(page), activeUser.getMemberVO().getCompanySeq(), 10, searchType, searchValue, isAvailable, false);
			formattedDate = new String[memberList.getList().size()];

			for( int i = 0; i < memberList.getList().size() ; i++){
				formattedDate[i] = format.format(memberList.getList().get(i).getRegDt());
			}

			modelAndView.addObject("currentPage", page);
			modelAndView.addObject("formattedDate",formattedDate);
			modelAndView.addObject("memberList", memberList);
			modelAndView.setViewName("05_admin/user_list");
		}else {
			MemberList memberList =  memberService.getListMember(Integer.parseInt(page), activeUser.getMemberVO().getCompanySeq(), 10, searchType, searchValue, isAvailable, true);
			CompanyVO companyVO = companyService.findByCustomInfo("companySeq", activeUser.getMemberVO().getCompanySeq());
			
			formattedDate = new String[memberList.getList().size()];
			  
			for( int i = 0; i < memberList.getList().size() ; i++){
				formattedDate[i] = format.format(memberList.getList().get(i).getRegDt());
			}
			
			modelAndView.addObject("currentPage", page);
			modelAndView.addObject("formattedDate",formattedDate);
			modelAndView.addObject("companyName", companyVO.getCompanyName());
			modelAndView.addObject("memberList", memberList);
			modelAndView.setViewName("05_admin/user_list");
		}
		return modelAndView;
	}

	@RequestMapping(value = "man/user/modify.html", method = RequestMethod.GET)
	public ModelAndView manUserModify( String userSeq, HttpServletRequest request, HttpSession session){
		ModelAndView modelAndView = new ModelAndView();
		//System.out.println("userSeq = " + userSeq);
		MemberVO memberVO = memberService.findByCustomInfo("userSeq", Integer.parseInt(userSeq));
		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		List<DepartmentVO> list = departmentService.selectList(activeUser.getMemberVO().getCompanySeq(), "true");
		if(activeUser.getMemberVO().getOnedepartmentSeq() != null) {
			List<DepartmentVO> secondList = departmentService.selectChildArrayList(activeUser.getMemberVO().getOnedepartmentSeq(), activeUser.getMemberVO().getCompanySeq());
			modelAndView.addObject("secondDepartmentList", secondList);
		}
		System.out.println("userId = " + memberVO.getUserId());

		modelAndView.addObject("memberVO", memberVO);
		modelAndView.addObject("DepartmentList", list);
		modelAndView.setViewName("05_admin/user_modify");
		return modelAndView;
	}

	@RequestMapping(value = "man/user/modify.html", method = RequestMethod.POST)
	public ModelAndView manUserModifyPOST( MemberVO formMemVO, HttpServletRequest request, HttpSession session){
		ModelAndView modelAndView = new ModelAndView();
		String page = request.getParameter("page"), searchType = request.getParameter("searchType"), searchValue = request.getParameter("searchValue"), isAvailable = request.getParameter("isAvailable");
		
		formMemVO.setUserPw(changeSHA256(formMemVO.getUserPw()));

		//System.out.println("@@@@@@@@@@@@@@@ TwoDepartmentSeq" + formMemVO.getTwodepartmentSeq());
		if("5".equals(formMemVO.getUserStatus())) {
			formMemVO.setEmailChkSession(changeSHA256(formMemVO.getUserId()));
			String from=messageSource.getMessage("send.email.ID", null, localeResolver.resolveLocale(request));
			//message : PageCreator ���� �����Դϴ�.
			String subject=messageSource.getMessage("member.control.007", null, localeResolver.resolveLocale(request));
				try { 
					MimeMessage message = javaMailSender.createMimeMessage();
					MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
					messageHelper.setTo(formMemVO.getEmail());
					//message : �ش� URL�� �����Ͻø� ���� ������ �Ϸ� �˴ϴ�.
					messageHelper.setText(messageSource.getMessage("member.control.007", null, localeResolver.resolveLocale(request))+ "\n"+ "http://" + messageSource.getMessage("basic.Info.IP", null, localeResolver.resolveLocale(request))+":8080/member/join/ok.html?validId="+changeSHA256(formMemVO.getUserId()));
					messageHelper.setFrom(from);
					messageHelper.setSubject(subject); 
					javaMailSender.send(message);
				} catch(Exception e){
					System.out.println(e);
			}
		}
		formMemVO.setChgDt(new Date());
		formMemVO.setChgIp(request.getRemoteAddr());
		memberService.updateMemberInfo( formMemVO, formMemVO.getUserSeq());
		MemberVO dbMemberVO = memberService.findByCustomInfo( "userId", formMemVO.getUserId() );

		modelAndView.addObject("modifySuccess", true);
		modelAndView.addObject("memberVO", dbMemberVO);
		modelAndView.setViewName("redirect:/man/user/list.html?page="+page+"&searchType="+searchType+"&searchValue="+searchValue+"&isAvailable="+isAvailable);
	
		return modelAndView;
	}
	
	@RequestMapping(value="/man/test.html", method = RequestMethod.GET)
	public ModelAndView manTestGET( MemberVO memberVO, HttpServletRequest request, String companySeq, HttpSession session){
		ModelAndView modelAndView = new ModelAndView();
		return modelAndView;
	}

	@RequestMapping(value = "man/user/write.html", method = RequestMethod.POST)
	public ModelAndView manUserWritePOST( MemberVO memberVO, HttpServletRequest request, HttpSession session){
		/* company Write .HTML ���� list�� POST������� ����..*/
		ModelAndView modelAndView = new ModelAndView();
		int limitUser =  Integer.parseInt(messageSource.getMessage("limit.user.count", null, localeResolver.resolveLocale(request)));
		int permitUser = memberService.selectCountWithPermisionUserByCompanySeq(memberVO.getCompanySeq());
		String page = request.getParameter("page"), searchType = request.getParameter("searchType"), searchValue = request.getParameter("searchValue"), isAvailable = request.getParameter("isAvailable");
		
		try{	
			if("".equals(memberVO.getEmail())) {
				if( permitUser >= limitUser ){
					modelAndView.addObject("msg", messageSource.getMessage("extend.local.088", null, localeResolver.resolveLocale(request)));
					modelAndView.addObject("type", "href");
					modelAndView.addObject("url", "/man/user/list.html?page="+page+"&searchType="+searchType+"&searchValue="+searchValue+"&isAvailable="+isAvailable);
					modelAndView.setViewName("inc/dummy");
				}

				memberVO.setRegDt(new Date());
				memberVO.setRegIp(request.getRemoteAddr());
				memberVO.setEmailChkSession(changeSHA256(memberVO.getUserId()));
				memberVO.setEmailChkGb("N");
				memberVO.setUserPw(changeSHA256(memberVO.getUserPw()));
				memberService.addMember(memberVO);
				CompanyVO companyVO = companyService.findByCustomInfo("companySeq", memberVO.getCompanySeq());
				modelAndView.setViewName("redirect:/man/user/list.html?page="+page+"&searchType="+searchType+"&searchValue="+searchValue+"&isAvailable="+isAvailable);
				return modelAndView;
			}
			memberVO.setRegDt(new Date());
			memberVO.setRegIp(request.getRemoteAddr());
			memberVO.setEmailChkSession(changeSHA256(memberVO.getUserId()));
			memberVO.setEmailChkGb("N");
			memberVO.setUserPw(changeSHA256(memberVO.getUserPw()));
			memberService.addMember(memberVO);
			CompanyVO companyVO = companyService.findByCustomInfo("companySeq", memberVO.getCompanySeq());
			//System.out.println("CurrentCommpanyName" + companyVO.getCompanyName());
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
			messageHelper.setTo(memberVO.getEmail());
			//message : �ش� URL�� �����Ͻø� ���� ������ �Ϸ� �˴ϴ�.
			messageHelper.setText(messageSource.getMessage("member.control.007", null, localeResolver.resolveLocale(request))+"\n"+"http://"+messageSource.getMessage("basic.Info.IP", null, localeResolver.resolveLocale(request))+":8080/member/join/ok.html?validId="+changeSHA256(memberVO.getUserId()));
			messageHelper.setFrom(messageSource.getMessage("send.email.ID", null, localeResolver.resolveLocale(request)));
			//message : PageCreator ���� �����Դϴ�.
			messageHelper.setSubject(messageSource.getMessage("member.control.007", null, localeResolver.resolveLocale(request))); 
			javaMailSender.send(message);

			modelAndView.setViewName("redirect:/man/user/list.html?page=1&searchType="+searchType+"&searchValue="+searchValue+"&isAvailable="+isAvailable);
		}catch( Exception e) {
			e.printStackTrace();
			//������ message : [����]����� ��� ����
			//������ message : �˼����� ���� �߻�
			modelAndView.addObject("msg", messageSource.getMessage("man.control.003", null, localeResolver.resolveLocale(request)));
			modelAndView.addObject("type", "href");
			modelAndView.addObject("url", "/man/user/list.html?page="+page+"&searchType="+searchType+"&searchValue="+searchValue+"&isAvailable="+isAvailable);
			modelAndView.setViewName("inc/dummy");
		}

		return modelAndView;
	}

	@RequestMapping(value = "man/user/write.html", method = RequestMethod.GET)
	public ModelAndView manUserWriteGET(HttpServletRequest request, HttpSession session){
		ModelAndView modelAndView = new ModelAndView();
		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		List<DepartmentVO> list = departmentService.selectList(activeUser.getMemberVO().getCompanySeq(), "true");
		if(activeUser.getMemberVO().getOnedepartmentSeq() != null) {
			List<DepartmentVO> secondList = departmentService.selectChildArrayList(activeUser.getMemberVO().getOnedepartmentSeq(), activeUser.getMemberVO().getCompanySeq());
			modelAndView.addObject("secondDepartmentList", secondList);
		}
		modelAndView.addObject("DepartmentList", list);
		modelAndView.addObject("currentCompanySeq", activeUser.getMemberVO().getCompanySeq());
		modelAndView.setViewName("05_admin/user_write");

		return modelAndView;
	}

	@RequestMapping(value = "man/user/delete.html", method = RequestMethod.POST)
	public @ResponseBody int manDeleteGET( String userSeq, HttpServletRequest request, HttpSession session){

		int numUserSeq = Integer.parseInt(userSeq);
		MemberVO memberVO = new MemberVO();
		/* ���� Ż�� */
		memberVO.setUserStatus("3");
		/* ����1, ���� 0*/
		return memberService.updateMemberInfo(memberVO, numUserSeq);
	}

	@RequestMapping(value = "man/device/list.html", method = RequestMethod.GET)
	public ModelAndView manDeviceListGET(String page, String searchType, String searchValue, HttpServletRequest request, HttpSession session) throws NumberFormatException, UnsupportedEncodingException{
		
		ModelAndView mav = new ModelAndView();		
		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String decodeValue ="";
		
		if(searchValue != null)
		decodeValue = URLDecoder.decode(searchValue, "UTF-8");
		DeviceList list = deviceService.selectDeviceList(Integer.parseInt(page), activeUser.getMemberVO().getCompanySeq(), searchType, decodeValue );
		
		mav.addObject("searchValue", decodeValue);
		mav.addObject("deviceList", list);
		mav.setViewName("05_admin/device_list");
		
		return mav;
	}

	@RequestMapping(value = "man/preference/modify.html", method = RequestMethod.GET)
	public ModelAndView manPreferenceModifyGET( String userSeq, HttpServletRequest request, HttpSession session) throws IOException{
		ModelAndView mav = new ModelAndView();

		Properties prop = new Properties();
		InputStream input = null;

		String localFilePath, iOSInstallPath, fileServerPath, basicIP, FTPID, FTPPW, emailID, emailPW, emailDomain, FTPIP, deviceFlag;
		localFilePath = iOSInstallPath = fileServerPath = basicIP = FTPID = FTPPW = emailID = emailPW = emailDomain = FTPIP = deviceFlag = "";
		
		//File file = new File("/usr/local/context-common.properties");
		//File file = new File("c:\\context-common.properties");
		
		File file = null;
		
		if(ConditionCompile.isIOS) {
			file = new File("/usr/local/context-common.properties");
		}else {
			file = new File("c:\\context-common.properties");
		}

		input = new FileInputStream(file);
		// load a properties file
		prop.load(input);

		// load a properties file
		// get the property value and print it out
		System.out.println(localFilePath = prop.getProperty("file.path.basic.URL"));
		System.out.println(iOSInstallPath = prop.getProperty("file.path.app.file.iOS"));
		//System.out.println(fileServerPath = prop.getProperty("file.path.app.file.app"));
		System.out.println(basicIP = prop.getProperty("basic.Info.IP"));
		System.out.println(FTPIP = prop.getProperty("FTP.Info.IP"));
		System.out.println(FTPID = prop.getProperty("FTP.Info.ID"));
		System.out.println(FTPPW = prop.getProperty("FTP.Info.PW"));
		System.out.println(emailID = prop.getProperty("send.email.ID"));
		System.out.println(emailPW = prop.getProperty("send.email.PW"));
		System.out.println(emailDomain = prop.getProperty("send.email.domain"));
		System.out.println(deviceFlag = prop.getProperty("device.is.used"));

		if(ConditionCompile.isDeviceOption) {
			mav.addObject("deviceFlag", deviceFlag);
		}
		mav.addObject("localFilePath", localFilePath);
		mav.addObject("iOSInstallPath", iOSInstallPath);
		mav.addObject("fileServerPath", fileServerPath);
		mav.addObject("basicIP", basicIP);
		mav.addObject("FTPIP", FTPIP);
		mav.addObject("FTPID", FTPID);
		mav.addObject("FTPPW", FTPPW);
		mav.addObject("emailID", emailID);
		mav.addObject("emailPW", emailPW);
		mav.addObject("emailDomain", emailDomain);

		mav.setViewName("05_admin/preference_page");
		return mav;
	}

	@RequestMapping(value = "man/preference/modify.html", method = RequestMethod.POST)
	public ModelAndView manPreferenceModifyPOST( String localFilePath, String iOSInstallPath, String fileServerPath, String basicIP, 
			String FTPIP, String FTPID, String FTPPW, String emailID, String deviceFlag, String emailPW, String emailDomain, HttpServletRequest request, HttpSession session) throws IOException, URISyntaxException{
		ModelAndView mav = new ModelAndView();
		Properties prop = new Properties();
		OutputStream output = null;
		InputStream input =null;

		//File file = new File("/usr/local/context-common.properties");
		//File file = new File("c:\\context-common.properties");
		// load a properties file

		File file = null;
		
		if(ConditionCompile.isIOS) {
			file = new File("/usr/local/context-common.properties");
		}else {
			file = new File("c:\\context-common.properties");
		}
		output = new FileOutputStream(file,false);

		prop.setProperty("file.path.basic.URL", localFilePath);
		prop.setProperty("file.path.app.file.iOS", iOSInstallPath);
		//prop.setProperty("file.path.app.file.app", fileServerPath);
		prop.setProperty("basic.Info.IP", basicIP);
		prop.setProperty("FTP.Info.IP", FTPIP);
		prop.setProperty("FTP.Info.ID", FTPID);
		prop.setProperty("FTP.Info.PW", FTPPW);
		prop.setProperty("send.email.ID", emailID);
		prop.setProperty("send.email.PW", emailPW);
		prop.setProperty("send.email.domain", emailDomain);
		if(ConditionCompile.isDeviceOption) {
			prop.setProperty("device.is.used", deviceFlag);
		}else {
			prop.setProperty("device.is.used", "false");
		}

		prop.setProperty("file.path.app.file", "/_upload/data/app/");
		prop.setProperty("file.upload.path.app.icon.file", "/_upload/images/app/icon/");
		prop.setProperty("file.upload.path.app.capture.file", "/_upload/images/app/capture/");
		prop.setProperty("file.upload.path.temp.images.file", "/_upload/_temp/images/");
		prop.setProperty("file.path.template.file", "/_upload/data/template/file/");
		prop.setProperty("file.upload.path.template.capture.file", "/_upload/images/app/templet/capture/");
		prop.setProperty("file.path.inapp.file", "/_upload/data/app/inapp/");
		prop.setProperty("file.upload.path.inapp.icon.file", "/_upload/images/inapp/icon/");
		prop.setProperty("file.upload.path.inapp.capture.file", "/_upload/images/inapp/capture/");
		prop.setProperty("file.path.contents.file", "/_upload/data/contents/");
		prop.setProperty("file.path.provision.file", "/_upload/provision/");
		prop.setProperty("file.path.pdf.file", "/_upload/data/pdf/");
		prop.setProperty("file.path.request.file", "/Users/Shared/FTPInput/Q/");
		//20180208 : lsy - download tab modify(property add)
		prop.setProperty("file.path.ios.down.file", "/_upload/Volumes/Data/data/server/newcms/data/app/ios/");
		prop.store(output, null);

		
		  
		if(ConditionCompile.isDeviceOption) {
			mav.addObject("deviceFlag", deviceFlag);
		}
		mav.addObject("localFilePath", localFilePath);
		mav.addObject("iOSInstallPath", iOSInstallPath);
		mav.addObject("fileServerPath", fileServerPath);
		mav.addObject("basicIP", basicIP);
		mav.addObject("FTPIP", FTPIP);
		mav.addObject("FTPID", FTPID);
		mav.addObject("FTPPW", FTPPW);
		mav.addObject("emailID", emailID);
		mav.addObject("emailPW", emailPW);
		mav.addObject("emailDomain", emailDomain);
		mav.setViewName("05_admin/preference_page");

		return mav;
	}

	@RequestMapping(value = "/man/department/management.html", method = RequestMethod.GET)
	public ModelAndView manDepartmentManagementGET(){
		ModelAndView mav = new ModelAndView();
		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		List<DepartmentVO> addList = departmentService.selectList(activeUser.getMemberVO().getCompanySeq(), "false");
		
		mav.addObject("DepartmentList", addList);
		mav.addObject("companySeq", activeUser.getMemberVO().getCompanySeq());
		mav.setViewName("05_admin/department_category");
		return mav;
	}

  	@RequestMapping( value = "/man/department/writeOK.html", method = RequestMethod.POST)
	public @ResponseBody DepartmentVO departmentWriteOKPOST( HttpSession session, HttpServletRequest req ){

		String companySeq, userId, userSeq, userGb, cateName, depth, categorySeq1;
		companySeq = userId = userSeq = userGb = cateName = depth = categorySeq1 = "";
		
		companySeq 	 = this.paramSet(req, "companySeq");
		cateName 	 = this.paramSet(req, "cateName");
		depth 		 = this.paramSet(req, "depth");
		categorySeq1 = this.paramSet(req, "categorySeq1");
		//categorySeq2 = this.paramSet(req, "categorySeq2");

		//�α�������
		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		userSeq	=	String.valueOf(activeUser.getMemberVO().getUserSeq());
		userId	=	activeUser.getMemberVO().getUserId();
		userGb	=	activeUser.getMemberVO().getUserGb();

		DepartmentVO departmentVO = new DepartmentVO();

		departmentVO.setCompanySeq(activeUser.getMemberVO().getCompanySeq());
		departmentVO.setDepartmentName(cateName);
		if(Integer.parseInt(depth) == 1){
			departmentVO.setDepartmentParent(0);
			//InCateVo.setCategorySeq(Integer.parseInt(categorySeq1));
		}else{
			departmentVO.setDepartmentParent(Integer.parseInt(categorySeq1));//�θ� ������ �־����.
			//InCateVo.setCategorySeq(Integer.parseInt(categorySeq2));
		}
		departmentVO.setDepth(depth);
		departmentVO.setRegUserSeq(activeUser.getMemberVO().getUserSeq());
		departmentVO.setUseGb("1");

		int departmentSeq = departmentService.insertDepartmentInfo(departmentVO);
		departmentVO.setDepartmentSeq(departmentSeq);

		return departmentVO;
	}
  	

  	@RequestMapping(value="/man/department/modifyOK.html" ,method={RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody List<DepartmentVO> categoryModifyOK(HttpSession session, HttpServletRequest req) throws Exception{

		String companySeq, userId, userSeq, userGb, cateName, depth, categorySeq1, categorySeq2, useGb;
		companySeq = userId = userSeq = userGb = cateName = depth = categorySeq1 = categorySeq2 = useGb = "";

		companySeq 		= this.paramSet(req, "companySeq");
		cateName 	 	= this.paramSet(req, "cateName");
		depth 		 	= this.paramSet(req, "depth");
		categorySeq1	= this.paramSet(req, "categorySeq1");
		categorySeq2 	= this.paramSet(req, "categorySeq2");
		useGb			= this.paramSet(req, "useGb");

		List<DepartmentVO> list = null;

		//�α�������
		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		userSeq	=	String.valueOf(activeUser.getMemberVO().getUserSeq());
		userId	=	activeUser.getMemberVO().getUserId();
		userGb	=	activeUser.getMemberVO().getUserGb();

		DepartmentVO departmentVO = new DepartmentVO();
		departmentVO.setCompanySeq(Integer.parseInt(companySeq));
		departmentVO.setDepartmentName(cateName);
		departmentVO.setUseGb(useGb);
		if(Integer.parseInt(depth) == 1){
			departmentVO.setDepartmentParent(0);
			departmentVO.setDepartmentSeq(Integer.parseInt(categorySeq1));
		}else{
			//InCateVo.setCategoryParent(Integer.parseInt(categorySeq2));//�θ� ������ �־����.
			departmentVO.setDepartmentSeq(Integer.parseInt(categorySeq2));
		}
		departmentVO.setDepth(depth);
		departmentVO.setRegUserSeq(Integer.parseInt(userSeq));
		departmentService.updateDepartmentInfo(departmentVO);

		if("1".equals(depth)) {
			list = departmentService.selectList(Integer.parseInt(companySeq),"false");
		}else if("2".equals(depth)){
			list = departmentService.selectChildArrayList(Integer.parseInt(categorySeq1), Integer.parseInt(companySeq));
		}
		return list;
	}

  	@RequestMapping(value="/man/department/childList.html" ,method={RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody Object[] categoryList2(HttpSession session, HttpServletRequest req) throws Exception{
		String companySeq, userId, userSeq, userGb, cateName, depth, categorySeq1, toUse ;
		companySeq = userId = userSeq = userGb = cateName = depth = categorySeq1 = toUse =  "";

		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		categorySeq1 	= this.paramSet(req, "categorySeq1");
		toUse			= this.paramSet(req, "toUse");

		Object[] departmentList = null;
		departmentList = departmentService.selectChildList(Integer.parseInt(categorySeq1), activeUser.getMemberVO().getCompanySeq(), toUse);

		System.out.println("====================================================================================3");
		System.out.println(departmentList);
		System.out.println("====================================================================================4");

		return departmentList;
	}

	@RequestMapping(value="/man/device/modify.html" , method=RequestMethod.GET )
	public ModelAndView manDeviceModifyGET(HttpSession session, HttpServletRequest req) throws Exception{
		ModelAndView mav = new ModelAndView();

		String deviceSeq;
		deviceSeq = "";
		deviceSeq = paramSet(req, "deviceSeq");
		DeviceVO deviceVO = deviceService.selectDeviceInfo(Integer.parseInt(deviceSeq));

		mav.addObject( "deviceVO" , deviceVO );
		mav.setViewName("05_admin/device_modify");

		return mav;
	}

	@RequestMapping(value="/man/device/modify.html" , method=RequestMethod.POST )
	public ModelAndView manDeviceModifyPOST( HttpSession session, DeviceVO deviceVO, HttpServletRequest req) throws Exception{
		ModelAndView mav = new ModelAndView();

		int result = deviceService.updateDeviceInfo(deviceVO);

		if (result < 0 ) {
			mav.addObject("msg", messageSource.getMessage("contents.control.003", null, localeResolver.resolveLocale(req)));
			mav.addObject("type", "href");
			mav.addObject("url", "/man/device/list.html?page=1");
			mav.setViewName("inc/dummy");
		}else {
			mav.addObject("msg", messageSource.getMessage("contents.control.002", null, localeResolver.resolveLocale(req)));
			mav.addObject("type", "href");
			mav.addObject("url", "/man/device/list.html?page=1");
			mav.setViewName("inc/dummy");
		}
		return mav;
	}

	@RequestMapping(value="/man/notice/list.html" , method=RequestMethod.GET )
	public ModelAndView manNoticelistGET( String page, String searchType, String searchValue,  HttpSession session, HttpServletRequest req) throws Exception{
		ModelAndView mav = new ModelAndView();
		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		String decodeValue ="";
		
		if(searchValue != null)
		 decodeValue = URLDecoder.decode(searchValue, "UTF-8");

		NoticeList addList = noticeService.selectNoticeList(Integer.parseInt(page), activeUser.getMemberVO().getCompanySeq(), searchType, decodeValue);

		mav.addObject("searchValue", decodeValue);
		mav.addObject("noticeList", addList);
		mav.setViewName("05_admin/notice_list");

		return mav;
	}

	@RequestMapping(value="/man/notice/modify.html" , method=RequestMethod.GET )
	public ModelAndView manNoticeModifyGET( HttpSession session, HttpServletRequest req) throws Exception{
		ModelAndView mav = new ModelAndView();

		String noticeSeq, useVal, appVal ;
		noticeSeq = useVal = appVal =  "";
		noticeSeq = paramSet(req, "noticeSeq");
		NoticeVO noticeVO = noticeService.selectNoticeInfo(Integer.parseInt(noticeSeq));

		List<NoticeSubVO> UserList    = noticeService.selectNoticeSubList(Integer.parseInt(noticeSeq));
		List<NoticeappSubVO> appList    = noticeService.selectNoticeappSubList(Integer.parseInt(noticeSeq));

		
		//TemplateList templateList = null;
		for(int i = 0; i < UserList.size(); i++){
			if( i== 0)useVal  += UserList.get(i).getUserSeq();
			else useVal  += ","+UserList.get(i).getUserSeq();
		}
		for(int i = 0; i < appList.size(); i++){
			if( i== 0)appVal  += appList.get(i).getStoreBundleId();
			else appVal  += ","+appList.get(i).getStoreBundleId();
		}
		if(noticeVO.getNoticeappSubVO() != null)
		mav.addObject("storeBundleId", noticeVO.getNoticeappSubVO().getStoreBundleId());
		if(noticeVO.getNoticeappSubVO() != null)
		mav.addObject("inappSeq", noticeVO.getNoticeappSubVO().getInappSeq());
		mav.addObject("useS", useVal );
		mav.addObject("noticeVO", noticeVO);
		mav.setViewName("05_admin/notice_modify");

		return mav;
	}

	@RequestMapping(value="/man/notice/modify.html" , method=RequestMethod.POST )
	public ModelAndView manNoticeModifyPOST( String[] useS, String storeBundleId, String inappSeq, NoticeVO updatedVO, HttpSession session, HttpServletRequest req) throws Exception{
		ModelAndView mav = new ModelAndView();

		NoticeSubVO noticeSubVO = new NoticeSubVO();
		NoticeappSubVO noticeappSubVO = new NoticeappSubVO();

		int result = noticeService.updateNoticeInfo(updatedVO);		
		if ( result < 0 ) {
			mav.addObject("msg", messageSource.getMessage("contents.control.003", null, localeResolver.resolveLocale(req)));
			mav.addObject("type", "href");
			mav.addObject("url", "/man/notice/list.html?page=1");
			mav.setViewName("inc/dummy");
		}else {
			try {
				noticeSubVO.setNoticeSeq(updatedVO.getNoticeSeq());	
				if("1".equals(updatedVO.getUseUserGb()) && !"".equals(updatedVO.getNoticeSeq())){
					noticeService.deleteNoticeSubInfo(noticeSubVO);
				}

				if("2".equals(updatedVO.getUseUserGb()) && !"".equals(updatedVO.getNoticeSeq())){
					noticeService.deleteNoticeSubInfo(noticeSubVO);
					for(int i=0; i<useS.length; i++){
						noticeSubVO.setUserSeq(Integer.parseInt(useS[i]));
						noticeService.insertNoticeSubInfo(noticeSubVO);
					}
				}
				
				noticeappSubVO.setNoticeSeq(updatedVO.getNoticeSeq());
				if("1".equals(updatedVO.getAppGb()) && !"".equals(updatedVO.getNoticeSeq())){
					noticeService.deleteNoticeappSubInfo(noticeappSubVO);
				}

				if("2".equals(updatedVO.getAppGb()) && !"".equals(updatedVO.getNoticeSeq())){
					noticeService.deleteNoticeappSubInfo(noticeappSubVO);
					noticeappSubVO.setStoreBundleId(storeBundleId);
					if(inappSeq != null && !"".equals(inappSeq))
					noticeappSubVO.setInappSeq(Integer.parseInt(inappSeq));
					noticeService.insertNoticeappSubInfo(noticeappSubVO);
				}
				
				mav.addObject("msg", messageSource.getMessage("contents.control.002", null, localeResolver.resolveLocale(req)));
				mav.addObject("type", "href");
				mav.addObject("url", "/man/notice/list.html?page=1");
				mav.setViewName("inc/dummy");
				
			}catch(Exception e ) {
				e.printStackTrace();
				mav.addObject("msg", messageSource.getMessage("contents.control.003", null, localeResolver.resolveLocale(req)));
				mav.addObject("type", "href");
				mav.addObject("url", "/man/notice/list.html?page=1");
				mav.setViewName("inc/dummy");
			}
		}

		return mav;
	}

	@RequestMapping(value="/man/notice/write.html" , method=RequestMethod.GET )
	public ModelAndView manNoticeWriteGET( NoticeVO noticeVO, HttpSession session, HttpServletRequest req) throws Exception{
		ModelAndView mav = new ModelAndView();
		mav.setViewName("05_admin/notice_write");
		return mav;
	}

	@RequestMapping(value="/man/notice/write.html" , method=RequestMethod.POST )
	public ModelAndView manNoticeWritePOST( String[] useS, String storeBundleId, String inappSeq, NoticeVO noticeVO, HttpSession session, HttpServletRequest req) throws Exception{
		ModelAndView mav = new ModelAndView();
		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		noticeVO.setCompanySeq(activeUser.getMemberVO().getCompanySeq());
		noticeVO.setRegUserSeq(activeUser.getMemberVO().getUserSeq());

		NoticeSubVO noticeSubVO = new NoticeSubVO();
		NoticeappSubVO noticeappSubVO = new NoticeappSubVO();

		try {
			noticeService.insertNoticeInfo(noticeVO);

			noticeSubVO.setNoticeSeq(noticeVO.getNoticeSeq());	
			if("1".equals(noticeVO.getUseUserGb()) && !"".equals(noticeVO.getNoticeSeq())){
				noticeService.deleteNoticeSubInfo(noticeSubVO);
			}

			if("2".equals(noticeVO.getUseUserGb()) && !"".equals(noticeVO.getNoticeSeq())){
				noticeService.deleteNoticeSubInfo(noticeSubVO);
				for(int i=0; i<useS.length; i++){
					noticeSubVO.setUserSeq(Integer.parseInt(useS[i]));
					noticeService.insertNoticeSubInfo(noticeSubVO);
				}
			}
			
			noticeappSubVO.setNoticeSeq(noticeVO.getNoticeSeq());
			if("1".equals(noticeVO.getAppGb()) && !"".equals(noticeVO.getNoticeSeq())){
				noticeService.deleteNoticeappSubInfo(noticeappSubVO);
			}

			if("2".equals(noticeVO.getAppGb()) && !"".equals(noticeVO.getNoticeSeq())){
				noticeService.deleteNoticeappSubInfo(noticeappSubVO);
				noticeappSubVO.setStoreBundleId(storeBundleId);
				if(inappSeq != null && !"".equals(inappSeq))
				noticeappSubVO.setInappSeq(Integer.parseInt(inappSeq));
				noticeService.insertNoticeappSubInfo(noticeappSubVO);
			}
			

			mav.setViewName("redirect:list.html?page=1");
		}catch(Exception e) {
			e.printStackTrace();
			mav.addObject("msg", messageSource.getMessage("man.control.003", null, localeResolver.resolveLocale(req)));
			mav.addObject("type", "href");
			mav.addObject("url", "/man/notice/list.html?page=1");
			mav.setViewName("inc/dummy");
		}
		return mav;
	}

	//20180213 - lsy : user recover
	@RequestMapping(value = "man/user/recover.html", method = RequestMethod.POST)
	public @ResponseBody int manRecoverPost( String userSeq, HttpServletRequest request, HttpSession session){

		int numUserSeq = Integer.parseInt(userSeq);
		MemberVO memberVO = new MemberVO();
		/* ���� Ż�� */
		memberVO.setUserStatus("4");
		/* ����1, ���� 0*/
		return memberService.updateMemberInfo(memberVO, numUserSeq);
	}

	//20180213 - lsy : user delete
	@RequestMapping(value = "man/user/eliminate.html", method = RequestMethod.POST)
	public @ResponseBody int manEleminatePost( String userSeq, HttpServletRequest request, HttpSession session){

		int numUserSeq = Integer.parseInt(userSeq);

		return memberService.deleteMemberInfo(numUserSeq);
	}
	
	private String paramSet(HttpServletRequest req, String targetName) {
		String value = "";
		value = (null == req.getParameter(targetName)) ? "" : "" + req.getParameter(targetName);
		return value;
	}

	public String changeSHA256(String str){
		String SHA = ""; 
		try{
			MessageDigest sh = MessageDigest.getInstance("SHA-256"); 
			sh.update(str.getBytes()); 
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer(); 
			for(int i = 0 ; i < byteData.length ; i++){
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			SHA = sb.toString();
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace(); 
			SHA = null; 
		}
		return SHA;
	}
}