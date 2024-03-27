package com.vampon.vamapiinterface.utils;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

import java.util.Map;

/**
 * 签名工具
 */
public class SignUtils {
    /**
     * 生成签名
     * @param hashMap
     * @param secretKey
     * @return
     */
    public static String getSign(Map<String, String> hashMap, String secretKey)
    {
        //使用Hutool的生成算法
        Digester md5 = new Digester(DigestAlgorithm.SHA256);
        //将部分传输内容和密钥拼接进行加密，服务端再使用同样的算法生成，如果结果相同那么就说明正确
        //但是为了防止拦截重放，还需要加入随机数和时间戳
        String content = hashMap.toString() + "." + secretKey;
        String digestHex = md5.digestHex(content);
        return digestHex;
    }
}
