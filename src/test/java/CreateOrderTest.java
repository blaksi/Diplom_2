import io.qameta.allure.Description;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.practicum.Steps;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;

public class CreateOrderTest {

    private final Steps step = new Steps();
    private String accessToken;
    private List<String> validIngredients;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
        accessToken = step.getAccessTokenForNewUser();
        validIngredients = step.getAnyValidIngredientIds();
    }

    @After
    public void tearDown() {
        step.deleteUser(accessToken);
    }

    @Test
    @Description("Создание заказа с авторизацией и валидными ингредиентами")
    public void shouldCreateOrderWithAuth() {
        step.createOrder(accessToken, validIngredients)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("order.owner", notNullValue());
    }

    @Test
    @Description("Создание заказа без авторизации и валидными ингредиентами")
    public void shouldCreateOrderNoAuth() {
        step.createOrder("", validIngredients)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
    }

    @Test
    @Description("Создание заказа без ингредиентов")
    public void shouldNotCreateOrderWithoutIngredients() {
        step.createOrder(accessToken, List.of())
                .then()
                .statusCode(400)
                .body("message", containsString("Ingredient ids must be provided"));
    }

    @Test
    @Description("Создание заказа с невалидным хэшем ингредиента")
    public void shouldFailWithInvalidIngredientHash() {
        step.createOrder(accessToken, List.of("61c0c5a71d1f82001bdaaad"))
                .then()
                .statusCode(500);
    }
}

