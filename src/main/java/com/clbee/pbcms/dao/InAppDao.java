package com.clbee.pbcms.dao;

import java.util.List;

import com.clbee.pbcms.vo.AppSubVO;
import com.clbee.pbcms.vo.InAppList;
import com.clbee.pbcms.vo.InappMetaVO;
import com.clbee.pbcms.vo.InappSubVO;
import com.clbee.pbcms.vo.InappVO;
import com.clbee.pbcms.vo.MemberVO;


public interface InAppDao {
	InappVO findByCustomInfo( String DBName, int intValue);
	InappVO findByCustomInfo( String DBName, String Value);
	List<InappVO> getListInappVO(String DBName, String storeBundleId, int userSeq);
	List<InappVO> getListInappVO(String DBName, String value);
	int getListCntByBundleId(InappVO vo, InAppList inAppList, MemberVO memberVO);
	List<InappVO> getListByBundleId(InappVO vo, InAppList inAppList, MemberVO memberVO);
	int getSeqAfterInsertAppInfo(InappVO vo);
	InappVO selectForUpdate(InappVO ivo, MemberVO memberVO);
	void updateInAppInfo(InappVO ivo, int inappSeq);
	Object[] getListInAppForRelatedApp( String appSeq );
	List findListByCustomInfo(String DBName, String value);
	List findListByCustomInfo(String DBName, int value);
	List<InappSubVO> selectInAppSubList ( int inAppSeq );
	int insertInAppSubInfo ( InappSubVO inAppSubVO );
	void deleteInAppSubInfo ( InappSubVO inAppSubVO );
	void deleteInAppInfo( String storeBundleId);
	boolean checkInappNameIfExist(String InappName, String storeBundleId);
	List<InappVO> getListInappIsAvailableByStoreBundleId ( String storeBundleId );
	int insertInAppMetaInfo ( InappMetaVO inappMetaVO );
	InappMetaVO findByCustomInfoForMetaVO( String DBName, int intValue);
	InappMetaVO findByCustomInfoForMetaVO( String DBName, String value);
	void updateInAppMetaInfo(InappMetaVO updatedVO, int inappMetaSeq);
}

