import io.qameta.allure.Description;
import io.restassured.RestAssured;
import models.CreateUser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.practicum.Steps;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class ChangeUserTest {

    private final Steps step = new Steps();
    private final String field;
    private final String newValue;
    private String accessToken;

    public ChangeUserTest(String field, String newValue) {
        this.field = field;
        this.newValue = newValue;
    }

    @Parameterized.Parameters(name = "Обновление поля")
    public static Object[][] data() {
        return new Object[][]{
                {"name", "NewName"},
                {"email", "new" + System.currentTimeMillis() + "@mail.ru"}
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        CreateUser user = step.generateRandomUser();
        accessToken = step.registerUser(user)
                .then().statusCode(200)
                .extract().path("accessToken").toString().replace("Bearer ", "");
    }

    @After
    public void tearDown() {
        if (accessToken != null) step.deleteUser(accessToken);
    }

    @Test
    @Description("Обновление данных пользователя")
    public void shouldUpdateUserField() {
        step.updateUserField(accessToken, field, newValue)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("user." + field, equalTo(newValue));
    }

    @Test
    @Description("Попытка изменения имени без авторизации")
    public void shouldNotUpdateUserWithoutAuth() {
        step.updateUserField("", field, newValue)
                .then()
                .statusCode(401)
                .body("message", containsString("You should be authorised"));
    }
}