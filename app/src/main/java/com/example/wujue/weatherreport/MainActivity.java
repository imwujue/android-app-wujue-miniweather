package com.example.wujue.weatherreport;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AndroidException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wujue.app.MyApplication;
import com.example.wujue.bean.City;
import com.example.wujue.util.NetUtil;
import com.example.wujue.bean.TodayWeather;
import com.example.wujue.util.ViewPagerAdapter;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements View.OnClickListener,ViewPager.OnPageChangeListener{
    private static final int UPDATE_TODAY_WEATHER =  1;
//    刷新按钮
    private ImageView mUpdateBtn;
//    今日天气信息
    private TextView cityTv, timeTv, weekTv, humidityTv, pmDataTv, pmQualityTv, temperatureTv,climateTv,windTv,city_name_Tv;
//    天气/PM2.5示意图
    private ImageView weatherImg, pmImg;
//    城市选择按钮
    private ImageView mCitySelect;
//    定位按钮
    private ImageView mlocationBtn;
//  viewpager展示页
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private List<View> views;
    private View view1, view2;
    private ImageView[] dots;
    private int[]ids = {R.id.dot1, R.id.dot2};
//    未来六日天气信息
    private TextView Futherday1, Futhertemperature1, Futherclimate1, Futherwind1;
    private TextView Futherday2, Futhertemperature2, Futherclimate2, Futherwind2;
    private TextView Futherday3, Futhertemperature3, Futherclimate3, Futherwind3;
    private TextView Futherday4, Futhertemperature4, Futherclimate4, Futherwind4;
    private ImageView Futherweather1, Futherweather2, Futherweather3, Futherweather4;

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
//         检查网络状态
        if(NetUtil.getNetworkState(this)!=NetUtil.NETWORK_NONE){
            Log.d("myWeather", "网络ok");
            Toast.makeText(MainActivity.this,"网络ok",Toast.LENGTH_LONG).show();
        }
        else {
            Log.d("myWeather", "网络error");
            Toast.makeText(MainActivity.this,"网络error",Toast.LENGTH_LONG).show();
        }
//        监听刷新按钮
        mUpdateBtn = (ImageView)findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);
//        监听城市选择按钮
        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);
//        监听定位按钮
        mlocationBtn = (ImageView)findViewById(R.id.title_location);
        mlocationBtn.setOnClickListener(this);
//        初始化滑动界面
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        LayoutInflater inflater = getLayoutInflater();
        views = new ArrayList<View>();
        view1 = inflater.inflate(R.layout.sixday1,null);
        view2 = inflater.inflate(R.layout.sixday2,null);
        views.add(view1);
        views.add(view2);
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

