package com.clbee.pbcms.service;

import java.util.List;

import com.clbee.pbcms.vo.LogList;
import com.clbee.pbcms.vo.LogVO;

public interface LogService {
	int insertLogInfo(LogVO logVO);
	Object selectLogInfo( String storeBundleId, String inappSeq, Integer userSeq, String pageGb, String dataGb);
	LogList selectLogList( int startNo);
}
