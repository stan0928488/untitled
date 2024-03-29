package org.example.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Currency;
import org.example.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {

    @Autowired
    private CurrencyRepository currencyRepository;

    // 查詢(全部資料)
    @PostMapping("/getAll")
    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }


    // 查詢(byID)
    @PostMapping("/getById")
    public Optional<Currency> getCurrencyById(@RequestBody Long id) {
        return currencyRepository.findById(id);
    }


    // 新增
    @PostMapping("/create")
    public Currency createCurrency(@RequestBody Currency currency) {
        // 取得當前日期時間
        currency.setUpdatedTime(LocalDateTime.now()); // 取得當前日期時間
        return currencyRepository.save(currency);
    }


    // 修改
    @PostMapping("/update")
    public ResponseEntity<String> updateCurrency(@RequestBody Currency currency) {
        Optional<Currency> optionalCurrency = currencyRepository.findById(currency.getId());
        if (optionalCurrency.isPresent()) {
            Currency existingCurrency = optionalCurrency.get();
            existingCurrency.setChineseName(currency.getChineseName());
            existingCurrency.setCode(currency.getCode());
            existingCurrency.setDescription(currency.getDescription());
            existingCurrency.setRate(currency.getRate());
            existingCurrency.setRateFloat(currency.getRateFloat());
            existingCurrency.setSymbol(currency.getSymbol());
            existingCurrency.setUpdatedTime(currency.getUpdatedTime());
            currencyRepository.save(existingCurrency);
            return ResponseEntity.ok("Currency with ID " + currency.getId() + " updated successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // 刪除(byID)
    @PostMapping("/delete")
    public ResponseEntity<String> deleteCurrency(@RequestBody Map<String, Long> requestBody) {
        Long id = requestBody.get("id");
        try {
            currencyRepository.deleteById(id);
            return ResponseEntity.ok("Currency with ID " + id + " deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete Currency with ID " + id + ".");
        }
    }


    // coindesk API
    @PostMapping("/importFromCoinDesk")
    public JSONArray importFromCoinDesk() {
        RestTemplate restTemplate = new RestTemplate();

        // coindesk API
        String COINDESK_APL_URL = "https://api.coindesk.com/v1/bpi/currentprice.json";
        String response = restTemplate.getForObject(COINDESK_APL_URL, String.class);

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
                String code = currencyNode.path("code").asText();

                // 检查数据库中是否已存在相同的货币记录
                Optional<Currency> optionalCurrency = currencyRepository.findByCode(code);
                Currency currency;

                if (optionalCurrency.isPresent()) {
                    // 如果数据库已存在该货币记录，则更新记录
                    currency = optionalCurrency.get();
                    System.out.println("DB已存在資料，更新");
                } else {
                    // 如果数据库中不存在该货币记录，则新建记录
                    currency = new Currency();
                    currency.setCode(code);
                    System.out.println("DB未存在資料，新建");
                }

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

                // 以currency儲存數據
//                Currency currency = new Currency();
                currency.setChineseName(currencyJson.getStr("chineseName"));
                currency.setCode(currencyJson.getStr("code"));
                currency.setDescription(currencyJson.getStr("description"));
                currency.setRate(currencyJson.getStr("rate"));
                currency.setRateFloat(currencyJson.getBigDecimal("rateFloat"));
                currency.setSymbol(currencyJson.getStr("symbol"));
                currency.setUpdatedTime(currentTime);
                resultArray.add(currency);
                System.out.println("resultArray: " + resultArray);
                // 新增至DB
                currencyRepository.save(currency);
            }

            return resultArray; // 返回解析后的 JSON 数组
        } catch (Exception e) {
            e.printStackTrace();
            // 如果解析失败，返回错误消息
            JSONArray errorArray = new JSONArray();
            JSONObject errorJson = new JSONObject();
            errorJson.put("error", "貨幣訊息錯誤");
            errorArray.add(errorJson);
            return errorArray;
        }
    }
}
