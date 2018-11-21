package com.example.wujue.bean;

public class TodayWeather {
//    今日天气
    private String city;
    private String updatetime;
    private String temperature;
    private String humidity;
    private String pm25;
    private String quality;
    private String wind_direction;
    private String wind_power;
    private String date;
    private String high;
    private String low;
    private String type;

    public String getCity(){
        return city;
    }

    public String getUpdatetime(){
        return  updatetime;
    }

    public String getTemperature(){
        return temperature;
    }

    public String getHumidity(){
        return humidity;
    }

    public String getPm25(){
        return pm25;
    }

    public String getQuality(){
        return  quality;
    }

    public String getWind_direction(){
        return wind_direction;
    }

    public String getWind_power(){
        return wind_power;
    }

    public String getDate(){
        return date;
    }

    public String getHigh(){
        return high;
    }

    public String getLow(){
        return low;
    }

    public String getType(){
        return type;
    }

    public void setCity(String city){
        this.city = city;
    }

    public void setUpdatetime(String updatetime){
        this.updatetime = updatetime;
    }

    public void setTemperature(String temperature){
        this.temperature = temperature;
    }

    public void setHumidity(String humidity){
        this.humidity = humidity;
    }

    public void setPm25(String pm25){
        this.pm25 = pm25;
    }

    public void setQuality(String quality){
        this.quality = quality;
    }

    public void setWind_direction(String wind_direction){
        this.wind_direction = wind_direction;
    }

    public void setWind_power(String wind_power){
        this.wind_power = wind_power;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setHigh(String high){
        this.high = high;
    }

    public void setLow(String low){
        this.low = low;
    }

    public void setType(String type){
        this.type = type;
    }

//    未来六天天气信息
    private String day1, high1, low1, climate1, wind1;
    private String day2, high2, low2, climate2, wind2;
    private String day3, high3, low3, climate3, wind3;
    private String day4, high4, low4, climate4, wind4;

    public String getDay1(){
        return day1;
    }

    public String getHigh1(){
        return high1;
    }

    public String getLow1(){
        return low1;
    }

    public String getClimate1(){
        return climate1;
    }

    public String getWind1(){
        return wind1;
    }

    public String getDay2(){
        return day2;
    }

    public String getHigh2(){
        return high2;
    }

    public String getLow2(){
        return low2;
    }

    public String getClimate2(){
        return climate2;
    }

    public String getWind2(){
        return wind2;
    }

    public String getDay3(){
        return day3;
    }

    public String getHigh3(){
        return high3;
    }

    public String getLow3(){
        return low3;
    }

    public String getClimate3(){
        return climate3;
    }

    public String getWind3(){
        return wind3;
    }

    public String getDay4(){
        return day4;
    }

    public String getHigh4(){
        return high4;
    }

    public String getLow4(){
        return low4;
    }

    public String getClimate4(){
        return climate4;
    }

    public String getWind4(){
        return wind4;
    }

    public void setDay1(String day1){
        this.day1 = day1;
    }

    public void setHigh1(String high1){
        this.high1 = high1;
    }

    public void setLow1(String low1){
        this.low1 = low1;
    }

    public void setClimate1(String climate1){
        this.climate1 = climate1;
    }

    public void setWind1(String wind1){
        this.wind1 = wind1;
    }

    public void setDay2(String day2){
        this.day2 = day2;
    }

    public void setHigh2(String high2){
        this.high2 = high2;
    }

    public void setLow2(String low2){
        this.low2 = low2;
    }

    public void setClimate2(String climate2){
        this.climate2 = climate2;
    }

    public void setWind2(String wind2){
        this.wind2 = wind2;
    }

    public void setDay3(String day3){
        this.day3 = day3;
    }

    public void setHigh3(String high3){
        this.high3 = high3;
    }

    public void setLow3(String low3){
        this.low3 = low3;
    }

    public void setClimate3(String climate3){
        this.climate3 = climate3;
    }

    public void setWind3(String wind3){
        this.wind3 = wind3;
    }

    public void setDay4(String day4){
        this.day4 = day4;
    }

    public void setHigh4(String high4){
        this.high4 = high4;
    }

    public void setLow4(String low4){
        this.low4 = low4;
    }

    public void setClimate4(String climate4){
        this.climate4 = climate4;
    }

    public void setWind4(String wind4){
        this.wind4 = wind4;
    }

    @Override
    public String toString(){
        return "TodayWeather{" +
                "city'" + city + '\''+
                ",updatetime='" + updatetime + '\'' +
                ",temperature='" + temperature + '\''+
                ",humidity='" + humidity + '\'' +
                ",pm25='" + pm25 + '\'' +
                ",quality='" + quality + '\'' +
                ",wind direction='" + wind_direction + '\''+
                ",wind power='" + wind_power + '\'' +
                ",date='" + date + '\'' +
                ",high='" + high + '\'' +
                ",low='" + low + '\'' +
                ",type='" + type + '\'' +
                '}'
                +"Day1{"+"day1:"+day1+"," +
                "temperature1:"+high1+"~"+low1+
                ",wind1:"+wind1+",type1:"+climate1
                +"Day2{"+"day2:"+day2+"," +
                "temperature2:"+high2+"~"+low2+
                ",wind2:"+wind2+",type2:"+climate2
                +"Day3{"+"day3:"+day3+"," +
                "temperature3:"+high3+"~"+low3+
                ",wind3:"+wind3+",type3:"+climate3
                +"Day4{"+"day4:"+day4+"," +
                "temperature4:"+high4+"~"+low4+
                ",wind4:"+wind4+",type4:"+climate4;
    }
}