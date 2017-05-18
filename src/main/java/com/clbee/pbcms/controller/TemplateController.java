package com.clbee.pbcms.controller;



import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import com.clbee.pbcms.service.CaptureService;
import com.clbee.pbcms.service.MemberService;
import com.clbee.pbcms.service.TemplateService;
import com.clbee.pbcms.util.DateUtil;
import com.clbee.pbcms.util.FileUtil;
import com.clbee.pbcms.util.Formatter;
import com.clbee.pbcms.util.ImageTask;
import com.clbee.pbcms.util.myUserDetails;
import com.clbee.pbcms.vo.CaptureVO;
import com.clbee.pbcms.vo.InappcategoryVO;
import com.clbee.pbcms.vo.MemberVO;
import com.clbee.pbcms.vo.TemplateList;
import com.clbee.pbcms.vo.TemplateSubVO;
import com.clbee.pbcms.vo.TemplateVO;

@Controller
@RequestMapping("/*.html")
public class TemplateController {
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	TemplateService templateService ;
	
	@Autowired
	MessageSource messageSource;		
	
	@Autowired
	CaptureService captureService;
	
	@Autowired
	LocaleResolver localeResolver;

	@RequestMapping(value="/template/list.html" ,method={RequestMethod.GET, RequestMethod.POST})
	public ModelAndView templateList(HttpSession session, HttpServletRequest req, TemplateList templateList) throws Exception{
		ModelAndView mav = new ModelAndView();
		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		int currentPage = 1;
		if(templateList.getCurrentPage()==null)templateList.setCurrentPage(1);
		
		String shField   = this.paramSet(req, "sh_field");
		String shKeyword = this.paramSet(req, "sh_keyword"); 

		// 로그인 Verifiy가 확인되면 데이터베이스에서 그 유저에 대한 모든 정보들을 가져온다.
		MemberVO memberVO =  memberService.findByUserName(SecurityContextHolder.getContext().getAuthentication().getName());
		session.setAttribute("memberInfo", memberVO);	
		session.setAttribute("currentPage", currentPage);

		//이 메소드의 0은 0과 상관없이 해당 아이디에 일치하는 모든 Record값을 가져옴
		//여기선 그냥 페이지 넘버를 의미함
		templateList = templateService.selectList(activeUser.getMemberVO(), templateList ,shField ,shKeyword);
		mav.addObject("shField", shField);
		mav.addObject("shKeyword", shKeyword);
		mav.addObject("templateList", templateList);
		mav.setViewName("/04_template/template_list");
		return mav;
	}

	@RequestMapping(value="/template/write.html" ,method={RequestMethod.GET, RequestMethod.POST})
	public ModelAndView templateWrite(HttpSession session, HttpServletRequest req) throws Exception{
		ModelAndView mav = new ModelAndView();			
		mav.setViewName("/04_template/template_write");
		return mav;
	}

