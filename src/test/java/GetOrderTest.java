import client.OrderClient;
import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import ordermodel.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import usermodel.User;
import usermodel.UserCredentials;
import usermodel.UserGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

public class GetOrderTest {
    private User user;
    private Order order;
    private UserClient userClient;
    private OrderClient orderClient;
    private String userToken;

    @Before
    public void setUp() {
        user = UserGenerator.getRandom();
        order = new Order();
        userClient = new UserClient();
        orderClient = new OrderClient();
        addingIngredients();
    }

    @After
    public void deleteUser() {
        if (userToken != null) {
            userClient.deleteUser(userToken);
        }
    }

    @Test
    @DisplayName("Получение информации о заказе конкретного пользователя после авторизации")
    @Description("Проверка возможности получить информацию о заказе после авторизации")
    public void getOrderByLoginUser() {
        userToken = userClient.createUser(user).extract().path("accessToken");
        userClient.loginUser(UserCredentials.from(user));
        ValidatableResponse responseOrder = orderClient.createOrderByLoginUser(order, userToken);
        String nameBurger = responseOrder.extract().path("name");

        orderClient.getOrderByLoginUser(userToken)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", is(true))
                .and()
                .body("orders[0].name", equalTo(nameBurger));
    }

    @Test
    public void getOrderByWithoutLogin() {
        orderClient.createOrderByWithoutUser(order);
        orderClient.getOrderByWithoutLoginUser()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", is(false))
                .and()
                .body("message", equalTo("You should be authorised"));
    }

    private void addingIngredients() {
        List<String> availableIngredients = orderClient.getIngredients().extract().path("data._id");
        List<String> ingredients = new ArrayList<>();
        ingredients.add(availableIngredients.get(0));
        ingredients.add(availableIngredients.get(1));
        ingredients.add(availableIngredients.get(2));
        order.setIngredients(ingredients);
    }
}
