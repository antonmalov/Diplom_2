import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import usermodel.User;
import usermodel.UserCredentials;
import usermodel.UserGenerator;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

public class LoginUserTest {
    private User user;
    private UserClient userClient;
    private String userToken;

    @Before
    public void setUp() {
        user = UserGenerator.getRandom();
        userClient = new UserClient();
        userToken = userClient.createUser(user).extract().path("accessToken");
    }

    @After
    public void deleteUser() {
        if (userToken != null) {
            userClient.deleteUser(userToken);
        }
    }

    @Test
    @DisplayName("Авторизация пользователя в системе")
    @Description("Проверка авторизации с корректными логином и паролем")
    public void checkLoginUser() {
        userClient.loginUser(UserCredentials.from(user))
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", is(true));
    }

    @Test
    @DisplayName("Авторизация с несуществующими данными")
    @Description("Проверка невозможности авторизации по несуществующим данным")
    public void checkLoginWithNonExistDate() {
        UserCredentials userCredentials = new UserCredentials("nonExistEmail", "nonExistPassword");
        userClient.loginUser(userCredentials)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", is(false))
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }
}
