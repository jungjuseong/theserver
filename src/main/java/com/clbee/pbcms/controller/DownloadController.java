package com.clbee.pbcms.controller;




import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import com.clbee.pbcms.service.AppService;
import com.clbee.pbcms.service.ContentsService;
import com.clbee.pbcms.service.InAppService;
import com.clbee.pbcms.service.MemberService;
import com.clbee.pbcms.service.ProvisionService;
import com.clbee.pbcms.service.TemplateService;
import com.clbee.pbcms.util.Entity;
import com.clbee.pbcms.util.StringUtil;
import com.clbee.pbcms.util.myUserDetails;
import com.clbee.pbcms.vo.AppVO;
import com.clbee.pbcms.vo.ContentVO;
import com.clbee.pbcms.vo.InappVO;
import com.clbee.pbcms.vo.MapList;
import com.clbee.pbcms.vo.MemberVO;
import com.clbee.pbcms.vo.ProvisionVO;
import com.clbee.pbcms.vo.TemplateVO;

@Controller
@RequestMapping("/*.html")
public class DownloadController  {
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	MessageSource messageSource;
	
	@Autowired
	AppService appService;
	
	@Autowired
	ContentsService contentsService;
	
	@Autowired
	LocaleResolver localeResolver;
	
	@Autowired
	InAppService inAppService;
	
	@Autowired
	ProvisionService provisionService;
	
	@Autowired
	TemplateService templateService;

