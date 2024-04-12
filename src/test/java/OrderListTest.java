import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import utils.APIs;
import utils.BaseURL;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

public class OrderListTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = BaseURL.BASE_URL;
    }

    @Test
    @DisplayName("Get order list")
    public void getOrderListTest() {
        given()
                .header("Content-type", "application/json")
                .when()
                .get(APIs.ORDER_PATH)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .assertThat()
                .body("orders", notNullValue());
    }
}
