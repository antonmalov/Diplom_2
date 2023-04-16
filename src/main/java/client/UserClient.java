package client;

import client.base.Client;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import usermodel.User;
import usermodel.UserCredentials;

import static io.restassured.RestAssured.given;

public class UserClient extends Client {

    private static final String USER_REGISTER = "auth/register";
    private static final String USER_LOGIN = "auth/login";
    private static final String USER = "auth/user";

    @Step("Create user {user}")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(getSpec())
                .body(user).log().all()
                .when()
                .post(USER_REGISTER)
                .then().log().all();
    }

    @Step("Login as {userCredentials}")
    public ValidatableResponse loginUser(UserCredentials userCredentials) {
        return given()
                .spec(getSpec())
                .body(userCredentials).log().all()
                .when()
                .post(USER_LOGIN)
                .then().log().all();
    }

    @Step("Get info user as {token}")
    public ValidatableResponse getUserInfo(String token) {
        return given()
                .spec(getSpec())
                .header("authorization", token).log().all()
                .when()
                .get(USER)
                .then().log().all();
    }

    @Step("Update user {user}")
    public ValidatableResponse updateUserWithLogin(User user, String token) {
        return given()
                .spec(getSpec())
                .header("authorization", token)
                .body(user).log().all()
                .when()
                .patch(USER)
                .then().log().all();
    }

    @Step("Update user without login {user}")
    public ValidatableResponse updateUserWithoutLogin(User user) {
        return given()
                .spec(getSpec())
                .body(user).log().all()
                .when()
                .patch(USER)
                .then().log().all();
    }


    @Step("Delete user {user}")
    public ValidatableResponse deleteUser(String token) {
        return given()
                .spec(getSpec())
                .header("authorization", token).log().all()
                .when()
                .delete(USER)
                .then().log().all();
    }




}
