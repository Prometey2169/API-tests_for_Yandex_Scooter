package courier;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import utils.APIs;

import static io.restassured.RestAssured.given;
public class CourierClient {
    @Step("Создание курьера")
    public static Response createCourier(Courier courier) {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post(APIs.CREATE_COURIER_PATH);
        return response;
    }

    @Step("Логин курьера в системе")
    public static Response signInCourier(Courier courier) {
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post(APIs.LOGIN_PATH);
        return response;
    }

    @Step("Удаление курьера")
    public static void deleteCourier(String id) {
        if (id != null)
            given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(id)
                    .when()
                    .delete(APIs.CREATE_COURIER_PATH + id);
    }
}
