package com.example.game.guesscelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURL = new ArrayList<String>();
    ArrayList<String> celebname = new ArrayList<String>();

    int choosecele = 0 , loccorrect = 0;
    String[] answers = new String[4];
    Button b0,b1,b2,b3;

    ImageView cele;

    public class ImageDownloader extends AsyncTask<String , Void , Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);

                HttpURLConnection connection  = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream in = connection.getInputStream();

                Bitmap mybit = BitmapFactory.decodeStream(in);

                return mybit;


            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }



    public class DownloadTask extends AsyncTask<String ,Void , String >{


        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1){
                    char current = (char) data;

                    result += current;
                    data = reader.read();
                }
                return result;

            }catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public void generatecele(){
        try {
            Random random = new Random();
            choosecele = random.nextInt(celebURL.size());

            ImageDownloader imagetask = new ImageDownloader();
            Bitmap celeimg;
            celeimg = imagetask.execute(celebURL.get(choosecele)).get();
            cele.setImageBitmap(celeimg);

            loccorrect = random.nextInt(4);
            int incorrect ;
            for(int i=0;i<4;i++){
                if(i==loccorrect){
                    answers[i] = celebname.get(choosecele);
                }else{
                    incorrect = random.nextInt(celebname.size());
                    while(incorrect == choosecele){
                        incorrect = random.nextInt(celebname.size());

                    }
                    answers[i] = celebname.get(incorrect);

                }
            }
            b0.setText(answers[0]);
            b1.setText(answers[1]);
            b2.setText(answers[2]);
            b3.setText(answers[3]);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cele = (ImageView) findViewById(R.id.imageView2);
        b0 = (Button) findViewById(R.id.button1);
        b1 = (Button) findViewById(R.id.button2);
        b2 = (Button) findViewById(R.id.button3);
        b3 =  (Button) findViewById(R.id.button4);
        DownloadTask  task  = new DownloadTask();
        String result = null;
        try {
            result = task.execute("http://www.posh24.se/kandisar").get();
            String[] splitresult = result.split("<div class=\"sidebarContainer\">");
            Pattern p1 = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m1 = p1.matcher(splitresult[0]);

            while(m1.find()){
                celebURL.add(m1.group(1));
            }
            Pattern p = Pattern.compile("alt=\"(.*?)\"");
            Matcher m = p.matcher(splitresult[0]);

            while(m.find()){
                celebname.add(m.group(1));
            }


        }catch (Exception e){

            e.printStackTrace();
        }
       generatecele();

    }

    public void cele(View view) {

        if(view.getTag().toString().equals(Integer.toString(loccorrect))){

            Toast.makeText(this , "Correct!" , Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(this , "Wrong! It was "+ celebname.get(choosecele) , Toast.LENGTH_LONG).show();

        }

        generatecele();

    }
}
