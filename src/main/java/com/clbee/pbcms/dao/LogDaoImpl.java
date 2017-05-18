package com.clbee.pbcms.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.clbee.pbcms.Json.SpaceObject;
import com.clbee.pbcms.vo.LogVO;


@Repository
public class LogDaoImpl implements LogDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public int insertLogInfo(LogVO logVO) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		
		try {
			tx = session.beginTransaction();
			
			if("".equals(logVO.getInappSeq())){
				logVO.setInappSeq(null);
			}
			
			session.save(logVO);
			
			tx.commit();
		}catch (Exception e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();	
		}finally {
			session.close();
		}
		
		return logVO.getLogSeq();
	}
	
	@Override
	public Object selectLogInfo( String storeBundleId, String inappSeq, Integer userSeq, String pageGb, String dataGb) {
		// TODO Auto-generated method stub

		Session session = sessionFactory.openSession();
		Transaction tx = null;
		List<LogVO> result = null;
		try {
			tx = session.beginTransaction();
	
			Criteria cr = session.createCriteria(LogVO.class);
	
			cr.add(
				Restrictions.and(
					Restrictions.eq("storeBundleId", storeBundleId),
					Restrictions.eq("regUserSeq", userSeq)
				)
			);
	
			if( inappSeq != null && !"".equals(inappSeq))
			cr.add( Restrictions.eq("inappSeq" , inappSeq));
			if( pageGb != null && !"".equals(pageGb))
			cr.add( Restrictions.eq("pageGb", pageGb));
			if( dataGb != null && !"".equals(dataGb))
			cr.add( Restrictions.eq("dataGb", dataGb));
			
	
			cr.addOrder(Order.desc("regDt"));
			
			
			result = cr.list();
			tx.commit();
		}catch (Exception e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();	
		}finally {
			session.close();
		}
		
		if(result != null && result.size() > 0)
		return result.get(0);
		else return new SpaceObject();
	}

	@Override
	public List<LogVO> selectLogList( int startNo ) {
		// TODO Auto-generated method stub
		
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		List<LogVO> result = null;
		try {
			tx = session.beginTransaction();
			
			Criteria cr = session.createCriteria(LogVO.class);

			cr.setFirstResult(startNo)
				.setMaxResults(10)
				.addOrder(Order.desc("regDt"));
			
			
			result = cr.list();
			tx.commit();
		}catch (Exception e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();	
		}finally {
			session.close();
		}
		return result;
	}

	@Override
	public int selectLogListCount() {
		// TODO Auto-generated method stub
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		List<LogVO> result = null;
		try {
			tx = session.beginTransaction();
			
			Criteria cr = session.createCriteria(LogVO.class);

			cr.addOrder(Order.desc("regDt"));
			
			
			result = cr.list();
			tx.commit();
		}catch (Exception e) {
			if(tx != null) tx.rollback();
			e.printStackTrace();	
		}finally {
			session.close();
		}
		return result.size();

	}
}
