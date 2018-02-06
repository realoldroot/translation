package com.zhengenshen.plugin.translation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * google bean
 *
 * @author zhengenshen
 * @create 2018-02-05 17:03
 */

public class GoogleBean {


    public static String build(String str) {

        System.out.println(str);
        Gson gson = new Gson();
        JsonArray jsonArray = gson.fromJson(str, JsonArray.class);
        StringBuilder sb = new StringBuilder();

        if (jsonArray.get(5).isJsonArray()) {
            JsonArray asJsonArray = jsonArray.get(5).getAsJsonArray();
            if (asJsonArray.get(0).isJsonArray()) {
                JsonArray asJsonArray1 = asJsonArray.get(0).getAsJsonArray();
                String asStr = asJsonArray.get(0).getAsJsonArray().get(0).getAsString();

                sb.append(asStr).append("\n");
                if (asJsonArray1.get(2).isJsonArray()) {
                    JsonElement jsonElement = asJsonArray1.get(2).getAsJsonArray();
                    if (jsonElement.isJsonArray()) {
                        JsonArray asJsonArray2 = jsonElement.getAsJsonArray();
                        asJsonArray2.forEach(value -> sb.append(value.getAsJsonArray().get(0).getAsString()).append("\n"));
                    }
                }
            }

        }

        return sb.toString();
    }

    public static void main(String[] args) {
        String str = "[null,null,\"en\",null,null,[[\"Project\",null,[[\"项目\",1000,true,false],[\"工程\",1000,true,false],[\"投射\",1000,true,false]],[[0,7]],\"Project\",0,0]]]";

        String asd = "asdasd你";


        boolean matches = asd.matches("[\\u4e00-\\u9fbb]+");

        System.out.println(matches);


    }

}
