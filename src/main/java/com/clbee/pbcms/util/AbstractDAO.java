package com.clbee.pbcms.util;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractDAO extends SqlSessionDaoSupport {
	
	protected Logger log = LoggerFactory.getLogger(super.getClass());
	
	@Inject
	@Named("sqlSessionTemplate")
	private SqlSession sqlSession;
	
	@Inject
	@Named("sqlSessionTemplateForReadWrite")
	private SqlSession sqlSessionForReadWrite;

//	@Resource(name = "sqlSessionFactory")
//	public void setSuperSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
//		super.setSqlSessionFactory(sqlSessionFactory);
//	}

	public int insert(String queryId, Object parameterObject) {
		return sqlSessionForReadWrite.insert(queryId, parameterObject);
	}

	public int update(String queryId, Object parameterObject) {
		return sqlSessionForReadWrite.update(queryId, parameterObject);
	}

	public int delete(String queryId, Object parameterObject) {
		return sqlSessionForReadWrite.delete(queryId, parameterObject);
	}

	public Object selectOne(String queryId, Object parameterObject) {
		return sqlSession.selectOne(queryId, parameterObject);
	}

	public List list(String queryId, Object parameterObject) {
		return sqlSession.selectList(queryId, parameterObject);
	}

	// RowBounds �̿��� ����¡�� ū �����Ϳ��� �������� �����Ƿ� �Ⱦ��°� ����
	public List listWithPaging(String queryId, Object parameterObject, int pageIndex, int pageSize) {
		int skipResults = pageIndex * pageSize;
		int maxResults = pageIndex * pageSize + pageSize;
		return sqlSession.selectList(queryId, parameterObject,
				new RowBounds(skipResults, maxResults));
	}
}
