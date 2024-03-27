package com.vampon.vamapiinterface;

import com.vampon.vamapiclientsdk.client.VamApiClient;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class client {

    @Resource
    private VamApiClient vamApiClient;

    public static void main(String[] args) {
        System.out.println("1");
        System.out.println("2");
    }

    public void run()
    {
        String url = "http://localhost:8123/api/poison";
        String method ="POST";
        String path = "/poison";
        String requestParams = "";
        String invokeResult = vamApiClient.invokeInterface(0,requestParams, url, method,path);
        System.out.println(invokeResult);
    }
}
