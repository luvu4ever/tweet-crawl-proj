package sampleapp;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


public class jsonparser {
    public static String getJsonFromFile(String fileName) {
        String jsonText = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = br.readLine()) != null) {
                jsonText += line;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonText;
    }

    public static String getJsonFromURL(String strURL){
        String jsonText = "";
        try{
            URL url = new URL(strURL);
            InputStream is = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            while((line = br.readLine()) != null){
                jsonText += line;
            }
            is.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return jsonText;
    }

    public static String epochTimeToDate(long epochTime) {
        Date date = new java.util.Date(epochTime);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-4"));
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

//    public static String epochTimeToDate(String epochTime) {
//        Date date = new java.util.Date(Long.parseLong(epochTime)*1000L);
//        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
//        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-4"));
//        String formattedDate = sdf.format(date);
//        return formattedDate;
//    }

    public static void main(String[] args) {
//        String strJson = getJsonFromFile("E:\\Work\\Project\\Crawl_Tweet\\shiet\\all.json");
//        System.out.println(jsonText);
        String strJson = getJsonFromURL("https://api-bff.nftpricefloor.com/projects/bored-ape-yacht-club/charts/all");
        try {
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(strJson);
            JSONObject mainJsonObj = (JSONObject) obj;

//            String slug = (String) mainJsonObj.get("slug");
//            System.out.println(slug);

            JSONArray jsonArrayTimestamp = (JSONArray) mainJsonObj.get("timestamps");
//            JSONArray jsonFloorEth = (JSONArray) mainJsonObj.get("floorEth");
            JSONArray jsonFloorUSD = (JSONArray) mainJsonObj.get("floorUsd");
            JSONArray jsonSalesCount = (JSONArray) mainJsonObj.get("salesCount");
            JSONArray jsonVolumeUSD = (JSONArray) mainJsonObj.get("volumeUsd");

            for (int i = 0; i < jsonArrayTimestamp.size(); i++) {
//                System.out.println(jsonArrayTimestamp.get(i));
                long timestamp = (long) jsonArrayTimestamp.get(i);
                double floorUSD = (double) jsonFloorUSD.get(i);
                long salesCount = (long) jsonSalesCount.get(i);
                double volumeUSD = (double) jsonVolumeUSD.get(i);
                System.out.println(epochTimeToDate(timestamp) + " " + floorUSD + " " + salesCount + " " + volumeUSD);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
