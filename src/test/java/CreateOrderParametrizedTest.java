import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import order.Order;
import order.OrderClient;
import order.ScooterColors;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import utils.BaseURL;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;

    @RunWith(Parameterized.class)
    public class CreateOrderParametrizedTest {
        String track;
        private final List<String> color;

        public CreateOrderParametrizedTest(List<String> color) {
            this.color = color;
        }

        @Parameterized.Parameters(name = "Scooter color - {0}")
        public static Object[][] chooseColor() {
            return new Object[][]{
                    {List.of(ScooterColors.BLACK_COLOR)},
                    {List.of(ScooterColors.GREY_COLOR)},
                    {List.of(ScooterColors.BLACK_COLOR, ScooterColors.GREY_COLOR)},
                    {List.of()},
            };
        }

        @Before
        public void setUp() {
            RestAssured.baseURI = BaseURL.BASE_URL;
        }

        @Test
        @DisplayName("Create an order using different scooter colors")
        public void createOrderWithDifferentColorsTest() {
            Order order = new Order("Игорь", "Овсянкин", "Профсоюзная 66", "Новые Черемушки", "89033000306", 3, "2024-04-11", "Доставка на 7 этаж", color);
            Response response = OrderClient.createOrder(order);
            //track для последующего удаления заказа
            track = response.then().extract().path("track").toString();
            response.then()
                    .assertThat()
                    .statusCode(HttpStatus.SC_CREATED)
                    .and()
                    .body("track", notNullValue());

        }

        @After
        public void tearDown() {

            OrderClient.cancelOrder(track);
        }
    }
