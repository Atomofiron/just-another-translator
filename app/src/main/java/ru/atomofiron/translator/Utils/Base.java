package ru.atomofiron.translator.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Для хранения и чтения истории и избранного.
 */
public class Base {

	private static final String CAL_PHRASE = "phrase";
	private static final String COL_TRANSLATION = "translation";
	private static final String COL_DIR = "direction";
	private static final String COL_TYPE = "type";
	private static final String TABLE_TRANSLATIONS = "translations";

	private Context co;
	private SQLiteDatabase db;

	public Base(Context co) {
		this.co = co;

		init();
	}

	private void init() {
		db = SQLiteDatabase.openOrCreateDatabase(getFilePath(), null);
		db.execSQL("create table if not exists " + TABLE_TRANSLATIONS + " ( _id integer primary key autoincrement, " +
				CAL_PHRASE + " text, " + COL_TRANSLATION + " text, " + COL_DIR + " text, " + COL_TYPE + " text " + ");");

	}

	public long put(Node node) {
		if (contains(node))
			remove(node);

		ContentValues cv = new ContentValues();
		cv.put(CAL_PHRASE, node.getPhrase());
		cv.put(COL_TRANSLATION, node.getTranslation());
		cv.put(COL_DIR, node.getDirection());
		cv.put(COL_TYPE, node.getTypeString());

		return db.insert(TABLE_TRANSLATIONS, null, cv);
	}
	public boolean contains(Node node) {
		if (!db.isOpen())
			init();

		Cursor cursor = db.rawQuery("select * from " + TABLE_TRANSLATIONS +
						" where " + CAL_PHRASE + "=? and "+ COL_DIR +"=? and "+ COL_TYPE +"=? ",
				new String[] { node.getPhrase(), node.getDirection(), node.getTypeString() });

		boolean contains = cursor.getCount() > 0;
		cursor.close();

		return contains;
	}

	public ArrayList<Node> get(Node.TYPE type) {
		ArrayList<Node> nodes = new ArrayList<>();

		try {
			Cursor cursor;
			cursor = db.rawQuery("select * from " + TABLE_TRANSLATIONS + " where " + COL_TYPE + "=?;", new String[] { type.toString() });

			 if (cursor.moveToFirst())
			 	do {
					String title = cursor.getString(cursor.getColumnIndex(CAL_PHRASE));
					String subtitle = cursor.getString(cursor.getColumnIndex(COL_TRANSLATION));
					String dir = cursor.getString(cursor.getColumnIndex(COL_DIR));
					nodes.add(0, new Node(title, subtitle, dir, type));
				} while (cursor.moveToNext());

			cursor.close();
		} catch (Exception e) {
			I.Loge(e.toString());
		}

		return nodes;
	}

	public int remove(Node node) {
		return db.delete(TABLE_TRANSLATIONS, CAL_PHRASE +"=? and "+ COL_DIR +"=? and "+ COL_TYPE +"=? ",
				new String[] { node.getPhrase(), node.getDirection(), node.getTypeString() });
	}

	public int clear(String type) {
		return db.delete(TABLE_TRANSLATIONS, COL_TYPE +"=? ",
				new String[] { type });
	}

	private String getFilePath() {
		return I.getFilesPath(co)+"/base.db";
	}

	public void close() {
		db.close();
	}
}
