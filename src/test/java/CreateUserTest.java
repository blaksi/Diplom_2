import io.qameta.allure.Description;
import io.restassured.RestAssured;
import models.CreateUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.Steps;

import static org.hamcrest.Matchers.*;

public class CreateUserTest {

    private final Steps step = new Steps();
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            step.deleteUser(accessToken);
        }// удаление после теста
    }

    @Test
    @Description("Успешная регистрация нового пользователя")
    public void shouldRegisterNewUser() {
        CreateUser user = step.generateRandomUser();
        accessToken = step.registerUser(user)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue())
                .extract().path("accessToken").toString().replace("Bearer ", "");
    }

    @Test
    @Description("Попытка повторной регистрации одного и того же пользователя")
    public void shouldFailToRegisterSameUserTwice() {
        CreateUser user = step.generateRandomUser();

        accessToken = step.registerUser(user).then().statusCode(200)
                .extract().path("accessToken").toString().replace("Bearer ", "");
        step.registerUser(user).then()
                .statusCode(403)
                .body("message", containsString("User already exists"));
    }

    @Test
    @Description("Попытка регистрации без обязательного поля")
    public void shouldFailToRegisterUserWithoutMail() {
        CreateUser user = new CreateUser();
        user.setName("Luka");
        user.setPassword("QQ123456");
        step.registerUser(user)
                .then()
                .statusCode(403)
                .body("message", containsString("Email, password and name are required fields"));
    }

}