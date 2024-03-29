# untitled
 coildeskApi專案

#H2資料庫連線資訊
Url=http://localhost:8080/h2-console
Driver Class=org.h2.Driver
JDBC URL=jdbc:h2:mem:testdb
User Name=sa
Password:

#資料庫sql
CREATE TABLE CurrencyRates (
    ID INT PRIMARY KEY,
    CHINESE_NAME VARCHAR(255),
    CODE VARCHAR(3),
    DESCRIPTION VARCHAR(255),
    RATE VARCHAR(20),
    RATE_FLOAT DECIMAL(18, 2),
    SYMBOL VARCHAR(10),
    UPDATED_TIME TIMESTAMP)

)

1.幣別DB維護功能
(/src/main/java/org.example/controller/CurrencyController)

a.查詢
  查詢全部資料
[post]http://localhost:8080/api/currencies/getAll
  查詢by ID
[post]http://localhost:8080/api/currencies/getById

b.新增
[post]http://localhost:8080/api/currencies/create

c.修改
[post]http://localhost:8080/api/currencies/update

d.刪除
[post]http://localhost:8080/api/currencies/delete


2.呼叫coindesk的API，並進行資料轉換，組成新API。 此新API提供：
甲、更新時間（時間格式範例：1990/01/01 00:00:00）。
乙、幣別相關資訊（幣別，幣別中文名稱，以及匯率）。
[post]http://localhost:8080/api/currencies/importFromCoinDesk


3.排程同步匯率(每20更新一次)
(/src/main/java/org.example/util/CurrencyDataSync)


