package com.example.wujue.weatherreport;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wujue.util.NetUtil;
import com.example.wujue.bean.TodayWeather;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final int UPDATE_TODAY_WEATHER =  1;
//    刷新按钮
    private ImageView mUpdateBtn;
//    今日天气信息
    private TextView cityTv, timeTv, weekTv, humidityTv, pmDataTv, pmQualityTv, temperatureTv,climateTv,windTv,city_name_Tv;
//    天气/PM2.5示意图
    private ImageView weatherImg, pmImg;
//    城市选择按钮
    private ImageView mCitySelect;


//  将解析的天气对象发送给主线程
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather)msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);
//        监听刷新按钮
        mUpdateBtn = (ImageView)findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);
//         检查网络状态
        if(NetUtil.getNetworkState(this)!=NetUtil.NETWORK_NONE){
            Log.d("myWeather", "网络ok");
            Toast.makeText(MainActivity.this,"网络ok",Toast.LENGTH_LONG).show();
        }
        else {
            Log.d("myWeather", "网络error");
            Toast.makeText(MainActivity.this,"网络error",Toast.LENGTH_LONG).show();
        }
//        监听城市选择按钮
        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
//        初始化界面
        initView();
    }

    @Override
    public void onClick(View view){
//        点击刷新按钮
        if(view.getId()==R.id.title_update_btn){
            SharedPreferences mySharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
            String cityCode = mySharedPreferences.getString("cityCode","101010100");
            Log.d("myWeather", cityCode);
            //        获取网络数据
            if(NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE){
                Log.d("myWeather", "网络ok");
                queryWeatherCode(cityCode);
            }
            else{
                Log.d("myWeather", "网络error");
                Toast.makeText(MainActivity.this,"网络error",Toast.LENGTH_LONG).show();
            }
        }
//        点击城市选择按钮
        if(view.getId()==R.id.title_city_manager){
            Intent i = new Intent(this, SelectCity.class);
            startActivityForResult(i,1);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1 && resultCode == RESULT_OK){
            String newCityCode = data.getStringExtra("cityCode");
            Log.d("myWeather", "选择的城市代码为"+newCityCode);

            if(NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE){
                Log.d("myWeather", "网络ok");
                queryWeatherCode(newCityCode);
            }
            else{
                Log.d("myWeather", "网络error");
                Toast.makeText(MainActivity.this,"网络error",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void queryWeatherCode(String cityCode){
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey="+cityCode;
        Log.d("myWeather", address);
//    开启线程发起网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try{
//                发出http请求
                    URL url = new URL(address);
                    con = (HttpURLConnection)url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
//                对获取的输入流进行读取
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String str;
                    while ((str=reader.readLine())!=null){
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);
//                 进行解析
                    todayWeather = parseXML(responseStr);
                    if(todayWeather != null){
                        Log.d("myWeather", todayWeather.toString());
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                        String city = todayWeather.getCity();
                        String time = todayWeather.getUpdatetime();
                        String humidity = todayWeather.getHumidity();
                        String pmData = todayWeather.getPm25();
                        String pmQuality = todayWeather.getQuality();
                        String week = todayWeather.getDate();
                        String tempHigh = todayWeather.getHigh();
                        String tempLow = todayWeather.getLow();
                        String climate = todayWeather.getType();
                        String wind = todayWeather.getWind_power();
                        editor.putString("city_name_Tv",city);
                        editor.putString("cityTv",city);
                        editor.putString("timeTv",time);
                        editor.putString("humidityTv",humidity);
                        editor.putString("pmDataTv",pmData);
                        editor.putString("pmQualityTv",pmQuality);
                        editor.putString("weekTv",week);
                        editor.putString("tempHighTv",tempHigh);
                        editor.putString("tempLowTv",tempLow);
                        editor.putString("climateTv",climate);
                        editor.putString("windTv",wind);
                        editor.apply();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(con!=null){
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    private TodayWeather parseXML(String xmldata) {
        TodayWeather todayWeather = null;
//        用来标记api文件中重名的标签
        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
//                    判断当前事件是否是文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
//                    判断当前事件是否是标签元素事件
                    case XmlPullParser.START_TAG:
                        if(xmlPullParser.getName().equals("resp")){
                            todayWeather = new TodayWeather();
                        }
                        if(todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "city:" + xmlPullParser.getText());
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "updatetime: " + xmlPullParser.getText());
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "humidity " + xmlPullParser.getText());
                                todayWeather.setHumidity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "temperature: " + xmlPullParser.getText());
                                todayWeather.setTemperature(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "pm25: " + xmlPullParser.getText());
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "quality: " + xmlPullParser.getText());
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang") && fengxiangCount == 0) {
                                eventType = xmlPullParser.next();
                                Log.d("myWeather", "wind direction: " + xmlPullParser.getText());
                                todayWeather.setWind_direction(xmlPullParser.getText());
                                fengxiangCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWind_power(xmlPullParser.getText());
                                Log.d("myWeather", "wind power: " + xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date") && dateCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setDate(xmlPullParser.getText());
                                Log.d("myWeather", "date: " + xmlPullParser.getText());
                                dateCount++;
                            } else if (xmlPullParser.getName().equals("high") && highCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                Log.d("myWeather", "high: " + xmlPullParser.getText());
                                highCount++;
                            } else if (xmlPullParser.getName().equals("low") && lowCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                Log.d("myWeather", "low: " + xmlPullParser.getText());
                                lowCount++;
                            } else if (xmlPullParser.getName().equals("type") && typeCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setType(xmlPullParser.getText());
                                Log.d("myWeather", "type: " + xmlPullParser.getText());
                                typeCount++;
                            }
                        }
                        break;
//                        判断当前事件是否是标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
//                进入下一个事件
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

    void initView(){
        city_name_Tv = (TextView)findViewById(R.id.title_city_name);
        cityTv = (TextView)findViewById(R.id.city);
        timeTv = (TextView)findViewById(R.id.time);
        humidityTv = (TextView)findViewById(R.id.humidity);
        weekTv = (TextView)findViewById(R.id.week_today);
        pmDataTv = (TextView)findViewById(R.id.pm_data);
        pmQualityTv = (TextView)findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView)findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView)findViewById(R.id.temperature);
        climateTv = (TextView)findViewById(R.id.climate);
        windTv = (TextView)findViewById(R.id.wind);
        weatherImg = (ImageView)findViewById(R.id.weather_img);

        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        String city_name= pref.getString("city_name_Tv","N/A");
        String city = pref.getString("cityTv","N/A");
        String time = pref.getString("timeTv","N/A");
        String humidity = pref.getString("humidityTv","N/A");
        String pmData = pref.getString("pmDataTv","N/A");
        String pmQuality = pref.getString("pmQualityTv","N/A");
        String week = pref.getString("weekTv","N/A");
        String tempHigh = pref.getString("tempHighTv","N/A");
        String tempLow = pref.getString("tempLowTv","N/A");
        String climate = pref.getString("climateTv","N/A");
        String wind = pref.getString("windTv","N/A");
        city_name_Tv.setText(city_name+"天气");
        cityTv.setText(city);
        timeTv.setText(time+"发布");
        humidityTv.setText("湿度"+humidity);
        pmDataTv.setText(pmData);
        pmQualityTv.setText(pmQuality);
        weekTv.setText(week);
        temperatureTv.setText(tempHigh+"~"+tempLow);
        climateTv.setText(climate);
        windTv.setText("风力"+wind);
    }

    void updateTodayWeather(TodayWeather todayWeather){
        city_name_Tv.setText(todayWeather.getCity()+"天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime()+"发布");
        humidityTv.setText("湿度"+todayWeather.getHumidity());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh()+"~"+todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力："+todayWeather.getWind_power());
        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
    }


}

