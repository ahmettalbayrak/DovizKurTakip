package doviz.app;

import android.content.ContentValues;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class KurHistory extends AppCompatActivity
{
    ListView lv;
    BaseAdapter ba;
    LayoutInflater li;
    WebView wv;
    String kurId = "";
    DB db;
    ArrayList<ContentValues> dS = new ArrayList<>();
    AssetManager aManager;
    String kurAdi = "";

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kur_history);
        aManager = getAssets();

        kurId = getIntent().getExtras().getString("kur_id");

        db = new DB(this);

        li = LayoutInflater.from(this);
        lv = (ListView) findViewById(R.id.lv);
        wv = (WebView) findViewById(R.id.wv);
        wv.getSettings().setJavaScriptEnabled(true);

        dS = db.getKurHistory(kurId);

        ba = new BaseAdapter()
        {
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
                kurAdi = cv.getAsString("kur");
                tAlis.setText(cv.getAsString("alis"));
                tSatis.setText(cv.getAsString("satis"));
                tKur.setText(cv.getAsString("tarih"));

                return view;
            }
        };

        lv.setAdapter(ba);

        String html = readHTMLTemplate();
        html = html.replace("$_KURADI_", kurAdi);
        String data = "['Tarih', 'Alis', 'Satis'],";
        for (ContentValues cv : dS)
        {
            String tarih = cv.getAsString("tarih");
            String alis = ""+ (cv.getAsDouble("alis") + Math.random() *20);
            String satis = cv.getAsString("satis");
            data += "['"+tarih+"', "+alis+", "+satis+"],";
        }
        data = data.substring(0, data.length()-1);
        html = html.replace("$_DATA_",data);
        Log.e("x","html : "+html);

        wv.loadData(html, "text/html","UTF-8");

    }

    public String readHTMLTemplate()
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            InputStream is = aManager.open("tmp.html");
            Scanner oku = new Scanner(is, "UTF-8");
            while (oku.hasNextLine())
                sb.append(oku.nextLine()+"\n");
            oku.close();
        } catch (Exception e) { }
        return sb.toString();
    }
}
