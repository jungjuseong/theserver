package com.clbee.pbcms.service;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.clbee.pbcms.dao.MemberDao;
import com.clbee.pbcms.vo.MemberList;
import com.clbee.pbcms.vo.MemberVO;

@Service
public class MemberServiceImpl implements MemberService/*, UserDetailsService*/ {
	
	@Autowired
	private MemberDao memberDao;

	@Override
	public void addMember( MemberVO m ) {
		memberDao.addMember( m );
	}
	

	@Override
	public int verifyIfExists( String DBName, String itSelf){
		return memberDao.selectItselfForExisting(DBName, itSelf);
	}

	
	
	@Override
	public int logInVerify( String username, String password ){
		

		
		List<MemberVO> appList = memberDao.logInVerify(username, password);

		Iterator iter = appList.iterator();
		
		
		if(iter.hasNext()) {

			MemberVO memberVO =  (MemberVO)iter.next();
			if("1".equals(memberVO.getUserStatus())){
				// 탈퇴
				return 3;
			}else if("2".equals(memberVO.getUserStatus())) {
				// 정지
				return 4;
			}else if("3".equals(memberVO.getUserStatus())) {
				// 강제 탈퇴
				return 5;
			}else if("5".equals(memberVO.getUserStatus())) {
				// 사용 대기
				return 6;
			}else if("4".equals(memberVO.getUserStatus())) {
			
				if(!"1".equals(memberVO.getUserGb())) {
					return 1;
				}else {
					if( "2".equals(memberVO.getDateGb()) ) {
						return 1;
					}else{
						try {
							SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy");
							DateFormat inputDF  = new SimpleDateFormat("MM/dd/yy");
							Date date = new Date();			
							Date formattedDate;
							
							formattedDate = inputDF.parse(format.format(date));
			
			
							Calendar cal = Calendar.getInstance();
							cal.setTime(formattedDate);
							
							int month = cal.get(Calendar.MONTH);
							int day = cal.get(Calendar.DAY_OF_MONTH);
							int year = cal.get(Calendar.YEAR);
			
							
							formattedDate = inputDF.parse(format.format(memberVO.getUserStartDt()));
							
							cal.setTime(formattedDate);
							int stMonth = cal.get(Calendar.MONTH);
							int stDay = cal.get(Calendar.DAY_OF_MONTH);
							int stYear = cal.get(Calendar.YEAR);
							
							formattedDate = inputDF.parse(format.format(memberVO.getUserEndDt()));
							
							cal.setTime(formattedDate);
							int endMonth = cal.get(Calendar.MONTH);
							int endDay = cal.get(Calendar.DAY_OF_MONTH);
							int endYear = cal.get(Calendar.YEAR);
							
			
	
							// 1. year(현재 년도)가 stYear보다 클경우 month와 day는 아무상관이없음
							// 2. year가 stYear와 같을경우 month를 비교함
							// 3. 이때 month가 stMonth보다 클경우 day는 아무상관이 없음
							// 4. month가 stMonth와 같을경우 stDay를 비교함
							// endYear도 1, 2, 3, 4 와 같음
							if(year > stYear || (year == stYear && (month > stMonth || (month == stMonth && day >= stDay)))){
								if(year < endYear || (year == endYear && (month < endMonth || (month == endMonth && day <= endDay)))){
									return 1;
								}else {
									return 2;
								}
							}else {
								return 2;
							}
							
							/*if((year >= stYear) && (month >= stMonth) && (day >= stDay)){
								if((year <= endYear) && (month <= endMonth) && (day <= endDay)){
									return 1;
								}else {
									return 2;
								}
							}else {
							}*/
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return -1;
							
						}
					}
					
				}
			}else {
				return -1;
			}
		}else {
			return 7;
		}
		/*list =  noticeDao.getListNoticeByCompany(companySeq, userSeq, storeBundleId);

		

		
		
		
		if(list!= null) {
			for(int i =0; i < list.size() ; i++) {
				formattedDate = inputDF.parse(format.format(list.get(i).getNoticeStartDt()));
				
				cal.setTime(formattedDate);
				int stMonth = cal.get(Calendar.MONTH);
				int stDay = cal.get(Calendar.DAY_OF_MONTH);
				int stYear = cal.get(Calendar.YEAR);
				
				formattedDate = inputDF.parse(format.format(list.get(i).getNoticeEndDt()));
				
				cal.setTime(formattedDate);
				int endMonth = cal.get(Calendar.MONTH);
				int endDay = cal.get(Calendar.DAY_OF_MONTH);
				int endYear = cal.get(Calendar.YEAR);
				

				if((year >= stYear) && (month >= stMonth) && (day >= stDay)){
					if((year <= endYear) && (month <= endMonth) && (day <= endDay)){
						addList.add(list.get(i));
					}else {
					}
				}else {
				}
			}
		}*/
	}

