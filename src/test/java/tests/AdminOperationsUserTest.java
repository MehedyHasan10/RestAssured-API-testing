package tests;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static utils.CloudinaryImageUpload.convertToPng;


public class AdminOperationsUserTest extends BaseTest {
    private static final String id = "677da0ce39bf0e1c082a0e7b";
    private static String active_token;
    private static String token;

    @Test(priority = 1)
    public void testUserLogin(){
        HashMap<String, String> data = new HashMap<>();
        data.put("email", "mehedisiam11@gmail.com");
        data.put("password", "#MeHeDy#siam209902");

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
    }
    
    @Test(priority = 2)
    public void testRegistrationUser() {
        String email = "test@example.com";
        try {
            String imagePath = "C:/Users/Admin/Pictures/Screenshots/a1qa.jpg";
            File imageFile = convertToPng(imagePath);

            HashMap<String, String> data = new HashMap<>();
            data.put("name", "test User");
            data.put("email", email);
            data.put("password", "#MeHeDy#siam209902");
            data.put("phone", "01735233728");
            data.put("address", "Baganbari, Sirajgonj");

            Response response = given()
                    .header("Content-Type", "multipart/form-data")
                    .multiPart("image", imageFile, "image/png")
                    .formParams(data)
                    .when()
                    .post("/users/process-register");

            response.then()
                    .statusCode(200)
                    .body("success", equalTo(true))
                    .body("message", equalTo(String.format("Please go to your %s to complete your registration",email)));
            
            System.out.println("Response: " + response.body().asString());
            active_token = response.jsonPath().getString("payload.token");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 3, dependsOnMethods = "testRegistrationUser")
    public void testActivateAccount() {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("token", active_token);
        
        Response response = given()
                .header("Content-Type", "application/json")
                .body(payload)
                .when()
                .post("/users/verify");
       
        response.then()
                .statusCode(201)
                .body("success", equalTo(true))
                .body("message", equalTo("User was registered successfully"));
    }
    
    @Test(priority = 4, dependsOnMethods = {"testUserLogin"})
    public void testGetListOfUsers() {
        given()
                .cookie("access_token", token)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Users Returned........"))
                .body("size()", greaterThan(0))
                .log().all();
    }

    @Test(priority = 5, dependsOnMethods = {"testUserLogin"})
    public void testAdminGetUserById() {
        given()
                .cookie("access_token", token)
                .pathParam("id", id)
                .when()
                .get("/users/{id}")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("user return successfully"))
                .body("payload.user._id", equalTo(id))  
                .log().all();
    }

    @Test(priority = 6, dependsOnMethods = {"testUserLogin"})
    public void testUpdateUserById() {
        String name = "update name";
        HashMap<String, String> updateData = new HashMap<>();
        updateData.put("name", name);

        given()
                .cookie("access_token", token)
                .header("Content-Type", "application/json")
                .body(updateData)
                .log().all()
                .when()
                .put("/users/" + id)
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("User updated successfully"))
                .body("payload.name", equalTo(name));
    }

    @Test(priority = 7, dependsOnMethods = {"testUserLogin"})
    public void testAdminBanUser() {
        given()
                .cookie("access_token", token)  
                .pathParam("id", id)  
                .when()
                .put("/users/ban-user/{id}")  
                .then()
                .statusCode(200)  
                .body("success", equalTo(true))  
                .body("message", equalTo("User is banned successfully"))  
                .log().all();  
    }

    @Test(priority = 8, dependsOnMethods = {"testUserLogin"})
    public void testAdminUnbanUser() {
        given()
                .cookie("access_token", token)
                .pathParam("id", id)
                .when()
                .put("/users/unban-user/{id}")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("User is unbanned successfully"))
                .log().all();
    }

    @Test(priority = 9, dependsOnMethods = {"testUserLogin"})
    public void testDeleteCategory() {
        given()
                .cookie("access_token", token)
                .log().all()
                .when()
                .delete("/users/" + id)
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("User deleted successfully"));
    }

    @Test(priority = 10, dependsOnMethods = {"testUserLogin","testGetListOfUsers","testAdminGetUserById","testUpdateUserById","testAdminBanUser","testAdminUnbanUser"})
    public void testUserLogout() {
        given()
                .cookie("access_token", token)
                .header("Content-Type", "application/json")
                .when()
                .post("/auth/logout")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("User logged out successfully"))
                .log().all();
    }
}
