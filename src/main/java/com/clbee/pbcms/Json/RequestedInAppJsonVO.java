package com.clbee.pbcms.Json;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.clbee.pbcms.vo.CaptureVO;
import com.clbee.pbcms.vo.InappVO;
import com.clbee.pbcms.vo.InappcategoryVO;

public class RequestedInAppJsonVO {

	Integer categorySeq;
	Integer categoryParent;
	String categoryName;
	String categoryDepth;
	
	Integer inappSeq;
	String inappName;
	String screenType;
	String verNum;
	String descriptionText;
	String inappUrl;
	String iconUrl;
	String inappSaveFile;
	String iconSaveFile;
	String inappSize;
	String regDt;
	String chgDt;
	
	
	List<String> captureUrlList;

	public RequestedInAppJsonVO( InappVO inAppVO, InappcategoryVO inappcategoryVO, String iconURL, String captureURL, String basicURL, String inappURL, List<CaptureVO> captureList ) {
		super();

		if(inAppVO != null){
			this.inappSeq = inAppVO.getInappSeq();
			this.inappName = inAppVO.getInappName();
			this.screenType = inAppVO.getScreenType();
			this.verNum = inAppVO.getVerNum();
			this.descriptionText = inAppVO.getDescriptionText();
			this.regDt = inAppVO.getRegDt().toString();
			this.chgDt = inAppVO.getChgDt().toString();
			this.inappUrl = basicURL + inappURL + inAppVO.getInappSaveFile();
			if(inAppVO.getIconSaveFile() == null)
				this.iconUrl = null;
			else 
				this.iconUrl = basicURL + iconURL + inAppVO.getIconSaveFile();
			this.inappSaveFile = inAppVO.getInappSaveFile();
			this.iconSaveFile = inAppVO.getIconSaveFile();
			this.inappSize = inAppVO.getInappSize();
		}

		
		if(inappcategoryVO != null){
			this.categorySeq = inappcategoryVO.getCategorySeq();
			this.categoryName = inappcategoryVO.getCategoryName();
			this.categoryParent = inappcategoryVO.getCategoryParent();
			this.categoryDepth = inappcategoryVO.getDepth();
		}
		this.captureUrlList = new ArrayList();
		if(captureList != null){
			for( int i =0; i< captureList.size() ; i++){
				captureUrlList.add( basicURL + captureURL+  captureList.get(i).getImgSaveFile());
			}
		}
		

	}
	
	public Integer getInappSeq() {
		return inappSeq;
	}
	public void setInappSeq(Integer inappSeq) {
		this.inappSeq = inappSeq;
	}
	public String getInappName() {
		return inappName;
	}
	public void setInappName(String inappName) {
		this.inappName = inappName;
	}
	public String getScreenType() {
		return screenType;
	}
	public void setScreenType(String screenType) {
		this.screenType = screenType;
	}
	public String getVerNum() {
		return verNum;
	}
	public void setVerNum(String verNum) {
		this.verNum = verNum;
	}
	public String getDescriptionText() {
		return descriptionText;
	}
	public void setDescriptionText(String descriptionText) {
		this.descriptionText = descriptionText;
	}
	public String getInappUrl() {
		return inappUrl;
	}
	public void setInappUrl(String inappUrl) {
		this.inappUrl = inappUrl;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getRegDt() {
		return regDt;
	}
	public void setRegDt(String regDt) {
		this.regDt = regDt;
	}

	public String getChgDt() {
		return chgDt;
	}

	public void setChgDt(String chgDt) {
		this.chgDt = chgDt;
	}

	public Integer getCategorySeq() {
		return categorySeq;
	}
	public void setCategorySeq(Integer categorySeq) {
		this.categorySeq = categorySeq;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public Integer getCategoryParent() {
		return categoryParent;
	}
	public void setCategoryParent(Integer categoryParent) {
		this.categoryParent = categoryParent;
	}
	public String getCategoryDepth() {
		return categoryDepth;
	}
	public void setCategoryDepth(String categoryDepth) {
		this.categoryDepth = categoryDepth;
	}
	public List<String> getCaptureUrlList() {
		return captureUrlList;
	}
	public void setCaptureUrlList(List<String> captureUrlList) {
		this.captureUrlList = captureUrlList;
	}

	public String getInappSaveFile() {
		return inappSaveFile;
	}

	public void setInappSaveFile(String inappSaveFile) {
		this.inappSaveFile = inappSaveFile;
	}

	public String getIconSaveFile() {
		return iconSaveFile;
	}

	public void setIconSaveFile(String iconSaveFile) {
		this.iconSaveFile = iconSaveFile;
	}

	public String getInappSize() {
		return inappSize;
	}

	public void setInappSize(String inappSize) {
		this.inappSize = inappSize;
	}
	
	
}
