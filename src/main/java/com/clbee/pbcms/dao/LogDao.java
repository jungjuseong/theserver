package com.clbee.pbcms.dao;

import java.util.List;

import com.clbee.pbcms.vo.LogVO;

public interface LogDao {
	int insertLogInfo(LogVO logVO);
	Object selectLogInfo( String storeBundleId, String inappSeq, Integer userSeq, String pageGb, String dataGb);
	List<LogVO> selectLogList( int startNo );
	int selectLogListCount();
}
