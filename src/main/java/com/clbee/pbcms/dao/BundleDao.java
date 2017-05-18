package com.clbee.pbcms.dao;

import java.util.List;


import com.clbee.pbcms.vo.BundleVO;
import com.clbee.pbcms.vo.ProvisionVO;;

public interface BundleDao {
/*	void insert( ProvisionVO vo );
	int update( ProvisionVO vo );
	List<ProvisionVO> selectList(int regUserSeq, int regCompanySeq);
	int getListCount();
	void delete(int provSeq, int regUserSeq, int regCompanySeq);
	ProvisionVO selectRow(int provSeq, int regUserSeq, int regCompanySeq) throws Exception;
	List selectList(ProvisionVO vo);
*/
	void insert(BundleVO vo);
	void deleteByAppSeq(int seq);
	List listByAppSeq(Integer appSeq);
	int getListCount(BundleVO vo, int companySeqe);
}
