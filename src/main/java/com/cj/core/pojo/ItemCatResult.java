package com.cj.core.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * 商品分类列表JSON数据中的第一层pojo.
 * @author cj
 */
public class ItemCatResult implements Serializable{
	private static final long serialVersionUID = -4646397222458359742L;
	//fields...

	private List data;


	//mehtods...

	public List getData() {
		return data;
	}

	public void setData(List data) {
		this.data = data;
	}
}
