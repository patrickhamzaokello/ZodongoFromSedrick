package com.pkasemer.zodongofoods.localDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pkasemer.zodongofoods.Models.FoodDBModel;

import java.util.ArrayList;
import java.util.List;

public class SenseDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "zodongo";
    private static final int DB_VERSION = 3;
    private static final String DB_TABLE = "CART";

    List<FoodDBModel> foodDBModelList;

    public SenseDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE CART (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "menuId INTEGER," +
                "menuName TEXT," +
                "price INTEGER," +
                "description REAL," +
                "menuTypeId INTEGER," +
                "menuImage TEXT," +
                "backgroundImage TEXT," +
                "ingredients TEXT," +
                "menuStatus INTEGER," +
                "created TEXT," +
                "modified TEXT," +
                "rating INTEGER," +
                "quantity INTEGER DEFAULT 1)";

        db.execSQL(sql);
    }

    //upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS " + DB_TABLE;
        db.execSQL(sql);
        onCreate(db);
    }


    public List<FoodDBModel> listTweetsBD() {
        String sql = "select * from " + DB_TABLE + " order by _id DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        foodDBModelList = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            do {
                int id = Integer.parseInt(cursor.getString(0));
                int menuId = Integer.parseInt(cursor.getString(1));
                String menuname = cursor.getString(2);
                int price = Integer.parseInt(cursor.getString(3));
                String description = cursor.getString(4);
                int menutypid = Integer.parseInt(cursor.getString(5));
                String menuimage = cursor.getString(6);
                String backgroundimage = cursor.getString(7);
                String ingredients = cursor.getString(8);
                int menuStatus = Integer.parseInt(cursor.getString(9));
                String created = cursor.getString(10);
                String modified = cursor.getString(11);
                int rating = Integer.parseInt(cursor.getString(12));
                int quantity = Integer.parseInt(cursor.getString(13));

                foodDBModelList.add(new FoodDBModel(menuId, menuname, price,description, menutypid, menuimage,backgroundimage, ingredients,menuStatus, created,modified, rating,quantity));
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return foodDBModelList;
    }

    public void addTweet(
            int menuId,
            String menuname,
            int price,
            String description,
            int menutypeid,
            String menuImage,
            String backgroundImage,
            String ingredients,
            int menustatus,
            String created,
            String modified,
            int rating,
            int quantity
    ) {
        ContentValues values = new ContentValues();
        values.put("menuId", menuId);
        values.put("menuName", menuname);
        values.put("price", price);
        values.put("description", description);
        values.put("menuTypeId", menutypeid);
        values.put("menuImage", menuImage);
        values.put("backgroundImage", backgroundImage);
        values.put("ingredients", ingredients);
        values.put("menuStatus", menustatus);
        values.put("created", created);
        values.put("modified", modified);
        values.put("rating", rating);
        values.put("quantity", quantity);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(DB_TABLE, null, values);
    }

    void updateTweet(FoodDBModel foodmenu) {
        ContentValues values = new ContentValues();
        values.put("menuName", foodmenu.getMenuName());
        values.put("description", foodmenu.getDescription());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(DB_TABLE, values, "menuId" + " = ?", new String[]{String.valueOf(foodmenu.getMenuId())});
    }

   public void updateMenuCount(Integer qtn, Integer menuID) {
        ContentValues values = new ContentValues();
        values.put("quantity", qtn);
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(DB_TABLE, values, "menuId" + " = ?", new String[]{String.valueOf(menuID)});
    }


    public void deleteTweet(String id_str) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DB_TABLE, "menuId" + " = ?", new String[]{id_str});
    }

    public boolean checktweetindb(String id_str) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE,
                new String[]{"menuId", "menuName", "description"},
                "menuId = ?",
                new String[]{id_str},
                null, null, null, null);
        if (cursor.moveToFirst()) {
            //recordexist
            cursor.close();
            return false;
        } else {
            //record not existing
            cursor.close();
            return true;
        }
    }

    public int countCart(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCount= db.rawQuery("select count(*) from " +DB_TABLE , null);
        mCount.moveToFirst();
        int count= mCount.getInt(0);
        mCount.close();

        return count;
    }


    public int getMenuQtn(String id_str) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE,
                new String[]{"quantity"},
                "menuId = ?",
                new String[]{id_str},
                null, null, null, null);
        if (cursor.moveToFirst()) {
            //recordexist
            int count= cursor.getInt(0);
            cursor.close();
            return count;
        } else {
            //record not existing
            int count= 1;
            cursor.close();
            return count;
        }
    }

    public int sumPriceCartItems() {
        String priceCol = "price";
        String quantityCol = "quantity";
        int result = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select sum("+ priceCol + " * " + quantityCol + ") from " + DB_TABLE, null);
        if (cursor.moveToFirst()) result = cursor.getInt(0);
        cursor.close();
        db.close();
        return result;
    }


}