	@Override
	public MemberVO findByUserName(String username){
		return memberDao.findByUserName(username);
	}

	@Override
	public MemberVO findByCustomInfo(String DBName, String value){
		return memberDao.findByCustomInfo(DBName, value);
	}

	@Override
	public int updateMemberInfo( MemberVO m, int userNum){
		return memberDao.updateMemberInfo(m, userNum);
	}

	@Override
	public MemberVO selectMemberSuccessYn(MemberVO memberVO) {
		return memberDao.selectMemberSuccessYn(memberVO);
	}

	@Override
	public Integer selectMemberCount(MemberVO memberVO) {
		return memberDao.selectMemberCount(memberVO);
	}

	@Override
	public MemberVO selectMemberSuccessYn_(MemberVO memberVO) {
		return memberDao.selectMemberSuccessYn_(memberVO);
	}

	@Override
	public Integer selectMemberCount_(MemberVO memberVO) {
		return memberDao.selectMemberCount_(memberVO);
	}

	@Override
	public void updateMemberPw(MemberVO memberVO) {
		memberDao.updateMemberPw(memberVO);		
	}

	@Override
	public MemberList getListMember( int currentPage, int companySeq, int maxResult, String searchType, String searchValue, String isAvailable, boolean isMember ) {

		MemberList list = null;
		int pageSize = 10;
		int totalCount = 0;
		int startNo = 0;
			try{
				totalCount = memberDao.getListMemberCount(companySeq, searchType, searchValue, isAvailable, isMember);
				System.out.println("totalCount = " + totalCount);
				
				list = new MemberList(pageSize, totalCount, currentPage, maxResult);
			
				startNo = (currentPage-1) *maxResult;
	
				List<MemberVO> vo = memberDao.getListMember(startNo, companySeq, maxResult, searchType, searchValue, isAvailable, isMember);
				
				list.setList(vo);
				
				System.out.println("[ListService] - selectList method");
				System.out.println("selectList[] " + vo.size());
				System.out.println(vo.size());
			}catch(Exception e){
				System.out.println("에러");
				e.printStackTrace();
			}
		return list;
	}


	@Override
	public MemberVO findByCustomInfo(String DBName, int value) {
		return memberDao.findByCustomInfo(DBName, value);
	}


	@Override
	public String findCompanyMemberIdByCompanySeqAndUserGb(int companySeq){
		
		MemberVO memberVO = memberDao.findCompanyMemberIdByCompanySeqAndUserGb(companySeq);
		
		if (memberVO != null)
			return memberVO.getUserId();
		else return "";
	}
	@Override
	public int selectCountWithPermisionUserByCompanySeq( int companySeq){

		return memberDao.selectCountWithPermisionUserByCompanySeq(companySeq); 
	}
	
	@Override
	public List<MemberVO> getUserList( int companySeq, String[] useS, String searchValue, String searchType  ) {
		// TODO Auto-generated method stub
		return memberDao.getUserList( companySeq, useS, searchValue, searchType );
	}


	@Override
	public List<MemberVO> getPermitList(int companySeq, String[] useS) {
		// TODO Auto-generated method stub
		return memberDao.getPermitList(companySeq, useS);
	}
}