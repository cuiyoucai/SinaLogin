package com.zbh.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.zbh.dao.SinaDao;
import com.zbh.model.Sina;
import com.zbh.service.SinaService;

@Component("SinaService")
public class SinaServiceImpl implements SinaService {
	@Resource
	private SinaDao dao;

	@Override
	public void insert(Sina sina) {
		dao.insert(sina);
	}

	@Override
	public List<Sina> select() {
		return dao.select();
	}
}
