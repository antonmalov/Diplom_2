import client.OrderClient;
import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import ordermodel.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import usermodel.User;
import usermodel.UserCredentials;
import usermodel.UserGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;

public class CreateOrderTest {
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
    }

    @After
    public void deleteUser() {
        if (userToken != null) {
            userClient.deleteUser(userToken);
        }
    }

    @Test
    @DisplayName("Создание заказа с валидными ингредиентами и авторизованным пользователем")
    @Description("Проверка, что авторизованный пользователь может создать заказ с валидными ингредиентами")
    public void orderCreateWithLoginAndIngredients() {
        addingIngredients();
        userToken = userClient.createUser(user).extract().path("accessToken");
        userClient.loginUser(UserCredentials.from(user));

        orderClient.createOrderByLoginUser(order, userToken)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", is(true))
                .and()
                .body("order.number", notNullValue())
                .and()
                .body("order._id", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов авторизованным пользователем")
    @Description("Проверка, что авторизованный пользователь не может создать заказ без ингредиентов")
    public void orderCreateWithLoginAndWithoutIngredients() {
        userToken = userClient.createUser(user).extract().path("accessToken");
        userClient.loginUser(UserCredentials.from(user));

        orderClient.createOrderByLoginUser(order, userToken)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("success", is(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа c ингредиентами неавторизованным пользователем")
    @Description("Проверка возможности создания заказа с ингредиентами неавторизованным пользователем")
    public void orderCreateWithoutLoginAndWithIngredients() {
        addingIngredients();
        orderClient.createOrderByWithoutUser(order)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", is(true))
                .and()
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов неавторизованным пользователем")
    @Description("Проверка, что неавторизованный пользователь не может создать заказ без ингредиентов")
    public void orderCreateWithoutLoginAndWithoutIngredients() {

        orderClient.createOrderByWithoutUser(order)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("success", is(false))
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с невалидным хешем ингредиентов")
    @Description("Проверка, что нельзя создать заказ с невалидными ингредиентами")
    public void orderCreateWithInvalidHashIngredients() {
        List<String> invalidListIngredients = new ArrayList<>();
        invalidListIngredients.add("456798poulikjhgdfgjhkjlikyuj");
        invalidListIngredients.add("9867rtyyuursdfghj;ooiluoyjt");
        invalidListIngredients.add("89o7yukhjkl4536uyredfggh");
        order.setIngredients(invalidListIngredients);
        orderClient.createOrderByWithoutUser(order)
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
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
