package com.amazingteam.competenceproject.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.amazingteam.competenceproject.model.Cloth;
import com.amazingteam.competenceproject.model.Meeting;
import com.amazingteam.competenceproject.model.Tag;
import com.amazingteam.competenceproject.model.Template;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final String TAG = this.getClass().getSimpleName();

    private static DatabaseHelper instance;

    private static final String DATABASE_NAME = "dressMeUp";

    private static final int DATABASE_VERSION = 5;
    private static final String TABLE_CLOTH = "clothes";
    private static final String TABLE_TAG = "tags";
    private static final String TABLE_CLOTH_TAG = "cloth_tags";
    private static final String TABLE_TEMPLATES = "templates";
    private static final String TABLE_MEETINGS = "meetings";
    private static final String TABLE_MEETINGS_TAGS = "meetings_tags";
    private static final String TABLE_MEETING_TEMPLATE = "meeting_template";

    private static final String KEY_ID = "id";

    private static final String KEY_NAME = "name";
    private static final String KEY_TYPE = "type";
    private static final String KEY_IMAGE = "image";

    private static final String KEY_TAG_NAME = "tag_name";
    private static final String KEY_TAG_WEIGHT = "tag_weight";
    private static final String KEY_TAG_CATEGORY = "tag_category";

    private static final String KEY_CLOTH_ID = "cloth_id";
    private static final String KEY_TAG_ID = "tag_id";

    private static final String KEY_TEMPLATE_NAME = "name";
    private static final String KEY_PHOTO_TEMPLATE_ID = "photo_template";
    private static final String KEY_ICON_TEMPLATE_ID = "template_icons";

    private static final String KEY_MEETING_NAME = "meeting_name";
    private static final String KEY_MEETING_ID = "meeting_id";
    private static final String KEY_MEETING_WEATHER_DEPENDANT = "meeting_weather_dependant";


    private static final String CREATE_TABLE_CLOTH = "CREATE TABLE "
            + TABLE_CLOTH + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME
            + " TEXT," + KEY_TYPE + " TEXT," + KEY_IMAGE + " TEXT" + ")";

    private static final String CREATE_TABLE_TAG = "CREATE TABLE " + TABLE_TAG
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TAG_NAME + " TEXT,"
            + KEY_TAG_WEIGHT + " INTEGER," + KEY_TAG_CATEGORY + " TEXT" + ")";

    private static final String CREATE_TABLE_CLOTH_TAG = "CREATE TABLE "
            + TABLE_CLOTH_TAG + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_CLOTH_ID + " INTEGER," + KEY_TAG_ID + " INTEGER" + ")";

    private static final String CREATE_TABLE_MEETING_TEMPLATE = "CREATE TABLE "
            + TABLE_MEETING_TEMPLATE + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_MEETING_ID + " INTEGER," + KEY_TEMPLATE_NAME + " TEXT" + ")";

    private static final String CREATE_TABLE_TEMPLATES = "CREATE TABLE "
            + TABLE_TEMPLATES + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_TEMPLATE_NAME
            + " TEXT," + KEY_PHOTO_TEMPLATE_ID + " INTEGER," + KEY_ICON_TEMPLATE_ID + " INTEGER" + ")";

    private static final String CREATE_TABLE_MEETINGS = "CREATE TABLE "
            + TABLE_MEETINGS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_MEETING_NAME
            + " TEXT," + KEY_MEETING_WEATHER_DEPENDANT + " INTEGER" + ")";

    private static final String CREATE_TABLE_MEETINGS_TAGS = "CREATE TABLE "
            + TABLE_MEETINGS_TAGS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_MEETING_ID + " INTEGER," + KEY_TAG_ID + " INTEGER" + ")";


    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CLOTH);
        db.execSQL(CREATE_TABLE_TAG);
        db.execSQL(CREATE_TABLE_CLOTH_TAG);
        db.execSQL(CREATE_TABLE_TEMPLATES);
        db.execSQL(CREATE_TABLE_MEETINGS);
        db.execSQL(CREATE_TABLE_MEETINGS_TAGS);
        db.execSQL(CREATE_TABLE_MEETING_TEMPLATE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLOTH_TAG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLOTH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMPLATES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEETINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEETINGS_TAGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEETING_TEMPLATE);

        onCreate(db);
    }

    public void closeDataBase() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    public long createCloth(Cloth cloth) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, cloth.getName());
        values.put(KEY_TYPE, cloth.getType());
        values.put(KEY_IMAGE, cloth.getImagePath());

        long clothId = database.insert(TABLE_CLOTH, null, values);

        for (Tag tag : cloth.getTagList()) {
            createClothTag(clothId, tag);
        }

        return clothId;
    }


    public int updateCloth(Cloth cloth) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, cloth.getName());
        values.put(KEY_TYPE, cloth.getType());
        values.put(KEY_IMAGE, cloth.getImagePath());

        for (long i : getAllClothTagConnections(cloth.getId())) {
            deleteClothTagConnection(i);
        }

        for (Tag tag : cloth.getTagList()) {
            createClothTag(cloth.getId(), tag);
        }

        return db.update(TABLE_CLOTH, values, KEY_ID + " = ?",
                new String[]{String.valueOf(cloth.getId())});
    }

    public Cloth getCloth(long cloth_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_CLOTH + " WHERE "
                + KEY_ID + " = " + cloth_id;

        Log.d(TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Cloth cloth = new Cloth();
        cloth.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        cloth.setName((c.getString(c.getColumnIndex(KEY_NAME))));
        cloth.setType(c.getString(c.getColumnIndex(KEY_TYPE)));
        cloth.setImagePath(c.getString(c.getColumnIndex(KEY_IMAGE)));
        cloth.setTagList(getAllClothTags(cloth.getId()));
        c.close();

        return cloth;
    }

    public void deleteCloth(long id) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (long i : getAllClothTagConnections(id)) {
            deleteClothTagConnection(i);
        }
        db.delete(TABLE_CLOTH, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public List<Cloth> getAllClothes() {
        List<Cloth> clothes = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_CLOTH;

        Log.d(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Cloth cloth = new Cloth();
                cloth.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                cloth.setName((c.getString(c.getColumnIndex(KEY_NAME))));
                cloth.setType(c.getString(c.getColumnIndex(KEY_TYPE)));
                cloth.setImagePath(c.getString(c.getColumnIndex(KEY_IMAGE)));
                cloth.setTagList(getAllClothTags(cloth.getId()));

                clothes.add(cloth);
            } while (c.moveToNext());
        }
        c.close();

        return clothes;
    }

    public List<Template> getAllTemplates() {
        List<Template> templates = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TEMPLATES;

        Log.d(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Template template = new Template();
                template.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                template.setName(c.getString(c.getColumnIndex(KEY_TEMPLATE_NAME)));
                template.setPhotoTemplateID(c.getInt(c.getColumnIndex(KEY_PHOTO_TEMPLATE_ID)));
                template.setTemplateIconID(c.getInt(c.getColumnIndex(KEY_ICON_TEMPLATE_ID)));

                templates.add(template);
            } while (c.moveToNext());

            c.close();
        }
        return templates;
    }

    public long createTag(Tag tag) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TAG_NAME, tag.getName());
        values.put(KEY_TAG_WEIGHT, tag.getWeight());
        values.put(KEY_TAG_CATEGORY, tag.getCategory());

        return db.insert(TABLE_TAG, null, values);
    }

    public long createTemplate(Template template) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TEMPLATE_NAME, template.getName());
        values.put(KEY_PHOTO_TEMPLATE_ID, template.getPhotoTemplateID());
        values.put(KEY_ICON_TEMPLATE_ID, template.getTemplateIconID());

        return db.insert(TABLE_TEMPLATES, null, values);
    }

    public long createMeeting(Meeting meeting) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MEETING_NAME, meeting.getName());
        values.put(KEY_MEETING_WEATHER_DEPENDANT, meeting.getWeatherDependant());

        long meetingId = db.insert(TABLE_MEETINGS, null, values);
        Log.d("TagList size : ", Integer.toString(meeting.getTagList().size()));
        for (Tag tag : meeting.getTagList()) {
            createMeetingTag(meetingId, tag);
        }
        List<Template> templates = getAllTemplates();
        for (String s : meeting.getClothesSet()) {
            for (Template template : templates)
                if (s.equals(template.getName()))
                    createMeetingTemplate(meetingId, template);
        }
        return meetingId;
    }

    private long createMeetingTag(long meeting_id, Tag tag) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_MEETING_ID, meeting_id);
        values.put(KEY_TAG_ID, tag.getId());


        return db.insert(TABLE_MEETINGS_TAGS, null, values);
    }

    private long createMeetingTemplate(long meeting_id, Template template) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MEETING_ID, meeting_id);
        values.put(KEY_TEMPLATE_NAME, template.getName());

        return db.insert(TABLE_MEETING_TEMPLATE, null, values);
    }

    public List<Meeting> getAllMeetings() {
        List<Meeting> meetings = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MEETINGS;

        Log.d(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Meeting meeting = new Meeting();
                meeting.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                meeting.setName((c.getString(c.getColumnIndex(KEY_MEETING_NAME))));
                meeting.setTagList(getAllMeetingTags(meeting.getId()));
                meeting.setClothesSet(getAllMeetingClothes(meeting.getId()));
                meeting.setWeatherDependant(c.getInt(c.getColumnIndex(KEY_MEETING_WEATHER_DEPENDANT)) == 1);
                meetings.add(meeting);
            } while (c.moveToNext());
        }
        c.close();

        return meetings;
    }

    public List<String> getAllMeetingClothes(long meetingId) {
        List<String> names = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_MEETING_TEMPLATE
                + " WHERE " + KEY_MEETING_ID + " = " + meetingId;
        Log.d("Cloth", selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                String name = c.getString(c.getColumnIndex(KEY_TEMPLATE_NAME));
                names.add(name);
            } while (c.moveToNext());
        }
        c.close();
        Log.d("names size : ", Integer.toString(names.size()));
        return names;
    }

    public List<Tag> getAllMeetingTags(long meetingId) {
        List<Tag> tags = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_MEETINGS + " tc, "
                + TABLE_TAG + " tg, " + TABLE_MEETINGS_TAGS + " tt WHERE tc."
                + KEY_ID + " = '" + meetingId + "'" + " AND tc." + KEY_ID
                + " = " + "tt." + KEY_MEETING_ID + " AND tg." + KEY_ID + " = "
                + "tt." + KEY_TAG_ID;

        Log.d(TAG, selectQuery);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Tag tag = new Tag();
                tag.setId(c.getInt((c.getColumnIndex(KEY_TAG_ID))));
                tag.setName((c.getString(c.getColumnIndex(KEY_TAG_NAME))));
                tag.setWeight(c.getInt(c.getColumnIndex(KEY_TAG_WEIGHT)));
                tag.setCategory(c.getString(c.getColumnIndex(KEY_TAG_CATEGORY)));

                tags.add(tag);
            } while (c.moveToNext());
        }
        c.close();

        return tags;
    }


    public List<Tag> getAllTags() {
        List<Tag> tags = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_TAG;

        Log.d(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Tag t = new Tag();
                t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                t.setName(c.getString(c.getColumnIndex(KEY_TAG_NAME)));
                t.setWeight(c.getInt(c.getColumnIndex(KEY_TAG_WEIGHT)));
                t.setCategory(c.getString(c.getColumnIndex(KEY_TAG_CATEGORY)));

                tags.add(t);
            } while (c.moveToNext());
        }
        c.close();

        return tags;
    }

    public List<Tag> getAllTagsByCategory(String category) {
        List<Tag> tags = new ArrayList<>();
        String selectQuery =
                "SELECT  * FROM " + TABLE_TAG
                        + " WHERE " + KEY_TAG_CATEGORY + "=" + "'" + category + "';";

        Log.d(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Tag t = new Tag();
                t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                t.setName(c.getString(c.getColumnIndex(KEY_TAG_NAME)));
                t.setWeight(c.getInt(c.getColumnIndex(KEY_TAG_WEIGHT)));
                t.setCategory(c.getString(c.getColumnIndex(KEY_TAG_CATEGORY)));

                tags.add(t);
            } while (c.moveToNext());
        }
        c.close();

        return tags;
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        String selectQuery =
                "SELECT  * FROM " + TABLE_TAG;

        Log.d(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                String string = c.getString(c.getColumnIndex(KEY_TAG_CATEGORY));
                if (!categories.contains(string)) categories.add(string);
            } while (c.moveToNext());
        }
        c.close();

        return categories;
    }

    public int getNumberOfItemsInCategory(String category) {
        int itemsInCategory = 0;
        String selectQuery =
                "SELECT * FROM " + TABLE_TAG
                        + " WHERE " + KEY_TAG_CATEGORY + "=" + "'" + category + "';";

        Log.d(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                itemsInCategory++;
            } while (c.moveToNext());
        }
        c.close();

        return itemsInCategory;
    }

    private long createClothTag(long cloth_id, Tag tag) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CLOTH_ID, cloth_id);
        values.put(KEY_TAG_ID, tag.getId());

        return db.insert(TABLE_CLOTH_TAG, null, values);
    }

    public List<Tag> getAllClothTags(long clothId) {
        List<Tag> tags = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_CLOTH + " tc, "
                + TABLE_TAG + " tg, " + TABLE_CLOTH_TAG + " tt WHERE tc."
                + KEY_ID + " = '" + clothId + "'" + " AND tc." + KEY_ID
                + " = " + "tt." + KEY_CLOTH_ID + " AND tg." + KEY_ID + " = "
                + "tt." + KEY_TAG_ID;

        Log.d(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Tag tag = new Tag();
                tag.setId(c.getInt((c.getColumnIndex(KEY_TAG_ID))));
                tag.setName((c.getString(c.getColumnIndex(KEY_TAG_NAME))));
                tag.setWeight(c.getInt(c.getColumnIndex(KEY_TAG_WEIGHT)));
                tag.setCategory(c.getString(c.getColumnIndex(KEY_TAG_CATEGORY)));

                tags.add(tag);
            } while (c.moveToNext());
        }
        c.close();

        return tags;
    }

    public List<Long> getAllClothTagConnections(long clothId) {
        List<Long> connections = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_CLOTH_TAG + " WHERE "
                + KEY_CLOTH_ID + " = " + clothId;

        Log.d(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                connections.add(c.getLong(c.getColumnIndex(KEY_ID)));
            } while (c.moveToNext());
        }
        c.close();

        return connections;
    }

    public void deleteClothTagConnection(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CLOTH_TAG, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public void dropAndCreateDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLOTH);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLOTH_TAG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMPLATES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEETINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEETINGS_TAGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEETING_TEMPLATE);

        onCreate(db);
    }

    public Tag getTagByName(String tagName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_TAG + " WHERE "
                + KEY_TAG_NAME + "=?";

        Log.d(TAG, selectQuery + " " + tagName);

        Cursor c = db.rawQuery(selectQuery, new String[]{tagName});

        if (c != null)
            c.moveToFirst();
        Tag tag = new Tag();
        tag.setId(c.getInt((c.getColumnIndex(KEY_ID))));
        tag.setName((c.getString(c.getColumnIndex(KEY_TAG_NAME))));
        tag.setWeight(c.getInt(c.getColumnIndex(KEY_TAG_WEIGHT)));
        tag.setCategory(c.getString(c.getColumnIndex(KEY_TAG_CATEGORY)));
        c.close();

        return tag;
    }

    public void deleteMeeting(int meetingId) {
        SQLiteDatabase db = this.getWritableDatabase();

        deleteMeetingClothSet(meetingId);
        for (long i : getAllMeetingTagConnections(meetingId)) {
            deleteMeetingTagConnection(i);
        }

        db.delete(TABLE_MEETINGS, KEY_ID + " = ?",
                new String[]{String.valueOf(meetingId)});
    }

    private void deleteMeetingClothSet(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEETING_TEMPLATE, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public void deleteMeetingTagConnection(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEETINGS_TAGS, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public List<Long> getAllMeetingTagConnections(int meetingId) {
        List<Long> connections = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_MEETINGS_TAGS + " WHERE "
                + KEY_MEETING_ID + " = " + meetingId;

        Log.d(TAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                connections.add(c.getLong(c.getColumnIndex(KEY_ID)));
            } while (c.moveToNext());
        }
        c.close();

        return connections;
    }

}
