package com.zbh.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

import com.zbh.dao.SinaDao;
import com.zbh.model.Sina;

@Component("SinaDao")
public class SinaDaoImpl implements SinaDao {
	@Resource
	private HibernateTemplate hibernateTemplate;

	@Override
	public void insert(Sina sina) {
		hibernateTemplate.save(sina);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Sina> select() {
		String hql = "from Sina sina";
		List<Sina> list = hibernateTemplate.find(hql);
		return list;
	}
}
