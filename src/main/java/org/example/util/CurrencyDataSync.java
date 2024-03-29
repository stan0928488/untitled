package org.example.util;

import cn.hutool.json.JSONArray;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.example.controller.CoinDeskController;
import org.example.controller.CurrencyController;
@Component
@EnableScheduling
@Slf4j
public class CurrencyDataSync {

    @Autowired
    private CoinDeskController CoinDeskController;

    @Autowired
    private CurrencyController CurrencyController;

    // 20秒執行一次
    @Scheduled(fixedRate = 20000)
    public void syncCurrencyData() {
        JSONArray resultArray = CurrencyController.importFromCoinDesk();
        System.out.println("排程數據同步成功: " + resultArray);
    }
}
