import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import usermodel.User;
import usermodel.UserGenerator;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;

public class CreateUserTest {
    private UserClient userClient;
    private String userToken;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void deleteUser() {
        if (userToken != null) {
            userClient.deleteUser(userToken);
        }
    }

    @Test
    @DisplayName("Создание пользователя с корректными данными")
    @Description("Проверка, что пользователь успешно создается")
    public void userCanBeCreatedWithValidDate() {
        User user = UserGenerator.getRandom();

        userToken = userClient.createUser(user)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", is(true))
                .extract().path("accessToken");
    }

    @Test
    @DisplayName("Создание двух пользователей с одинаковыми логинами")
    @Description("Проверка невозможности создания пользователей с одинаковыми логинами")
    public void notNotBeCreatedTwoEqualUser() {
        User user = UserGenerator.getRandom();

        userClient.createUser(user)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", is(true));

        userClient.createUser(user)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", is(false))
                .and()
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя с без почты")
    @Description("Проверка невозможности создания пользователя без почты")
    public void notBeCreatedWithoutEmail() {
        User user = new User("", "345435", "Sergey");

        userClient.createUser(user)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", is(false))
                .and()
                .assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя с без пароля")
    @Description("Проверка невозможности создания пользователя без пароля")
    public void notBeCreatedWithoutPassword() {
        User user = new User("123213@yandex.ru", "", "Sergey");

        userClient.createUser(user)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", is(false))
                .and()
                .assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя с без имени")
    @Description("Проверка невозможности создания пользователя без имени")
    public void notBeCreatedWithoutName() {
        User user = new User("545432@yandex.ru", "hfgh343", "");

        userClient.createUser(user)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", is(false))
                .and()
                .assertThat().body("message", equalTo("Email, password and name are required fields"));
    }
}