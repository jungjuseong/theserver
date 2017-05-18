package com.clbee.pbcms.dao;

import java.util.List;
















import com.clbee.pbcms.vo.ContentList;
import com.clbee.pbcms.vo.ContentVO;
import com.clbee.pbcms.vo.ContentsappSubVO;
import com.clbee.pbcms.vo.MemberVO;

public interface ContentsDao {
	int insertContentInfo( ContentVO contentVO);
	int insertContentsappSubInfo ( ContentsappSubVO contentappSubVO);
	int updateNullableContentInfo(ContentVO updatedVO);
	int updateContentInfo( ContentVO updatedVO, int contentID);
	int updateContentsappSubInfo(ContentsappSubVO updatedVO, int contentsappSubSeq);
	ContentVO selectByContentId( int contentID );
	List<ContentVO> getListContents( int startNo, int MaxResult, String[] sort, String searchSeq, Integer valueSeq, String searchType, String searchValue, boolean isMember);
	int getListContentsCount( String searchSeq, Integer valueSeq, String[] sort, String searchType, String searchValue, boolean isMember);
	/*List<ContentVO> getListContentOfCheckBox(int startNo, int MaxResult, String[] sort, String searchSeq, Integer valueSeq, String searchType, String searchValue);*/
	/*int getListContentsCountOfCheckBox( String searchSeq, Integer valueSeq, String[] sort, String searchType, String searchValue);*/
	ContentVO selectByUltimateCondition(ContentVO cvo);
	List<ContentVO> getListByCustomInfo(String DBName, String value);
	List<ContentVO> getListByCustomInfo(String DBName, int value);
}
