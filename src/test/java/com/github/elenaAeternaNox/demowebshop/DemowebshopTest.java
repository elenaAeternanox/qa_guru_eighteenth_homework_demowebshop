package com.github.elenaAeternaNox.demowebshop;

import com.codeborne.selenide.Configuration;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
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
        step("Add product to Wishlist", () -> {given()
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
    void checkWishListOnUI() {
        body = "addtocart_53.EnteredQuantity=2";

        step("Get cookie by api and set it to browser", () -> {
                    step("Get cookie and set it to browser by API", () -> {
                        String authorizationCookie = given()
                                .contentType("application/x-www-form-urlencoded")
                                .formParam("Email", "elena@qa.guru")
                                .formParam("Password", "elena@qa.guru")
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

                    step("Refresh page", () ->
                            refresh());
                });

            step("Add product to Wishlist", () -> {
                given()
                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                        .when()
                        .body(body)
                        .post("addproducttocart/details/53/2")
                        .then()
                        .statusCode(200)
                        .body("updatetopwishlistsectionhtml", is("(2)"))
                        .body("message", is("The product has been added to your <a href=\"/wishlist\">wishlist</a>"));
            });

            step("Open wish list", () ->
                    open("wishlist"));

            step("Check product in WishList", () ->
                    $(".product > a").shouldHave(text("3rd Album")));

            step("Check product quantity in WishList", () ->
                    $(".qty-input").shouldHave(value("2")));
    }
}
