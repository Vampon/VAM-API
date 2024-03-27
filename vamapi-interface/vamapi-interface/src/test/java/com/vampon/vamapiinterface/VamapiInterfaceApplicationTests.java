package com.vampon.vamapiinterface;

import com.vampon.vamapiclientsdk.client.VamApiClient;
import com.vampon.vamapiclientsdk.model.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VamapiInterfaceApplicationTests {
    @Resource
    private VamApiClient vamApiClient;
    @Test
    void contextLoads() {
//        String res1 = vamApiClient.getNameByGet("vampon");
//        User user = new User();
//        user.setUserName("tampon");
//        String res2 = vamApiClient.getUsernameByPost(user);
//        System.out.println(res1);
//        System.out.println(res2);
        String url = "http://localhost:8123/api/random_number";
        String method ="POST";
        String path = "/api/random_number";
        String requestParams = "range=10";
        String invokeResult = vamApiClient.invokeInterface(0,requestParams, url, method,path);
        System.out.println(invokeResult);
    }

//    @Test
//    void testVamApiClient()
//    {
//        String accessKey = "vampon";
//        String secretKey = "qwertyuiop";
//        VamApiClient vamApiClient = new VamApiClient(accessKey, secretKey);
//        String res1 = vamApiClient.getNameByGet("Vampon");
//        String res2 = vamApiClient.getnameByPost("Vampon");
//        User user = new User();
//        user.setUserName("tmapom");
//        String res3 = vamApiClient.getUsernameByPost(user);
//        System.out.println(res1);
//        System.out.println(res2);
//        System.out.println(res3);
//    }

}
