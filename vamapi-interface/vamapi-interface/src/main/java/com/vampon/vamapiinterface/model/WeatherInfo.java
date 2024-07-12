package com.vampon.vamapiinterface.model;

import lombok.Data;

@Data
public class WeatherInfo {
    private String province;

    private String city;

    private String weather;

    private String temperature;

    private String winddirection;

    private String windpower;
}
