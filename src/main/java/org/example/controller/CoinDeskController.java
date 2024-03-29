package org.example.controller;

import cn.hutool.json.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import cn.hutool.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class CoinDeskController {

    @PostMapping("/api/coindesk")
    public JSONArray getCoinDeskData() {
        RestTemplate restTemplate = new RestTemplate();
        // coindesk API
        String COINDESK_APL_URL = "https://api.coindesk.com/v1/bpi/currentprice.json";
        String response = restTemplate.getForObject(COINDESK_APL_URL, String.class);

        // 參數處理
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            JsonNode bpiNode = rootNode.path("bpi");

            // 取得當前日期時間
            LocalDateTime currentTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

            // 數據處理
            JSONArray resultArray = new JSONArray();
            for (JsonNode currencyNode : bpiNode) {
                JSONObject currencyJson = new JSONObject();
                currencyJson.put("code", currencyNode.path("code").asText());
                currencyJson.put("symbol", currencyNode.path("symbol").asText());
                currencyJson.put("rate", currencyNode.path("rate").asText());
                currencyJson.put("description", currencyNode.path("description").asText());
                currencyJson.put("rateFloat", currencyNode.path("rate_float").asDouble());
                // 貨幣中文名稱
                if (currencyNode.path("code").asText().equals("USD")) {
                    currencyJson.put("chineseName", "美元");
                } else if (currencyNode.path("code").asText().equals("GBP")) {
                    currencyJson.put("chineseName", "英鎊");
                } else if (currencyNode.path("code").asText().equals("EUR")) {
                    currencyJson.put("chineseName", "歐元");
                }
                currencyJson.put("updatedTime", currentTime.format(formatter));
                resultArray.add(currencyJson);
            }
            return resultArray;
        } catch (Exception e) {
            e.printStackTrace();
            JSONArray errorArray = new JSONArray();
            JSONObject errorJson = new JSONObject();
            errorJson.put("error", "貨幣訊息錯誤");
            errorArray.add(errorJson);
            return errorArray;
        }
    }
}