	@RequestMapping(value="/template/writeOk.html" ,method={RequestMethod.GET, RequestMethod.POST})
	public ModelAndView templateWriteOk(HttpSession session, HttpServletRequest req, @RequestParam("zipFile") MultipartFile file) throws Exception{
		
		ModelAndView mav    = new ModelAndView();	
		TemplateVO tempVo   = new TemplateVO();
		String userSeq, userId, userGb;
		userSeq = userId = userGb = "";
		
		
		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();	
		
		userSeq	=	String.valueOf(activeUser.getMemberVO().getUserSeq());
		userId	=	activeUser.getMemberVO().getUserId();
		userGb	=	activeUser.getMemberVO().getUserGb();
		
		HashMap<String, String> map = new HashMap<String, String>();
		try{
			map = uploadFile2(map, file, req);	
	
			tempVo.setUploadOrgFile(map.get("orgFileName"));//원본파일명
			//tempVo.setDistrProfile(map.get("fileExt"));//파일확장자
			tempVo.setUploadSaveFile(map.get("saveFileName"));//저장파일명

				
		String templateName, ostypeGb, verNum, templateTypeGb, appContentsAmt, appContentsGb, descriptionText, useGb, useUserGb;
		templateName = ostypeGb = verNum = templateTypeGb = appContentsAmt = appContentsGb = descriptionText = useGb = useUserGb = "";
		
		templateName 		= this.paramSet(req, "fm_template_name");    //템플릿명
		ostypeGb 			= this.paramSet(req, "fm_ostype_gb"); 		 //OS구분 1:Universal 2:iPhone 3:iPad 4:Android
		verNum 				= this.paramSet(req, "fm_ver_num"); 		 //버전
		templateTypeGb 		= this.paramSet(req, "fm_template_type_gb"); //템플릿 구분 1:서가앱 2:단일앱
		appContentsAmt 		= this.paramSet(req, "fm_app_contents_amt"); //콘텐츠수량
		appContentsGb	 	= this.paramSet(req, "fm_app_contents_gb");  //콘텐츠수량구분
		descriptionText 	= this.paramSet(req, "fm_description_text"); //설명
		useGb 				= this.paramSet(req, "fm_use_gb"); 			 //사용여부구분 1:예 2:아니오
		useUserGb	 		= this.paramSet(req, "fm_use_user_gb"); 	 //사용회원 1:ALL 2:특정회원
		
		
		System.out.println("Template GB@@@@@@@@@@@@@@@@@@@@@@@@ = " + appContentsGb);
		
		tempVo.setTemplateName(templateName);		
		tempVo.setOstypeGb(ostypeGb);
		tempVo.setVerNum(verNum);
		tempVo.setTemplateTypeGb(templateTypeGb);
		tempVo.setAppContentsAmt(appContentsAmt);
		tempVo.setAppContentsGb(appContentsGb);
		tempVo.setDescriptionText(descriptionText);
		//org
		//save
		tempVo.setUseGb(useGb);
		tempVo.setUseUserGb(useUserGb);
		
		//tempVo.setUseAvailDt(useAvailDt);
		//tempVo.setUseDisableDt(useDisableDt);
		tempVo.setCompletGb("2");
		tempVo.setLimitGb("2");
		//tempVo.setLimitDt(limitDt);
		
		tempVo.setRegUserSeq(Integer.parseInt(userSeq));
		tempVo.setRegUserId(userId);
		tempVo.setRegUserGb(userGb);
		//tempVo.setRegDt(DateUtil.getDate("yyyyMMdd"));
		tempVo.setChgUserSeq(Integer.parseInt(userSeq));
		tempVo.setChgUserId(userId);
		tempVo.setChgUserGb(userGb);
		//tempVo.setChgDt(new Date());			
		
		int templateSeq = templateService.insertTemplate(tempVo);
		//tempVo.setTemplateSeq(templateSeq);
		
		//이미지 처러
		String tempPath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(req)) + messageSource.getMessage("file.upload.path.temp.images.file", null, localeResolver.resolveLocale(req));
		String toPath = "";
		//String toPath = messageSource.getMessage("file.upload.path.app.icon.file", null, localeResolver.resolveLocale(request));				
		//아이콘이미지
		//String iconOrgFile = appVO.getIconOrgFile();
		//String iconSaveFile = appVO.getIconSaveFile();
		//if(FileUtil.movefile(iconSaveFile, tempPath, toPath)){
			//....쿼리
		//}
		//캡쳐이미지 처리
		String[] imgOrgFileArr = req.getParameterValues("imgOrgFile");
		String[] imgSaveFileArr = req.getParameterValues("imgSaveFile");
		String imgOrgFile = null;
		String imgSaveFile = null;
		toPath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(req)) + messageSource.getMessage("file.upload.path.template.capture.file", null, localeResolver.resolveLocale(req));				
		if(imgSaveFileArr!=null&&imgSaveFileArr.length>0){
			for(int i=0;i<imgSaveFileArr.length;i++){
				imgOrgFile = imgOrgFileArr[i];				 
				imgSaveFile = imgSaveFileArr[i];				 
				if(FileUtil.movefile(imgSaveFile, tempPath, toPath)){
					//....쿼리 tb_capture
					CaptureVO captureVO = new CaptureVO();
					captureVO.setCaptureGb("4");
					captureVO.setBoardSeq(templateSeq);
					captureVO.setUserSeq(Integer.parseInt(userSeq));
					captureVO.setImgOrgFile(imgOrgFile);
					captureVO.setImgSaveFile(imgSaveFile);
					captureService.insert(captureVO);					
				}
			}
		}
		mav.setViewName("redirect:/template/list.html");

		}catch(Exception e){
			String templateFilePath =  messageSource.getMessage("file.path.template.file",null, localeResolver.resolveLocale(req))+tempVo.getUploadSaveFile();
			System.out.println("templateFilePath==="+templateFilePath);
			deleteFile(templateFilePath);
			HashMap mv = new HashMap();
			//message : 입력정보가 잘못되었습니다.
			mv.put("msg", messageSource.getMessage("temp.control.001", null, localeResolver.resolveLocale(req)));
			mv.put("type", "href");
			mv.put("url", "/template/list.html");
			mav.addAllObjects(mv);
			mav.setViewName("inc/dummy");			
		}
		return mav;
	}

	@RequestMapping(value="/template/user_pop.html" ,method={RequestMethod.GET, RequestMethod.POST})
	public ModelAndView templateUserPop(HttpSession session, HttpServletRequest req) throws Exception{		
		ModelAndView mav = new ModelAndView();	
		String useS;
		useS = "";
		useS 		= this.paramSet(req, "useS");
		MemberVO memVo = new MemberVO();	
		memVo.setUserGb("127"); //,88,99,76

		List userList = templateService.selectUserList(memVo,useS); //	
		List userList2 = null;		
		if(!"".equals(useS)){
			userList2 = templateService.selectUserList3(memVo,useS); //
			useS = ","+useS+",";
		}
		mav.addObject("userList", userList);
		mav.addObject("userList2", userList2);
		mav.addObject("use_user_seq", useS);	
		mav.setViewName("/04_template/template_pop_members");
		return mav;
	}

	@RequestMapping(value="/template/user_sh_list.htm" ,method={RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody Object[] templateUserShList(HttpSession session, HttpServletRequest req) throws Exception{		
		
		String shKeyword, shUseS ;		
		shKeyword = shUseS = "";		
		shKeyword 		= this.paramSet(req, "sh_keyword");
		shUseS 			= this.paramSet(req, "shUseS");		

		MemberVO memVo = new MemberVO();	
		Object[] UserList = null;
		memVo.setUserId(shKeyword);
		UserList = templateService.selectUserList2(memVo, shUseS);
		
		return UserList;				
	}

	/**
	 * 아이콘이미지 업로드
	 */
	@RequestMapping(value = "/template/iconfileupload.html", method = RequestMethod.POST)
	public @ResponseBody HashMap<Object, Object> app_icon_file_upload(HttpServletRequest request, HttpSession session, @RequestParam("iconFile") MultipartFile file) throws Exception{
		System.out.println("@@@@@@@@@@@@template/iconfileupload.html");
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
	 * 캡쳐 이미지 업로드
	 */
	@RequestMapping(value = "/template/deletetmpimg.html", method = RequestMethod.POST)
	public @ResponseBody HashMap<Object, Object> app_delete_temp_img(HttpServletRequest request, HttpSession session){
		System.out.println("@@@@@@@@@@@@template/deletetmpimg.html");
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		try{
			String saveFileName = request.getParameter("saveFileName");
			String fileStatus = request.getParameter("fileStatus");
			String savePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.upload.path.temp.images.file", null, localeResolver.resolveLocale(request));
			File file = new File(savePath+saveFileName);
			if(!FileUtil.delete(file)){
				//message : 삭제 중 오류가 발생했습니다.
				map.put("error", messageSource.getMessage("temp.control.002", null, localeResolver.resolveLocale(request)));
				return map;
			}	
			map.put("error", "none");
		}catch(Exception e){
			//message : 삭제 중 오류가 발생했습니다.
			map.put("error", messageSource.getMessage("temp.control.002", null, localeResolver.resolveLocale(request)));
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
	
					if (!("image/jpeg".equals(mimeType)||"image/gif".equals(mimeType)||"image/png".equals(mimeType)||"image/jpg".equals(mimeType))) {
						//message : 등록할 수 없는 파일 형태입니다.
						map.put("error", messageSource.getMessage("temp.control.003", null, localeResolver.resolveLocale(request)));
						outputStream.close();
						inputStream.close();
						return map;
						//throw new Exception("등록할 수 없는 파일형태입니다.");
					}
	
					String savePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.upload.path.temp.images.file", null, localeResolver.resolveLocale(request));
					String webPath = messageSource.getMessage("file.upload.path.temp.images.file", null, localeResolver.resolveLocale(request));
					outputStream = new FileOutputStream(savePath + saveFileName);
					 
					int readBytes = 0;
					 
					byte[] buffer = new byte[8192];
					 
					while ((readBytes = inputStream.read(buffer, 0 , 8192)) != -1) {
						outputStream.write(buffer, 0, readBytes);
					}
					//파일 비율검수 및 리사이즈 
					BufferedImage uploadImages = ImageIO.read(new File(savePath + saveFileName));
					int orgHeight = uploadImages.getHeight();
					int orgWidth = uploadImages.getWidth();

					//아이콘일때
					int toBeWidth = 300;
					int toBeHeight = 300;
					if ("iconFile".equals(map.get("fileType"))) {
						if(orgHeight!=orgWidth){
							//message : 이미지 비율이 맞지 않습니다. 아이콘 이미지는 정사각형만 등록이 가능합니다.
							map.put("error", messageSource.getMessage("temp.control.004", null, localeResolver.resolveLocale(request)));
							outputStream.close();
							inputStream.close();
							return map;						
						}
					}
					if ("captureFile".equals(map.get("fileType"))) {
						/*
						if(orgHeight!=orgWidth){
							map.put("error", "이미지 비율이 맞지 않습니다.");
							outputStream.close();
							inputStream.close();
							return map;						
						}
						*/
						//orgHeight/orgWidth=toBeHeight/toBeWidth
						if(orgHeight>768){
							toBeWidth = Integer.valueOf(orgWidth/orgHeight*768);
							toBeHeight = 768;
						}else{
							toBeWidth = orgWidth;
							toBeHeight = orgHeight;
						}
					}
					BufferedImage originalImage = ImageIO.read(new File(savePath + saveFileName));
					BufferedImage resizeImage = ImageTask.resizeImage(originalImage, originalImage.getType(), toBeWidth, toBeHeight);
					ImageIO.write(resizeImage, fileExt, new File(savePath + saveFileName)); 

					map.put("webPath", webPath+saveFileName);
					map.put("error", "none");
					//파일 비율검수 및 리사이즈 
					outputStream.close();
					inputStream.close();
					outputStream = null;
					inputStream = null;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			//message : 업로드 중 알 수 없는 에러가 발생했습니다.
			map.put("error", messageSource.getMessage("temp.control.005", null, localeResolver.resolveLocale(request)));		}
		return map;
	}

	/**
	 * 캡쳐 이미지 업로드
	 */
	@RequestMapping(value = "/template/capturefileupload.html", method = RequestMethod.POST)
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

	public HashMap<String, String> uploadFile2(HashMap map, MultipartFile upLoadFile, HttpServletRequest request) throws Exception {

		InputStream inputStream = null;
		OutputStream outputStream = null;
		
		if (upLoadFile != null) {
			if (upLoadFile.getSize() > 0) {
	
				String orgFileName = upLoadFile.getOriginalFilename();
				String orgFileOnlyName = orgFileName.substring(0, orgFileName.lastIndexOf("."));
				 
				String fileExt = orgFileName.substring(orgFileName.lastIndexOf(".") + 1, orgFileName.length());
				
				fileExt = fileExt.toLowerCase();
				
				String saveFileOnlyName = DateUtil.getDate("yyyyMMdd_hhmmss") + "_" + Formatter.getRandomNumber(100);
				String saveFileName = saveFileOnlyName + "." + fileExt;

				inputStream = upLoadFile.getInputStream();				
				map.put("orgFileName", orgFileOnlyName+ "." + fileExt);
				map.put("saveFileName", saveFileOnlyName+ "." + fileExt);
				map.put("fileSize", upLoadFile.getSize());
				map.put("fileExt", fileExt);
				if (!("zip").equals(fileExt.toLowerCase())||("ZIP").equals(fileExt.toLowerCase())) {					 
					throw new Exception(messageSource.getMessage("temp.control.003", null, localeResolver.resolveLocale(request)));
				}						 
				String savePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.path.template.file", null, localeResolver.resolveLocale(request));
				outputStream = new FileOutputStream(savePath + saveFileName);
				 
				int readBytes = 0;
				 
				byte[] buffer = new byte[8192];
				 
				while ((readBytes = inputStream.read(buffer, 0 , 8192)) != -1) {
					outputStream.write(buffer, 0, readBytes);
				}
				outputStream.close();
				inputStream.close();
			}
		}
		return map;
	}

	private void deleteFile(String provisionFilePath) {
		// TODO Auto-generated method stub
		File file = new File(provisionFilePath);
		if(!FileUtil.delete(file)){
			System.out.println("파일삭제실페");
		}		
	}

	@RequestMapping(value="/template/modify.html" ,method={RequestMethod.GET, RequestMethod.POST})
	public ModelAndView templateModify(HttpSession session, HttpServletRequest req) throws Exception{
		
		ModelAndView mav 		  = new ModelAndView();		
		TemplateVO tempVo   	  = new TemplateVO();
		CaptureVO  CapVo   	  	  = new CaptureVO();
		String UserSeq, useVal;
		UserSeq = useVal = "";
		
		String thisSeq   = this.paramSet(req, "thisSeq");
		String shField   = this.paramSet(req, "sh_field");
		String shKeyword = this.paramSet(req, "sh_keyword"); 
		CapVo.setCaptureGb("4");
		CapVo.setBoardSeq(Integer.parseInt(thisSeq));
		
		tempVo      			   		= templateService.selectView(Integer.parseInt(thisSeq));
		List<TemplateSubVO> UserList    = templateService.selectUserList2(Integer.parseInt(thisSeq));
		List captureList 				= captureService.selectListByBoardSeqWithGb(CapVo);
		//TemplateList templateList = null;
		int UserCnt = UserList.size();
		for(int i = 0; i < UserCnt; i++){
			useVal  += ",|"+UserList.get(i).getUserSeq()+"|";
		}
		if(!"".equals(useVal)){
			useVal = useVal.replace(",,",",");
			useVal = useVal+",";
		}

		mav.addObject("shField", shField);
		mav.addObject("shKeyword", shKeyword);
		mav.addObject("tempView", tempVo);
		mav.addObject("UserCnt", UserCnt);
		mav.addObject("useSelVal", useVal);
		mav.addObject("UserList", UserList);
		mav.addObject("captureList",captureList);
		mav.setViewName("04_template/template_modify");
		return mav;
	}

	@RequestMapping(value="/template/modifyOk.html" ,method={RequestMethod.GET, RequestMethod.POST})
	public ModelAndView templateModifyOk(HttpSession session, HttpServletRequest req, @RequestParam("zipFile") MultipartFile file) throws Exception{
		
		ModelAndView mav  	 	  = new ModelAndView();	
		TemplateVO tempVo   	  = new TemplateVO();
		TemplateSubVO tempSubVo   = new TemplateSubVO();
		HashMap mv 				  = new HashMap();
		String userSeq, userId, userGb;
		userSeq = userId = userGb = "";
		
		
		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();	
		
		userSeq	=	String.valueOf(activeUser.getMemberVO().getUserSeq());
		userId	=	activeUser.getMemberVO().getUserId();
		userGb	=	activeUser.getMemberVO().getUserGb();
		
		HashMap<String, String> map = new HashMap<String, String>();
		try{

		String shField   = this.paramSet(req, "sh_field");
		String shKeyword = this.paramSet(req, "sh_keyword"); 

		String templateSeq, templateName, ostypeGb, verNum, templateTypeGb, appContentsAmt, appContentsGb, descriptionText, useGb, useUserGb, limitGb, useS, repUseS, zipOrgFile, zipSaveFile;
		templateSeq = templateName = ostypeGb = verNum = templateTypeGb = appContentsAmt = appContentsGb = descriptionText = useGb = useUserGb = limitGb = useS = repUseS = zipOrgFile = zipSaveFile = "";

		templateSeq 		= this.paramSet(req, "templateSeq");    	 //템플릿SEQ
		templateName 		= this.paramSet(req, "fm_template_name");    //템플릿명
		ostypeGb 			= this.paramSet(req, "fm_ostype_gb"); 		 //OS구분 1:ALL 2:IOS 3:IOS(PAD) 4:Android
		verNum 				= this.paramSet(req, "fm_ver_num"); 		 //버전
		templateTypeGb 		= this.paramSet(req, "fm_template_type_gb"); //템플릿 구분 1:서가앱 2:단일앱
		appContentsAmt 		= this.paramSet(req, "fm_app_contents_amt"); //콘텐츠수량
		appContentsGb	 	= this.paramSet(req, "fm_app_contents_gb");  //콘텐츠수량구분
		descriptionText 	= this.paramSet(req, "fm_description_text"); //설명
		useGb 				= this.paramSet(req, "fm_use_gb"); 			 //사용여부구분 1:예 2:아니오
		useUserGb	 		= this.paramSet(req, "fm_use_user_gb"); 	 //사용회원 1:ALL 2:특정회원
		limitGb	 			= this.paramSet(req, "fm_limit_gb"); 	 	 //제한여부
		useS	 			= this.paramSet(req, "useS"); 				 //특정회원
		zipOrgFile			= this.paramSet(req, "zipOrgFile");
		zipSaveFile			= this.paramSet(req, "zipSaveFile");

		if(file.getSize() > 0){
			map = uploadFile2(map, file,req);			
			tempVo.setUploadOrgFile(map.get("orgFileName"));//원본파일명
			//tempVo.setDistrProfile(map.get("fileExt"));//파일확장자
			tempVo.setUploadSaveFile(map.get("saveFileName"));//저장파일명
		}else{
			tempVo.setUploadOrgFile(null);  //temp원본파일명
			tempVo.setUploadSaveFile(null);//temp저장파일명
		}
		if(file.getSize() == 0 && !"".equals(zipOrgFile) && !"".equals(zipSaveFile)){
			tempVo.setUploadOrgFile(zipOrgFile);  //temp원본파일명
			tempVo.setUploadSaveFile(zipSaveFile);//temp저장파일명
		}

		tempVo.setTemplateSeq(Integer.parseInt(templateSeq));		
		tempVo.setTemplateName(templateName);		
		tempVo.setOstypeGb(ostypeGb);
		tempVo.setVerNum(verNum);
		tempVo.setTemplateTypeGb(templateTypeGb);
		tempVo.setAppContentsAmt(appContentsAmt);
		tempVo.setAppContentsGb(appContentsGb);
		tempVo.setDescriptionText(descriptionText);
		//org
		//save
		tempVo.setUseGb(useGb);
		tempVo.setUseUserGb(useUserGb);
		
		//tempVo.setUseAvailDt(useAvailDt);
		//tempVo.setUseDisableDt(useDisableDt);
		tempVo.setCompletGb("2");
		tempVo.setLimitGb(limitGb);
		//tempVo.setLimitDt(limitDt);
		
		tempVo.setRegUserSeq(Integer.parseInt(userSeq));
		tempVo.setRegUserId(userId);
		tempVo.setRegUserGb(userGb);
		//tempVo.setRegDt(DateUtil.getDate("yyyyMMdd"));
		tempVo.setChgUserSeq(Integer.parseInt(userSeq));
		tempVo.setChgUserId(userId);
		tempVo.setChgUserGb(userGb);
		//tempVo.setChgDt(new Date());			
		
		templateService.updateTemplate(tempVo);
		//tempVo.setTemplateSeq(templateSeq);
		
		//이미지 처러
		String tempPath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(req)) + messageSource.getMessage("file.upload.path.temp.images.file", null, localeResolver.resolveLocale(req));
		String toPath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(req)) + messageSource.getMessage("file.upload.path.template.capture.file", null, localeResolver.resolveLocale(req));	
		
		//String toPath = messageSource.getMessage("file.upload.path.app.icon.file", null, localeResolver.resolveLocale(request));				
		//아이콘이미지
		//String iconOrgFile = appVO.getIconOrgFile();
		//String iconSaveFile = appVO.getIconSaveFile();
		//if(FileUtil.movefile(iconSaveFile, tempPath, toPath)){
			//....쿼리
		//}
		//캡쳐이미지 처리
		String[] captureSeqArr = req.getParameterValues("captureSeq");
		String[] imgOrgFileArr = req.getParameterValues("imgOrgFile");
		String[] imgSaveFileArr = req.getParameterValues("imgSaveFile");
		String[] thisFileTypeArr = req.getParameterValues("thisFileType");
		String captureSeq = null;
		String imgOrgFile = null;
		String imgSaveFile = null;
		toPath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(req)) + messageSource.getMessage("file.upload.path.template.capture.file", null, localeResolver.resolveLocale(req));				
		if(imgSaveFileArr!=null&&imgSaveFileArr.length>0){
			for(int i=0;i<imgSaveFileArr.length;i++){
				captureSeq = captureSeqArr[i];
				imgOrgFile = imgOrgFileArr[i];				 
				imgSaveFile = imgSaveFileArr[i];	
				if("0".equals(captureSeq)&&FileUtil.movefile(imgSaveFile, tempPath, toPath)){
					//....쿼리 tb_capture
					CaptureVO captureVO = new CaptureVO();
					captureVO.setCaptureGb("4");
					captureVO.setBoardSeq(Integer.parseInt(templateSeq));
					captureVO.setUserSeq(Integer.parseInt(userSeq));
					captureVO.setImgOrgFile(imgOrgFile);
					captureVO.setImgSaveFile(imgSaveFile);
					captureService.insert(captureVO);
					captureVO = null;
				}				
			}
		}
		//기존파일삭제
		//captureSeq
/*			var html = '<input typd="hidden" name="deleteFileSeq" value="'+captureSeq+'"/>';
		html += '<input typd="hidden" name="deleteSaveFileName" value="'+saveFileName+'"/>';
		html += '<input typd="hidden" name="deleteFileType" value="'+thisFileType+'"/>';*/			
		String[] deleteFileSeqArr = req.getParameterValues("deleteFileSeq");
		String[] deleteSaveFileNameArr = req.getParameterValues("deleteSaveFileName");
		String[] deleteFileTypeArr = req.getParameterValues("deleteFileType");
		Integer deleteFileSeq = null;
		String deleteSaveFileName = null;
		String deleteFileType = null;
		String deleteFilePath = null;
		if(deleteFileTypeArr!=null&&deleteFileTypeArr.length>0){
			for(int i=0;i<deleteFileTypeArr.length;i++){
				deleteFileSeq = Integer.parseInt(deleteFileSeqArr[i]);
				deleteSaveFileName = deleteSaveFileNameArr[i];				 
				deleteFileType = deleteFileTypeArr[i];		
				if("capture".equals(deleteFileType)){//켑쳐파일 삭제
					deleteFilePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(req)) + messageSource.getMessage("file.upload.path.template.capture.file", null, localeResolver.resolveLocale(req));
					if(FileUtil.delete(new File(deleteFilePath+deleteSaveFileName))){
						CaptureVO captureVO = new CaptureVO();
						captureVO.setCaptureSeq(deleteFileSeq);
						captureService.delete(captureVO);	
						captureVO = null;							
					}
				}
			}
		}

		tempSubVo.setTemplateSeq(Integer.parseInt(templateSeq));	
		if(!"".equals(useS)){repUseS      = useS.replace("|", "");}		
		String[] resultVal = repUseS.split(",");
		if("1".equals(useUserGb) && !"".equals(templateSeq)){
			templateService.deleteTemplateSub(tempSubVo);
		}
		if("2".equals(useUserGb) && !"".equals(templateSeq)){
			templateService.deleteTemplateSub(tempSubVo);
			for(int i=1; i<resultVal.length; i++){
				tempSubVo.setUserSeq(Integer.parseInt(resultVal[i]));
				templateService.insertTemplateSub(tempSubVo);
			}
		}

		//message : 정보가 수정 되었습니다.
		mv.put("msg", messageSource.getMessage("temp.control.007", null, localeResolver.resolveLocale(req)));
		mv.put("type", "href");
		mv.put("url", "/template/list.html");
		mav.addAllObjects(mv);
		mav.setViewName("inc/dummy");

		}catch(Exception e){
			e.printStackTrace();
			String templateFilePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(req)) + messageSource.getMessage("file.path.template.file",null, localeResolver.resolveLocale(req))+tempVo.getUploadSaveFile();
			System.out.println("templateFilePath==="+templateFilePath);
			deleteFile(templateFilePath);
			//message : 입력 정보가 잘못되었습니다.
			mv.put("msg", messageSource.getMessage("temp.control.001", null, localeResolver.resolveLocale(req)));
			mv.put("type", "href");
			mv.put("url", "/template/list.html");
			mav.addAllObjects(mv);
			mav.setViewName("inc/dummy");
		}
		return mav;
	}

	@RequestMapping(value = "/template/deleteupload.html", method = RequestMethod.POST)
	public @ResponseBody HashMap<Object, Object> upload_temp_file(HttpServletRequest req, HttpSession session){
		System.out.println("@@@@@@@@@@@@template/deleteupload.html");
		HashMap<Object, Object> map = new HashMap<Object, Object>();
		try{
			
			String templateSeq   = this.paramSet(req, "templateSeq");
			//String saveFileName  = this.paramSet(req, "saveFileName");
			String saveFileName  = this.paramSet(req, "zipSaveFile");
			TemplateVO tempVo = new TemplateVO();
			String savePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(req)) + messageSource.getMessage("file.path.template.file", null, localeResolver.resolveLocale(req));
			File file = new File(savePath+saveFileName);
			if(!FileUtil.delete(file)){
				//message : 삭제 중 오류가 발생했습니다.
				map.put("error", messageSource.getMessage("temp.control.002", null, localeResolver.resolveLocale(req)));
				return map;
			}
			tempVo.setTemplateSeq(Integer.parseInt(templateSeq));
			templateService.updateTemplateFile(tempVo);
			map.put("error", "none");
		}catch(Exception e){
			//message : 삭제 중 오류가 발생했습니다.
			map.put("error", messageSource.getMessage("temp.control.002", null, localeResolver.resolveLocale(req)));
			return map;			
		}
		return map;
	}	
		
	private String paramSet(HttpServletRequest req, String targetName) {
		String value = "";
		value = (null == req.getParameter(targetName)) ? "" : "" + req.getParameter(targetName);
		
		return value;
	}
}

