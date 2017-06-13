package com.clbee.pbcms.controller;

import com.clbee.pbcms.service.AppService;
import com.clbee.pbcms.service.BundleService;
import com.clbee.pbcms.service.CaptureService;
import com.clbee.pbcms.service.ChangelistService;
import com.clbee.pbcms.service.ContentsService;
import com.clbee.pbcms.service.InAppCategoryService;
import com.clbee.pbcms.service.InAppService;
import com.clbee.pbcms.service.LogService;
import com.clbee.pbcms.service.MemberService;
import com.clbee.pbcms.service.ProvisionService;
import com.clbee.pbcms.service.TemplateService;
import com.clbee.pbcms.service.NoticeService;
import com.clbee.pbcms.util.AuthenticationException;
import com.clbee.pbcms.util.DateUtil;
import com.clbee.pbcms.util.Entity;
import com.clbee.pbcms.util.FileUtil;
import com.clbee.pbcms.util.Formatter;
import com.clbee.pbcms.util.ImageTask;
import com.clbee.pbcms.util.myUserDetails;
import com.clbee.pbcms.vo.AppHistoryVO;
import com.clbee.pbcms.vo.AppList;
import com.clbee.pbcms.vo.AppSubVO;
import com.clbee.pbcms.vo.AppVO;
import com.clbee.pbcms.vo.BundleVO;
import com.clbee.pbcms.vo.CaptureVO;
import com.clbee.pbcms.vo.InAppList;
import com.clbee.pbcms.vo.InappSubVO;
import com.clbee.pbcms.vo.InappVO;
import com.clbee.pbcms.vo.InappcategoryVO;
import com.clbee.pbcms.vo.LogList;
import com.clbee.pbcms.vo.MemberVO;
import com.clbee.pbcms.vo.ProvisionVO;
import com.clbee.pbcms.vo.TemplateList;
import com.clbee.pbcms.vo.TemplateVO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppController
{

	@Autowired
	AppService appService;
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	ProvisionService provisionService;
	
	@Autowired
	MessageSource messageSource;
	
	@Autowired
	InAppService inAppService;
	
	@Autowired
	CaptureService captureService;
	
	@Autowired
	InAppCategoryService inAppCategoryService;
	
	@Autowired
	BundleService bundleService;
	
	@Autowired
	TemplateService templateService;
	
	@Autowired
	LocaleResolver localeResolver;
	
	@Autowired
	ChangelistService changelistService;
	 
	@Autowired
	LogService logService;
	
	@Autowired
	NoticeService noticeService;

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//edit for the    format you need
		dateFormat.setLenient(false);
		//binder.registerCustomEditor(Integer.class, new CustomPrimitiveFormat(Integer.class, true));
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
		binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class,  true));
		//binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, false));
	}

	@RequestMapping(value = "app/list.html", method = RequestMethod.GET)
	public String home(HttpSession session, HttpServletRequest request, ModelMap modelMap, AppList appList) throws UnsupportedEncodingException {		
		System.out.println("currentUserName" + SecurityContextHolder.getContext().getAuthentication().getName());
		myUserDetails activeUser = null;
			
		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
			throw new AuthenticationException();
		}else {
			activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>)SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		GrantedAuthority element = authorities.iterator().next();
		String authority = element.getAuthority();

		System.out.println("appList.getSearchValue() = " + appList.getSearchValue());
		if(appList.getSearchValue() != null)
			appList.setSearchValue(URLDecoder.decode(appList.getSearchValue(), "UTF-8"));
		if("anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
			return "redirect:/down/list.html";
		}else{
			if(appList.getCurrentPage()==null)appList.setCurrentPage(1);
			System.out.println("여기 "+appList.getTotalCount());
			appList = appService.selectList(activeUser.getMemberVO(), appList);
			modelMap.addAttribute("appList", appList);
			modelMap.addAttribute("authority", authority);
		
			return "01_app/app_list";
		}
	}

	@RequestMapping(value = "app/provision/list.html", method = RequestMethod.GET)
	public String app_provision_list(ProvisionVO vo, ModelMap modelMap, int appSeq) throws Exception {
		myUserDetails activeUser = null;
			
		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
			throw new AuthenticationException();
		}else {
			activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}		
		//for member seq
		int regUserSeq = activeUser.getMemberVO().getUserSeq();
		//for company seq
		int regCompanySeq = activeUser.getMemberVO().getCompanySeq();
		//provSeq
		vo.setRegUserSeq(regUserSeq);
		vo.setRegCompanySeq(regCompanySeq);
		List list = provisionService.selectList(vo, appSeq);
		modelMap.addAttribute("provisionList", list);
		modelMap.addAttribute("provisionVo", vo);
		modelMap.addAttribute("appSeq", appSeq);
		return "01_app/app_pop_provision";
	}

	@RequestMapping(value = "app/template/list.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String app_template_list(TemplateVO temVO, ModelMap modelMap, TemplateList templateList) throws Exception {
		myUserDetails activeUser = null;
			
		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
			throw new AuthenticationException();
		}else {
			activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}

		if(templateList.getCurrentPage()==null||"".equals(templateList.getCurrentPage())){templateList.setCurrentPage(1);}
			
		System.out.println("changedOsTypeGb = " + temVO.getOstypeGb());

		templateList = templateService.selectList(temVO, activeUser.getMemberVO(), templateList, "Paging");
		modelMap.addAttribute("templateList", templateList);
		modelMap.addAttribute("TemplateVO", temVO);
		return "01_app/app_pop_template";
	}

	//���ø��̹�������
	@RequestMapping(value = "app/template/capture.html", method = RequestMethod.GET)
	public @ResponseBody List man_provision_modify(HttpServletRequest request, HttpSession session, CaptureVO vo) throws Exception{
		myUserDetails activeUser = null;
			
		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
			throw new AuthenticationException();
		}else {
			activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		List list = null;
		list = captureService.selectListByBoardSeqWithGb(vo);
		return list;
	}

	@RequestMapping(value = "app/regist.html", method = RequestMethod.GET)
	public String app_regist(HttpSession session, HttpServletRequest request, AppVO appVO, ModelMap modelMap, AppList appList) {
		try{
			if(appVO.getRegGb()==null||appVO.getRegGb().length()==0){
				System.out.println("ppVO.getRegGb()1==="+appVO.getRegGb());
				appVO.setRegGb("2");
				System.out.println("ppVO.getRegGb()2==="+appVO.getRegGb());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		modelMap.addAttribute("appVO", appVO);
		modelMap.addAttribute("appList", appList);
		return "01_app/app_write";
	}

	/**
	 * �������̹��� ���ε�
	 */
	@RequestMapping(value = "app/iconfileupload.html", method = RequestMethod.POST)
	public @ResponseBody HashMap<Object, Object> app_icon_file_upload(HttpServletRequest request, HttpSession session, @RequestParam("iconFile") MultipartFile file) throws Exception{
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		try{
			String fileType = request.getParameter("fileType");
			map.put("fileType", fileType);
			map = uploadFile(map, file, request);
		}catch(Exception e){
			return map;
		}
		return map;
	}

	/**
	 * ĸ�� �̹��� ���ε�
	 */
	@RequestMapping(value = "app/deletetmpimg.html", method = RequestMethod.POST)
	public @ResponseBody HashMap<Object, Object> app_delete_temp_img(HttpServletRequest request, HttpSession session){
		System.out.println("@@@@@@@@@@@@app/deletetmpimg.html");
		HashMap<Object, Object> map = new HashMap<Object, Object>();

		try{
			String saveFileName = request.getParameter("saveFileName");
			String fileStatus = request.getParameter("fileStatus");
			String savePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) +  messageSource.getMessage("file.upload.path.temp.images.file", null, localeResolver.resolveLocale(request));
			File file = new File(savePath+saveFileName);
			if(!FileUtil.delete(file)){
				//���� ���� ������ �߻��߽��ϴ�.
				map.put("error", messageSource.getMessage("app.control.001", null, localeResolver.resolveLocale(request)));
				return map;
			}
			map.put("error", "none" );
		}catch(Exception e){
			//���� ���� ������ �߻��߽��ϴ�.
			map.put("error", messageSource.getMessage("app.control.001", null, localeResolver.resolveLocale(request)));
			return map;
		}
		return map;
	}

	public HashMap<Object, Object> uploadFile(HashMap map, MultipartFile upLoadFile, HttpServletRequest request) throws Exception {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		//InputStream tikaInputStream = null;
		try{
			if (upLoadFile != null) {
				if (upLoadFile.getSize() > 0) {
					String savePath = "";
					String webPath = "";
					String orgFileName = upLoadFile.getOriginalFilename();
					String orgFileOnlyName = orgFileName.substring(0, orgFileName.lastIndexOf("."));
						 
					String fileExt = orgFileName.substring(orgFileName.lastIndexOf(".") + 1, orgFileName.length());
						
					fileExt = fileExt.toLowerCase();
						
					String saveFileOnlyName = DateUtil.getDate("yyyyMMdd_hhmmss") + "_" + Formatter.getRandomNumber(100);
					String saveFileName = saveFileOnlyName + "." + fileExt;

					inputStream = upLoadFile.getInputStream();				
					map.put("orgFileName", orgFileName);
					map.put("saveFileName", saveFileName);
					map.put("fileSize", upLoadFile.getSize());
					map.put("fileExt", fileExt);
					String mimeType = upLoadFile.getContentType();
					if("inappFile".equals(map.get("fileType"))){
	/*					if (!("image/jpeg".equals(mimeType)||"image/gif".equals(mimeType)||"image/png".equals(mimeType)||"image/jpg".equals(mimeType))) {					 
							map.put("error", "����� �� ���� ���� �����Դϴ�.");
							outputStream.close();
							inputStream.close();
							return map;
							//throw new Exception("����� �� ���� ���������Դϴ�.");
						}*/
						savePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) +  messageSource.getMessage("file.path.inapp.file", null, localeResolver.resolveLocale(request));
						//webPath = messageSource.getMessage("file.web.path.temp.images.file", null, localeResolver.resolveLocale(request));
					}else{
						if (!("image/jpeg".equals(mimeType)||"image/gif".equals(mimeType)||"image/png".equals(mimeType)||"image/jpg".equals(mimeType))) {					 
							map.put("error", messageSource.getMessage("app.control.002", null, localeResolver.resolveLocale(request)));
							//outputStream.close();
							inputStream.close();
							return map;
							//throw new Exception("����� �� ���� ���������Դϴ�.");
						}
						savePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) +  messageSource.getMessage("file.upload.path.temp.images.file", null, localeResolver.resolveLocale(request));
						webPath = messageSource.getMessage("file.upload.path.temp.images.file", null, localeResolver.resolveLocale(request));
					}
					  
					outputStream = new FileOutputStream(savePath + saveFileName);
						 
					int readBytes = 0;
					byte[] buffer = new byte[8192];
						
					while ((readBytes = inputStream.read(buffer, 0 , 8192)) != -1) {
						outputStream.write(buffer, 0, readBytes);
					}
					if("inappFile".equals(map.get("fileType"))){
					}else{
						//���� �����˼� �� �������� 
						BufferedImage uploadImages = ImageIO.read(new File(savePath + saveFileName));	
						int orgHeight = uploadImages.getHeight();
						int orgWidth = uploadImages.getWidth();
							
						//�������϶�
						int toBeWidth = 300;
						int toBeHeight = 300;
						/*if ("iconFile".equals(map.get("fileType"))) {							
							if(orgHeight!=orgWidth){
								map.put("error", messageSource.getMessage("app.control.003", null, localeResolver.resolveLocale(request)));
								outputStream.close();
								inputStream.close();
								return map;						
							}							 
						}*/
						/*if ("captureFile".equals(map.get("fileType"))) {
								
						if(orgHeight!=orgWidth){
							map.put("error", "�̹��� ������ ���� �ʽ��ϴ�.");
							outputStream.close();
							inputStream.close();
							return map;						
						}
						 */
						//orgHeight/orgWidth=toBeHeight/toBeWidth
						/*	if(orgHeight>768){
							toBeWidth = Integer.valueOf(orgWidth/orgHeight*768);
							toBeHeight = 768;							
							}else{
								toBeWidth = orgWidth;
								toBeHeight = orgHeight;
							}
						}*/
						BufferedImage originalImage = ImageIO.read(new File(savePath + saveFileName));
						BufferedImage resizeImage = ImageTask.resizeImage(originalImage, originalImage.getType(), toBeWidth, toBeHeight);
						ImageIO.write(resizeImage, fileExt, new File(savePath + saveFileName));
					}
						
					map.put("webPath", webPath+saveFileName);
					System.out.println("webPath+saveFileName = " + webPath+saveFileName);
					map.put("error", "none");
					//���� �����˼� �� �������� 
					outputStream.close();
					//outputStream.flush();
					inputStream.close();
					outputStream = null;
					inputStream = null;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			map.put("error", messageSource.getMessage("app.control.004", null, localeResolver.resolveLocale(request)));		}
		return map;
	}

	/**
	 * ĸ�� �̹��� ���ε�
	 */
	@RequestMapping(value = "app/capturefileupload.html", method = RequestMethod.POST)
	public @ResponseBody HashMap<Object, Object> app_capture_file_upload(HttpServletRequest request, HttpSession session, @RequestParam("captureFile") MultipartFile file){
		System.out.println("@@@@@@@@@@@@app/capturefileupload.html");
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		try{
			String fileType = request.getParameter("fileType");
			map.put("fileType", fileType);
			map = uploadFile(map, file, request);
		}catch(Exception e){
			return map;			
		}
		return map;
	}
	/**
	 * provid check
	 */
	@RequestMapping(value = "app/checkprovid.html", method = RequestMethod.GET)
	public @ResponseBody int app_check_provid(HttpServletRequest request, String companySeq,  HttpSession session, BundleVO vo, int osType) throws Exception{
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		vo.setOsType(osType);
		int cnt = 99;
		try{
			cnt = bundleService.getListCount(vo, Integer.parseInt(companySeq));
			return cnt;
		}catch(Exception e){
			e.printStackTrace();
			map.put("cnt", cnt);
			return cnt;
		}
	}

  	@RequestMapping(value = "app/regist.html", method = RequestMethod.POST)
	public String app_regist_impl(AppVO appVO, HttpServletRequest request, ModelMap modelMap) throws Exception {
		//Map reqMap = WebUtil.getRequestMap(request); // Request Map ��ü ����
		//Map model = new HashMap();	
		/*
		String area_idx = StringUtil.getVConv(reqMap.get("area"), "I");
		String searchAreOverseasYn = StringUtils.defaultString(StringUtil.getVConv( reqMap.get("oversea"), "S", "N")); // ���� N , �ؿ� Y
		*/
		//WebUtil.checkParameter(request);		
		
		//System.out.println(appVO.toString());
		//����� ���� 
		try{
			myUserDetails activeUser = null;
			if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
				throw new AuthenticationException();
			}else {
				activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			}	
			//for member seq
			int regUserSeq = activeUser.getMemberVO().getUserSeq();
			//for company seq
			int regCompanySeq = activeUser.getMemberVO().getCompanySeq();
			//provSeqjavascript:appModify('682');
			String regUserId = activeUser.getMemberVO().getUserId();
			String regUserGb = activeUser.getMemberVO().getUserGb();
			appVO.setRegUserSeq(regUserSeq);
			appVO.setRegUserId(regUserId);
			appVO.setRegUserGb(regUserGb);
			appVO.setInstallGb("1");

	/*		List list = provisionService.selectList(vo);
	    	modelMap.addAttribute("provisionList", list);
	    	modelMap.addAttribute("provisionVo", vo);*/
			//db�Է�

			int appSeq = appService.getSeqAfterInsertAppInfo(appVO);
			//�̹��� ó��
			String tempPath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.upload.path.temp.images.file", null, localeResolver.resolveLocale(request));
			String toPath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request))	 + messageSource.getMessage("file.upload.path.app.icon.file", null, localeResolver.resolveLocale(request));				
			//�������̹���
			String iconOrgFile = appVO.getIconOrgFile();
			String iconSaveFile = appVO.getIconSaveFile();
			if(FileUtil.movefile(iconSaveFile, tempPath, toPath)){
				//....����
			}
			//ĸ���̹��� ó��
			String[] imgOrgFileArr = request.getParameterValues("imgOrgFile");
			String[] imgSaveFileArr = request.getParameterValues("imgSaveFile");
			String imgOrgFile = null;
			String imgSaveFile = null;
			toPath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.upload.path.app.capture.file", null, localeResolver.resolveLocale(request));				
			if(imgSaveFileArr!=null&&imgSaveFileArr.length>0){
				for(int i=0;i<imgSaveFileArr.length;i++){
					imgOrgFile = imgOrgFileArr[i];
					imgSaveFile = imgSaveFileArr[i];
					if(FileUtil.movefile(imgSaveFile, tempPath, toPath)){
						//....���� tb_capture
						CaptureVO captureVO = new CaptureVO();
						captureVO.setCaptureGb("1");
						captureVO.setBoardSeq(appSeq);
						captureVO.setUserSeq(regUserSeq);
						captureVO.setImgOrgFile(imgOrgFile);
						captureVO.setImgSaveFile(imgSaveFile);
						captureService.insert(captureVO);					
					}
				}
			}

			//���κ��� �������̺� �Է� �� ����
			String[] provSeqArr = request.getParameterValues("provSeq");
			//String[] provIdArr = request.getParameterValues("provId");
			String storeBundleId1 = request.getParameter("storeBundleId1");
			String storeBundleId2 = request.getParameter("storeBundleId2");
			String[] provTestGbArr = request.getParameterValues("provTestGb");

			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ regist appVO.getRegGb() = " + appVO.getRegGb());
			


			List list =  inAppCategoryService.getListInAppCategory("storeBundleId", storeBundleId1+storeBundleId2);

			if(list == null || list.size() == 0 ){
				if("1".equals(appVO.getRegGb())){
					InappcategoryVO InCateVo = new InappcategoryVO();
		
					InCateVo.setStoreBundleId(storeBundleId1+storeBundleId2);
					InCateVo.setCategoryName("default");
					InCateVo.setCategoryParent(0);
					//InCateVo.setCategorySeq(Integer.parseInt(categorySeq1));
		
					InCateVo.setDepth("1");
					InCateVo.setRegUserSeq(activeUser.getMemberVO().getUserSeq());
					InCateVo.setRegUserId(activeUser.getMemberVO().getUserId());
					InCateVo.setRegUserGb(activeUser.getMemberVO().getUserGb());
					InCateVo.setRegDt(new Date());
					InCateVo.setChgUserSeq(activeUser.getMemberVO().getUserSeq());
					InCateVo.setChgUserId(activeUser.getMemberVO().getUserId());
					InCateVo.setChgUserGb(activeUser.getMemberVO().getUserGb());
					InCateVo.setChgDt(new Date());
					inAppCategoryService.insertInAppInfo(InCateVo);
				}
			}

			int provSeq = 0;
			String provId = null;
			String provTestGb = null;
			int ostype = 2;
			ostype = Integer.parseInt(appVO.getOstype());
			if("4".equals(appVO.getOstype())){
				bundleService.delete(appSeq);
				provId = storeBundleId1+storeBundleId2;				 
				BundleVO bundleVO  = new BundleVO();
				bundleVO.setAppSeq(appSeq);
				bundleVO.setBundleName(provId);
				bundleVO.setOsType(ostype);
				bundleService.insert(bundleVO);
			}else{
				if(provSeqArr!=null&&provSeqArr.length>0){
					bundleService.delete(appSeq);
					for(int i=0;i<provSeqArr.length;i++){
						provSeq = Integer.parseInt(provSeqArr[i]);				 
						provId = storeBundleId1+storeBundleId2;
						provTestGb = provTestGbArr[i];
						BundleVO bundleVO  = new BundleVO();
						bundleVO.setAppSeq(appSeq);
						bundleVO.setProvSeq(provSeq);
						bundleVO.setBundleName(provId);
						bundleVO.setOsType(ostype);
						bundleVO.setProvTestGb(provTestGb);
						bundleService.insert(bundleVO);
					}
				}else{
					bundleService.delete(appSeq);
					provId = storeBundleId1+storeBundleId2;				 
					BundleVO bundleVO  = new BundleVO();
					bundleVO.setAppSeq(appSeq);
					bundleVO.setBundleName(provId);
					bundleVO.setOsType(ostype);
					bundleService.insert(bundleVO);
				}
			}
			
	    	//return "redirect:/app/list.html";
			String url = "redirect:/app/modify.html";
			String parameters = "?currentPage=1&appSeq="+appSeq+"&searchValue=";
			return url + parameters;
		}catch(Exception e){
				
			//e.printStackTrace();
			String returnUrl = "/inc/dummy";
			modelMap.addAttribute("msg", messageSource.getMessage("app.control.005", null, localeResolver.resolveLocale(request)));
			modelMap.addAttribute("type", "-1");
			return returnUrl;
		}
 	}

  	@RequestMapping(value = "app/deleteBoth.html", method = RequestMethod.POST)
	public ModelAndView appDeleteBothPOST(HttpSession session, HttpServletRequest request) {
  		
  		ModelAndView mav = new ModelAndView();
  		String currentPage, appSeq, searchValue, isAvailable, storeBundleId ;
  		
  		currentPage = paramSet(request, "currentPage");
  		searchValue = paramSet(request, "searchValue");
  		isAvailable = paramSet(request, "isAvailable");
  		appSeq = paramSet(request, "appSeq");
  		storeBundleId = paramSet(request, "storeBundleId");
  		
  		try{
  			appService.deleteAppInfo(Integer.parseInt(appSeq));
  			inAppService.deleteInAppInfo(storeBundleId);
  			mav.setViewName("redirect:/app/list.html?currentPage="+currentPage+"&appSeq="+appSeq+"&searchValue="+searchValue+"&isAvailable="+isAvailable);
  		}catch(Exception e){
  			e.printStackTrace();
  			
  			mav.setViewName("/inc/dummy");
  	  		mav.addObject("msg", messageSource.getMessage("app.control.001", null, localeResolver.resolveLocale(request)));
  	  		mav.addObject("type", "-1");
  		}

  		return mav;
  	}
  	@RequestMapping(value={"/app/log/list.html"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public ModelAndView manLogListGET(String startDate, String endDate, HttpSession session, HttpServletRequest request)
      throws Exception
    {
      ModelAndView mav = new ModelAndView();
      myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      
      AppVO appVO = null;
      
      int isSingle = 0;
      String storeBundleId;
      
      String page = paramSet(request, "page");
      String decodeValue = paramSet(request, "decodeValue");
      
      String searchType = paramSet(request, "searchType");
      String searchValue = paramSet(request, "searchValue");
      if ((paramSet(request, "inappSeq") != null) && (!"".equals(paramSet(request, "inappSeq"))))
      {
        storeBundleId = paramSet(request, "inappSeq");
        isSingle = 2;
      }
      else
      {
        storeBundleId = paramSet(request, "storeBundleId");
        appVO = this.appService.selectByStoreId(storeBundleId);
        isSingle = 1;
      }
      if (searchValue != null) {
        decodeValue = URLDecoder.decode(searchValue, "UTF-8");
      }
      LogList addList = this.logService.selectLogList(Integer.parseInt(page), storeBundleId, decodeValue, searchType, startDate, endDate);
      
      mav.addObject("searchValue", decodeValue);
      mav.addObject("logList", addList);
      if (appVO != null) {
        mav.addObject("isSingle", Integer.valueOf(isSingle));
      }
      mav.addObject("startDate", startDate);
      mav.addObject("endDate", endDate);
      mav.setViewName("01_app/log_list");
      
      return mav;
    }
  	
  	@RequestMapping(value = "app/deleteApp.html", method = RequestMethod.POST)
	public ModelAndView appDeleteAppPOST(HttpSession session, HttpServletRequest request) {
  		
  		ModelAndView mav = new ModelAndView();
  		String currentPage, appSeq, searchValue, isAvailable ;
  		
  		currentPage = paramSet(request, "currentPage");
  		searchValue = paramSet(request, "searchValue");
  		isAvailable = paramSet(request, "isAvailable");
  		appSeq = paramSet(request, "appSeq");
  		
  		try{
  			appService.deleteAppInfo(Integer.parseInt(appSeq));//앱 삭제
  			//appService.deleteAppHistoryInfo(Integer.parseInt(appSeq));//앱 히스토리 삭제
  			//bundleService.delete(Integer.parseInt(appSeq));//번들 삭제
  			//noticeService.deleteNoticeSubAppSeqInfo(Integer.parseInt(appSeq));//공지 앱 삭제
  			//appService.deleteAppSubAppSeqInfo(Integer.parseInt(appSeq));//앱서브 삭제
  			//프로비젼 삭
  			
  			mav.setViewName("redirect:/app/list.html?currentPage="+currentPage+"&appSeq="+appSeq+"&searchValue="+searchValue+"&isAvailable="+isAvailable);
  		}catch(Exception e){
  			e.printStackTrace();
  			
  			mav.setViewName("/inc/dummy");
  	  		mav.addObject("msg", messageSource.getMessage("app.control.001", null, localeResolver.resolveLocale(request)));
  	  		mav.addObject("type", "-1");
  		}
  		return mav;
  	}
  	
	@RequestMapping(value = "app/modify.html", method = RequestMethod.GET)
	public String app_modify(HttpSession session, HttpServletRequest request, ModelMap modelMap, InappVO inappVO, InAppList inAppList, CaptureVO vo, AppVO appVO, AppList appList) {
		List<?> captureList = null;
		List<?> bundleList = null;
		int inappCnt = 0;
		TemplateVO templateVO = null;
		List<AppSubVO> UserList = null;
		String useVal ="";

		try{
			myUserDetails activeUser = null;
			if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
				throw new AuthenticationException();
			}else {
				activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			}
			System.out.println("@@@@@@@@@@@@@@"+appVO.getAppSeq());
			appVO = appService.selectForUpdate(appVO, activeUser.getMemberVO());
			if(appVO==null){
				//throw new Exception("�߸��� �����Դϴ�.");
				String returnUrl = "/inc/dummy";
				modelMap.addAttribute("msg", messageSource.getMessage("app.control.006", null, localeResolver.resolveLocale(request)));
				modelMap.addAttribute("type", "-1");
				return returnUrl;
			}
			bundleList = bundleService.listByAppSeq(appVO.getAppSeq());
			vo.setCaptureGb("1");
			vo.setBoardSeq(appVO.getAppSeq());
			captureList = captureService.selectListByBoardSeqWithGb(vo);
			//System.out.println(appVO.toString());


			if(appVO.getTemplateSeq() != null)
			templateVO = templateService.selectByTempId(appVO.getTemplateSeq());
			UserList    = appService.selectAppSubList(appVO.getAppSeq());
			//TemplateList templateList = null;
			for(int i = 0; i < UserList.size(); i++){
				if( i== 0)useVal  += UserList.get(i).getUserSeq();
				else useVal  += ","+UserList.get(i).getUserSeq();
			}

			inappVO.setStoreBundleId(appVO.getStoreBundleId());
			inAppList = inAppService.getListByBundleId(inappVO, inAppList, activeUser.getMemberVO());
			for( int i =0 ; i< inAppList.getList().size() ; i++) {
				if( "1".equals(inAppList.getList().get(i).getCompletGb()) && "1".equals(inAppList.getList().get(i).getUseGb()) ) inappCnt++;
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		modelMap.addAttribute("inappCnt", inappCnt);
		modelMap.addAttribute("useSelVal", useVal);
		modelMap.addAttribute("inappCnt", inappCnt);
		modelMap.addAttribute("appVO", appVO);
		modelMap.addAttribute("appList", appList);
		modelMap.addAttribute("bundleList", bundleList);
		modelMap.addAttribute("captureList", captureList);
		modelMap.addAttribute("templateVO", templateVO);
    	return "01_app/app_modify";
    }

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "app/modify.html", method = RequestMethod.POST)
	public String app_modify_impl(HttpSession session, String[] useS, String isAvailable, HttpServletRequest request, ModelMap modelMap, CaptureVO vo, AppVO appVO, AppList appList) {

		//����� ���� 
		myUserDetails activeUser = null;
		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
			throw new AuthenticationException();
		}else {
			activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		//for member seq
		//for company seq
		//provSeq
		Entity param = new Entity();
		AppSubVO appSubVO = new AppSubVO();

		String[] provSeqArr = request.getParameterValues("provSeq");
		//String[] provIdArr = request.getParameterValues("provId");
		String storeBundleId1 = request.getParameter("storeBundleId1");
		String storeBundleId2 = request.getParameter("storeBundleId2");
		String[] provTestGbArr = request.getParameterValues("provTestGb");
		String isCompleteNoToYes = request.getParameter("isCompleteNoToYes");

		//couponNum�� Null�� ��� ""���� �ٲ�
		if(appVO.getCouponNum() == null)appVO.setCouponNum("");
		try{
			AppVO appVO2 = appService.selectForUpdate(appVO, activeUser.getMemberVO());
			if(appVO2==null){
				//throw new Exception("�߸��� �����Դϴ�.");
				String returnUrl = "/inc/dummy";
				modelMap.addAttribute("msg", messageSource.getMessage("app.control.006", null, localeResolver.resolveLocale(request)));
				modelMap.addAttribute("type", "-1");
				return returnUrl;				
			}
			//��볯¥ �̻�볯¥
			if(!appVO.getUseGb().equals(appVO2.getUseGb())){
				if("1".equals(appVO.getUseGb())){
					appVO.setUseAvailDt(new Date());
				}else{
					appVO.setUseDisableDt(new Date());
				}
			}

			//���� ��¥
			if(appVO.getLimitGb()!=null){
				if(!appVO.getLimitGb().equals(appVO2.getLimitGb())){
					if("1".equals(appVO.getLimitGb())){
						appVO.setLimitDt(new Date());
					}
				}				
			}

			// if("UPDATEOTHERYES") �ȿ� ������ �� 2���� �ڵ尡
			// �ٱ����� ���ͼ� ���� ã�� ������ ��ó�� �׽�Ʈ�� �������, �Ϸ� ��ư�� ��������,
			// ������ ���� ��ϵȰ� �ִ� ���ٱ��� üũ�ؼ� üũ�ؼ� javascript��, UPDATEOTHERYES�� �ϴ°ʹ�ſ�
			// �����ܿ��� size != 0���� ��� üŰ�Ѱ�
			param.setValue("store_bundle_id",storeBundleId1+storeBundleId2);
			param.setValue("OSTYPE", appVO.getOstype());
			List<LinkedHashMap<Object, Object>> appVOForBundleIdList = appService.getRowIsCompletedByBundleId(param);

			// �� ������Ʈ�� �ι�° ������Ʈ���� ���� ��������
			// Sorting�Ҷ� ���� �������� ������Ʈ�� ������� Sorting�ϱ� ����
			// �׸��� �� ������Ʈ�����, �ι�° ������Ʈ�� �ϼ� = '�ƴϿ�'�϶� '��'�� �������
			// bundle id�� �Ȱ��� ������ ���� �ش� ���� ����ߴ��ϱ� ������ ����
			// Sorting�ɶ� �̾�����Ʈ�� �ι�° ������Ʈ���� �ؿ� �������ߵǱ� ������ ���� ������Ʈ�� ��
			if("UPDATEOTHERYES".equals(isCompleteNoToYes) && appVOForBundleIdList.size() != 0){
				System.out.println("@@@@@@@@@@@ UPDATEOTHERYES!!!!");
				AppHistoryVO appHistoryVOForHashMap = new AppHistoryVO(appVOForBundleIdList.get(0));
				appService.insertAppHistoryInfo(appHistoryVOForHashMap);
				appService.deleteAppInfo(((Long) appVOForBundleIdList.get(0).get("appSeq")).intValue());
			}
			appVO.setChgUserSeq(activeUser.getMemberVO().getUserSeq());
			appVO.setChgUserId(activeUser.getMemberVO().getUserId());
			appVO.setChgUserGb(activeUser.getMemberVO().getUserGb());

			//�̰��� ��ó���� �õ��� ������Ʈ �Դϴ�.
			if("Adhoc".equals(appVO.getProvisionGb()));
				appVO.setProvisionGb("1");
			if("AppStore".equals(appVO.getProvisionGb()));
				appVO.setProvisionGb("2");
			appService.updateAppInfo(appVO, appVO.getAppSeq());

			//�̹��� ó��
			String tempPath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.upload.path.temp.images.file", null, localeResolver.resolveLocale(request));
			String toPath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.upload.path.app.icon.file", null, localeResolver.resolveLocale(request));
			
			//�������̹���
			//String iconOrgFile = appVO.getIconOrgFile();
			String iconSaveFile = appVO.getIconSaveFile();
			if(FileUtil.movefile(iconSaveFile, tempPath, toPath)){
				//....����
			}
			//ĸ���̹��� ó��
			String[] captureSeqArr = request.getParameterValues("captureSeq");
			String[] imgOrgFileArr = request.getParameterValues("imgOrgFile");
			String[] imgSaveFileArr = request.getParameterValues("imgSaveFile");
			String captureSeq = null;
			String imgOrgFile = null;
			String imgSaveFile = null;
			toPath =   messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.upload.path.app.capture.file", null, localeResolver.resolveLocale(request));				
			if(imgSaveFileArr!=null&&imgSaveFileArr.length>0){
				for(int i=0;i<imgSaveFileArr.length;i++){
					captureSeq = captureSeqArr[i];
					imgOrgFile = imgOrgFileArr[i];				 
					imgSaveFile = imgSaveFileArr[i];				 
					if("0".equals(captureSeq)&&FileUtil.movefile(imgSaveFile, tempPath, toPath)){
						//....���� tb_capture
						CaptureVO captureVO = new CaptureVO();
						captureVO.setCaptureGb("1");
						captureVO.setBoardSeq(appVO.getAppSeq());
						captureVO.setUserSeq(activeUser.getMemberVO().getUserSeq());
						captureVO.setImgOrgFile(imgOrgFile);
						captureVO.setImgSaveFile(imgSaveFile);
						captureService.insert(captureVO);		
						captureVO = null;
					}
				}
			}
			//�������ϻ���
			//captureSeq
/*			var html = '<input typd="hidden" name="deleteFileSeq" value="'+captureSeq+'"/>';
			html += '<input typd="hidden" name="deleteSaveFileName" value="'+saveFileName+'"/>';
			html += '<input typd="hidden" name="deleteFileType" value="'+thisFileType+'"/>';*/			
			String[] deleteFileSeqArr = request.getParameterValues("deleteFileSeq");
			String[] deleteSaveFileNameArr = request.getParameterValues("deleteSaveFileName");
			String[] deleteFileTypeArr = request.getParameterValues("deleteFileType");
			Integer deleteFileSeq = null;
			String deleteSaveFileName = null;
			String deleteFileType = null;
			String deleteFilePath = null;
			if(deleteFileTypeArr!=null&&deleteFileTypeArr.length>0){
				for(int i=0;i<deleteFileTypeArr.length;i++){
					deleteFileSeq = Integer.parseInt(deleteFileSeqArr[i]);
					deleteSaveFileName = deleteSaveFileNameArr[i];				 
					deleteFileType = deleteFileTypeArr[i];
					if("capture".equals(deleteFileType)){//�������� ����
						deleteFilePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.upload.path.app.capture.file", null, localeResolver.resolveLocale(request));
						if(FileUtil.delete(new File(deleteFilePath+deleteSaveFileName))){
							CaptureVO captureVO = new CaptureVO();
							captureVO.setCaptureSeq(deleteFileSeq);
							captureService.delete(captureVO);
							captureVO = null;
						}
					}else if("icon".equals(deleteFileType)){//���������ϻ���
						deleteFilePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.upload.path.app.icon.file", null, localeResolver.resolveLocale(request));
						FileUtil.delete(new File(deleteFilePath+deleteSaveFileName));
					}
				}
			}
			//���κ��� �������̺� �Է� �� ����
			int provSeq = 0;
			String provId = null;
			String provTestGb = null;
			int ostype = 2;
			ostype = Integer.parseInt(appVO.getOstype());
			if("4".equals(appVO.getOstype())){
				bundleService.delete(appVO.getAppSeq());
				provId = storeBundleId1+storeBundleId2;				 
				BundleVO bundleVO  = new BundleVO();
				bundleVO.setAppSeq(appVO.getAppSeq());
				bundleVO.setBundleName(provId);
				bundleVO.setOsType(ostype);
				bundleService.insert(bundleVO);
			}else{
				if(provSeqArr!=null&&provSeqArr.length>0){
					bundleService.delete(appVO.getAppSeq());
					for(int i=0;i<provSeqArr.length;i++){
						provSeq = Integer.parseInt(provSeqArr[i]);				 
						provId = storeBundleId1+storeBundleId2;				 
						provTestGb = provTestGbArr[i];
						BundleVO bundleVO  = new BundleVO();
						bundleVO.setAppSeq(appVO.getAppSeq());
						bundleVO.setProvSeq(provSeq);
						bundleVO.setBundleName(provId);
						bundleVO.setOsType(ostype);
						bundleVO.setProvTestGb(provTestGb);
						bundleService.insert(bundleVO);
					}
				}else{
					bundleService.delete(appVO.getAppSeq());
					provId = storeBundleId1+storeBundleId2;				 
					BundleVO bundleVO  = new BundleVO();
					bundleVO.setAppSeq(appVO.getAppSeq());
					bundleVO.setBundleName(provId);
					bundleVO.setOsType(ostype);
					bundleService.insert(bundleVO);
				}				
			}
			// Ư��ȸ�� ����Ʈ ����

			/*if(useS.length >0) {
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
				for(int i=0; i<useS.length; i++){
					System.out.println("useS = " + useS[i]);
				}
			}*/

			appSubVO.setAppSeq(appVO.getAppSeq());	
			if("1".equals(appVO.getUseUserGb()) && !"".equals(appVO.getAppSeq())){
				appService.deleteAppSubInfo(appSubVO);
			}

			//MemberVO memberVO = memberService.findByCustomInfo("userSeq", Integer.parseInt(useS[i]));
			List<MemberVO> memberList = memberService.getPermitList(activeUser.getMemberVO().getCompanySeq(), useS);
			if("2".equals(appVO.getUseUserGb()) && !"".equals(appVO.getAppSeq())){
				appService.deleteAppSubInfo(appSubVO);
				for(int i=0; i<useS.length; i++){
					appSubVO.setUserSeq(Integer.parseInt(useS[i]));
					for( int j=0; j < memberList.size(); j++) {
						if ( memberList.get(j).getUserSeq() == Integer.parseInt(useS[i]) ) {
							if(memberList.get(j).getTwodepartmentSeq() == null ) {
								appSubVO.setDepartmentSeq(memberList.get(j).getOnedepartmentSeq());
							}else {
								appSubVO.setDepartmentSeq(memberList.get(j).getTwodepartmentSeq());
							}
							appService.insertAppSubInfo(appSubVO);
						}
					}
				}
			}

			String url = "redirect:/app/list.html";
			String parameters = "?currentPage=1&searchValue="+"&isAvailable="+isAvailable;
				 /*"&appSeq="+appVO.getAppSeq()+"&searchType="+appList.getSearchType()+"&searchValue="+appList.getSearchValue();*/
			return url + parameters;
		}catch(Exception e){
			e.printStackTrace();
			String returnUrl = "/inc/dummy";
			//message : ��� ����
			modelMap.addAttribute("msg", messageSource.getMessage("app.control.005", null, localeResolver.resolveLocale(request)));
			modelMap.addAttribute("type", "-1");
			return returnUrl;
		}
    }

	@RequestMapping(value = "app/inapp/list.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String app_inapp_list(HttpSession session, HttpServletRequest request, ModelMap modelMap, InAppList inAppList, InappVO vo) {		

		System.out.println("currentUserName" + SecurityContextHolder.getContext().getAuthentication().getName());
		myUserDetails activeUser = null;
		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
			throw new AuthenticationException();
		}else {
			activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		AppVO appVO = appService.selectByStoreId(vo.getStoreBundleId());
		
		if("ROLE_ADMIN_SERVICE".equals(activeUser.getAuthorities().iterator().next().getAuthority())) {
			activeUser.setMemberVO(appVO.getRegMemberVO());
		}

		vo.setStoreBundleId(appVO.getStoreBundleId());
		int completAndUsingCnt =0;
		if(inAppList.getCurrentPage()==null)inAppList.setCurrentPage(1);
		inAppList = inAppService.getListByBundleId(vo, inAppList, activeUser.getMemberVO());
		
		if(inAppList != null && inAppList.getList() != null) {
			for( int i =0 ; i< inAppList.getList().size() ; i++) {
				if( "1".equals(inAppList.getList().get(i).getCompletGb()) && "1".equals(inAppList.getList().get(i).getUseGb()) ) completAndUsingCnt++;
			}
		}

		System.out.println("@@@@@@@@@@ availableCnt = " + completAndUsingCnt);
		modelMap.addAttribute("appContentsAmt", appVO.getAppContentsAmt());
		modelMap.addAttribute("appContentsGb", appVO.getAppContentsGb());
		modelMap.addAttribute("vo", vo);
		modelMap.addAttribute("inAppList", inAppList);
		modelMap.addAttribute("availableCnt", completAndUsingCnt);

		return "01_app/app_pop_inapp";
	}
  

	@RequestMapping(value = "app/inapp/regist.html", method = RequestMethod.GET)
	public String app_inapp_regist(HttpSession session, String appSeq, HttpServletRequest request, ModelMap modelMap, InAppList inAppList, InappVO vo) {
		modelMap.addAttribute("vo", vo);
		modelMap.addAttribute("inAppList", inAppList);
    	return "01_app/app_write_inapp";
    }

	@RequestMapping(value = "app/inapp/regist.html", method = RequestMethod.POST)
	public String app_inapp_regist_impl(AppVO appVO, String appSeq, String storeBundleId, HttpServletRequest request, ModelMap modelMap, InAppList inAppList, InappVO vo, @RequestParam("inappFile") MultipartFile file) throws Exception {
		//Map reqMap = WebUtil.getRequestMap(request); // Request Map ��ü ����
		//Map model = new HashMap();	
		/*
		String area_idx = StringUtil.getVConv(reqMap.get("area"), "I");
		String searchAreOverseasYn = StringUtils.defaultString(StringUtil.getVConv( reqMap.get("oversea"), "S", "N")); // ���� N , �ؿ� Y
		*/
		//WebUtil.checkParameter(request);		

		//System.out.println(appVO.toString());
		//����� ����
		String returnUrl = "redirect:/app/inapp/list.html";
		try{
			HashMap<Object, Object> map = new HashMap<Object, Object>();
			String fileType = "inappFile";
			map.put("fileType", fileType);
			map = uploadFile(map, file, request);
			if(!"none".equals(map.get("error"))){
				returnUrl = "/inc/dummy";
				modelMap.addAttribute("msg", map.get("error"));
				modelMap.addAttribute("type", "-1");
				return returnUrl;
			}
			vo.setInappOrgFile((String)map.get("orgFileName"));
			vo.setInappSaveFile((String)map.get("saveFileName"));
			long fileSize = (Long) map.get("fileSize");
			vo.setInappSize( String.valueOf(fileSize));

			myUserDetails activeUser = null;
			if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
				throw new AuthenticationException();
			}else {
				activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			}		
			//for member seq
			int regUserSeq = activeUser.getMemberVO().getUserSeq();
			//for company seq
			//provSeq
			String regUserId = activeUser.getMemberVO().getUserId();
			String regUserGb = activeUser.getMemberVO().getUserGb();
			vo.setRegUserSeq(regUserSeq);
			vo.setRegUserId(regUserId);
			vo.setRegUserGb(regUserGb);
			vo.setChgUserSeq(regUserSeq);
			vo.setChgUserId(regUserId);
			vo.setChgUserGb(regUserGb);
	/*		List list = provisionService.selectList(vo);
	    	modelMap.addAttribute("provisionList", list);
	    	modelMap.addAttribute("provisionVo", vo);*/
			//db�Է�
			int inappSeq = inAppService.getSeqAfterInsertInAppInfo(vo);
			//�̹��� ó��
			String tempPath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.upload.path.temp.images.file", null, localeResolver.resolveLocale(request));
			String toPath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.upload.path.inapp.icon.file", null, localeResolver.resolveLocale(request));				
			//�������̹���
			String iconOrgFile = appVO.getIconOrgFile();
			String iconSaveFile = appVO.getIconSaveFile();
			if(FileUtil.movefile(iconSaveFile, tempPath, toPath)){
				//....����
			}
			//ĸ���̹��� ó��
			String[] imgOrgFileArr = request.getParameterValues("imgOrgFile");
			String[] imgSaveFileArr = request.getParameterValues("imgSaveFile");
			String imgOrgFile = null;
			String imgSaveFile = null;
			toPath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.upload.path.inapp.capture.file", null, localeResolver.resolveLocale(request));				
			if(imgSaveFileArr!=null&&imgSaveFileArr.length>0){
				for(int i=0;i<imgSaveFileArr.length;i++){
					imgOrgFile = imgOrgFileArr[i];				 
					imgSaveFile = imgSaveFileArr[i];				 
					if(FileUtil.movefile(imgSaveFile, tempPath, toPath)){
						//....���� tb_capture
						CaptureVO captureVO = new CaptureVO();
						captureVO.setCaptureGb("2");
						captureVO.setBoardSeq(inappSeq);
						captureVO.setUserSeq(regUserSeq);
						captureVO.setImgOrgFile(imgOrgFile);
						captureVO.setImgSaveFile(imgSaveFile);
						captureService.insert(captureVO);					
					}
				}
			}

		  /*  XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
		    XSSFSheet sheet = wb.getSheetAt(0);
		    XSSFRow row;
		    XSSFCell cell;

		    int rows; // No of rows
		    rows = sheet.getPhysicalNumberOfRows();

		    int cols = 0; // No of columns
		    int tmp = 0;

		    // This trick ensures that we get the data properly even if it doesn't start from first few rows
		    for(int i = 0; i < 10 || i < rows; i++) {
		        row = sheet.getRow(i);
		        if(row != null) {
		            tmp = sheet.getRow(i).getPhysicalNumberOfCells();
		            if(tmp > cols) cols = tmp;
		        }
		    }

		    for(int r = 0; r < rows; r++) {
		        row = sheet.getRow(r);
		        if(row != null) {
		            for(int c = 0; c < cols; c++) {
		                cell = row.getCell((int)c);
		                if(cell != null) {
		                    // Your code here
		                	System.out.println("@@@@@@@@@@@@@ cell value = " + cell.getStringCellValue());
		                }
		            }
		        }
		    }*/

		String url = "redirect:/app/inapp/list.html";
		String parameters = "?storeBundleId="+storeBundleId;
		returnUrl =  url + parameters;		
    	return returnUrl;
		}catch(Exception e){
			e.printStackTrace();
			returnUrl = "/inc/dummy";
			//message : ��� ����
			modelMap.addAttribute("msg", messageSource.getMessage("app.control.005", null, localeResolver.resolveLocale(request)));
			modelMap.addAttribute("type", "-1");
			return returnUrl;
		}
    }

	@RequestMapping(value = "app/inapp/modify.html", method = RequestMethod.GET)
	public String app_inapp_modify(HttpSession session, HttpServletRequest request, String inappSeq, ModelMap modelMap, CaptureVO cvo, InAppList inAppList, InappVO ivo) {
		List captureList = null;
		List<InappSubVO> addList;
		String useS = "";
		//List bundleList = null;
		int completAndUsingCnt = 0;
		
		AppVO appVO = appService.selectByStoreId(ivo.getStoreBundleId());

		try{
			myUserDetails activeUser = null;
			if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
				throw new AuthenticationException();
			}else {
				activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			}
			ivo = inAppService.selectForUpdate(ivo, activeUser.getMemberVO());
			//BundleVO bvo = new BundleVO();
			//bundleList = bundleService.listByAppSeq(appVO.getAppSeq());
			cvo.setCaptureGb("2");
			cvo.setBoardSeq(ivo.getInappSeq());
			captureList = captureService.selectListByBoardSeqWithGb(cvo);

			if(inAppList.getCurrentPage()==null)inAppList.setCurrentPage(1);
			inAppList = inAppService.getListByBundleId(ivo, inAppList, activeUser.getMemberVO());
			
			//System.out.println(appVO.toString());
			for( int i =0 ; i< inAppList.getList().size() ; i++) {
				if( "1".equals(inAppList.getList().get(i).getCompletGb()) && "1".equals(inAppList.getList().get(i).getUseGb()) ) completAndUsingCnt++;
			}
			addList = inAppService.selectInAppSubList(Integer.parseInt(inappSeq));
			
			//TemplateList templateList = null;
			for(int i = 0; i < addList.size(); i++){
				if( i== 0)useS  += addList.get(i).getUserSeq();
				else useS  += ","+addList.get(i).getUserSeq();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		modelMap.addAttribute("appContentsAmt", appVO.getAppContentsAmt());
		modelMap.addAttribute("appContentsGb", appVO.getAppContentsGb());
		modelMap.addAttribute("useS", useS);
		modelMap.addAttribute("ivo", ivo);
		modelMap.addAttribute("availableCnt", completAndUsingCnt);
		modelMap.addAttribute("inAppList", inAppList);
		//modelMap.addAttribute("bundleList", bundleList);
		modelMap.addAttribute("captureList", captureList);
    	return "01_app/app_modify_inapp";
    }

	@RequestMapping(value = "app/inapp/modify.html", method = RequestMethod.POST)
	public String app_inapp_modify_impl(HttpSession session, HttpServletRequest request, String[] useS, ModelMap modelMap, CaptureVO cvo, InappVO ivo, InAppList inappList, @RequestParam("inappFile") MultipartFile file) {
		Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>)SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		GrantedAuthority element = authorities.iterator().next();
		String authority = element.getAuthority();
		//����� ���� 
		myUserDetails activeUser = null;
		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
			throw new AuthenticationException();
		}else {
			activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		InappSubVO inappSubVO = new InappSubVO();

		try {
			InappVO ivo2 = inAppService.selectForUpdate(ivo, activeUser.getMemberVO());
			if(ivo2==null){
				//throw new Exception("�߸��� �����Դϴ�.");
				String returnUrl = "/inc/dummy";
				//message : �߸��� �����Դϴ�.
				modelMap.addAttribute("msg", messageSource.getMessage("app.control.006", null, localeResolver.resolveLocale(request)));
				modelMap.addAttribute("type", "-1");
				return returnUrl;
			}
			//��볯¥ �̻�볯¥

			if(!ivo2.getUseGb().equals(ivo.getUseGb())){
				if("1".equals(ivo.getUseGb())){
					ivo.setUseAvailDt(new Date());
				}else{
					ivo.setUseDisableDt(new Date());
				}
			}

			if("ROLE_ADMIN_SERVICE".equals(authority)){
				//���� ��¥
				if(!ivo2.getLimitGb().equals(ivo.getLimitGb())){
					if("1".equals(ivo.getLimitGb())){
						ivo.setLimitDt(new Date());
					}
				}
			}

			if(ivo.getInappSaveFile()==null){
				HashMap<Object, Object> map = new HashMap<Object, Object>();
				String fileType = "inappFile";
				map.put("fileType", fileType);
				map = uploadFile(map, file, request);
				ivo.setInappOrgFile((String)map.get("orgFileName"));
				ivo.setInappSaveFile((String)map.get("saveFileName"));
				ivo.setInappSize(String.valueOf(map.get("fileSize")));
				System.out.println("map.get('error')====="+"["+map.get("error")+"]");
				if(!"none".equals(map.get("error"))){
					String returnUrl = "/inc/dummy";
					modelMap.addAttribute("msg", map.get("error"));
					modelMap.addAttribute("type", "-1");
					return returnUrl;
				}				
			}

			ivo.setChgUserSeq(activeUser.getMemberVO().getUserSeq());
			ivo.setChgUserId(activeUser.getMemberVO().getUserId());
			ivo.setChgUserGb(activeUser.getMemberVO().getUserGb());

			inAppService.updateInAppInfo(ivo, ivo.getInappSeq());
			//�̹��� ó��
			String tempPath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.upload.path.temp.images.file", null, localeResolver.resolveLocale(request));
			String toPath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.upload.path.inapp.icon.file", null, localeResolver.resolveLocale(request));				
			//�������̹���
			String iconOrgFile = ivo.getIconOrgFile();
			String iconSaveFile = ivo.getIconSaveFile();
			if(FileUtil.movefile(iconSaveFile, tempPath, toPath)){
				//....����
			}
			//ĸ���̹��� ó��
			String[] captureSeqArr = request.getParameterValues("captureSeq");
			String[] imgOrgFileArr = request.getParameterValues("imgOrgFile");
			String[] imgSaveFileArr = request.getParameterValues("imgSaveFile");
			String captureSeq = null;
			String imgOrgFile = null;
			String imgSaveFile = null;
			toPath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.upload.path.inapp.capture.file", null, localeResolver.resolveLocale(request));				
			if(imgSaveFileArr!=null&&imgSaveFileArr.length>0){
				for(int i=0;i<imgSaveFileArr.length;i++){
					captureSeq = captureSeqArr[i];
					imgOrgFile = imgOrgFileArr[i];
					imgSaveFile = imgSaveFileArr[i];
					if("0".equals(captureSeq)&&FileUtil.movefile(imgSaveFile, tempPath, toPath)){
						//....���� tb_capture
						CaptureVO captureVO = new CaptureVO();
						captureVO.setCaptureGb("2");
						captureVO.setBoardSeq(ivo.getInappSeq());
						captureVO.setUserSeq(activeUser.getMemberVO().getUserSeq());
						captureVO.setImgOrgFile(imgOrgFile);
						captureVO.setImgSaveFile(imgSaveFile);
						captureService.insert(captureVO);		
						captureVO = null;
					}
				}
			}

			//��������
			//captureSeq
/*			var html = '<input typd="hidden" name="deleteFileSeq" value="'+captureSeq+'"/>';
			html += '<input typd="hidden" name="deleteSaveFileName" value="'+saveFileName+'"/>';
			html += '<input typd="hidden" name="deleteFileType" value="'+thisFileType+'"/>';*/			
			String[] deleteFileSeqArr = request.getParameterValues("deleteFileSeq");
			String[] deleteSaveFileNameArr = request.getParameterValues("deleteSaveFileName");
			String[] deleteFileTypeArr = request.getParameterValues("deleteFileType");
			Integer deleteFileSeq = null;
			String deleteSaveFileName = null;
			String deleteFileType = null;
			String deleteFilePath = null;
			
			
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			if(deleteFileTypeArr != null)
			for(int i =0; i < deleteFileSeqArr.length ; i ++)
			System.out.println("deleteFileSeqArr = " + deleteFileSeqArr);
			if(deleteSaveFileNameArr != null)
			for(int i =0; i < deleteSaveFileNameArr.length ; i ++)
			System.out.println("deleteSaveFileNameArr = " + deleteSaveFileNameArr);
			if(deleteSaveFileNameArr != null)
			for(int i =0; i < deleteSaveFileNameArr.length ; i ++)
			System.out.println("deleteFileTypeArr = " + deleteFileTypeArr);
			
			if(deleteFileTypeArr!=null&&deleteFileTypeArr.length>0){
				for(int i=0;i<deleteFileTypeArr.length;i++){
					deleteFileSeq = Integer.parseInt(deleteFileSeqArr[i]);
					deleteSaveFileName = deleteSaveFileNameArr[i];
					deleteFileType = deleteFileTypeArr[i];
					if("capture".equals(deleteFileType)){//�������� ����
						deleteFilePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.upload.path.inapp.capture.file", null, localeResolver.resolveLocale(request));
						if(FileUtil.delete(new File(deleteFilePath+deleteSaveFileName))){
							CaptureVO captureVO = new CaptureVO();
							captureVO.setCaptureSeq(deleteFileSeq);
							captureService.delete(captureVO);
							captureVO = null;
						}
					}else if("icon".equals(deleteFileType)){//���������ϻ���
						deleteFilePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.upload.path.inapp.icon.file", null, localeResolver.resolveLocale(request));
						FileUtil.delete(new File(deleteFilePath+deleteSaveFileName));
					}else if("inapp".equals(deleteFileType)){//�ξ����ϻ���
						deleteFilePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.path.inapp.file", null, localeResolver.resolveLocale(request));
						FileUtil.delete(new File(deleteFilePath+deleteSaveFileName));
					}
				}
			}

			inappSubVO.setInappSeq(ivo.getInappSeq());	
			if("1".equals(ivo.getUseUserGb()) && !"".equals(ivo.getInappSeq())){
				inAppService.deleteInAppSubInfo(inappSubVO);
			}

			//MemberVO memberVO = memberService.findByCustomInfo("userSeq", Integer.parseInt(useS[i]));
			List<MemberVO> memberList = memberService.getPermitList(activeUser.getMemberVO().getCompanySeq(), useS);
			if("2".equals(ivo.getUseUserGb()) && !"".equals(ivo.getInappSeq())){
				inAppService.deleteInAppSubInfo(inappSubVO);
				for(int i=0; i<useS.length; i++){
					inappSubVO.setUserSeq(Integer.parseInt(useS[i]));
					for( int j=0; j < memberList.size(); j++) {
						if ( memberList.get(j).getUserSeq() == Integer.parseInt(useS[i]) ) {
							if(memberList.get(j).getTwodepartmentSeq() == null ) {
								inappSubVO.setDepartmentSeq(memberList.get(j).getOnedepartmentSeq());
							}else {
								inappSubVO.setDepartmentSeq(memberList.get(j).getTwodepartmentSeq());
							}
							inAppService.insertInAppSubInfo(inappSubVO);
						}
					}
				}
			}

			/*XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream());
			XSSFSheet sheet = wb.getSheetAt(0);
			XSSFRow row;
			XSSFCell cell;

			int rows; // No of rows
			rows = sheet.getPhysicalNumberOfRows();

			int cols = 0; // No of columns
			int tmp = 0;

			// This trick ensures that we get the data properly even if it doesn't start from first few rows
			for(int i = 0; i < 10 || i < rows; i++) {
			    row = sheet.getRow(i);
			    if(row != null) {
			            tmp = sheet.getRow(i).getPhysicalNumberOfCells();
			            if(tmp > cols) cols = tmp;
			        }
			    }

			    for(int r = 0; r < rows; r++) {
			        row = sheet.getRow(r);
			        if(row != null) {
			            for(int c = 0; c < cols; c++) {
			                cell = row.getCell((int)c);
			                if(cell != null) {
			                    // Your code here
			                	
			                	System.out.println("@@@@@@@@@@@@@ cell value = " + cell.getStringCellValue());
			                	System.out.println("@@@@@@@@@@@@@ cell Type = " + cell.getCellType() );
			                	
			                }
			            }
			        }
			    }*/

			String url = "redirect:/app/inapp/list.html";
			String parameters = "?&storeBundleId="+ivo.getStoreBundleId();
			return url + parameters;
		}catch(Exception e){
			e.printStackTrace();
			//return "redirect:/app/list.html";
			//throw new Exception("�߸��� �����Դϴ�.");
			String returnUrl = "/inc/dummy";
			//message : ���� ����
			modelMap.addAttribute("msg", messageSource.getMessage("app.control.006", null, localeResolver.resolveLocale(request)));
			modelMap.addAttribute("type", "-1");
			return returnUrl;			
		}
    }

	@RequestMapping(value = "app/template/appcontentsamt.html", method = RequestMethod.GET)
	public @ResponseBody TemplateVO app_get_template_app_contents_amt(HttpServletRequest request, TemplateVO vo){
		try{
			int seq = vo.getTemplateSeq();
			vo = templateService.selectByTempId(seq);
		}catch(Exception e){
			e.printStackTrace();
			return vo;			
		}
		return vo;
	}

	@RequestMapping(value="/app/category/category_write.html" ,method={RequestMethod.GET, RequestMethod.POST})
	public ModelAndView categoryWrite(HttpSession session, HttpServletRequest req) throws Exception{
		ModelAndView mav = new ModelAndView();	
		String storeBundleId, userSeq, isInapp;
		storeBundleId = userSeq =isInapp = "";
		storeBundleId 		= this.paramSet(req, "storeBundleId");
		isInapp 		= this.paramSet(req, "isInapp");
		
		//�α�������
		myUserDetails activeUser = null;
		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
			throw new AuthenticationException();
		}else {
			activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		userSeq	=	String.valueOf(activeUser.getMemberVO().getUserSeq());
		
		InappcategoryVO InCateVo = new InappcategoryVO();		
		InCateVo.setStoreBundleId(storeBundleId);		
		List<InappcategoryVO> InAppList = inAppCategoryService.selectInAppList(InCateVo);
	
		
		mav.addObject("InAppList", InAppList);
		if(InAppList != null)
		mav.addObject("InAppCnt", InAppList.size());
		mav.addObject("storeBundleId", storeBundleId);
		mav.addObject("userSeq", userSeq);
		mav.addObject("isInapp", isInapp);
		mav.setViewName("/01_app/app_pop_category");
		return mav;
	}
  
  
	@RequestMapping(value="/app/category/category_writeOK.html" ,method={RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody InappcategoryVO categoryWriteOK(HttpSession session, HttpServletRequest req) throws Exception{
		
		String storeBundleId, userId, userSeq, userGb, cateName, depth, categorySeq1;
		storeBundleId = userId = userSeq = userGb = cateName = depth = categorySeq1 = "";
		
		storeBundleId = this.paramSet(req, "storeBundleId");
		cateName 	  = this.paramSet(req, "cateName");
		depth 		  = this.paramSet(req, "depth");
		categorySeq1  = this.paramSet(req, "categorySeq1");
		//categorySeq2 = this.paramSet(req, "categorySeq2");
		
		AppVO appVO = appService.selectByStoreId(storeBundleId);
		//�α�������
		myUserDetails activeUser = null;
		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
			throw new AuthenticationException();
		}else {
			activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		userSeq	=	String.valueOf(activeUser.getMemberVO().getUserSeq());
		userId	=	activeUser.getMemberVO().getUserId();
		userGb	=	activeUser.getMemberVO().getUserGb();
		
		InappcategoryVO InCateVo = new InappcategoryVO();

		InCateVo.setStoreBundleId(storeBundleId);
		InCateVo.setCategoryName(cateName);
		if(Integer.parseInt(depth) == 1){
			InCateVo.setCategoryParent(0);
			//InCateVo.setCategorySeq(Integer.parseInt(categorySeq1));
		}else{
			InCateVo.setCategoryParent(Integer.parseInt(categorySeq1));//�θ� ������ �־����.
			//InCateVo.setCategorySeq(Integer.parseInt(categorySeq2));
		}
		InCateVo.setDepth(depth);
		InCateVo.setRegUserSeq(Integer.parseInt(userSeq));
		InCateVo.setRegUserId(userId);
		InCateVo.setRegUserGb(userGb);
		InCateVo.setRegDt(new Date());
		InCateVo.setChgUserSeq(Integer.parseInt(userSeq));
		InCateVo.setChgUserId(userId);
		InCateVo.setChgUserGb(userGb);
		InCateVo.setChgDt(new Date());

		int categorySeq = inAppCategoryService.insertInAppInfo(InCateVo);
		InCateVo.setCategorySeq(categorySeq);

		return InCateVo;
	}
  
  
  	@RequestMapping(value="/app/category/category_modifyOK.html" ,method={RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody InappcategoryVO categoryModifyOK(HttpSession session, HttpServletRequest req) throws Exception{
		String storeBundleId, userId, userSeq, userGb, cateName, depth, categorySeq1, categorySeq2;
		storeBundleId = userId = userSeq = userGb = cateName = depth = categorySeq1 = categorySeq2 = "";

		storeBundleId= this.paramSet(req, "storeBundleId");
		cateName 	 = this.paramSet(req, "cateName");
		depth 		 = this.paramSet(req, "depth");
		categorySeq1 = this.paramSet(req, "categorySeq1");
		categorySeq2 = this.paramSet(req, "categorySeq2");

		AppVO appVO = appService.selectByStoreId(storeBundleId);
		//�α�������
		myUserDetails activeUser = null;
		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
			throw new AuthenticationException();
		}else {
			activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		userSeq	=	String.valueOf(activeUser.getMemberVO().getUserSeq());
		userId	=	activeUser.getMemberVO().getUserId();
		userGb	=	activeUser.getMemberVO().getUserGb();

		InappcategoryVO InCateVo = new InappcategoryVO();
		InCateVo.setStoreBundleId(storeBundleId);
		InCateVo.setCategoryName(cateName);
		if(Integer.parseInt(depth) == 1){
			InCateVo.setCategoryParent(0);
			InCateVo.setCategorySeq(Integer.parseInt(categorySeq1));
		}else{
			//InCateVo.setCategoryParent(Integer.parseInt(categorySeq2));//�θ� ������ �־����.
			InCateVo.setCategorySeq(Integer.parseInt(categorySeq2));
		}
		InCateVo.setDepth(depth);
		InCateVo.setRegUserSeq(Integer.parseInt(userSeq));
		InCateVo.setRegUserId(userId);
		InCateVo.setRegUserGb(userGb);
		InCateVo.setRegDt(new Date());
		InCateVo.setChgUserSeq(Integer.parseInt(userSeq));
		InCateVo.setChgUserId(userId);
		InCateVo.setChgUserGb(userGb);
		InCateVo.setChgDt(new Date());
		inAppCategoryService.updateInAppInfo(InCateVo);
		return InCateVo;
	}

  	@RequestMapping(value="/app/category/category_deleteOK.html" ,method={RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody InappcategoryVO categoryDeleteOK(HttpSession session, HttpServletRequest req) throws Exception{
		
		String storeBundleId, cateName, depth, categorySeq1, categorySeq2;
		storeBundleId = cateName = depth = categorySeq1 = categorySeq2 = "";
		
		storeBundleId	= this.paramSet(req, "storeBundleId");
		cateName 	 	= this.paramSet(req, "cateName");
		depth 		 	= this.paramSet(req, "depth");
		categorySeq1 	= this.paramSet(req, "categorySeq1");
		categorySeq2 	= this.paramSet(req, "categorySeq2");
		
		AppVO appVO = appService.selectByStoreId(storeBundleId);
		
		//�α�������
		
		/*myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		userSeq	=	String.valueOf(activeUser.getMemberVO().getUserSeq());
		userId	=	activeUser.getMemberVO().getUserId();
		userGb	=	activeUser.getMemberVO().getUserGb();*/

		InappcategoryVO InCateVo = new InappcategoryVO();

		InCateVo.setStoreBundleId(storeBundleId);
		InCateVo.setCategoryName(cateName);
		if(Integer.parseInt(depth) == 1){
			InCateVo.setCategoryParent(0);
			InCateVo.setCategorySeq(Integer.parseInt(categorySeq1));
		}else{
			InCateVo.setCategoryParent(Integer.parseInt(categorySeq2));//�θ� ������ �־����.
			InCateVo.setCategorySeq(Integer.parseInt(categorySeq2));
		}
		inAppCategoryService.deleteInAppInfo(InCateVo);
		return InCateVo;
	}	
  	
	@RequestMapping(value="/app/inapp/getList.html" ,method= RequestMethod.POST)
	public @ResponseBody List<InappVO> appInappGetListPOST(String storeBundleId,  HttpSession session, HttpServletRequest req) throws Exception{
		List<InappVO> list = null;

		System.out.println("storeBundleId = " + storeBundleId);
		if(storeBundleId != null && !"".equals(storeBundleId)) {
			list = inAppService.getListInappIsAvailableByStoreBundleId( storeBundleId);
		}

		return list;
	}

  	@RequestMapping(value="/app/category/category_list2.html" ,method={RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody Object[] categoryList2(HttpSession session, HttpServletRequest req) throws Exception{
		
		String storeBundleId, userId, userSeq, userGb, cateName, depth, categorySeq1 ;
		storeBundleId = userId = userSeq = userGb = cateName = depth = categorySeq1 = "";
		
		storeBundleId 		= this.paramSet(req, "storeBundleId");
		cateName 	= this.paramSet(req, "cateName");
		depth 		= this.paramSet(req, "depth");
		categorySeq1 = this.paramSet(req, "categorySeq1");
		//categorySeq1 = this.paramSet(req, "categorySeq2");
				
		InappcategoryVO InCateVo = new InappcategoryVO();
		Object[] InAppList = null;
		System.out.println("====================================================================================1");
		InCateVo.setStoreBundleId(storeBundleId);
		InCateVo.setCategoryParent(Integer.parseInt(categorySeq1));	
		System.out.println("====================================================================================2");
		
		InAppList = inAppCategoryService.selectInAppList2(InCateVo);
		
		System.out.println("====================================================================================3");
		System.out.println(InAppList);
		System.out.println("====================================================================================4");
		
		return InAppList;
	}

	@RequestMapping(value="/app/history/list.html" ,method={RequestMethod.GET, RequestMethod.POST})
	public ModelAndView appModifyHistory(HttpSession session, HttpServletRequest req) throws Exception{
		ModelAndView mav = new ModelAndView();
		mav.setViewName("01_app/app_pop_modify_history");

		return mav;
	}

	@RequestMapping(value="/assignment/user.html" ,method=RequestMethod.GET)
	public ModelAndView assignmentUserGET(String[] useS,HttpSession session, HttpServletRequest req) throws Exception{
		ModelAndView mav = new ModelAndView();
		myUserDetails activeUser = null;

		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
			throw new AuthenticationException();
		}else {
			activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		List<MemberVO> addList = memberService.getUserList(activeUser.getMemberVO().getCompanySeq(), useS, null, null);
		List<MemberVO> addList2 = memberService.getPermitList(activeUser.getMemberVO().getCompanySeq(), useS);

		if(addList != null) {
			for( int i =0; i< addList.size() ;i++){
				String tempString = addList.get(i).getUserId();
				if(tempString.length() > 11) {
					tempString = tempString.substring(0, 9);
					tempString +="...";
					addList.get(i).setUserId(tempString);
				}
			}
		}
		if(addList2 != null) {
			for( int i =0; i< addList2.size() ;i++){
				String tempString = addList2.get(i).getUserId();
				if(tempString.length() > 11) {
					tempString = tempString.substring(0, 9);
					tempString +="...";
					addList2.get(i).setUserId(tempString);
				}
			}
		}
		System.out.println("");
		mav.addObject("userList", addList);
		mav.addObject("permitList", addList2);
		mav.setViewName("01_app/assignment_user");
		return mav;
	}

	@RequestMapping(value="/assignment/app.html" ,method=RequestMethod.GET)
	public ModelAndView assignmentAppGET(Integer[] useA, HttpSession session, HttpServletRequest req) throws Exception{
		ModelAndView mav = new ModelAndView();
		myUserDetails activeUser = null;
		
		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
			throw new AuthenticationException();
		}else {
			activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}

		List<AppVO> addList = appService.getListIsAvailableByCompanySeq(activeUser.getMemberVO().getCompanySeq());
		//List<AppVO> addList2 = appService.getPermitList(activeUser.getMemberVO().getCompanySeq(), useA);

		System.out.println("appList = " + addList);
		mav.addObject("appList", addList);
		mav.setViewName("05_admin/select_app_contents");
		return mav;
	}
	
	@RequestMapping(value="/assignment/search.html" ,method=RequestMethod.POST)
	public @ResponseBody List assignmentSearchPOST(String[] useS,HttpSession session, HttpServletRequest req) throws Exception{
		ModelAndView mav = new ModelAndView();
		myUserDetails activeUser = null;

		if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String) {
			throw new AuthenticationException();
		}else {
			activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		}
		
		
		String searchValue, searchType;
		searchValue = searchType = "";

		searchType = paramSet(req, "searchType");
		searchValue = paramSet(req, "searchValue");
		for( int i =0; i< useS.length ; i++) {
			System.out.println( "["+i+"] useS Value = " + useS[i] );
		}

		List<MemberVO> addList = memberService.getUserList(activeUser.getMemberVO().getCompanySeq(), useS, searchValue, searchType);

		if(addList != null) {
			for( int i =0; i< addList.size() ;i++){
				String tempString = addList.get(i).getUserId();
				if(tempString.length() > 11) {
					tempString = tempString.substring(0, 9);
					tempString +="...";
					addList.get(i).setUserId(tempString);
				}
			}
		}
		
		return addList;
	}
	
	
	@RequestMapping(value="/app/inapp/checkIfInappName.html" ,method=RequestMethod.POST)
	public @ResponseBody boolean checkIfInappNamePOST(String inappName, String storeBundleId, String[] useS,HttpSession session, HttpServletRequest req) throws Exception{
		ModelAndView mav = new ModelAndView();
		boolean isExist=  inAppService.checkInappNameIfExist(inappName, storeBundleId);
		
		return isExist;
	}
	
	
	@RequestMapping(value="/app/history.html" ,method=RequestMethod.GET)
	public ModelAndView appHistoryGET(String inappName, String appSeq, String[] useS,HttpSession session, HttpServletRequest req) throws Exception{
	
		ModelAndView mav = new ModelAndView();
		mav.setViewName("01_app/app_pop_modify_history");

		return mav;
	}
	
	
	private String paramSet(HttpServletRequest req, String targetName) {
		String value = "";
		value = (null == req.getParameter(targetName)) ? "" : "" + req.getParameter(targetName);
		
		return value;
	}
}