package com.clbee.pbcms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clbee.pbcms.dao.InAppCategoryDao;
import com.clbee.pbcms.dao.InAppDao;
import com.clbee.pbcms.vo.AppVO;
import com.clbee.pbcms.vo.InAppList;
import com.clbee.pbcms.vo.InappMetaVO;
import com.clbee.pbcms.vo.InappSubVO;
import com.clbee.pbcms.vo.InappVO;
import com.clbee.pbcms.vo.MemberVO;


@Service
public class InAppServiceImpl implements InAppService {

	@Autowired InAppDao inAppDao;
	
	@Override
	public InappVO findByCustomInfo(String DBName, int intValue) {
		// TODO Auto-generated method stub
		return inAppDao.findByCustomInfo(DBName, intValue);
	}

	@Override
	public InappVO findByCustomInfo(String DBName, String Value) {
		// TODO Auto-generated method stub
		return inAppDao.findByCustomInfo(DBName, Value);
	}

	@Override
	public List<InappVO> getListInappVO(String DBName, String storeBundleId, int userSeq) {
		// TODO Auto-generated method stub
		return inAppDao.getListInappVO(DBName, storeBundleId, userSeq);
	}

	@Override
	public List<InappVO> getListInappVO(String DBName, String value) {
		// TODO Auto-generated method stub
		return inAppDao.getListInappVO(DBName, value);
	}

	@Override
	public InAppList getListByBundleId(InappVO vo, InAppList inAppList, MemberVO memberVO) {
		// TODO Auto-generated method stub
		//AppList list = null;
		int pageSize = 10;
		int maxResult = 10;
		int totalCount = 0;
		
		try{
			totalCount = inAppDao.getListCntByBundleId(vo, inAppList, memberVO);
			System.out.println("totalCount = " + totalCount);
			
			inAppList.calc(pageSize, totalCount, inAppList.getCurrentPage(), maxResult);		

			List<InappVO> list = inAppDao.getListByBundleId(vo, inAppList, memberVO);
			
			inAppList.setList(list);
			
			System.out.println("[ListService] - selectList method");
			System.out.println("selectList[] " + list.size());
			System.out.println(list.size());
			
		}catch(Exception e){
			System.out.println("¿¡·¯");
			e.printStackTrace();
		}
		return inAppList;
	}

	@Override
	public int getSeqAfterInsertInAppInfo(InappVO vo) {
		// TODO Auto-generated method stub
		return inAppDao.getSeqAfterInsertAppInfo(vo);
	}

	@Override
	public InappVO selectForUpdate(InappVO ivo, MemberVO memberVO) {
		// TODO Auto-generated method stub
		return inAppDao.selectForUpdate(ivo, memberVO);
	}

	@Override
	public void updateInAppInfo(InappVO ivo, int inappSeq) {
		// TODO Auto-generated method stub
		inAppDao.updateInAppInfo(ivo, inappSeq);
	}

	@Override
	public Object[] getListInAppForRelatedApp(String appSeq) {
		// TODO Auto-generated method stub
		return inAppDao.getListInAppForRelatedApp( appSeq );
	}

	@Override
	public List findListByCustomInfo(String DBName, String value) {
		// TODO Auto-generated method stub
		return inAppDao.findListByCustomInfo(DBName, value);
	}

	@Override
	public List findListByCustomInfo(String DBName, int value) {
		// TODO Auto-generated method stub
		return inAppDao.findListByCustomInfo(DBName, value);
	}

	@Override
	public List<InappSubVO> selectInAppSubList(int inAppSeq) {
		// TODO Auto-generated method stub
		return inAppDao.selectInAppSubList(inAppSeq);
	}

	@Override
	public int insertInAppSubInfo(InappSubVO inAppSubVO) {
		// TODO Auto-generated method stub
		return inAppDao.insertInAppSubInfo(inAppSubVO);
	}

	@Override
	public void deleteInAppSubInfo(InappSubVO inAppSubVO) {
		// TODO Auto-generated method stub
		inAppDao.deleteInAppSubInfo(inAppSubVO);
	}

	@Override
	public boolean checkInappNameIfExist(String InappName, String storeBundleId) {
		// TODO Auto-generated method stub
		return inAppDao.checkInappNameIfExist(InappName, storeBundleId);
	}

	@Override
	public List<InappVO> getListInappIsAvailableByStoreBundleId (String storeBundleId) {
		// TODO Auto-generated method stub
		return inAppDao.getListInappIsAvailableByStoreBundleId (storeBundleId);
	}

	@Override
	public int insertInAppMetaInfo(InappMetaVO inappMetaVO) {
		// TODO Auto-generated method stub
		return inAppDao.insertInAppMetaInfo(inappMetaVO);
	}

	@Override
	public InappMetaVO findByCustomInfoForMetaVO(String DBName, int intValue) {
		// TODO Auto-generated method stub
		return inAppDao.findByCustomInfoForMetaVO(DBName, intValue);
	}

	@Override
	public InappMetaVO findByCustomInfoForMetaVO(String DBName, String value) {
		// TODO Auto-generated method stub
		return inAppDao.findByCustomInfoForMetaVO(DBName, value);
	}

	@Override
	public void updateInAppMetaInfo(InappMetaVO updatedVO, int inappMetaSeq) {
		// TODO Auto-generated method stub
		inAppDao.updateInAppMetaInfo(updatedVO, inappMetaSeq);
	}

	@Override
	public void deleteInAppInfo( String storeBundleId ) {
		// TODO Auto-generated method stub
		inAppDao.deleteInAppInfo(storeBundleId);
	}
}
