package ru.yandex.practicum;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.CreateUser;
import java.util.List;
import java.util.Map;
import java.util.Random;
import static io.restassured.RestAssured.given;

public class Steps {

    @Step("Генерация пользователя с уникальным email")
    public CreateUser generateRandomUser() {
        String email = "user" + System.currentTimeMillis() + new Random().nextInt(1000) + "@yandex.ru";
        return new CreateUser("Luka", email, "Test123!");
    }

    @Step("Регистрация пользователя")
    public Response registerUser(CreateUser user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .post("/api/auth/register");
    }

    @Step("Авторизация пользователя")
    public Response authUser(CreateUser user) {
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .post("/api/auth/login");
    }

    @Step("Получение accessToken нового пользователя")
    public String getAccessTokenForNewUser() {
        CreateUser user = generateRandomUser();
        return registerUser(user)
                .then()
                .extract()
                .path("accessToken")
                .toString()
                .replace("Bearer ", "");
    }

    @Step("Удаление пользователя")
    public void deleteUser(String accessToken) {
        if (accessToken != null && !accessToken.isEmpty()) {
            given().header("Authorization", "Bearer " + accessToken)
                    .delete("/api/auth/user");
        }
    }

    @Step("Обновление поля")
    public Response updateUserField(String token, String field, String value) {
        return given()
                .header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .body(Map.of(field, value))
                .patch("/api/auth/user");
    }

    @Step("Получение списка ингредиентов")
    public List<String> getAnyValidIngredientIds() {
        return given()
                .get("/api/ingredients")
                .then()
                .statusCode(200)
                .extract()
                .path("data._id");
    }

    @Step("Создание заказа")
    public Response createOrder(String token, List<String> ingredientIds) {
        return given().header("Authorization", "Bearer " + token)
                .header("Content-type", "application/json")
                .body(Map.of("ingredients", ingredientIds))
                .post("/api/orders");
    }

    @Step("Получение заказов пользователя")
    public Response getUserOrders(String token) {
        return given().header("Authorization", "Bearer " + token)
                .get("/api/orders");
    }
}