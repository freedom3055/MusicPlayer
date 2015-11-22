package com.example.musicplayer.db.base;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.musicplayer.activity.MusicApplication;
import com.example.musicplayer.db.base.annotation.Column;
import com.example.musicplayer.db.base.annotation.ID;
import com.example.musicplayer.db.base.annotation.TableName;

public class DAOImp<M> implements DAO<M> {

	protected SQLiteDatabase db;
	private DBHelper dbHelper;

	public DAOImp() {
		super();
		this.dbHelper = new DBHelper(MusicApplication.getContext());
		this.db = dbHelper.getWritableDatabase();
	}

	@Override
	public long insert(M m) {
		ContentValues values = new ContentValues();

		fillColumn(m, values);

		return db.insert(getTableName(), null, values);
	}

	@Override
	public int delete(Serializable id) {
		return db.delete(getTableName(), "_id = ?",
				new String[] { id.toString() });
	}

	@Override
	public int update(M m) {
		ContentValues values = new ContentValues();

		fillColumn(m, values);

		return db.update(getTableName(), values, "_id = ?",
				new String[] { getId(m) });
	}

	@Override
	public List<M> findAll() {
		List<M> result = null;
		Cursor cursor = db.query(getTableName(), null, null, null, null, null,
				null);
		if (cursor != null) {
			result = new ArrayList<M>();
			while (cursor.moveToNext()) {
				M m = getInstantce();

				fillField(cursor, m);

				result.add(m);
			}
			cursor.close();
		}

		return result;
	}

	@Override
	public List<M> findByConditionqueryquery(String table, String[] columns,
			String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {

		List<M> result = null;
		Cursor cursor = db.query(table, columns, selection, selectionArgs,
				groupBy, having, orderBy);
		if (cursor != null) {
			result = new ArrayList<M>();
			while (cursor.moveToNext()) {
				M m = getInstantce();

				fillField(cursor, m);

				result.add(m);
			}
			cursor.close();
		}

		return result;
	}

	@Override
	public List<M> findByConditionqueryquery(String table, String[] columns,
			String selection, String[] selectionArgs) {

		return findByConditionqueryquery(table, columns, selection,
				selectionArgs, null, null, null);
	}

	@Override
	public void clean() {
		db.execSQL("DROP TABLE IF EXISTS " + getTableName());
		dbHelper.onCreate(db);
	}

	/**
	 * 获取表名
	 * @return
	 */
	private String getTableName() {
		M m = getInstantce();

		TableName tableName = m.getClass().getAnnotation(TableName.class);
		if (tableName != null) {
			return tableName.value();
		}
		return "";
	}

	/**
	 * 模型对象转化为ContentValues对象
	 * @param m
	 * @param values
	 */
	private void fillColumn(M m, ContentValues values) {
		Field[] fields = m.getClass().getDeclaredFields();
		for (Field item : fields) {
			item.setAccessible(true);

			Column column = item.getAnnotation(Column.class);
			if (column != null) {
				String key = column.value();

				try {

					String value = item.get(m).toString();

					if (value.length() > 0) {
						values.put(key, value);
					}

				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 获取主键
	 * @param m
	 * @return
	 */
	private String getId(M m) {

		Field[] fields = m.getClass().getDeclaredFields();
		for (Field item : fields) {
			item.setAccessible(true);
			ID id = item.getAnnotation(ID.class);

			if (id != null) {

				try {

					return item.get(m).toString();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}

		}

		return "";
	}

	/**
	 * 填充模型对象
	 * @param cursor
	 * @param m
	 */
	private void fillField(Cursor cursor, M m) {
		Field[] fields = m.getClass().getDeclaredFields();
		for (Field item : fields) {
			item.setAccessible(true);
			Column column = item.getAnnotation(Column.class);
			if (column != null) {
				int columnIndex = cursor.getColumnIndex(column.value());
				String value = cursor.getString(columnIndex);

				try {

					if (item.getType() == int.class) {
						item.set(m, Integer.valueOf(value));
					} else if (item.getType() == long.class) {
						item.set(m, Long.valueOf(value));
					}else {
						item.set(m, value);
					}

				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}

		}

	}

	/**
	 * 获取相应的模型对象
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private M getInstantce() {
		Class clazz = getClass();
		java.lang.reflect.Type superclass = clazz.getGenericSuperclass();
		if (superclass != null && superclass instanceof ParameterizedType) {
			java.lang.reflect.Type[] type = ((ParameterizedType) superclass)
					.getActualTypeArguments();
			try {
				return (M) ((Class) type[0]).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	protected void finalize() throws Throwable {
		db.close();
		super.finalize();
	}

}