	@RequestMapping(value="/down/list.html" ,method={RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downList(HttpSession session, HttpServletRequest req, MapList mapList, MemberVO memVO) throws Exception{

		ModelAndView mav = new ModelAndView();
		Entity param = new Entity();
		
		String toChk, toChk_, loginUserCompanyGb, todayDate;
		toChk = toChk_ = loginUserCompanyGb = todayDate = "";
		String authority = null;
		int loginUserSeq, loginUserCompanySeq;
		String isMobile = paramSet(req, "isMobile");

		if(!"anonymousUser".equals(SecurityContextHolder.getContext().getAuthentication().getPrincipal())){
			myUserDetails activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();		
			loginUserSeq        = activeUser.getMemberVO().getUserSeq();
			loginUserCompanySeq = activeUser.getMemberVO().getCompanySeq();
			loginUserCompanyGb  = activeUser.getMemberVO().getCompanyGb();

			Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>)SecurityContextHolder.getContext().getAuthentication().getAuthorities();

			GrantedAuthority element = authorities.iterator().next();
			authority = element.getAuthority();
			todayDate = appService.getSelectTodayDate();   

			param.setValue("REGSEQ", loginUserSeq); //로그인 SEQ
			param.setValue("COMPANY_SEQ", loginUserCompanySeq); //로그인 COMPANY_SEQ
			param.setValue("COMPANY_GB", loginUserCompanyGb); //로그인 개인/회사 구분
			param.setValue("TODAYDATE", todayDate); //오늘 년월일
			param.setValue("AUTHORITY", authority); //로그인 권한 키
			param.setValue("ISMOBILE", isMobile);
			param.setValue("sessionUserSeq", activeUser.getMemberVO().getUserSeq());
		}else{
			param.setValue("AUTHORITY", "ROLE_ANONYMOUS"); //로그인 권한 키	
			param.setValue("ISMOBILE", isMobile);
			param.setValue("COMPANY_GB", ""); //로그인 개인/회사 구분
		}

		toChk  = paramSet(req, "toChk");
		toChk_ = paramSet(req, "toChk_");

		int pageSize = 10;
		int maxResult = 10;
		int totalCount = 0;

		if(mapList.getCurrentPage()==null)mapList.setCurrentPage(1);
		
		if(toChk != null) {
			if(toChk_.toString().indexOf("5") > -1){
				param.setValue("searchChk", toChk_+",6,7");
			}else{
				param.setValue("searchChk", toChk_);
			}
		}

		param.setValue("searchType", mapList.getSearchType());
		param.setValue("searchValue", mapList.getSearchValue());

		List listCnt = appService.getSelectListCount(param);
		HashMap temp = (HashMap)listCnt.get(0);
		totalCount = Integer.parseInt(temp.get("DOWNTOTALCOUNT").toString());
		mapList.calc(pageSize, totalCount, mapList.getCurrentPage(), maxResult);		
		param.setValue("startNo", mapList.getStartNo());

		List downList = appService.getSelectList(param);

		System.out.println(mapList + "========================================================================");
		//maplist = (MapList) appService.getSelectList(param, maplist);

		mav.addObject("path",  messageSource.getMessage("file.path.app.file", null, localeResolver.resolveLocale(req)));
		mav.addObject("DownList", downList);
		mav.addObject("MapList", mapList);
		mav.addObject("ToChk", toChk);
		mav.addObject("ToChk_", toChk_);

		mav.setViewName("/03_down/down_list");
		return mav;
	}

	@RequestMapping(value="/down/down.html" ,method={RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downLoad(HttpSession session, HttpServletRequest req, MapList mapList, ModelMap modelMap) throws Exception{

		ModelAndView mav = new ModelAndView();
		Entity param = new Entity();

		String downGubun, downName, downKaU, downVer, downSeq, coupon_Num, ostype, fileName, downCnt, downType, isCoupon, directDown, iconName;
		downGubun = downName = downKaU = downVer = downSeq = coupon_Num= ostype = fileName = downCnt = downType = isCoupon = directDown = iconName = "";

		downGubun  		= paramSet(req, "downGubun");
		downName  		= paramSet(req, "downName");
		downKaU  		= paramSet(req, "downKaU");
		downVer	  	 	= paramSet(req, "downVer");
		downSeq	    	= paramSet(req, "downSeq");
		ostype			= paramSet(req, "ostype");
		fileName		= paramSet(req, "fileName");
		downCnt			= paramSet(req, "downCnt");
		coupon_Num		= paramSet(req, "coupon_Num");
		downType		= paramSet(req, "downType");
		isCoupon		= paramSet(req, "isCoupon");
		directDown      = paramSet(req, "directDown");
		iconName		= paramSet(req, "iconName");

		if("Android".equals(ostype)){
			if("1".equals(directDown)) {
				String path, orgName, saveName;
				path = orgName = saveName = "";

				System.out.println("ostype is = " + ostype);
				AppVO appVO = appService.selectById(Integer.parseInt(paramSet(req, "downSeq")));
				path = messageSource.getMessage("file.path.app.file", null, localeResolver.resolveLocale(req)) + "android/"+downKaU+downVer+"/";
				orgName = appVO.getFileName();
				saveName = appVO.getFileName();
				req.setAttribute("filepath", path);
				req.setAttribute("orgFileName", orgName+downVer+".apk");
				req.setAttribute("saveFileName", saveName+downVer+".apk");

				mav.setViewName("/inc/download");
			}else {
				mav.addObject("DownGubun", downGubun);
				mav.addObject("DownName", downName);
				mav.addObject("DownKaU", downKaU);
				mav.addObject("DownVer", downVer);
				mav.addObject("downSeq", downSeq);
				mav.addObject("ostype", ostype);
				mav.addObject("downCnt", downCnt);
				mav.addObject("coupon_Num", coupon_Num);
				mav.addObject("fileName", fileName);
				mav.addObject("osFlag", "Android");
				mav.addObject("downType", downType);
				mav.addObject("isCoupon", isCoupon);
				mav.addObject("iconName", iconName);
				mav.addObject("iconPath", messageSource.getMessage("file.upload.path.app.icon.file", null, localeResolver.resolveLocale(req)));
				mav.addObject("filePath", messageSource.getMessage("file.path.app.file", null, localeResolver.resolveLocale(req)) + "android/");
				mav.setViewName("android_app");
			}
		}else if("HTML5".equals(ostype) || "ePub3".equals(ostype) || "PDF".equals(ostype)){
			
			String path, orgName, saveName, onlySaveName;
			path = orgName = saveName = onlySaveName = "";

			System.out.println("ostype is = " + ostype);
			ContentVO contetnsVO = contentsService.selectByContentId(Integer.parseInt(paramSet(req, "downSeq")));
			path = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(req)) + messageSource.getMessage("file.path.contents.file", null, localeResolver.resolveLocale(req));
			orgName = contetnsVO.getUploadOrgFile();
			saveName = contetnsVO.getUploadSaveFile();
			onlySaveName = saveName.substring(0,saveName.lastIndexOf("."));
			req.setAttribute("filepath", path);
			req.setAttribute("orgFileName", orgName);
			req.setAttribute("saveFileName", onlySaveName+"/"+saveName);
			
			mav.setViewName("/inc/download");
			return mav;
		}else {
			if("1".equals(directDown)) {
				String path, orgName, saveName;
				path = orgName = saveName = "";
				System.out.println("ostype is = " + ostype);
				AppVO appVO = appService.selectById(Integer.parseInt(paramSet(req, "downSeq")));
				path = messageSource.getMessage("file.path.app.file", null, localeResolver.resolveLocale(req)) + "ios/"+downKaU+downVer+"/";
				orgName = appVO.getFileName();
				saveName = appVO.getFileName();
				req.setAttribute("filepath", path);
				req.setAttribute("orgFileName", orgName+downVer+".ipa");
				req.setAttribute("saveFileName", saveName+downVer+".ipa");
				
				mav.setViewName("/inc/download");
			}else {
				mav.addObject("DownGubun", downGubun);
				mav.addObject("DownName", downName);
				mav.addObject("DownKaU", downKaU);
				mav.addObject("DownVer", downVer);
				mav.addObject("downSeq", downSeq);
				mav.addObject("ostype", ostype);
				mav.addObject("coupon_Num", coupon_Num);
				mav.addObject("fileName", fileName);
				mav.addObject("osFlag", "iOS");
				mav.addObject("downCnt", downCnt);
				mav.addObject("downType", downType);
				mav.addObject("isCoupon", isCoupon);
				mav.addObject("iconName", iconName);
				mav.addObject("iconPath", messageSource.getMessage("file.upload.path.app.icon.file", null, localeResolver.resolveLocale(req)));
				mav.addObject("filePath", messageSource.getMessage("file.path.app.file.iOS", null, localeResolver.resolveLocale(req)));
				mav.setViewName("android_app");
			}
		}
		return mav;
	}

	@RequestMapping(value="/down/pop_coupon.html" ,method={RequestMethod.GET, RequestMethod.POST})
	public ModelAndView downLoadCoupon(HttpSession session, HttpServletRequest req, MapList mapList) throws Exception{
		ModelAndView mav = new ModelAndView();
		Entity param = new Entity();

		mav.setViewName("/03_down/down_pop_coupon");
		return mav;
	}

	@RequestMapping(value="/down/couponOk" ,method={RequestMethod.GET, RequestMethod.POST})
	public @ResponseBody Entity downLoadCouponOk(HttpSession session, HttpServletRequest req, MapList mapList) throws Exception{

		//ModelAndView mav = new ModelAndView();
		Entity param = new Entity();
		String couponNum;
		couponNum = "";
		couponNum = paramSet(req, "fm_coupon_num");
		param.setValue("coupon_Gb", "1");
		param.setValue("coupon_Num", couponNum);
		List downList = appService.getSelectCouponList(param);

		param.setValue("TotalCnt", downList.size());
		param.setValue("DownList", downList);
		//mav.setViewName("/03_down/down_pop_coupon");
		//return mav;
		return param;
	}

	@RequestMapping(value="downloadCounting.html" ,method= RequestMethod.POST)
	public @ResponseBody int downloadCountingPOST( String abc, HttpSession session, HttpServletRequest req, MapList mapList) throws Exception{
        /*"sort" : sort,
        "nonmemDownGb" : result.NONDOWNYN,
        "nonmemDownSTDT" : result.NONMEMDOWNSTDT,
        "nonmemDownENDT" : result.NONMEMDOWNENDT,
        "nonmemDownCnt" : reseult.NONDOWNCNT*/
        String sort = paramSet(req, "sort");
        String couponGb = paramSet(req, "couponGb");
        String nonmemDownGb = paramSet(req, "nonmemDownGb");
        String downCnt = paramSet(req, "downCnt");
        String nonmemDownSTDT = paramSet(req, "nonmemDownSTDT");
        String nonmemDownENDT = paramSet(req, "nonmemDownENDT");
        String downSeq = paramSet(req, "downSeq");
        String isCoupon = paramSet(req, "isCoupon");
		Entity param = new Entity();

		//DB에 ""로 값이 들어가있으면 0으로 강제 초기화한다.
		if("".equals(downCnt)) downCnt = "0";
			try{
				if("1".equals(isCoupon)){
					// 쿠폰일 경우
					if("CONTENTS".equals(sort)){
						System.out.println("11111111111111111");
						// 다운로딩 횟수
						// dgggg
						// affff
						ContentVO contentVO = new ContentVO();
						contentVO.setNonmemDownCnt(String.valueOf(Integer.parseInt(downCnt) +1 ));
						contentsService.updateContentInfo(contentVO, Integer.parseInt(downSeq));
					}else if("APP".equals(sort)){
						System.out.println("222222222222222222");
						AppVO appVO = new AppVO();
						appVO.setNonmemDownCnt(String.valueOf(Integer.parseInt(downCnt) +1 ));
						appService.updateAppInfo(appVO, Integer.parseInt(downSeq));
						System.out.println("@@@@@@@@@@@@@@@@@@@");
					}
				}else{
					// 쿠폰이 아닐 경우
					if("CONTENTS".equals(sort)){
						// 다운로딩 횟수
						System.out.println("33333333333333333333");
						ContentVO contentVO = new ContentVO();
						contentVO.setMemDownCnt(String.valueOf(Integer.parseInt(downCnt) +1 ));
						contentsService.updateContentInfo(contentVO, Integer.parseInt(downSeq));
					}else if("APP".equals(sort)){
						System.out.println("444444444444444444444");
						AppVO appVO = new AppVO();
						appVO.setMemDownCnt(String.valueOf(Integer.parseInt(downCnt) +1 ));
						appService.updateAppInfo(appVO, Integer.parseInt(downSeq));
					}
				}
			}catch(Exception e){
				// 실패일때 0
				e.printStackTrace();
				return 0;
			}
		// 성공일때 1
		return 1;
	}

	private String paramSet(HttpServletRequest req, String targetName) {
		String value = "";
		value = (null == req.getParameter(targetName)) ? "" : "" + req.getParameter(targetName);

		return value;
	}

	/* 해당 Reuqest 주소 down/download_proc.html는 
	 * RequestedInAppJsonVO 여기와 연관이 되어있으므로, 이 down/download_proc.html을 바꾸려면 
	 * RequestedInAppJsonVO 여기 정보에 있는 String값도 바꿔줘야됩니다. - 바뀜 2015-05-14 안바꿔 줘도됨.
	 * */
	@RequestMapping(value = "down/download_proc.html", method = {RequestMethod.GET, RequestMethod.POST})
	public String app_regist(HttpSession session, HttpServletRequest req, ModelMap modelMap) {

		//사용자 정보 
		/*
		 * 경로 , 파일명, 쿠폰번호, 번들아이디, 
		 * 
		 */

		//로그인인인지, 쿠폰인지, seq, 타입,
		Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>)SecurityContextHolder.getContext().getAuthentication().getAuthorities();

		GrantedAuthority element = authorities.iterator().next();
		String authority = element.getAuthority();
		myUserDetails activeUser = null;

		String returnUrl = "/inc/dummy";
		//message : 알 수 업
		String msg = messageSource.getMessage("down.control.001", null, localeResolver.resolveLocale(req));
		String errorType = "close";

		String path = "";
		String orgName = "";
		String saveName = "";

		String downSeq, downGubun, downKaU, downVer, couponNum, ostype, couponOrGeneral;
		downSeq = downGubun = downKaU = downVer = couponNum= ostype = couponOrGeneral = "";

		downSeq   = paramSet(req, "downSeq");
		downGubun    = paramSet(req, "DownGubun");
		downKaU  	= paramSet(req, "DownKaU");
		downVer	    = paramSet(req, "DownVer");
		couponNum	    = paramSet(req, "coupon_Num");
		ostype      = paramSet(req, "ostype");
		couponOrGeneral = paramSet ( req, "couponOrGeneral");

		if( !"INAPP".equals(downGubun) && !"PROVISION".equals(downGubun) && !"TEMPLATE".equals(downGubun))
			activeUser = (myUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();	

		try{
			if((downSeq==null||"".equals(downSeq))&&(downGubun==null||"".equals(downGubun))){
				//message : 잘못된 접근입니다.
				msg = messageSource.getMessage("down.control.002", null, localeResolver.resolveLocale(req));
				throw new Exception();
			}
			if("APP".equals(downGubun)){
				//앱인경우
				// 나는 앱은 모르겠고~~~
				if(couponNum!=null&&!"".equals(couponNum)){
					//쿠폰다운로드인 경우
				}else{
					//그량 리스트에서 받는 경우
				}
			}else if("CONTENTS".equals(downGubun)){
				//컨텐트인 경우
				ContentVO cvo = new ContentVO();
				ContentVO cvo1 = new ContentVO();
				if(couponNum!=null&&!"".equals(couponNum)){
					//쿠폰다운로드인 경우
					cvo.setContentsSeq(Integer.parseInt(downSeq));
					cvo.setCouponNum(couponNum);
					cvo.setCouponGb("1");
					cvo1 = contentsService.selectByUltimateCondition(cvo);
					if(cvo1.getContentsSeq()==null){
						//message : 잘못된 접근입니다.
						msg = messageSource.getMessage("down.control.002", null, localeResolver.resolveLocale(req));
						throw new Exception();
					}
					if(cvo1.getNonmemDownGb()!=null&&"1".equals(cvo1.getNonmemDownGb())){
						int cnt = Integer.parseInt(StringUtil.null2string(cvo1.getNonmemDownCnt(), "0"));
						int amt = Integer.parseInt(cvo1.getNonmemDownAmt());
						if(!(amt>cnt)){
							//message : 다운로드 횟수가 초과되었습니다. 관리자에게 문의해주세요.
							msg = messageSource.getMessage("down.control.003", null, localeResolver.resolveLocale(req));
							throw new Exception();
						}
					}
				}else{
					//그량 리스트에서 받는 경우
					//쿠폰다운로드가 아닌 경우
					cvo.setContentsSeq(Integer.parseInt(downSeq));
					cvo1 = contentsService.selectByUltimateCondition(cvo);
						if(cvo1.getContentsSeq()==null){
							//message : 잘못된 접근입니다.
							msg = messageSource.getMessage("down.control.002", null, localeResolver.resolveLocale(req));
							throw new Exception();
						}
					if(cvo1.getDistrGb()!=null&&"1".equals(cvo1.getDistrGb())){
						if(activeUser==null||activeUser.getMemberVO()==null){
							//message : 회원전용입니다.
							msg = messageSource.getMessage("down.control.006", null, localeResolver.resolveLocale(req));
							throw new Exception();
						}else{
							if("1".equals(activeUser.getMemberVO().getCompanyGb())){
								if(!cvo1.getCompanySeq().equals(activeUser.getMemberVO().getCompanySeq())){
									//message : 권한이 바르지 않습니다.
									msg = messageSource.getMessage("down.control.007", null, localeResolver.resolveLocale(req));
									throw new Exception();
								}
							}else if("2".equals(activeUser.getMemberVO().getCompanyGb())){
								if(!cvo1.getRegUserSeq().equals(activeUser.getMemberVO().getUserSeq())){
									//message : 권한이 바르지 않습니다.
									msg = messageSource.getMessage("down.control.007", null, localeResolver.resolveLocale(req));
									throw new Exception();
								}
							}
						}
					}else{
						if(cvo1.getCouponGb()!=null&&"1".equals(cvo1.getCouponGb())){
							//message : 권한이 바르지 않습니다.
							msg = messageSource.getMessage("down.control.007", null, localeResolver.resolveLocale(req));
							throw new Exception();
						}
					}
					if(cvo1.getMemDownGb()!=null&&"1".equals(cvo1.getMemDownGb())){
						int cnt = Integer.parseInt(StringUtil.null2string(cvo1.getMemDownCnt(), "0"));
						int amt = Integer.parseInt(cvo1.getMemDownAmt());
						if(!(amt>cnt)){
							//message : 다운로드 횟수가 초과되었습니다. 관리자에게 문의해주세요.
							msg = messageSource.getMessage("down.control.003", null, localeResolver.resolveLocale(req));
							throw new Exception();
						}
					}else if(cvo1.getMemDownGb()!=null&&"2".equals(cvo1.getMemDownGb())){
						Date sdt = cvo1.getMemDownStartDt();
						Date edt = cvo1.getMemDownEndDt();
						if(!(sdt.before(new Date())&&edt.after(new Date()))){
							//message : 다운로드 기간이 유효하지 않습니다.
							msg = messageSource.getMessage("down.control.004", null, localeResolver.resolveLocale(req));
							throw new Exception();						
						}
					}
				}
				//req.setAttribute(arg0, arg1);
				path = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(req)) + messageSource.getMessage("file.path.contents.file", null, localeResolver.resolveLocale(req));
				orgName = cvo1.getUploadOrgFile();
				saveName = cvo1.getUploadSaveFile();
				modelMap.put("filepath", path);
				modelMap.put("orgFileName", orgName);
				modelMap.put("saveFileName", saveName);
				return "/inc/download";
			}else if("INAPP".equals(downGubun)){
				if("COUPON".equals(couponOrGeneral)){
					InappVO inappVO = inAppService.findByCustomInfo("couponNum",couponNum);
					path = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(req)) + messageSource.getMessage("file.path.inapp.file", null, localeResolver.resolveLocale(req));
					orgName = inappVO.getInappOrgFile();
					saveName = inappVO.getInappSaveFile();

					modelMap.put("filepath", path);
					modelMap.put("orgFileName", orgName);
					modelMap.put("saveFileName", saveName);
					return "/inc/download";
				}else{
					InappVO inappVO = inAppService.findByCustomInfo("inappSeq", Integer.parseInt(paramSet(req, "downSeq")));
					path = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(req)) + messageSource.getMessage("file.path.inapp.file", null, localeResolver.resolveLocale(req));
					orgName = inappVO.getInappOrgFile();
					saveName = inappVO.getInappSaveFile();

					modelMap.put("filepath", path);
					modelMap.put("orgFileName", orgName);
					modelMap.put("saveFileName", saveName);
				}
				return "/inc/download";
			}else if("PROVISION".equals(downGubun)){
				ProvisionVO provisionVO = provisionService.findByCustomInfo( "provSeq", Integer.parseInt(paramSet(req, "downSeq")));
				path = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(req)) + messageSource.getMessage("file.path.provision.file", null, localeResolver.resolveLocale(req));
				orgName = provisionVO.getDistrProfileName();
				saveName = provisionVO.getDistrProfileSaveName();

				modelMap.put("filepath", path);
				modelMap.put("orgFileName", orgName);
				modelMap.put("saveFileName", saveName+".mobileprovision");
				return "/inc/download";
			}else if("TEMPLATE".equals(downGubun)){
				TemplateVO templateVO = templateService.selectByTempId(Integer.parseInt(paramSet(req, "downSeq")));
				path = messageSource.getMessage("file.path.basic.URL", null, localeResolver.resolveLocale(req)) + messageSource.getMessage("file.path.template.file", null, localeResolver.resolveLocale(req));
				orgName = templateVO.getUploadOrgFile();
				saveName = templateVO.getUploadSaveFile();

				modelMap.put("filepath", path);
				modelMap.put("orgFileName", orgName);
				modelMap.put("saveFileName", saveName);
				return "/inc/download";
			}else if("APPDOWNLOAD".equals(downGubun)){
				AppVO appVO = appService.selectById(Integer.parseInt(paramSet(req, "downSeq")));
				if("Android".equals(ostype)){
					path = messageSource.getMessage("file.path.app.file.android", null, localeResolver.resolveLocale(req));
					orgName = appVO.getFileName()+".apk";
					saveName = appVO.getFileName()+".apk";
				}else{
					if("ROLE_COMPANY_DISTRIBUTOR".equals(authority)){
						/*${filePath}${DownKaU}${DownVer}/${fileName}${DownVer}.apk"
*/						path = messageSource.getMessage("file.path.app.file.iOSPath", null, localeResolver.resolveLocale(req))+downKaU+downVer+"/";
						orgName = appVO.getFileName()+".ipa";
						saveName = appVO.getFileName()+".ipa";
					}else{
						path = messageSource.getMessage("file.path.app.file.iOSPath", null, localeResolver.resolveLocale(req))+downKaU+downVer+"/";
						orgName = appVO.getFileName()+downVer+".ipa";
						saveName = appVO.getFileName()+downVer+".ipa";
					}
				}
				modelMap.put("filepath", path);
				modelMap.put("orgFileName", orgName);
				modelMap.put("saveFileName", saveName);

				return "redirect:"+path+orgName;
			}
			//message : 잘못된 접근입니다.
			msg = messageSource.getMessage("down.control.002", null, localeResolver.resolveLocale(req));
			throw new Exception();
		}catch(Exception e){
			e.printStackTrace();
			modelMap.addAttribute("msg", msg);
			modelMap.addAttribute("type", errorType);
			return returnUrl;
		}
    }	
}