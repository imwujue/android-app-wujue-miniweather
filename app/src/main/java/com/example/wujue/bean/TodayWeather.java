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
    private String day5, high5, low5, climate5, wind5;
    private String day6, high6, low6, climate6, wind6;

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

    public String getDay5(){
        return day5;
    }

    public String getHigh5(){
        return high5;
    }

    public String getLow5(){
        return low5;
    }

    public String getClimate5(){
        return climate5;
    }

    public String getWind5(){
        return wind5;
    }

    public String getDay6(){
        return day6;
    }

    public String getHigh6(){
        return high6;
    }

    public String getLow6(){
        return low6;
    }

    public String getClimate6(){
        return climate6;
    }

    public String getWind6(){
        return wind6;
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
                '}';
    }
}