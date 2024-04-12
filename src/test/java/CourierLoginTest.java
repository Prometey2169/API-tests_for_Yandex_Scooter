import courier.Courier;
import courier.CourierClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.BaseURL;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CourierLoginTest {
    private static String login;
    private static String password;
    private static String firstName;


    Courier courier;
    String id;

    @Before
    public void setUp() {
        RestAssured.baseURI = BaseURL.BASE_URL;
        login = RandomStringUtils.randomAlphabetic(10);
        password = RandomStringUtils.randomAlphabetic(8);
        firstName = RandomStringUtils.randomAlphabetic(8);
    }

    @Test
    @DisplayName("Корректная авторизация курьера")
    public void loginCourierPositiveTest() {
        courier = new Courier(login, password, firstName);
        CourierClient.createCourier(courier);
        Response response = CourierClient.signInCourier(courier);
        response.then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .and()
                .body("id", notNullValue());
        //id для последующего удаления курьера
        id = response.then().extract().path("id").toString();
    }

    @Test
    @DisplayName("Попытка входа для незарегистрированного курьера")
    public void loginNotExistedCourierTest() {
        courier = new Courier(login, password, firstName);
        CourierClient.signInCourier(courier)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Вход курьера без логина")
    public void loginWithoutLoginTest() {
        courier = new Courier(password, firstName);
        CourierClient.createCourier(courier);
        CourierClient.signInCourier(courier)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Вход курьера без пароля")
    public void loginWithoutPasswordTest() {
        courier = new Courier(login, firstName);
        CourierClient.createCourier(courier);
        CourierClient.signInCourier(courier)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для входа"));
    }


    @Test
    @DisplayName("Попытка входа с неверным логином курьера")
    public void loginWithIncorrectLoginTest() {
        courier = new Courier(login, password, firstName);
        CourierClient.createCourier(courier);
        Courier incorrectCourier = new Courier(RandomStringUtils.randomAlphabetic(10), courier.getPassword(), courier.getFirstName());
        CourierClient.signInCourier(incorrectCourier)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Попытка входа с неверным паролем курьера")
    public void loginWithIncorrectPasswordTest() {
        courier = new Courier(login, password, firstName);
        CourierClient.createCourier(courier);
        Courier incorrectCourier = new Courier(courier.getLogin(), RandomStringUtils.randomAlphabetic(10), courier.getFirstName());
        CourierClient.signInCourier(incorrectCourier)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));
    }



    @After
    public void tearDown() {

        CourierClient.deleteCourier(id);
    }
}
