package com.example.wujue.bean;

public class TodayWeather {
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