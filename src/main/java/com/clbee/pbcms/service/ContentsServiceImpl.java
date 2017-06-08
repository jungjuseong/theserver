package com.clbee.pbcms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.clbee.pbcms.dao.ContentsDao;
import com.clbee.pbcms.vo.AppList;
import com.clbee.pbcms.vo.AppVO;
import com.clbee.pbcms.vo.ContentList;
import com.clbee.pbcms.vo.ContentVO;
import com.clbee.pbcms.vo.ContentsappSubVO;
import com.clbee.pbcms.vo.MemberList;
import com.clbee.pbcms.vo.MemberVO;


@Service
public class ContentsServiceImpl implements ContentsService {
	@Autowired
	ContentsDao contentDao;

	@Override
	public int insertContentInfo(ContentVO contentVO ) {
		return contentDao.insertContentInfo( contentVO );
	}
	
	@Override
	public int insertContentsappSubInfo(ContentsappSubVO contentsappSubVO) {
		// TODO Auto-generated method stub
		return contentDao.insertContentsappSubInfo( contentsappSubVO );
	}
	@Override
	public int updateNullableContentInfo(ContentVO updatedVO) {
		
		return contentDao.updateNullableContentInfo(updatedVO);
	}

	@Override
	public int updateContentsappSubInfo ( ContentsappSubVO contentappSubVO, int contentsappSubSeq ){

		return contentDao.updateContentsappSubInfo ( contentappSubVO, contentsappSubSeq);
	}
	
	@Override
	public int updateContentInfo(ContentVO content, int contentID) {
		// TODO Auto-generated method stub
		
		return contentDao.updateContentInfo( content, contentID );
	}
	
	@Override
	public ContentList getListContents( int currentPage, int maxResult, String[] sort,  String searchSeq, Integer valueSeq, String searchType, String searchValue, boolean isMember ) {

		ContentList list = null;
		int pageSize = 10;
		int totalCount = 0;
		int startNo = 0;
			try{
				totalCount = contentDao.getListContentsCount( searchSeq, valueSeq, sort,  searchType, searchValue, isMember );
				System.out.println("totalCount = " + totalCount);
				
				list = new ContentList(pageSize, totalCount, currentPage, maxResult);
			
				startNo = (currentPage-1) *maxResult;
	
				List<ContentVO> vo = contentDao.getListContents(startNo, maxResult, sort, searchSeq, valueSeq,  searchType, searchValue, isMember);
				
				list.setList(vo);
				
				System.out.println("[ListService] - selectList method");
				System.out.println("selectList[] " + vo.size());
				System.out.println(vo.size());
			}catch(Exception e){
				System.out.println("����");
				e.printStackTrace();
			}
		return list;
	}


	@Override
	public ContentVO selectByContentId(int contentID) {
		// TODO Auto-generated method stub
		return contentDao.selectByContentId(contentID);
	}


	@Override
	public ContentVO selectByUltimateCondition(ContentVO cvo) {
		// TODO Auto-generated method stub
		return contentDao.selectByUltimateCondition(cvo);
	}


	@Override
	public List<ContentVO> getListByCustomInfo(String DBName, String value) {
		// TODO Auto-generated method stub
		return contentDao.getListByCustomInfo(DBName, value);
	}


	@Override
	public List<ContentVO> getListByCustomInfo(String DBName, int value) {
		// TODO Auto-generated method stub
		return contentDao.getListByCustomInfo(DBName, value);
	}

	@Override
	public void deleteContentsInfo( int contentsSeq ){
		// TODO Auto-generated method stub
		contentDao.deleteContentsInfo(contentsSeq);
	}

}
