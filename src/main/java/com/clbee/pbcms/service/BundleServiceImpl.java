package com.clbee.pbcms.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clbee.pbcms.dao.BundleDao;
import com.clbee.pbcms.dao.ProvisionDao;
import com.clbee.pbcms.vo.BundleVO;
import com.clbee.pbcms.vo.ProvisionVO;

@Service
public class BundleServiceImpl implements BundleService {
	@Autowired BundleDao dao;
	@Override
	public void delete(int seq) {
		dao.deleteByAppSeq(seq);
		// TODO Auto-generated method stub
	}
	@Override
	public void insert(BundleVO vo) {
		dao.insert(vo);
		// TODO Auto-generated method stub
	}
	@Override
	public int getListCount(BundleVO vo, int companySeq ) {
		// TODO Auto-generated method stub
		return dao.getListCount(vo, companySeq);
	}	
	
	@Override
	public List listByAppSeq(Integer appSeq) {
		// TODO Auto-generated method stub
		return dao.listByAppSeq(appSeq);
	}

}
