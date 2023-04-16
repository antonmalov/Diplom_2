import client.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import usermodel.User;
import usermodel.UserCredentials;
import usermodel.UserGenerator;

import java.util.Locale;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

public class UpdateUserTest {
    private User user;
    private UserClient userClient;
    private String userToken;
    private User newUser;

    @Before
    public void setUp() {
        user = UserGenerator.getRandom();
        userClient = new UserClient();
        userToken = userClient.createUser(user).extract().path("accessToken");
        newUser = UserGenerator.getRandom();
    }

    @After
    public void deleteUser() {
        if (userToken != null) {
            userClient.deleteUser(userToken);
        }
    }

    @Test
    @DisplayName("Изменение данных пользователя после авторизации")
    @Description("Проверка возможности изменения данных авторизованным пользователем")
    public void updaterUserWithLogin() {
        userClient.loginUser(UserCredentials.from(user));

        userClient.updateUserWithLogin(newUser, userToken)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", is(true))
                .and()
                .body("user.email", equalTo(newUser.getEmail().toLowerCase(Locale.ROOT)))
                .and()
                .body("user.name", equalTo(newUser.getName()));
    }

    @Test
    @DisplayName("Изменение данных пользователя без авторизации")
    @Description("Проверка невозможности изменения данных неавторизованным пользователем")
    public void updaterUserWithoutLogin() {
        userClient.updateUserWithoutLogin(newUser)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("You should be authorised"))
                .and()
                .body("success", is(false));
    }
}



