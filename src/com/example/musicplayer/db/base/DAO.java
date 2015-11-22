package com.example.musicplayer.db.base;

import java.io.Serializable;
import java.util.List;

public interface DAO<M> {
	
	
	
	long insert(M m);

	
	int delete(Serializable id);

	
	int update(M m);

	
	List<M> findAll();

	List<M> findByConditionqueryquery(String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy);
	
	List<M> findByConditionqueryquery(String table, String[] columns,
			String selection, String[] selectionArgs);
	
	
	void clean();
}
