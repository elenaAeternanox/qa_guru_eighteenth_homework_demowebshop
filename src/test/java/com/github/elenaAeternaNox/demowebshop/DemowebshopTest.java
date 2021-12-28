package com.github.elenaAeternaNox.demowebshop;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class DemowebshopTest {

    private String body;

    @BeforeAll
    static void prepare() {
        RestAssured.baseURI = "http://demowebshop.tricentis.com/";
        Configuration.baseUrl = "http://demowebshop.tricentis.com/";
    }

    @Test
    @Tag("API")
    void checkWishListAPI() {
        body = "addtocart_53.EnteredQuantity=1";
        step("Add product to Wishlist", () -> {
            given()
                    .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                    .body(body)
                    .when()
                    .post("addproducttocart/details/53/2")
                    .then()
                    .statusCode(200)
                    .body("updatetopwishlistsectionhtml", is("(1)"))
                    .body("message", is("The product has been added to your <a href=\"/wishlist\">wishlist</a>"));
        });
    }

    @Test
    @Tags({@Tag("API"), @Tag("UI")})
    void checkUsersAddress() {
        String login = "elena@qa.guru";
        String password = "elena@qa.guru";
        SelenideElement address = $(".address-list");

        step("Get cookie and set it to browser by API", () -> {
            String authorizationCookie = given()
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("Email", login)
                    .formParam("Password", password)
                    .when()
                    .post("login")
                    .then()
                    .statusCode(302)
                    .extract()
                    .cookie("NOPCOMMERCE.AUTH");

            step("Open minimal content, because cookie can be set when site is opened", () ->
                    open("Themes/DefaultClean/Content/images/logo.png"));

            step("Set cookie to to browser", () ->
                    getWebDriver().manage().addCookie(
                            new Cookie("NOPCOMMERCE.AUTH", authorizationCookie)));
        });

        step("Open user's address", () ->
                open("customer/addresses"));

        step("Check user's address", () -> {

            step("Check the address's title", () ->
                    address.$(".title").shouldHave(text("qa qa")));

            step("Check the user's name", () ->
                    address.$(".name").shouldHave(text("qa qa")));

            step("Check the email", () ->
                    address.$(".email").shouldHave(text("Email: elena@qu.guru")));

            step("Check the phone number", () ->
                    address.$(".phone").shouldHave(text("Phone number: +1234567")));

            step("Check the address", () ->
                    address.$(".address1").shouldHave(text("Address")));

            step("Check the city, state, zip code", () ->
                    address.$(".city-state-zip").shouldHave(text("City, Alberta ZipCode")));

            step("Check the country", () ->
                    address.$(".country").shouldHave(text("Canada")));
        });
    }
}
