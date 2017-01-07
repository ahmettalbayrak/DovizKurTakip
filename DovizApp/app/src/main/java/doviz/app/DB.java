package doviz.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;

public class DB extends SQLiteOpenHelper
{
    SQLiteDatabase db;

    public DB(Context c)
    {
        super(c, ""+c.getDatabasePath("doviz.db"),null,3);
        getWritableDatabase().close();
    }

    public void onCreate(SQLiteDatabase db)
    {
        Log.e("x","Veritabani Olusturuldu");
        String q = "create table kurlar (id integer primary key, kur text, alis text, satis text, tarih text);";
        db.execSQL(q);
    }

    public void kurlariGuncelle(Elements kurlar, String tarih)
    {
        db = getWritableDatabase();
        for (Element kur : kurlar)
        {
            String ad = kur.attr("Kod");
            String al = kur.select("ForexBuying").text();
            String sat= kur.select("ForexSelling").text();
            String q = "insert into kurlar (kur, alis, satis, tarih) values (?,?,?, ?)";
            Object[] params = new Object[] { ad, al, sat, tarih};
            Log.e("x","DB INSERT [Q] => "+q+" Params : "+ Arrays.toString(params));
            db.execSQL(q, params);
        }
        db.close();
    }

    public ArrayList<ContentValues> getKurlar(String tarih)
    {
        db = getWritableDatabase();
        Cursor c = db.rawQuery("select * from kurlar where tarih = '"+tarih+"' order by tarih desc", null);
        ArrayList<ContentValues> al = new ArrayList<>();
        while (c.moveToNext())
        {
            String cols[] = { "id", "kur", "alis","satis","tarih" };
            ContentValues cv = new ContentValues();
            for (int i = 0; i<cols.length; i++)
                cv.put(cols[i], c.getString(i));

            al.add(cv);
        }

        c.close();
        db.close();
        return al;
    }


    public ArrayList<ContentValues> getKurHistory(String ad)
    {
        db = getWritableDatabase();
        Cursor c = db.rawQuery("select * from kurlar where kur = '"+ad+"' order by tarih desc", null);
        ArrayList<ContentValues> al = new ArrayList<>();
        while (c.moveToNext())
        {
            String cols[] = { "id", "kur", "alis","satis","tarih" };
            ContentValues cv = new ContentValues();
            for (int i = 0; i<cols.length; i++)
                cv.put(cols[i], c.getString(i));

            al.add(cv);
        }

        c.close();
        db.close();
        return al;
    }


    public String getSonTarih()
    {
        db = getWritableDatabase();
        String res = "";
        Cursor c = db.rawQuery("select datetime('now') as 'suan'",null);
        c.moveToNext();
        res = c.getString(0);
        c.close();
        db.close();
        return res;

    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
    }
}
