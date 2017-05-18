package com.clbee.pbcms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clbee.pbcms.dao.LogDao;
import com.clbee.pbcms.vo.LogList;
import com.clbee.pbcms.vo.LogVO;
import com.clbee.pbcms.vo.NoticeList;
import com.clbee.pbcms.vo.NoticeVO;

@Service
public class LogServiceImpl implements LogService {

	
	@Autowired
	LogDao logDao;
	
	@Override
	public int insertLogInfo(LogVO logVO) {
		// TODO Auto-generated method stub
		return logDao.insertLogInfo(logVO);
	}

	@Override
	public Object selectLogInfo( String storeBundleId, String inappSeq, Integer userSeq, String pageGb, String dataGb) {
		// TODO Auto-generated method stub
		return logDao.selectLogInfo(storeBundleId, inappSeq, userSeq, pageGb, dataGb);
	}

	@Override
	public LogList selectLogList( int currentPage ) {
		// TODO Auto-generated method stub
		LogList list = null;
		// ��ü ������ 1,2,3,4,5,6,7,8,9,19�ؿ� �Ķ���� ���ڰ� ǥ�õ� ����
		int pageSize = 10;
		// ���������� �ִ� �����õ� Ƚ��
		int maxResult = 10; 
		int totalCount = 0;
		int startNo = 0;
			try{
				totalCount = logDao.selectLogListCount();
				System.out.println("totalCount = " + totalCount);
					
				list = new LogList(pageSize, totalCount, currentPage, maxResult);
					
				startNo = (currentPage-1) *maxResult;
			
				List<LogVO> vo = logDao.selectLogList(startNo);
						
				list.setList(vo);

			}catch(Exception e){
				System.out.println("����");
				e.printStackTrace();
			}
		return list;
	}

}
