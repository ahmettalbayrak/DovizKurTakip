package doviz.app;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    DB db;
    ArrayList<ContentValues> dS = new ArrayList<>();
    ListView lv;
    BaseAdapter ba;
    LayoutInflater li;
    ActionBar ab;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ab = getSupportActionBar();

        db = new DB(this);

        li = LayoutInflater.from(this);
        lv = (ListView) findViewById(R.id.lv);

        ba = new BaseAdapter() {
            @Override
            public int getCount() {
                return dS.size();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            public View getView(int i, View view, ViewGroup viewGroup)
            {
                if (view == null)
                    view = li.inflate(R.layout.kur_item, null);

                TextView tAlis = (TextView) view.findViewById(R.id.tvAlis);
                TextView tSatis = (TextView) view.findViewById(R.id.tvSatis);
                TextView tKur = (TextView) view.findViewById(R.id.tvKur);

                ContentValues cv = dS.get(i);
                tAlis.setText(cv.getAsString("alis"));
                tSatis.setText(cv.getAsString("satis"));
                tKur.setText(cv.getAsString("kur"));

                return view;
            }
        };

        lv.setAdapter(ba);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                String id = dS.get(i).getAsString("kur");
                Intent intent = new Intent(MainActivity.this, KurHistory.class);
                intent.putExtra("kur_id",id);
                startActivity(intent);
            }
        });

        refresh();

    }


    void refresh()
    {
        new AsyncTask<String,String,String>()
        {
            String tarih = "";
            protected String doInBackground(String... strings)
            {
                try
                {

                    Elements kurlar = Jsoup.connect("http://www.tcmb.gov.tr/kurlar/today.xml")
                            .timeout(30000)
                            .userAgent("Mozilla")
                            .get()
                            .select("Tarih_Date > Currency");

                    tarih = db.getSonTarih();

                    db.kurlariGuncelle(kurlar, tarih);

                } catch (Exception e) { Log.e("x","refresh Ex : "+e.toString()); }
                return null;
            }

            protected void onPostExecute(String s)
            {
                String trh = "SGT : "+tarih;
                ab.setSubtitle(trh);

                ba.notifyDataSetChanged();
                dS = db.getKurlar(tarih);
            }
        }.execute();
    }
}
