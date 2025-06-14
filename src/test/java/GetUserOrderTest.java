import io.qameta.allure.Description;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.Steps;

import static org.hamcrest.CoreMatchers.*;

public class GetUserOrderTest {


    private final Steps step = new Steps();
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        accessToken = step.getAccessTokenForNewUser();
        step.createOrder(accessToken, step.getAnyValidIngredientIds()); // создаём заказ
    }

    @After
    public void tearDown() {
        step.deleteUser(accessToken);
    }

    @Test
    @Description("Получение заказов авторизованным пользователем")
    public void shouldGetOrdersWithAuth() {
        step.getUserOrders(accessToken)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Test
    @Description("Попытка получить заказы без авторизации")
    public void shouldNotGetOrdersWithoutAuth() {
        step.getUserOrders("")
                .then()
                .statusCode(401)
                .body("message", containsString("You should be authorised"));
    }
}

