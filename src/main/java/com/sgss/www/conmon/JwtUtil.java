package com.sgss.www.conmon;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    private static final String SECRET = "#@!sgssP@ssw0rd2SGSS!!!PPPKKKMMM";

    public static  String sign(String userId, int maxAge) {
        try {
            Calendar nowTime=Calendar.getInstance();
            nowTime.add(Calendar.SECOND,maxAge);
            Date expireDate=nowTime.getTime();
            System.out.println(expireDate);
            Map<String,Object> map=new HashMap<>();
            map.put("alg","HS256");
            map.put("type","JWT");
            String token= com.auth0.jwt.JWT.create()
                    .withHeader(map)
                    .withClaim("tokenId",userId)
                    .withExpiresAt(expireDate)
                    .sign(Algorithm.HMAC256(SECRET));
            return token;
        } catch(Exception e) {
            return null;
        }
    }


    /**
     * get the object of jwt if not expired
     * @param token
     * @return POJO object
     */
    public static Map<String, Claim> unsign(String token) {
        final JWTVerifier verifier = com.auth0.jwt.JWT.require(Algorithm.HMAC256(SECRET)).build();
        try {
            DecodedJWT jwt=verifier.verify(token);

            return jwt.getClaims();
        } catch (Exception e) {
            return null;
        }
    }
    public static void main(String[] args) throws InterruptedException {
        String token ="eyJ0eXBlIjoiSldUIiwiYWxnIjoiSFMyNTYiLCJ0eXAiOiJKV1QifQ.eyJ1c2VyX2lkIjoiMSIsImV4cCI6MTUzNzA2NTk1MX0.b0pdx-_YPSZ9DT09LyhrnaU_D26GyvTzyadmwwahq-Y";
        System.out.println(token);
        Map<String, Claim> map=unsign(token);
        System.out.println(map.get("user_id").asString());
      /*  System.out.println(map.get("type"));
        Thread.sleep(3*1000);
        System.out.println(new Date());
        Map<String, Claim> map2=unsign(token);
        System.out.println(map2);
        System.out.println(map2.get("user_id").asString());*/
    }
}
