package client;

import client.base.Client;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import ordermodel.Order;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderClient extends Client {
    private static final String INGREDIENTS = "ingredients";
    private static final String GET_ALL_ORDERS = "orders/all";
    private static final String ORDERS = "orders";

    @Step("Get available ingredients")
    public ValidatableResponse getIngredients() {
        return given()
                .spec(getSpec()).log().all()
                .when()
                .get(INGREDIENTS)
                .then().log().all();
    }

    @Step("Получение заказов конкретного пользователя")
    public ValidatableResponse getOrderByLoginUser(String token) {
        return given()
                .spec(getSpec())
                .header("authorization", token).log().all()
                .get(ORDERS)
                .then().log().all();
    }

    @Step("Получение заказов неавторизованного пользователя")
    public ValidatableResponse getOrderByWithoutLoginUser() {
        return given()
                .spec(getSpec()).log().all()
                .get(ORDERS)
                .then().log().all();
    }

    @Step("Получение всех заказов")
    public ValidatableResponse getAllOrders() {
        return given()
                .spec(getSpec())
                .get(GET_ALL_ORDERS)
                .then().log().all();
    }

    @Step("Создание заказа авторизованным пользователем")
    public ValidatableResponse createOrderByLoginUser(Order order, String token) {
        return given()
                .spec(getSpec())
                .header("authorization", token)
                .body(order).log().all()
                .post(ORDERS)
                .then().log().all();
    }

    @Step("Создание заказа неавторизованным пользователем")
    public ValidatableResponse createOrderByWithoutUser(Order order) {
        return given()
                .spec(getSpec())
                .body(order).log().all()
                .post(ORDERS)
                .then().log().all();
    }
}
