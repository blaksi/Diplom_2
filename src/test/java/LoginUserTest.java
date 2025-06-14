import io.qameta.allure.Description;
import io.restassured.RestAssured;
import models.CreateUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.Steps;

import static org.hamcrest.Matchers.*;

public class LoginUserTest {
    private final Steps step = new Steps();
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @After
    public void tearDown() {   // удаление после теста
        if (accessToken != null) {
            step.deleteUser(accessToken);
        }
    }

    @Test
    @Description("Успешная авторизация")
    public void checkAuthenticationUser() {
        CreateUser user = step.generateRandomUser();
        accessToken = step.registerUser(user)
                .then().extract().path("accessToken").toString().replace("Bearer ", "");

        step.authUser(user)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @Description("Попытка  авторизации с неверным паролем")
    public void shouldFailAuthInvalidUser() {
        CreateUser user = step.generateRandomUser();
        accessToken = step.registerUser(user)
                .then().extract().path("accessToken").toString().replace("Bearer ", "");
        user.setPassword("45667sdfg");
        step.authUser(user)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", containsString("email or password are incorrect"));
    }

    @Test
    @Description("Попытка  авторизации с неверным логином")
    public void shouldFailAuthInvalidUserLogin() {
        CreateUser user = step.generateRandomUser();
        accessToken = step.registerUser(user)
                .then().extract().path("accessToken").toString().replace("Bearer ", "");
        user.setEmail("45667s@dfddg.ru");
        step.authUser(user)
                .then()
                .statusCode(401)
                .body("success", equalTo(false))
                .body("message", containsString("email or password are incorrect"));
    }

}
