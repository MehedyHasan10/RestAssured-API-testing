package tests;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static utils.JwtUtils.extractUserIdFromToken;

public class CommonOperationsTest extends BaseTest{
    private static String token;
    private static String id;

    @Test(priority = 1)
    public void testUserLogin() {
        HashMap<String, String> data = new HashMap<>();
        data.put("email", "user_email");
        data.put("password", "user_password");

        token = given()
                .header("Content-Type", "application/json")
                .body(data)
                .when()
                .post("/auth/login")
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("User logged in successfully"))
                .extract()
                .cookie("access_token");
        
        id = extractUserIdFromToken(token);
    }

    @Test(priority = 2, dependsOnMethods = {"testUserLogin"})
    public void testUpdatePassword() {
        HashMap<String, String> updateData = new HashMap<>();
        updateData.put("oldPassword", "#MeHeDy#siam209902");
        updateData.put("newPassword", "#MeHeDy#siam130838");
        updateData.put("confirmedPassword", "#MeHeDy#siam130838");

        given()
                .cookie("access_token", token)
                .header("Content-Type", "application/json")
                .body(updateData)
                .when()
                .put("/users/update-password/" + id)
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Update password successfully"));
    }
    
    @Test(priority = 3)
    public void testForgetPassword() {
        String email = "mehedisiam10@gmail.com";
        HashMap<String, String> requestData = new HashMap<>();
        requestData.put("email", email);

        Response response = given()
                .header("Content-Type", "application/json")
                .body(requestData)
                .when()
                .post("/users/forget-password");

        response.then().log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo(String.format("Please go to your %s to reset password",email)));
        
        token = response.jsonPath().getString("payload.token");
    }

    @Test(priority = 4, dependsOnMethods = {"testForgetPassword"})
    public void testResetPassword() {
        HashMap<String, String> resetData = new HashMap<>();
        resetData.put("token", token);
        resetData.put("password", "#MeHeDy#siam209902");

        given()
                .header("Content-Type", "application/json")
                .body(resetData)
                .when()
                .put("/users/reset-password")
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Reset password successfully"));
    }

    @Test(priority = 5, dependsOnMethods = {"testUserLogin"})
    public void testUserLogout() {
        given()
                .cookie("access_token", token)
                .header("Content-Type", "application/json")
                .when()
                .post("/auth/logout")
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("User logged out successfully"));
    }
}
