package ru.atomofiron.translator.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ru.atomofiron.translator.I;

public class Base {

	private static final String colTitle = "title";
	private static final String colSubTitle = "subtitle";
	private static final String colDir = "dir";
	private static final String colType = "type";
	private static final String tableName = "tableName";

	private Context co;
	private SQLiteDatabase db;

	public Base(Context co) {
		this.co = co;
		String path = getFilePath();
		db = SQLiteDatabase.openOrCreateDatabase(path, null);
		db.execSQL("create table if not exists " + tableName + " ( _id integer primary key autoincrement, " +
				colTitle + " text, " + colSubTitle + " text, " + colDir + " text, " + colType + " text " + ");");
	}

	public long put(Node node) {
		if (contains(node))
			return 0;

		ContentValues cv = new ContentValues();
		cv.put(colTitle, node.title);
		cv.put(colSubTitle, node.subtitle);
		cv.put(colDir, node.dir);
		cv.put(colType, node.getType());

		return db.insert(tableName, null, cv);
	}
	public boolean contains(Node node) {
		Cursor cursor = db.rawQuery("select * from " + tableName +
						" where " + colTitle+"=? and "+colSubTitle+"=? and "+colDir+"=? and "+colType+"=? ",
				new String[] { node.title, node.subtitle, node.dir, node.getType() });

		boolean contains = cursor.getCount() > 0;
		cursor.close();

		return contains;
	}

	public ArrayList<Node> get(String type) {
		ArrayList<Node> nodes = new ArrayList<>();

		try {
			Cursor cursor;
			cursor = db.rawQuery("select * from " + tableName + " where " + colType + "=?;", new String[] { type });

			 if (cursor.moveToFirst())
			 	do {
					String title = cursor.getString(cursor.getColumnIndex(colTitle));
					String subtitle = cursor.getString(cursor.getColumnIndex(colSubTitle));
					String dir = cursor.getString(cursor.getColumnIndex(colDir));
					nodes.add(new Node(title, subtitle, dir, type));
				} while (cursor.moveToNext());

			cursor.close();
		} catch (Exception e) {
			I.Loge(e.toString());
		}

		return nodes;
	}

	public int remove(Node node) {
		return db.delete(tableName, colTitle+"=? and "+colSubTitle+"=? and "+colDir+"=? and "+colType+"=? ",
				new String[] { node.title, node.subtitle, node.dir, node.getType() });
	}

	private String getFilePath() {
		return I.getFilesPath(co)+"/base.db";
	}

	public void close() {
		db.close();
	}
}
