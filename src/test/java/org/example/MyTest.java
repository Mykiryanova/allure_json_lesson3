package org.example;
import com.sun.org.apache.xpath.internal.operations.Equals;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

public class MyTest {
static Properties prop = new Properties();
static Map<String, String> headers = new HashMap<>();

    @BeforeAll
    static void setUp() throws IOException {
        RestAssured.filters(new AllureRestAssured());
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        headers.put("Authorization", "Basic bWFzaGE6MjEyMQ==");
        FileInputStream  fis = new FileInputStream("src/test/resources/properties");
        prop.load(fis);
    }

     @Test
    void getIp() {

        RestAssured.get("http://httpbin.org/ip")
                .then()
                .statusCode(200)
                .statusLine(MyProperties.status)
                .contentType(MyProperties.json);
    }

    @Test
    void getUserAgent() {
        given()
                .when()
                .request("GET", "http://httpbin.org/user-agent")
                .then()
                .statusCode(200)
                .statusLine(MyProperties.status)
                .contentType(MyProperties.json);
    }

    @Test
    void getImagesWepb() {
        given()
                .log().all()
                .when()
                .request("GET", "http://httpbin.org//image/webp")
                .then()
                .statusCode(200)
                .statusLine(MyProperties.status)
                .contentType("image/webp");
    }

    @Test
    void getImagesPng() {
        given()
                .log().uri()
                .when()
                .request("GET", "http://httpbin.org/image/png")
                .then()
                .statusCode(200)
                .statusLine(MyProperties.status)
                .contentType("image/png");
    }


    @Test
    void postResponseHeader() {
        given()
                .when()
                .post("http://httpbin.org/response-headers?freeform={freeform}", "Hello World!")
                .prettyPeek()
                .then()
                .statusCode(200)
                .statusLine(MyProperties.status)
                .body("freeform", equalTo ("Hello World!"))
                .contentType(MyProperties.json);
    }

    @Test
    void getResponseHeader() {
        String result = given()
                .when()
                .get("http://httpbin.org/response-headers?freeform={freeform}", (String)prop.get("freeform"))
                .then()
                .statusCode(200)
                .statusLine(MyProperties.status)
                .contentType(MyProperties.json)
                .extract()
                .response()
                .jsonPath()
                .getString("freeform");
        assertThat(result, equalTo("My name is Mariya"));
    }

    @Test
    void getUtf() {
        RestAssured.get("http://httpbin.org/encoding/utf8")
                .then()
                .statusCode(200)
                .statusLine(MyProperties.status)
                .contentType("text/html; charset=utf-8");
    }
    @Test
    void getDecoded() {
        given()
                .when()
                .get("http://httpbin.org/base64/{Text_decode}", (String)prop.get("Text_decode"))
               .prettyPeek()
                .then()
                .statusCode(200)
                .statusLine(MyProperties.status)
                .contentType("text/html; charset=utf-8")
                .time(lessThan(10L), TimeUnit.SECONDS);
    }
    @Test
    void getAuthBad() {
                given()
                .headers(headers)
                .when()
                .get("http://httpbin.org/basic-auth/{login}/{password_bad}", (String)prop.get("login"), (String)prop.get("password_bad"))
                .then()
                .statusCode(401);
                 }
    @Test
    void getAuthGood() {
        String result = given()
                .headers(headers)
                .when()
                .get("http://httpbin.org/basic-auth/{login}/{password_good}", (String)prop.get("login"), (String)prop.get("password_good"))
                .prettyPeek()
                .then()
                .statusCode(200)
                .statusLine(MyProperties.status)
                .contentType(MyProperties.json)
                .time(lessThan(10L), TimeUnit.SECONDS)
                .extract()
                .response()
                .jsonPath()
                .getString("user");
        assertThat(result, equalTo("masha"));
    }





}