//        点击城市定位按钮
        if(view.getId()==R.id.title_location){
            MyApplication app = (MyApplication)getApplication();
            List<City> cityList = app.getCityList();
            Context context = MainActivity.this;
            LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
            String currentCity = getCurrentLocation(locationManager,context);
            if(currentCity == null){
                return;
            }
            for(City city1:cityList){
                if(city1.getCity().equals(currentCity)){
                    String cityCode = city1.getNumber();
                    queryWeatherCode(cityCode);
                }
            }
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
//                        使用SharePreferences对数据进行存储
                        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
//                        今日天气
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
//                        未来六日天气
                        String day1 = todayWeather.getDay1();
                        String high1 = todayWeather.getHigh1();
                        String low1 = todayWeather.getLow1();
                        String climate1 = todayWeather.getClimate1();
                        String wind1 = todayWeather.getWind1();

                        String day2 = todayWeather.getDay2();
                        String high2 = todayWeather.getHigh2();
                        String low2 = todayWeather.getLow2();
                        String climate2 = todayWeather.getClimate2();
                        String wind2 = todayWeather.getWind2();

                        String day3 = todayWeather.getDay3();
                        String high3 = todayWeather.getHigh3();
                        String low3 = todayWeather.getLow3();
                        String climate3 = todayWeather.getClimate3();
                        String wind3 = todayWeather.getWind3();

                        String day4 = todayWeather.getDay4();
                        String high4 = todayWeather.getHigh4();
                        String low4 = todayWeather.getLow4();
                        String climate4 = todayWeather.getClimate4();
                        String wind4 = todayWeather.getWind4();

                        editor.putString("day1",day1);
                        editor.putString("high1",high1);
                        editor.putString("low1",low1);
                        editor.putString("climate1",climate1);
                        editor.putString("wind1",wind1);

                        editor.putString("day2",day2);
                        editor.putString("high2",high2);
                        editor.putString("low2",low2);
                        editor.putString("climate2",climate2);
                        editor.putString("wind2",wind2);

                        editor.putString("day3",day3);
                        editor.putString("high3",high3);
                        editor.putString("low3",low3);
                        editor.putString("climate3",climate3);
                        editor.putString("wind3",wind3);

                        editor.putString("day4",day4);
                        editor.putString("high4",high4);
                        editor.putString("low4",low4);
                        editor.putString("climate4",climate4);
                        editor.putString("wind4",wind4);

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
                            } else if(xmlPullParser.getName().equals("date") && dateCount == 1){
                                eventType = xmlPullParser.next();
                                todayWeather.setDay1(xmlPullParser.getText());
                                Log.d("myWeather", "date1: " + xmlPullParser.getText());
                                dateCount++;
                            } else if(xmlPullParser.getName().equals("date") && dateCount == 2){
                                eventType = xmlPullParser.next();
                                todayWeather.setDay2(xmlPullParser.getText());
                                Log.d("myWeather", "date2: " + xmlPullParser.getText());
                                dateCount++;
                            } else if(xmlPullParser.getName().equals("date") && dateCount == 3){
                                eventType = xmlPullParser.next();
                                todayWeather.setDay3(xmlPullParser.getText());
                                Log.d("myWeather", "date3: " + xmlPullParser.getText());
                                dateCount++;
                            } else if(xmlPullParser.getName().equals("date") && dateCount == 4){
                                eventType = xmlPullParser.next();
                                todayWeather.setDay4(xmlPullParser.getText());
                                Log.d("myWeather", "date4: " + xmlPullParser.getText());
                                dateCount++;
                            } else if(xmlPullParser.getName().equals("high") && highCount == 1){
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh1(xmlPullParser.getText().substring(2).trim());
                                Log.d("myWeather", "high1: " + xmlPullParser.getText());
                                highCount++;
                            } else if(xmlPullParser.getName().equals("high") && highCount == 2){
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh2(xmlPullParser.getText().substring(2).trim());
                                Log.d("myWeather", "high2: " + xmlPullParser.getText());
                                highCount++;
                            } else if(xmlPullParser.getName().equals("high") && highCount == 3){
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh3(xmlPullParser.getText().substring(2).trim());
                                Log.d("myWeather", "high3: " + xmlPullParser.getText());
                                highCount++;
                            } else if(xmlPullParser.getName().equals("high") && highCount == 4){
                                eventType = xmlPullParser.next();
                                todayWeather.setHigh4(xmlPullParser.getText().substring(2).trim());
                                Log.d("myWeather", "high4: " + xmlPullParser.getText());
                                highCount++;
                            } else if(xmlPullParser.getName().equals("low") && lowCount == 1){
                                eventType = xmlPullParser.next();
                                todayWeather.setLow1(xmlPullParser.getText().substring(2).trim());
                                Log.d("myWeather", "low1: " + xmlPullParser.getText());
                                lowCount++;
                            } else if(xmlPullParser.getName().equals("low") && lowCount == 2){
                                eventType = xmlPullParser.next();
                                todayWeather.setLow2(xmlPullParser.getText().substring(2).trim());
                                Log.d("myWeather", "low2: " + xmlPullParser.getText());
                                lowCount++;
                            } else if(xmlPullParser.getName().equals("low") && lowCount == 3){
                                eventType = xmlPullParser.next();
                                todayWeather.setLow3(xmlPullParser.getText().substring(2).trim());
                                Log.d("myWeather", "low3: " + xmlPullParser.getText());
                                lowCount++;
                            } else if(xmlPullParser.getName().equals("low") && lowCount == 4){
                                eventType = xmlPullParser.next();
                                todayWeather.setLow4(xmlPullParser.getText().substring(2).trim());
                                Log.d("myWeather", "low4: " + xmlPullParser.getText());
                                lowCount++;
                            } else if(xmlPullParser.getName().equals("type") && typeCount == 1){
                                eventType = xmlPullParser.next();
                                todayWeather.setClimate1(xmlPullParser.getText());
                                Log.d("myWeather", "climate1: " + xmlPullParser.getText());
                                typeCount++;
                            } else if(xmlPullParser.getName().equals("type") && typeCount == 2){
                                eventType = xmlPullParser.next();
                                todayWeather.setClimate2(xmlPullParser.getText());
                                Log.d("myWeather", "climate2: " + xmlPullParser.getText());
                                typeCount++;
                            } else if(xmlPullParser.getName().equals("type") && typeCount == 3){
                                eventType = xmlPullParser.next();
                                todayWeather.setClimate3(xmlPullParser.getText());
                                Log.d("myWeather", "climate3: " + xmlPullParser.getText());
                                typeCount++;
                            } else if(xmlPullParser.getName().equals("type") && typeCount == 4){
                                eventType = xmlPullParser.next();
                                todayWeather.setClimate4(xmlPullParser.getText());
                                Log.d("myWeather", "climate4: " + xmlPullParser.getText());
                                typeCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 1) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWind1(xmlPullParser.getText());
                                Log.d("myWeather", "wind power1: " + xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 2) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWind2(xmlPullParser.getText());
                                Log.d("myWeather", "wind power2: " + xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 3) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWind3(xmlPullParser.getText());
                                Log.d("myWeather", "wind power3: " + xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 4) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWind4(xmlPullParser.getText());
                                Log.d("myWeather", "wind power4: " + xmlPullParser.getText());
                                fengliCount++;
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
//        今日天气
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


