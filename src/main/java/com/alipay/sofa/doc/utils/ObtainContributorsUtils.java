package com.alipay.sofa.doc.utils;

import java.util.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ObtainContributorsUtils {
    @Value("${sofa.doc.token}")
    public String token;
    public Map<String, Object> getContributors(String httpUrl, String filePath) {
        Map<String, Object> commitInfo = new HashMap<>();
        try {
            String url = "https://api.github.com/repos/" + httpUrl + "/commits?path=" + filePath;
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
            connection.setRequestProperty("Authorization", "Token "+ token);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                JSONArray jsonArray = JSONArray.parseArray(response.toString());
                String latestName = null;
                LocalDateTime latestDate = null;
                if (jsonArray.size() > 0) {
                    JSONObject latestCommit = jsonArray.getJSONObject(0);
                    JSONObject authorObject = latestCommit.getJSONObject("commit").getJSONObject("author");
                    latestName = authorObject.getString("name");
                    String date = authorObject.getString("date");
                    latestDate = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
                } else {
                    System.out.println("No commits found for the given URL.");
                }
                Set<String> uniqueNames = new HashSet<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject commitObject = jsonArray.getJSONObject(i);
                    JSONObject authorObject = commitObject.getJSONObject("commit").getJSONObject("author");
                    String name = authorObject.getString("name");
                    uniqueNames.add(name);
                }
                String uniqueNamesString = String.join(", ", uniqueNames);
                String contributors = uniqueNamesString.replace(" ", "&nbsp;");
                String lastName = latestName.replace(" ", "&nbsp;");
                commitInfo.put("latestName", "<span style='color:grey'>"+  lastName +"</span>");
                commitInfo.put("latestDate", "<span style='color:grey'>"+ latestDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) +"</span>");
                commitInfo.put("uniqueNames", "<span style='color:grey'>"+  contributors +"</span>");
            } else {
                System.out.println("HTTP request failed with response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commitInfo;
    }
}
