package com.clbee.pbcms.service;

import java.util.HashMap;
import java.util.List;

import com.clbee.pbcms.vo.AppList;
import com.clbee.pbcms.vo.ContentList;
import com.clbee.pbcms.vo.ContentVO;
import com.clbee.pbcms.vo.ContentsappSubVO;

public interface ContentsService {
	int insertContentInfo( ContentVO content );
	int insertContentsappSubInfo( ContentsappSubVO contentsappSubVO );
	int updateNullableContentInfo(ContentVO updatedVO);
	int updateContentInfo( ContentVO content, int contentID );
	int updateContentsappSubInfo ( ContentsappSubVO contentappSubVO, int contentsappSubSeq );
	
	ContentList getListContents( int currentPage, int maxResult, String[] sort,  String searchSeq, Integer valueSeq, String searchType, String searchValue, boolean isMember );
	ContentVO selectByContentId( int contentID );
	/*ContentList getListContentOfCheckBox( int currentPage, int maxResult, String[] sort, String searchSeq, Integer valueSeq, String searchType, String searchValue );*/
	ContentVO selectByUltimateCondition(ContentVO cvo);
	List<ContentVO> getListByCustomInfo(String DBName, String value);
	List<ContentVO> getListByCustomInfo(String DBName, int value);
	
}
