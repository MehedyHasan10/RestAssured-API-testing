package tests;

import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

public abstract class BaseTest {
    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://localhost:3001";
        RestAssured.basePath = "/api";
    }
}