//        未来六日天气
//        Futherday1 = (TextView) findViewById(R.id.day1);
        Futherday1 = view1.findViewById(R.id.day1);
        Futhertemperature1 = view1.findViewById(R.id.temperature1);
        Futherclimate1 = view1.findViewById(R.id.climate1);
        Futherwind1 = view1.findViewById(R.id.wind1);
        Futherweather1 = view1.findViewById(R.id.weather_img1);

        Futherday2 = view1.findViewById(R.id.day2);
        Futhertemperature2 = view1.findViewById(R.id.temperature2);
        Futherclimate2 = view1.findViewById(R.id.climate2);
        Futherwind2 = view1.findViewById(R.id.wind2);
        Futherweather2 = view1.findViewById(R.id.weather_img2);

        Futherday3 = view2.findViewById(R.id.day3);
        Futhertemperature3 = view2.findViewById(R.id.temperature3);
        Futherclimate3 = view2.findViewById(R.id.climate3);
        Futherwind3 = view2.findViewById(R.id.wind3);
        Futherweather3 = view2.findViewById(R.id.weather_img3);

        Futherday4 = view2.findViewById(R.id.day4);
        Futhertemperature4 = view2.findViewById(R.id.temperature4);
        Futherclimate4 = view2.findViewById(R.id.climate4);
        Futherwind4 = view2.findViewById(R.id.wind4);
        Futherweather4 = view2.findViewById(R.id.weather_img4);

        viewPagerAdapter = new ViewPagerAdapter(this,views);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOnClickListener(this);

        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
//        今日天气
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
//        未来六日天气
        String futureday1 = pref.getString("day1","N/A");
        String futurehigh1 = pref.getString("high1","N/A");
        String futurelow1 = pref.getString("low1","N/A");
        String futurewind1 = pref.getString("wind1","N/A");
        String futureclimate1 = pref.getString("climate1","N/A");
        String futureday2 = pref.getString("day2","N/A");
        String futurehigh2 = pref.getString("high2","N/A");
        String futurelow2 = pref.getString("low2","N/A");
        String futurewind2 = pref.getString("wind2","N/A");
        String futureclimate2 = pref.getString("climate2","N/A");
        String futureday3 = pref.getString("day3","N/A");
        String futurehigh3 = pref.getString("high3","N/A");
        String futurelow3 = pref.getString("low3","N/A");
        String futurewind3 = pref.getString("wind3","N/A");
        String futureclimate3 = pref.getString("climate3","N/A");
        String futureday4 = pref.getString("day4","N/A");
        String futurehigh4 = pref.getString("high4","N/A");
        String futurelow4 = pref.getString("low4","N/A");
        String futurewind4 = pref.getString("wind4","N/A");
        String futureclimate4 = pref.getString("climate4","N/A");
        Futherday1.setText(futureday1);
        Futhertemperature1.setText(futurehigh1+"~"+futurelow1);
        Futherclimate1.setText(futureclimate1);
        Futherwind1.setText("风力"+futurewind1);
        Futherday2.setText(futureday2);
        Futhertemperature2.setText(futurehigh2+"~"+futurelow2);
        Futherclimate2.setText(futureclimate2);
        Futherwind2.setText("风力"+futurewind2);
        Futherday3.setText(futureday3);
        Futhertemperature3.setText(futurehigh3+"~"+futurelow3);
        Futherclimate3.setText(futureclimate3);
        Futherwind3.setText("风力"+futurewind3);
        Futherday4.setText(futureday4);
        Futhertemperature4.setText(futurehigh4+"~"+futurelow4);
        Futherclimate4.setText(futureclimate4);
        Futherwind4.setText("风力"+futurewind4);
