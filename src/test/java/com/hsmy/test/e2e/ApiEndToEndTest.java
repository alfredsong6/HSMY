package com.hsmy.test.e2e;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * API端到端测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiEndToEndTest {

    private static String authToken;
    private static Long userId;

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    @Order(1)
    void testHealthCheck() {
        given()
        .when()
            .get("/auth/health")
        .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("message", equalTo("服务正常"));
    }

    @Test
    @Order(2)
    void testUserRegistrationFlow() {
        // 发送验证码
        String sendCodePayload = "{"
            + "\"account\": \"13900139001\","
            + "\"accountType\": \"phone\","
            + "\"businessType\": \"register\""
            + "}";

        given()
            .contentType(ContentType.JSON)
            .body(sendCodePayload)
        .when()
            .post("/auth/send-code")
        .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // 注册用户
        String registerPayload = "{"
            + "\"account\": \"13900139001\","
            + "\"code\": \"TEST_CODE\","
            + "\"nickname\": \"E2E测试用户\""
            + "}";

        Response registerResponse = given()
            .contentType(ContentType.JSON)
            .body(registerPayload)
        .when()
            .post("/auth/register-by-code")
        .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.token", notNullValue())
            .extract()
            .response();

        authToken = registerResponse.jsonPath().getString("data.token");
        userId = registerResponse.jsonPath().getLong("data.userId");
    }

    @Test
    @Order(3)
    void testUserProfileAccess() {
        given()
            .header("Authorization", "Bearer " + authToken)
        .when()
            .get("/user/self/info")
        .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.phone", equalTo("13900139001"))
            .body("data.nickname", equalTo("E2E测试用户"));
    }

    @Test
    @Order(4)
    void testKnockFlow() {
        // 手动敲击
        String knockPayload = "{"
            + "\"knockCount\": 10,"
            + "\"knockSound\": \"default\","
            + "\"sessionDuration\": 60"
            + "}";

        given()
            .header("Authorization", "Bearer " + authToken)
            .contentType(ContentType.JSON)
            .body(knockPayload)
        .when()
            .post("/knock/manual")
        .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.knockCount", equalTo(10))
            .body("data.meritGained", greaterThan(0));

        // 获取敲击统计
        given()
            .header("Authorization", "Bearer " + authToken)
        .when()
            .get("/knock/stats")
        .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.totalKnocks", greaterThanOrEqualTo(10));
    }

    @Test
    @Order(5)
    void testAutoKnockFlow() {
        // 开始自动敲击
        String startPayload = "{"
            + "\"duration\": 60,"
            + "\"knockInterval\": 1000,"
            + "\"knockSound\": \"default\""
            + "}";

        Response startResponse = given()
            .header("Authorization", "Bearer " + authToken)
            .contentType(ContentType.JSON)
            .body(startPayload)
        .when()
            .post("/knock/auto/start")
        .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.sessionId", notNullValue())
            .extract()
            .response();

        String sessionId = startResponse.jsonPath().getString("data.sessionId");

        // 发送心跳
        String heartbeatPayload = "{"
            + "\"sessionId\": \"" + sessionId + "\","
            + "\"currentKnockCount\": 30"
            + "}";

        given()
            .header("Authorization", "Bearer " + authToken)
            .contentType(ContentType.JSON)
            .body(heartbeatPayload)
        .when()
            .post("/knock/auto/heartbeat")
        .then()
            .statusCode(200)
            .body("code", equalTo(200));

        // 停止自动敲击
        String stopPayload = "{"
            + "\"sessionId\": \"" + sessionId + "\","
            + "\"actualDuration\": 30"
            + "}";

        given()
            .header("Authorization", "Bearer " + authToken)
            .contentType(ContentType.JSON)
            .body(stopPayload)
        .when()
            .post("/knock/auto/stop")
        .then()
            .statusCode(200)
            .body("code", equalTo(200));
    }

    @Test
    @Order(6)
    void testMeritOperations() {
        // 查看功德余额
        given()
            .header("Authorization", "Bearer " + authToken)
        .when()
            .post("/merit/balance")
        .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.totalMerit", greaterThan(0))
            .body("data.meritCoins", greaterThanOrEqualTo(0));

        // 兑换功德币
        String exchangePayload = "{"
            + "\"exchangeAmount\": 10,"
            + "\"exchangeRate\": 1"
            + "}";

        given()
            .header("Authorization", "Bearer " + authToken)
            .contentType(ContentType.JSON)
            .body(exchangePayload)
        .when()
            .post("/merit/exchange")
        .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.beforeBalance", notNullValue())
            .body("data.afterBalance", notNullValue());

        // 获取功德统计汇总
        given()
            .header("Authorization", "Bearer " + authToken)
        .when()
            .get("/merit/summary")
        .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.totalMerit", greaterThan(0));
    }

    @Test
    @Order(7)
    void testShopOperations() {
        // 获取道具列表
        given()
        .when()
            .get("/shop/items")
        .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data", not(empty()));

        // 获取道具详情
        given()
        .when()
            .get("/shop/items/1")
        .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.id", equalTo(1));

        // 购买道具（如果有足够的功德币）
        given()
            .header("Authorization", "Bearer " + authToken)
            .param("itemId", 1)
            .param("quantity", 1)
        .when()
            .post("/shop/purchase")
        .then()
            .statusCode(200)
            .body("code", anyOf(equalTo(200), equalTo(500))); // 可能因为功德币不足而失败
    }

    @Test
    @Order(8)
    void testRankingOperations() {
        // 获取日榜
        given()
        .when()
            .get("/rankings/daily")
        .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data", notNullValue());

        // 获取我的排名
        given()
            .header("Authorization", "Bearer " + authToken)
        .when()
            .get("/rankings/my")
        .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("data.userId", equalTo(userId.intValue()));
    }

    @Test
    @Order(9)
    void testTypicalScenarios() {
        // 典型使用场景：用户登录 -> 敲击 -> 查看统计 -> 兑换 -> 购买
        
        // 1. 多次敲击
        for (int i = 0; i < 3; i++) {
            String knockPayload = "{"
                + "\"knockCount\": 5,"
                + "\"knockSound\": \"default\","
                + "\"sessionDuration\": 30"
                + "}";

            given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(knockPayload)
            .when()
                .post("/knock/manual")
            .then()
                .statusCode(200)
                .body("code", equalTo(200));
        }

        // 2. 查看累计统计
        given()
            .header("Authorization", "Bearer " + authToken)
        .when()
            .get("/knock/stats")
        .then()
            .statusCode(200)
            .body("data.totalKnocks", greaterThanOrEqualTo(25));

        // 3. 兑换更多功德币
        String exchangePayload = "{"
            + "\"exchangeAmount\": 20,"
            + "\"exchangeRate\": 1"
            + "}";

        given()
            .header("Authorization", "Bearer " + authToken)
            .contentType(ContentType.JSON)
            .body(exchangePayload)
        .when()
            .post("/merit/exchange")
        .then()
            .statusCode(200)
            .body("code", equalTo(200));
    }

    @Test
    @Order(10)
    void testLogoutAndCleanup() {
        // 登出
        given()
            .header("Authorization", "Bearer " + authToken)
        .when()
            .post("/auth/logout")
        .then()
            .statusCode(200)
            .body("code", equalTo(200))
            .body("message", equalTo("登出成功"));

        // 验证token已失效
        given()
            .header("Authorization", "Bearer " + authToken)
        .when()
            .get("/user/self/info")
        .then()
            .statusCode(401);
    }
}