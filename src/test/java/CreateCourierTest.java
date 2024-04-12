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

public class CreateCourierTest {
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
    @DisplayName("Регистрация нового курьера")
    public void createCourierPositiveTest() {
        courier = new Courier(login, password, firstName);
        Response response = CourierClient.createCourier(courier);
        id = CourierClient.signInCourier(courier).then().extract().path("id").toString();
        response.then().assertThat().statusCode(HttpStatus.SC_CREATED)
                .and()
                .body("ok", equalTo(true));

    }

    @Test
    @DisplayName("Создание курьера без имени")
    public void createCourierNoFirstNameTest() {
        courier = new Courier(login, password);
        CourierClient.createCourier(courier)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Регистрация нового курьера без логина")
    public void createCourierNoLoginTest() {
        courier = new Courier(password, firstName);
        CourierClient.createCourier(courier)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));

    }

    @Test
    @DisplayName("Создание курьера без создания пароля")
    public void createCourierNoPasswordTest() {
        courier = new Courier(login, firstName);
        CourierClient.createCourier(courier)
                .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));

    }

    @Test
    @DisplayName("Создание двух одинаковых курьеров")
    public void createDuplicateCourierTest() {
        courier = new Courier(login, password, firstName);
        CourierClient.createCourier(courier);
        CourierClient.createCourier(courier)
                .then().assertThat().statusCode(HttpStatus.SC_CONFLICT)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @After
    public void tearDown() {
        CourierClient.deleteCourier(id);
    }
}
