package com.zbh.dao;

import java.util.List;

import com.zbh.model.Sina;

public interface SinaDao {
	public void insert(Sina sina);
	public List<Sina> select();
}