//      显示翻动点
        dots = new ImageView[views.size()];
        for(int i = 0; i<views.size(); i++){
            dots[i] = (ImageView)findViewById(ids[i]);
        }
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
        setWeatherImg(todayWeather.getType(),weatherImg);
        setPmImg(Integer.parseInt(todayWeather.getPm25()));
        Log.d("myWeather", todayWeather.getDay1());
        Log.d("myWeather", todayWeather.getHigh1());
        Log.d("myWeather", todayWeather.getLow1());
        Log.d("myWeather", todayWeather.getClimate1());
        Log.d("myWeather", todayWeather.getWind1());

        Futherday1.setText(todayWeather.getDay1());
        Futhertemperature1.setText(todayWeather.getHigh1()+"~"+todayWeather.getLow1());
        Futherclimate1.setText(todayWeather.getClimate1());
        Futherwind1.setText("风力"+todayWeather.getWind1());
        setWeatherImg(todayWeather.getClimate1(),Futherweather1);

        Futherday2.setText(todayWeather.getDay2());
        Futhertemperature2.setText(todayWeather.getHigh2()+"~"+todayWeather.getLow2());
        Futherclimate2.setText(todayWeather.getClimate2());
        Futherwind2.setText("风力"+todayWeather.getWind2());
        setWeatherImg(todayWeather.getClimate2(),Futherweather2);

        Futherday3.setText(todayWeather.getDay3());
        Futhertemperature3.setText(todayWeather.getHigh3()+"~"+todayWeather.getLow3());
        Futherclimate3.setText(todayWeather.getClimate3());
        Futherwind3.setText("风力"+todayWeather.getWind3());
        setWeatherImg(todayWeather.getClimate3(),Futherweather3);

        Futherday4.setText(todayWeather.getDay4());
        Futhertemperature4.setText(todayWeather.getHigh4()+"~"+todayWeather.getLow4());
        Futherclimate4.setText(todayWeather.getClimate4());
        Futherwind4.setText("风力"+todayWeather.getWind4());
        setWeatherImg(todayWeather.getClimate4(),Futherweather4);

        Toast.makeText(MainActivity.this,"更新成功！",Toast.LENGTH_SHORT).show();
    }

    private String getCurrentLocation(LocationManager locationManager, Context context){
//        判断是否打开定位功能
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            Log.d("location","!!!!!");
            Toast.makeText(context,"打开程序定位权限",Toast.LENGTH_LONG).show();
            return null;
        }
//        使用网络提供商提供的经纬度地址
        List<String> providerList = locationManager.getProviders(true);
        Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currentLocation == null){
            Toast.makeText(context,"无法获取当前位置",Toast.LENGTH_LONG).show();
            return null;
        }
        return getCityByLocation(currentLocation,context);
    }

    private String getCityByLocation(Location location, Context context){
//        位置解析包
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try{
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            Log.d("location","####");
            if(addresses.size() > 0){
                Address address = addresses.get(0);
                String city = address.getLocality();
//                使用正则式除掉"市县乡州村"
                Log.d("location",city);
                city = city.replaceAll("([市县乡州村])","");
                Log.d("location",city);
                Toast.makeText(context,String.format("当前位置 %s", city), Toast.LENGTH_SHORT).show();
                return city;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){

    }

    @Override
    public  void onPageSelected(int position){
        for(int a = 0; a<ids.length; a++){
            if(a == position){
                dots[a].setImageResource(android.R.drawable.button_onoff_indicator_on);
            }else{
                dots[a].setImageResource(android.R.drawable.button_onoff_indicator_off);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state){

    }

    public void setWeatherImg(String type, ImageView weatherImg){
        if(type.equals("暴雪")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoxue);
        } else if(type.equals("暴雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_baoyu);
        } else if(type.equals("大暴雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
        } else if(type.equals("大雪")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_daxue);
        } else if(type.equals("大雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_dayu);
        } else if(type.equals("多云")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_duoyun);
        } else if(type.equals("雷阵雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
        } else if(type.equals("雷阵雨冰雹")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
        } else if(type.equals("晴")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_qing);
        } else if(type.equals("沙尘暴")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
        } else if(type.equals("特大暴雨")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
        } else if(type.equals("雾")){
            weatherImg.setImageResource(R.drawable.biz_plugin_weather_wu);
        }
    }

    public void setPmImg(int pm2_5){
        if(pm2_5<=50){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_0_50);
        } else if(pm2_5>50 && pm2_5<=100){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
        } else if(pm2_5>100 && pm2_5<=150){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_101_150);
        } else if(pm2_5>150 && pm2_5<=200){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_151_200);
        } else if(pm2_5>200 && pm2_5<=300){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_201_300);
        } else if(pm2_5>300){
            pmImg.setImageResource(R.drawable.biz_plugin_weather_51_100);
        }
    }

}

