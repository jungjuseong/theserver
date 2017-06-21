package com.clbee.pbcms.util;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.clbee.pbcms.service.MemberService;
import com.clbee.pbcms.vo.MemberVO;


@Service
public class myUserDetailsService implements UserDetailsService {

	@Autowired
	MemberService memberService;
	
	
	
	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		
		System.out.println("Hello [loadUserByUserName");
		System.out.println(username);
		
		MemberVO memberVO = memberService.findByUserName(username);
		
		System.out.println("password" + memberVO.getUserPw());
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		System.out.println("userGb Number // = " + memberVO.getUserGb());
		switch(Integer.parseInt(memberVO.getUserGb())) {
			case 1 :	/* ��� ����� */
				 authorities.add(new GrantedAuthorityImpl("ROLE_COMPANY_USER"));
				break;
			case 5 :	/*	��� �������� ��� */
				 authorities.add(new GrantedAuthorityImpl("ROLE_COMPANY_CREATOR"));
				break;
			case 21 :	/* ��� �������� ��� */
				authorities.add(new GrantedAuthorityImpl("ROLE_COMPANY_DISTRIBUTOR"));
			break;
			case 29 :
				authorities.add(new GrantedAuthorityImpl("ROLE_COMPANY_MIDDLEADMIN"));
				break;
			case 127 :	/* userGb�� ȸ���� ��� */
				/* companyGb�� ���ȸ�� �ϰ��*/
				if("1".equals(memberVO.getCompanyGb())) {
					 authorities.add(new GrantedAuthorityImpl("ROLE_COMPANY_MEMBER"));
				}
				/* companyGb�� ����ȸ���� ��� */
				else{
					 authorities.add(new GrantedAuthorityImpl("ROLE_INDIVIDUAL_MEMBER"));
				}
				break;
			case 255 :	/*	���� �������� ��� */
				 authorities.add(new GrantedAuthorityImpl("ROLE_ADMIN_SERVICE"));
				break;
		}

        System.out.println("memberVo.getUserStatus() " + memberVO.getUserStatus() );
        
        
        if(ConditionCompile.bookFeature) {
			if("5".equals(memberVO.getUserStatus())){
				System.out.println("getUserStatus : 5");
				return new myUserDetails(username, memberVO.getUserPw(),authorities, memberVO, false, true ); 
			}else if("4".equals(memberVO.getUserStatus())){
				System.out.println("getUserStats : 4 ");
				return new myUserDetails(username, memberVO.getUserPw(),authorities, memberVO, true, true );
			}
        }else {
        	if("5".equals(memberVO.getUserStatus())){
				System.out.println("getUserStatus : 5");
				return new myUserDetails(username, memberVO.getUserPw(),authorities, memberVO, false, false ); 
			}else if("4".equals(memberVO.getUserStatus())){
				System.out.println("getUserStats : 4 ");
				return new myUserDetails(username, memberVO.getUserPw(),authorities, memberVO, true, false );
			}
        }
		System.out.println("bofore!!!");
		return null;

	}	
}
