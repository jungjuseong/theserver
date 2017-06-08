package com.clbee.pbcms.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.clbee.pbcms.util.AbstractDAO;
import com.clbee.pbcms.vo.AppList;
import com.clbee.pbcms.vo.AppVO;
import com.clbee.pbcms.vo.ContentList;
import com.clbee.pbcms.vo.ContentVO;
import com.clbee.pbcms.vo.ContentsappSubVO;
import com.clbee.pbcms.vo.InappVO;
import com.clbee.pbcms.vo.InappcategoryVO;
import com.clbee.pbcms.vo.MemberVO;

@Repository
public class ContentsDaoImpl extends AbstractDAO implements ContentsDao {

	@Autowired
	private SessionFactory sessionFactory;
	

	
	@Override
	public int insertContentInfo(ContentVO contentVO){
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			session.save(contentVO);
			
			tx.commit();
		}catch (Exception e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();	
		}finally {
			session.close();
		}
		
		return contentVO.getContentsSeq();
	}

	@Override
	public int insertContentsappSubInfo(ContentsappSubVO contentappSubVO) {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			session.save(contentappSubVO);
			
			tx.commit();
		}catch (Exception e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();	
		}finally {
			session.close();
		}
		
		return contentappSubVO.getContentsappSubSeq();
	}

	@Override
	public int updateNullableContentInfo(ContentVO updatedVO) {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			ContentVO contentVO = (ContentVO)session.get(ContentVO.class, updatedVO.getContentsSeq());
			session.update(contentVO);
			tx.commit();
			
			return 1;
		}catch (Exception e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();	
			return 0;
		}finally {
			session.close();
		}
	}
	
	@Override
	public int updateContentsappSubInfo(ContentsappSubVO updatedVO, int contentsappSubSeq) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = null;
				
		try {
			tx = session.beginTransaction();
			ContentsappSubVO contentsappSubVO = (ContentsappSubVO)session.get(ContentsappSubVO.class, contentsappSubSeq);
		
						
						
			if(updatedVO.getContentsappSubSeq() != null && !"".equals(updatedVO.getContentsappSubSeq()))
				contentsappSubVO.setContentsappSubSeq(updatedVO.getContentsappSubSeq());
			if(updatedVO.getContentsSeq() != null && !"".equals(updatedVO.getContentsSeq()))
				contentsappSubVO.setContentsSeq(updatedVO.getContentsSeq());
			if(updatedVO.getInappSeq() != null && !"".equals(updatedVO.getInappSeq()))
				contentsappSubVO.setInappSeq(updatedVO.getInappSeq());
			if(updatedVO.getStoreBundleId() != null && !"".equals(updatedVO.getStoreBundleId()))
				contentsappSubVO.setStoreBundleId(updatedVO.getStoreBundleId());	


					
			session.update(contentsappSubVO);
			tx.commit();
			return 1;
		}catch (Exception e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();	
			return 0;
		}finally {
			session.close();
		}
	}


	
	
	@Override
	public int updateContentInfo(ContentVO updatedVO, int contentSeq) {

			// TODO Auto-generated method stub
			Session session = sessionFactory.openSession();
			Transaction tx = null;
			
			
			try {
				tx = session.beginTransaction();
	
				ContentVO contentVO = (ContentVO)session.get(ContentVO.class, contentSeq);
	
				
				
				if(updatedVO.getAppName() != null && !"".equals(updatedVO.getAppName()))
					contentVO.setAppName(updatedVO.getAppName());
				if(updatedVO.getAppType() != null && !"".equals(updatedVO.getAppType()))
					contentVO.setAppType(updatedVO.getAppType());
				if(updatedVO.getChgDt() != null)
					contentVO.setChgDt(updatedVO.getChgDt());
				if(updatedVO.getChgUserGb() != null && !"".equals(updatedVO.getChgUserGb()))
					contentVO.setChgUserGb(updatedVO.getChgUserGb());	
				if(updatedVO.getChgUserId() != null && !"".equals(updatedVO.getChgUserId()))
					contentVO.setChgUserId(updatedVO.getChgUserId());
				if(updatedVO.getChgUserSeq() != null && updatedVO.getChgUserSeq() != 0)
					contentVO.setChgUserSeq(updatedVO.getChgUserSeq());	
				if(updatedVO.getCompanySeq() != null && updatedVO.getCompanySeq() != 0 ) 
					contentVO.setCompanySeq(updatedVO.getCompanySeq());
	
				
				if(updatedVO.getCompletGb() != null && !"".equals(updatedVO.getCompletGb()))
					contentVO.setCompletGb(updatedVO.getCompletGb());	
				if(updatedVO.getContentsName() != null && !"".equals(updatedVO.getContentsName()))
					contentVO.setContentsName(updatedVO.getContentsName());	
				if(updatedVO.getContentsSize() != null && !"".equals(updatedVO.getContentsSize()))
					contentVO.setContentsSize(updatedVO.getContentsSize());	
				if(updatedVO.getContentsUrl() != null  && !"".equals(updatedVO.getContentsUrl()))
					contentVO.setContentsUrl(updatedVO.getContentsUrl());	
				if(updatedVO.getContentsType() != null && !"".equals(updatedVO.getContentsType()))
					contentVO.setContentsType(updatedVO.getContentsType());	
	
				if(updatedVO.getCouponGb() != null && !"".equals(updatedVO.getCouponGb()))
					contentVO.setCouponGb(updatedVO.getCouponGb());
				if(updatedVO.getCouponNum() != null && !"".equals(updatedVO.getCouponNum()))
					contentVO.setCouponNum(updatedVO.getCouponNum());
				if(updatedVO.getDescriptionText() != null && !"".equals(updatedVO.getDescriptionText()))
					contentVO.setDescriptionText(updatedVO.getDescriptionText());										
				if(updatedVO.getDistrGb() != null && !"".equals(updatedVO.getDistrGb()))
					contentVO.setDistrGb(updatedVO.getDistrGb());
				if(updatedVO.getLimitDt() != null )
					contentVO.setLimitDt(updatedVO.getLimitDt());
				if(updatedVO.getLimitGb() != null && !"".equals(updatedVO.getLimitGb()))
					contentVO.setLimitGb(updatedVO.getLimitGb());
				if(updatedVO.getMemDownAmt() != null && !"".equals(updatedVO.getMemDownAmt()))
					contentVO.setMemDownAmt(updatedVO.getMemDownAmt());	
				if(updatedVO.getMemDownCnt() != null && !"".equals(updatedVO.getMemDownCnt()))
					contentVO.setMemDownCnt(updatedVO.getMemDownCnt());	
				if(updatedVO.getMemDownEndDt() != null )
					contentVO.setMemDownEndDt(updatedVO.getMemDownEndDt());	
				if(updatedVO.getMemDownGb() != null  && !"".equals(updatedVO.getMemDownGb()))
					contentVO.setMemDownGb(updatedVO.getMemDownGb());	
				if(updatedVO.getMemDownStartDt() != null )
					contentVO.setMemDownStartDt(updatedVO.getMemDownStartDt());	
				
				if(updatedVO.getNonmemDownAmt() != null  && !"".equals(updatedVO.getNonmemDownAmt())) 
					contentVO.setNonmemDownAmt(updatedVO.getNonmemDownAmt());
				if(updatedVO.getNonmemDownCnt() != null && !"".equals(updatedVO.getNonmemDownCnt()))
					contentVO.setNonmemDownCnt(updatedVO.getNonmemDownCnt());
				if(updatedVO.getNonmemDownEndDt() != null)
					contentVO.setNonmemDownEndDt(updatedVO.getNonmemDownEndDt());
				if(updatedVO.getNonmemDownGb() != null && !"".equals(updatedVO.getNonmemDownGb()))
					contentVO.setNonmemDownGb(updatedVO.getNonmemDownGb());
				if(updatedVO.getNonmemDownStarDt() != null)
					contentVO.setNonmemDownStarDt(updatedVO.getNonmemDownStarDt());
				if(updatedVO.getRegDt() != null  )
					contentVO.setRegDt(updatedVO.getRegDt());
				if(updatedVO.getRegUserGb() != null && !"".equals(updatedVO.getRegUserGb()))
					contentVO.setRegUserGb(updatedVO.getRegUserGb());	
				if(updatedVO.getRegUserId() != null && !"".equals(updatedVO.getRegUserId()))
					contentVO.setRegUserId(updatedVO.getRegUserId());	
				if(updatedVO.getRegUserSeq() != null && updatedVO.getRegUserSeq() != 0)
					contentVO.setRegUserSeq(updatedVO.getRegUserSeq());	
				if(updatedVO.getRegUserSeq() != null  && !"".equals(updatedVO.getRegUserSeq()))
					contentVO.setRegUserSeq(updatedVO.getRegUserSeq());	
				if(updatedVO.getUploadOrgFile() != null && !"".equals(updatedVO.getUploadOrgFile()))
					contentVO.setUploadOrgFile(updatedVO.getUploadOrgFile());	
				if(updatedVO.getUploadSaveFile() != null && !"".equals(updatedVO.getUploadSaveFile()))
					contentVO.setUploadSaveFile(updatedVO.getUploadSaveFile());	
				if(updatedVO.getUseAvailDt() != null )
					contentVO.setUseAvailDt(updatedVO.getUseAvailDt());	
				if(updatedVO.getUseDisableDt() != null )
					contentVO.setUseDisableDt(updatedVO.getUseDisableDt());	
				if(updatedVO.getUseGb() != null && !"".equals(updatedVO.getUseGb()))
					contentVO.setUseGb(updatedVO.getUseGb());	
				if(updatedVO.getVerNum() != null && !"".equals(updatedVO.getVerNum()))
					contentVO.setVerNum(updatedVO.getVerNum());	

			
				session.update(contentVO);
				tx.commit();
				return 1;
			}catch (Exception e) {
				if(tx != null) tx.rollback();
				e.printStackTrace();	
				return 0;
			}finally {
				session.close();
			}
	}


	@Override
	public ContentVO selectByContentId(int contentID) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		ContentVO contentVO = null;
		
		try {
			tx = session.beginTransaction();
					
			Criteria cr = session.createCriteria(ContentVO.class);
			cr.add(Restrictions.eq("contentsSeq", contentID));
			contentVO = (ContentVO) cr.uniqueResult();
					
			tx.commit();
		}catch (Exception e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();	
		}finally {
			session.close();
		}

		return contentVO;
	}

	
	@Override
	public List<ContentVO> getListContents( int startNo, int MaxResult,String[] sort, String searchSeq, Integer valueSeq, String searchType, String searchValue, boolean isMember) {

		Session session = sessionFactory.openSession();
		Transaction tx = null;
		List<ContentVO> list = null;
		
		try {
			tx = session.beginTransaction();
			
	
			Criteria cr = session.createCriteria(ContentVO.class);
			Disjunction orCondition = Restrictions.or();
			if(isMember == true)
			cr.add(Restrictions.eq(searchSeq, valueSeq));/* 媛쒖씤�쉶�썝怨� 湲곗뾽 �쉶�썝�씪�븣 search Seq
			 											  * �삁瑜쇰뱾�뼱 湲곗뾽�쉶�썝�씪寃쎌슦 searchSeq (DBName�� compaynSeq媛� �뱾�뼱媛꾨떎 )
			 											  * 媛쒖씤�쉶�썝�씪 寃쎌슦 searchSeq �뒗 userSeq媛� �뱾�뼱媛꾨떎.
			 											  **/
			cr.setMaxResults(MaxResult);
			cr.setFirstResult(startNo);
			cr.addOrder(Order.desc("regDt"));
			
			if("contentsName".equals(searchType) && !"".equals(searchValue) && searchValue != null) {
				/* != null �씠 �븘�땶議곌굔��,, 肄섑뀗痢좊뒗 肄섑뀗痢� �씠由꾩쑝濡쒕쭔 寃��깋�븯湲� �븣臾몄엯�땲�떎. */
				System.out.println("searchType = Executed as " + searchType);
				System.out.println("searchValue = Executed as " + searchValue);
				cr.add(Restrictions.like(searchType, "%"+searchValue+"%"));
			}
				
				
			if( sort.length == 0 || sort[0] == null||  "1".equals(sort[0])){
			}else{
				for( int i =0; i< sort.length; i++){
					System.out.println("[DAO] ForLoop = " + i );
					System.out.println("[DAO] SORT realValue = " + String.valueOf(Integer.parseInt(sort[i])-1) );
					orCondition.add(Restrictions.eq("contentsType", String.valueOf(Integer.parseInt(sort[i])-1)));
				}
			}
	
			cr.add(orCondition);
			list = cr.list();
			
			tx.commit();
		}catch (Exception e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();	
		}finally {
			session.close();
		}
		
		return list;
	}
	
	@Override
	public int getListContentsCount( String searchSeq, Integer valueSeq, String[] sort, String searchType, String searchValue, boolean isMember) {
		// TODO Auto-generated method stub

			
		
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		List<ContentVO> list = null;
		
		try {
			tx = session.beginTransaction();
			
			Disjunction orCondition = Restrictions.or();;
			Criteria cr = session.createCriteria(ContentVO.class);
			if(isMember == true)
			cr.add(Restrictions.eq(searchSeq, valueSeq));/* 媛쒖씤�쉶�썝怨� 湲곗뾽 �쉶�썝�씪�븣 search Seq
			 											  * �삁瑜쇰뱾�뼱 湲곗뾽�쉶�썝�씪寃쎌슦 searchSeq (DBName�� compaynSeq媛� �뱾�뼱媛꾨떎 )
			 											  * 媛쒖씤�쉶�썝�씪 寃쎌슦 searchSeq �뒗 userSeq媛� �뱾�뼱媛꾨떎.
			 											  **/
	
			if("contentsName".equals(searchType) && !"".equals(searchValue) && searchValue != null) {
				/* != null �씠 �븘�땶議곌굔��,, 肄섑뀗痢좊뒗 肄섑뀗痢� �씠由꾩쑝濡쒕쭔 寃��깋�븯湲� �븣臾몄엯�땲�떎. */
				cr.add(
						Restrictions.like(searchType, "%"+searchValue+"%"));
			}
				
			
			if( sort.length == 0 || sort[0] == null || "1".equals(sort[0])){
			}else{
				for( int i =0; i< sort.length; i++){
					orCondition.add(Restrictions.eq("contentsType", String.valueOf(Integer.parseInt(sort[i])-1)));
				}
			}
			cr.add(orCondition);
			list = cr.list();
			tx.commit();
		}catch (Exception e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();	
		}finally {
			session.close();
		}

		return list.size();
	}


	@Override
	public ContentVO selectByUltimateCondition(ContentVO cvo) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		ContentVO contentVO = null;
		
		try {
			tx = session.beginTransaction();
					
			Criteria cr = session.createCriteria(ContentVO.class);
			cr.add(Restrictions.eq("contentsSeq", cvo.getContentsSeq()));
			if(cvo.getCouponGb()!=null&&!"".equals(cvo.getCouponGb())&&"1".equals(cvo.getCouponGb())&&cvo.getCouponNum()!=null&&!"".equals(cvo.getCouponNum())){

			}else{
	
			}
			
			contentVO = (ContentVO) cr.uniqueResult();
			tx.commit();
		}catch (Exception e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();	
		}finally {
			session.close();
		}

		return contentVO;
	}


	@Override
	public List<ContentVO> getListByCustomInfo(String DBName, String value) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		List list = null;
		try {
			tx = session.beginTransaction();
			Criteria cr = session.createCriteria(ContentVO.class);
			
			cr.add(Restrictions.eq(DBName, value));
			
			list =  cr.list();
			tx.commit();
		}catch (Exception e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();	
		}finally {
			session.close();
		}
		
		return list;
	}


	@Override
	public List<ContentVO> getListByCustomInfo(String DBName, int value) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		List list = null;
		
		try {
			tx = session.beginTransaction();
			Criteria cr = session.createCriteria(ContentVO.class);
			
			cr.add(Restrictions.eq(DBName, value));
			
			list =  cr.list();
			tx.commit();
		}catch (Exception e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();	
		}finally {
			session.close();
		}
		
		return list;
	}
	
	@Override
	public void deleteContentsInfo(int contentsSeq){
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			
			Criteria cr = session.createCriteria(ContentVO.class);
			cr.add(
				Restrictions.eq("contentsSeq", contentsSeq)
			);
			ContentVO contentVO = (ContentVO)cr.uniqueResult();
			session.delete(contentVO);
			
			tx.commit();
		}catch (Exception e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();	
		}finally {
			session.close();
		}
	}


}
