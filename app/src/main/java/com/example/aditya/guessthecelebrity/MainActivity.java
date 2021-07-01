package com.example.aditya.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    int count;
    ArrayList<String> celeb_name;
    ArrayList<String> celeb_url;
    ArrayList<Integer> alreadydone;
    GridLayout gridLayout;
    Button b0;
    Button b1;
    Button b2;
    Button b3;
    int ansindex;
    TextView score;
    TextView correct;
    int myscore;
    int x;
    Random rand=new Random();
    public class urlgetter extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... urls) {
            String result="";
            try {
                URL url=new URL(urls[0]);
               HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();
                while (data!=-1){
                    char curr=(char) data;
                    result+=curr;
                    data=reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }

        }
    }
    public void onbuttonclick(View view) {
        x++;
        if (ansindex == Integer.parseInt(view.getTag().toString())) {
              myscore++;
              score.setText(String.valueOf(myscore)+"/"+String.valueOf(count));
             correct.setText("Correct");
        }
        else {
            score.setText(String.valueOf(myscore)+"/"+String.valueOf(count));

            correct.setText("Wrong");
        }
        if(count<50){
            createcontent();
        }
        else {
            gridLayout.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            correct.setText("All Done");
        }
    }

     public void createcontent(){
        count++;
        ArrayList<String> options=new ArrayList<>();
         ImageDownloader imageDownloader=new ImageDownloader();
         Bitmap bmp;
         try {
             bmp=imageDownloader.execute(celeb_url.get(x)).get();
             imageView.setImageBitmap(bmp);
         } catch (Exception e) {
             e.printStackTrace();
             Log.i("herrrrrrr","urlgone");
         }
         ansindex=rand.nextInt(4);
         String ans=celeb_name.get(x);
         for(int i=0;i<4;i++){
             if(i==ansindex){
                 options.add(ans);
             }
             else {

                 int guesswrongind=rand.nextInt(50);
                 String wrongans=celeb_name.get(guesswrongind);
                 while(wrongans.equals(ans)){
                     guesswrongind=rand.nextInt(50);
                     wrongans=celeb_name.get(guesswrongind);
                 }
                 options.add(wrongans);
             }
         }

         b0.setText(options.get(0));
         b1.setText(options.get(1));
         b2.setText(options.get(2));
         b3.setText(options.get(3));


     }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        urlgetter urlget=new urlgetter();
        String res="";
        celeb_name=new ArrayList<String>();
        celeb_url=new ArrayList<String>();
        alreadydone=new ArrayList<Integer>();
        String prefix="https://www.onthisday.com/";
        imageView=findViewById(R.id.imageView);
        gridLayout=findViewById(R.id.gridlayout);
        b0=findViewById(R.id.button0);
        b1=findViewById(R.id.button1);
        b2=findViewById(R.id.button2);
        b3=findViewById(R.id.button3);
        score=findViewById(R.id.Score);
        count=0;
        myscore=0;
        correct=findViewById(R.id.corrorwrong);
        x=0;
        try {
            res=urlget.execute("https://www.onthisday.com/people/most-popular.php").get();
            Pattern p=Pattern.compile("alt=\"(.*?)\" />");
            Matcher m=p.matcher(res);

            while (m.find()){
                celeb_name.add(m.group(1));
            }
            p=Pattern.compile("img src=\"(.*?)\" ");
            m=p.matcher(res);
            while (m.find()){
                String temp=m.group();
                celeb_url.add(prefix+temp.substring(10,temp.length()-2));
                Log.i("Celeb",prefix+temp.substring(10,temp.length()-2));
            }
            p=Pattern.compile("data-src=\"(.*?)\" ");
            m=p.matcher(res);
            while (m.find()){
                String temp=m.group();
                celeb_url.add(prefix+temp.substring(10,temp.length()-2));
                //Log.i("Celeb",prefix+m.group().substring(10));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i=0;i<celeb_name.size();i++){
            Log.i("Name",celeb_name.get(i));
        }
        createcontent();


    }
    public class ImageDownloader extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url=new URL(urls[0]);
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in=connection.getInputStream();
                Bitmap bitmap= BitmapFactory.decodeStream(in);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        }
    }
}
