package com.clbee.pbcms.service;

import java.util.List;

import com.clbee.pbcms.vo.AppList;
import com.clbee.pbcms.vo.AppVO;
import com.clbee.pbcms.vo.BundleVO;
import com.clbee.pbcms.vo.ProvisionVO;

public interface BundleService {
/*
	int getListCount();
	List selectList(int regUserSeq, int regCompanySeq);
	void delete(int provSeq, int regUserSeq, int regCompanySeq);
	ProvisionVO selectRow(int provSeq, int regUserSeq, int regCompanySeq) throws Exception;
	int update(ProvisionVO vo);
	void insert(ProvisionVO vo);
	List selectList(ProvisionVO vo);*/
	
	void delete(int seq);
	void insert(BundleVO vo);
	List listByAppSeq(Integer appSeq);
	int getListCount(BundleVO vo, int companySeq);
}
