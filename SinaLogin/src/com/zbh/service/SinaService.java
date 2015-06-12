package com.zbh.service;

import java.util.List;

import com.zbh.model.Sina;

public interface SinaService {
	public void insert(Sina sina);
	public List<Sina> select();
}
