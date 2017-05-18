package com.clbee.pbcms.controller;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.GrantedAuthority;
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

import thredds.inventory.FeatureCollectionConfig.UpdateConfig;

import com.clbee.pbcms.service.AppService;
import com.clbee.pbcms.service.ContentsService;
import com.clbee.pbcms.service.InAppService;
import com.clbee.pbcms.util.DateUtil;
import com.clbee.pbcms.util.FileUtil;
import com.clbee.pbcms.util.Formatter;
import com.clbee.pbcms.util.myUserDetails;
import com.clbee.pbcms.vo.AppVO;
import com.clbee.pbcms.vo.CompanyVO;
import com.clbee.pbcms.vo.ContentList;
import com.clbee.pbcms.vo.ContentVO;
import com.clbee.pbcms.vo.ContentsappSubVO;
import com.clbee.pbcms.vo.InappcategoryVO;
import com.clbee.pbcms.vo.MemberList;
import com.clbee.pbcms.vo.MemberVO;

@Controller
public class ContentsController {
	
	@Autowired
	ContentsService contentsService;

	@Autowired
	MessageSource messageSource;
	
	@Autowired
	AppService appService;
	
	@Autowired
	InAppService inAppService;
	
	@Autowired
	LocaleResolver localeResolver;
	
	@InitBinder
	private void initBinder(WebDataBinder binder) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//edit for the    format you need
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	    binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class,  true));
	}

	@RequestMapping( value = "/contents/list.html", method={RequestMethod.GET, RequestMethod.POST})
	public ModelAndView contentsListGETPOST( String toChk, String[] toChk_, String page, String searchType, String searchValue, HttpServletRequest request, HttpSession session, String isPost) throws ParseException {
		System.out.println("@@@@@@@@contentList[GET, POST] is POST = " + isPost );

		ModelAndView modelAndView = new ModelAndView();

		String[] formattedDate = null;
		String[] formattedDownInfo = null;
		ContentList contentList = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd ");

		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>)SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		String authority = null;

		GrantedAuthority element = authorities.iterator().next();
		authority = element.getAuthority();

		if( !("true".equals(isPost))) toChk_ = new String[0];

		//if( "true".equals(isPost)){
			if("ROLE_COMPANY_MEMBER".equals(authority) || "ROLE_COMPANY_MIDDLEADMIN".equals(authority) || "ROLE_COMPANY_DISTRIBUTOR".equals(authority) || "ROLE_COMPANY_CREATOR".equals(authority)) {
				System.out.println("[POST] page = " + page);
				
				for(int i =0; i<toChk_.length ;i++) {
					System.out.println("toChk_["+i+"] = " + toChk_[i] );
				}
				contentList = contentsService.getListContents(Integer.parseInt(page), 10, toChk_, "companySeq", activeUser.getMemberVO().getCompanySeq(), searchType, searchValue, true);
				formattedDate = new String[contentList.getList().size()];	
				formattedDownInfo = new String[contentList.getList().size()];
				modelAndView.addObject("ToChk", toChk);
				modelAndView.addObject("ToChk_", toChk_);
			}else if("ROLE_INDIVIDUAL_MEMBER".equals(authority)){
				contentList = contentsService.getListContents(Integer.parseInt(page), 10, toChk_, "regUserSeq", activeUser.getMemberVO().getUserSeq(), searchType, searchValue, true);
				formattedDate = new String[contentList.getList().size()];
				formattedDownInfo = new String[contentList.getList().size()];
				modelAndView.addObject("ToChk", toChk);
				modelAndView.addObject("ToChk_", toChk_);
			}else if("ROLE_ADMIN_SERVICE".equals(authority)) {
				contentList = contentsService.getListContents(Integer.parseInt(page), 10, toChk_, null, null, searchType, searchValue, false);
				formattedDate = new String[contentList.getList().size()];
				formattedDownInfo = new String[contentList.getList().size()];
				modelAndView.addObject("ToChk", toChk);
				modelAndView.addObject("ToChk_", toChk_);
			}
		/*
		 * }else{
			if("ROLE_COMPANY_MEMBER".equals(authority) || "ROLE_COMPANY_DISTRIBUTOR".equals(authority) || "ROLE_COMPANY_CREATOR".equals(authority)) {
				contentList =  contentsService.getListContents(Integer.parseInt(page), 10, activeUser.getMemberVO().getCompanySeq(), 10, searchType, searchValue,true);
				CompanyVO companyVO = companyService.findByCustomInfo("companySeq", activeUser.getMemberVO().getCompanySeq());
				formattedDate = new String[contentList.getList().size()];
				formattedDownInfo = new String[contentList.getList().size()];
			}else if() {	
			}
			else if("ROLE_ADMIN_SERVICE".equals(authority)) {
				contentList =  contentsService.getListContents(Integer.parseInt(page), activeUser.getMemberVO().getCompanySeq(), 10, searchType, searchValue, false);
				formattedDate = new String[contentList.getList().size()];
				formattedDownInfo = new String[contentList.getList().size()];
			}
		}*/

		if(contentList != null) {
			for( int i = 0; i < contentList.getList().size() ; i++){
				formattedDate[i] = format.format(contentList.getList().get(i).getRegDt());
				if("2".equals(contentList.getList().get(i).getDistrGb()) && "1".equals(contentList.getList().get(i).getCouponGb())) {
					if("1".equals(contentList.getList().get(i).getNonmemDownGb())) {
						formattedDownInfo[i] = contentList.getList().get(i).getNonmemDownAmt() + " "+messageSource.getMessage("app.list.text1", null, localeResolver.resolveLocale(request));
					}else if("2".equals(contentList.getList().get(i).getNonmemDownGb())) {
						formattedDownInfo[i] = format.format(contentList.getList().get(i).getNonmemDownStarDt()) + " ~ "  + format.format(contentList.getList().get(i).getNonmemDownEndDt());
					}
				}else{
					if("1".equals(contentList.getList().get(i).getMemDownGb())) {
						formattedDownInfo[i] = contentList.getList().get(i).getMemDownAmt() + " "+messageSource.getMessage("app.list.text1", null, localeResolver.resolveLocale(request));
					}else if("2".equals(contentList.getList().get(i).getMemDownGb())){
						formattedDownInfo[i] = format.format(contentList.getList().get(i).getMemDownStartDt()) + " ~ "  + format.format(contentList.getList().get(i).getMemDownEndDt());
					}
				}
			}
		}

		modelAndView.addObject("currentPage", page);
		modelAndView.addObject("formattedDownInfo",formattedDownInfo);
		modelAndView.addObject("formattedDate",formattedDate);
		modelAndView.addObject("contentList", contentList);
		modelAndView.addObject("searchValue", searchValue);
		modelAndView.setViewName("02_contents/contents_list");
		/*List contentListHash =  contentsService.getListContentOfCheckBox(sdf, 1, new ContentList(2, 2, 2, 2), "");
		modelAndView.addObject("contentListHash", contentListHash);*/

		return modelAndView;
	}

	@RequestMapping( value = "contents/write.html", method=RequestMethod.GET)
	public ModelAndView contentsWriteGET(ContentVO content){

		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ModelAndView modelAndView = new ModelAndView();

		modelAndView.addObject("memberVO", activeUser.getMemberVO());
		modelAndView.setViewName("02_contents/contents_write");

		return modelAndView;
	}

	@RequestMapping( value = "contents/write.html", method=RequestMethod.POST)
	public ModelAndView contentsWritePOST( HttpServletRequest request, String relatedAppName, String relatedAppType, String relatedBundleId, String relatedInAppSeq, ContentVO contentVO,  @RequestParam("uploadContentsFile") MultipartFile file){

		ModelAndView modelAndView = new ModelAndView();
		HashMap<String, String> map = new HashMap<String, String>();
		ContentsappSubVO contentsappSubVO = new ContentsappSubVO();
		try {
			map = uploadContents(map, file, request);
			unzip(map, request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		contentVO.setUploadOrgFile(map.get("orgFileName")+"."+map.get("fileExt"));
		contentVO.setUploadSaveFile(map.get("saveFileName")+"."+map.get("fileExt"));
		contentVO.setContentsSize(map.get("fileSize"));
		contentVO.setAppName(relatedAppName);
		contentVO.setAppType(relatedAppType);
		int contentsSeq = contentsService.insertContentInfo( contentVO );
		
		
		if(relatedInAppSeq != null && !"".equals(relatedInAppSeq))
		contentsappSubVO.setStoreBundleId(relatedBundleId);
		if(relatedInAppSeq != null && !"".equals(relatedInAppSeq))
		contentsappSubVO.setInappSeq(Integer.parseInt(relatedInAppSeq));
		contentsappSubVO.setContentsSeq(contentsSeq);
		
		contentsService.insertContentsappSubInfo(contentsappSubVO);
		
		modelAndView.setViewName("redirect:/contents/list.html?page=1");
		return modelAndView;
	}

	@RequestMapping( value = "contents/modify.html", method=RequestMethod.GET)
	public ModelAndView contentsModifyGET( String contentsSeq, HttpServletRequest request, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView();
		ContentVO contentVO = contentsService.selectByContentId(Integer.parseInt(contentsSeq));
		modelAndView.addObject("contentVO", contentVO);
		modelAndView.setViewName("02_contents/contents_modify");
		return modelAndView;
	}

	@RequestMapping( value = "contents/modify.html", method=RequestMethod.POST)
	public ModelAndView contentsModifyPOST( String contentsappSubSeq,  String relatedAppName, String relatedAppType, String relatedBundleId, String relatedInAppSeq, String page, String fileChanged, ContentVO contentFormVO, @RequestParam("contentsFile") MultipartFile uploadFile , HttpServletRequest request, HttpSession session) {
		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ModelAndView modelAndView = new ModelAndView();
		HashMap<String, String> map = new HashMap<String, String>();
		String savePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.path.contents.file", null, localeResolver.resolveLocale(request));
		String saveFIleName = contentFormVO.getUploadSaveFile().substring( 0, contentFormVO.getUploadSaveFile().lastIndexOf("."));
		ContentsappSubVO contentsappSubVO = new ContentsappSubVO();
		
		
		if("1".equals(fileChanged)) {
			System.out.println("퍼일이 변경되었습니다!!!!!!!!!!");
			try{
				
				/*File deleteFile = new File(savePath+contentFormVO.getUploadSaveFile());
				if(!FileUtil.delete(deleteFile)){
					modelAndView.addObject("msg", messageSource.getMessage("contents.control.001", null, localeResolver.resolveLocale(request)));
					modelAndView.addObject("type", "href");
					modelAndView.addObject("url", "/contents/modify.html?page="+page+"&contentSeq="+contentFormVO.getContentsSeq());
					modelAndView.setViewName("inc/dummy");
					//return 0;
				}*/
				
				File deleteFolder = new File(savePath+saveFIleName);

				if(!FileUtil.delete(deleteFolder)){
					modelAndView.addObject("msg", messageSource.getMessage("contents.control.001", null, localeResolver.resolveLocale(request)));
					modelAndView.addObject("type", "href");
					modelAndView.addObject("url", "/contents/modify.html?page="+page+"&contentsSeq="+contentFormVO.getContentsSeq());
					modelAndView.setViewName("inc/dummy");
				}
				
				//필요한부분만 업데이트하기위해 일단 객체를 새로 생성함..
				
				map = uploadContents(map, uploadFile, request);
				//압축 풀기
				unzip(map, request);

				contentFormVO.setUploadOrgFile(map.get("orgFileName") +"."+ map.get("fileExt"));
				contentFormVO.setUploadSaveFile(map.get("saveFileName") + "." + map.get("fileExt"));
				contentFormVO.setContentsSize(map.get("fileSize"));
				contentFormVO.setAppName(relatedAppName);
				contentFormVO.setAppType(relatedAppType);
				
				
				if(relatedInAppSeq != null && !"".equals(relatedInAppSeq))
				contentsappSubVO.setStoreBundleId(relatedBundleId);
				if(relatedInAppSeq != null && !"".equals(relatedInAppSeq))
				contentsappSubVO.setInappSeq(Integer.parseInt(relatedInAppSeq));
				contentsappSubVO.setContentsSeq(contentFormVO.getContentsSeq());
				
				if(contentsappSubSeq != null && !"".equals(contentsappSubSeq))	contentsService.updateContentsappSubInfo(contentsappSubVO, Integer.parseInt(contentsappSubSeq));
				else contentsService.insertContentsappSubInfo(contentsappSubVO);
				contentsService.updateContentInfo(contentFormVO, contentFormVO.getContentsSeq());
				
				modelAndView.addObject("msg", messageSource.getMessage("contents.control.002", null, localeResolver.resolveLocale(request)));
				modelAndView.addObject("type", "href");
				modelAndView.addObject("url", "/contents/list.html?page="+page);
				modelAndView.setViewName("inc/dummy");
			}catch(Exception e){
				e.printStackTrace();
				//return 0;			
				modelAndView.addObject("msg", messageSource.getMessage("contents.control.003", null, localeResolver.resolveLocale(request)));
				modelAndView.addObject("type", "href");
				modelAndView.addObject("url", "/contents/modify.html?page="+page+"&contentsSeq="+contentFormVO.getContentsSeq());
				modelAndView.setViewName("inc/dummy");
			}
			//return 1;
		}else {
			contentFormVO.setAppName(relatedAppName);
			contentFormVO.setAppType(relatedAppType);
			
			
			contentsappSubVO.setStoreBundleId(relatedBundleId);
			if(relatedInAppSeq != null && !"".equals(relatedInAppSeq))
			contentsappSubVO.setInappSeq(Integer.parseInt(relatedInAppSeq));
			contentsappSubVO.setContentsSeq(contentFormVO.getContentsSeq());

			
			if(contentsappSubSeq != null && !"".equals(contentsappSubSeq))	contentsService.updateContentsappSubInfo(contentsappSubVO, Integer.parseInt(contentsappSubSeq));
			else contentsService.insertContentsappSubInfo(contentsappSubVO);
			contentsService.updateContentInfo(contentFormVO, contentFormVO.getContentsSeq());
			modelAndView.addObject("msg", messageSource.getMessage("contents.control.002", null, localeResolver.resolveLocale(request)));
			modelAndView.addObject("type", "href");
			modelAndView.addObject("url", "/contents/list.html?page="+page);
			modelAndView.setViewName("inc/dummy");
		}
		return modelAndView;
	}

	private void unzip( HashMap map, HttpServletRequest request){
		byte[] buffer = new byte[1024];
		String savePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.path.contents.file", null, localeResolver.resolveLocale(request));
		
		System.out.println("unzip!!!");
		
		try{
			File folder = new File(savePath+map.get("saveFileName")+"/view");
			if(!folder.exists()){
	    		folder.mkdir();
	    	}
			ZipInputStream zis = 
		    		new ZipInputStream(new FileInputStream(savePath+map.get("saveFileName")+"/"+map.get("saveFileName")+".zip"));
			ZipEntry ze = zis.getNextEntry();
			
			
			System.out.println("@@@@@@ze = " +ze );
			
			while(ze!=null){
    			
		    	   String fileName = ze.getName();
		           File newFile = new File(savePath+map.get("saveFileName")+"/view" + File.separator + fileName);
		                
		           System.out.println("file unzip : "+ newFile.getAbsoluteFile());
		                
		           //create all non exists folders
		           //else you will hit FileNotFoundException for compressed folder
		           if (ze.isDirectory())
		           {	
		        	   System.out.println("[IS A DIRECTORY]");
		        	   String temp = newFile.getCanonicalPath();
		        	   new File(temp).mkdir();
		           }else{
		        	   System.out.println("[NOT A DIRECTORY]");
		        	   FileOutputStream fos = new FileOutputStream(newFile);             
	
		        	   int len;
		        	   while ((len = zis.read(buffer)) > 0) {
		        		   fos.write(buffer, 0, len);
			           }
			        		
		        	   fos.close();
		           }
		            ze = zis.getNextEntry();
		    	}
		    	
		        zis.closeEntry();
		    	zis.close();
		    		
		    	System.out.println("Done");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public HashMap<String, String> uploadContents(HashMap map, MultipartFile upLoadFile,  HttpServletRequest request) throws Exception {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		float floatValue = (float)upLoadFile.getSize()/(1024*1024);
		DecimalFormat format = new DecimalFormat(".##");
		String savePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.path.contents.file", null, localeResolver.resolveLocale(request));
		if (upLoadFile != null) {
			if (upLoadFile.getSize() > 0) {
				String orgFileName = upLoadFile.getOriginalFilename();
				String orgFileOnlyName = orgFileName.substring(0, orgFileName.lastIndexOf("."));
				String fileExt = orgFileName.substring(orgFileName.lastIndexOf(".") + 1, orgFileName.length());
				fileExt = fileExt.toLowerCase();
				
				String saveFileOnlyName = DateUtil.getDate("yyyyMMdd_hhmmss") + "_" + Formatter.getRandomNumber(100);
				String saveFileName = saveFileOnlyName + "." + fileExt;

				File folder = new File(savePath+saveFileOnlyName);
				System.out.println("folder = " + folder);
		    	if(!folder.exists()){
		    		System.out.println("Exist");
		    		try{
		    			boolean flag= folder.mkdir();
		    			System.out.println("flag = " + flag);
		    		}catch(Exception e){
		    			e.printStackTrace();
		    		}
		    	}
				
				
				inputStream = upLoadFile.getInputStream();				
				map.put("orgFileName", orgFileOnlyName);
				map.put("saveFileName", saveFileOnlyName);
				if ( floatValue < 1 ) {
					map.put("fileSize", "0"+ format.format(floatValue)+"MB");
				}else map.put("fileSize", format.format(floatValue)+"MB");
				
				map.put("fileExt", fileExt);				
				/*if (!("mobileprovision").equals(fileExt.toLowerCase())||("keystore").equals(fileExt.toLowerCase())) {					 
					throw new Exception("�벑濡앺븷 �닔 �뾾�뒗 �뙆�씪�삎�깭�엯�땲�떎.");
				}						 */
				
				outputStream = new FileOutputStream(savePath +saveFileOnlyName +"/"+ saveFileName);
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

	@RequestMapping( value = "/contents/deleteFile.html", method=RequestMethod.POST)
	public @ResponseBody int deleteF( String contentsSeq, String uploadSaveFile, HttpServletRequest request, HttpSession session ) {

		System.out.println("contentsSeq = " + contentsSeq);
		System.out.println("uploadSaveFile = " + uploadSaveFile);
		try{
			String savePath = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(request)) + messageSource.getMessage("file.path.contents.file", null, localeResolver.resolveLocale(request));
			File file = new File(savePath+uploadSaveFile);
			if(!FileUtil.delete(file)){
				return 0;
			}
			ContentVO contentVO = new ContentVO();
			contentVO.setContentsSeq(Integer.parseInt(contentsSeq));
			contentVO.setUploadSaveFile("");
			contentsService.updateNullableContentInfo(contentVO);
		}catch(Exception e){
			return 0;
		}
		return 1;
	}

	@RequestMapping( value = "/contents/write/popUpContents.html", method= {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView contentsWritePopCategoryGET(ContentVO content, String isPost){
		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		ModelAndView mav = new ModelAndView();
		MemberVO memberVO = new MemberVO();
		List<AppVO> appList = null;
		String authority = activeUser.getAuthorities().iterator().next().getAuthority();

		if("true".equals(isPost)) ;
		if ("ROLE_COMPANY_MEMBER".equals(authority) || "ROLE_COMPANY_DISTRIBUTOR".equals(authority) || "ROLE_COMPANY_CREATOR".equals(authority)) {
			/*해당 정보를 넣어줘야 null값이 안떠서 특정 조건을 실행함*/
			memberVO.setUserGb(activeUser.getMemberVO().getUserGb());
			/* 기업이기 때문에 CompanySeq를 넣어줘야함*/
			memberVO.setCompanySeq(activeUser.getMemberVO().getCompanySeq());
		}else if("ROLE_INDIVIDUAL_MEMBER".equals(authority)){
			/*해당 정보를 넣어줘야 null값이 안떠서 특정 조건을 실행함*/
			memberVO.setUserGb(activeUser.getMemberVO().getUserGb());
			/* 개인이기 때문에 userSeq를 넣어줘야함*/
			memberVO.setUserSeq(activeUser.getMemberVO().getUserSeq());
		}
		appList =  appService.selectAppListForRelatedApp(memberVO);			
		mav.addObject("appList", appList);
		mav.setViewName("02_contents/contents_pop_category");
		return mav;
	}

	@RequestMapping( value = "/contents/getInAppList.html", method=RequestMethod.POST)
	public @ResponseBody Object[] contentsInAppListPOST( String contentsSeq, String uploadSaveFile, HttpServletRequest request, HttpSession session ) {
		myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Object[] InAppList = null;
		String appSeq 		= this.paramSet(request, "storeBundleId");
		String authority = activeUser.getAuthorities().iterator().next().getAuthority();
		InAppList = inAppService.getListInAppForRelatedApp(appSeq);		
		System.out.println("====================================================================================3");
		System.out.println(InAppList);
		System.out.println("====================================================================================4");
		return InAppList;
	}

	private String paramSet(HttpServletRequest req, String targetName) {
		String value = "";
		value = (null == req.getParameter(targetName)) ? "" : "" + req.getParameter(targetName);
		
		return value;
	}	
}
