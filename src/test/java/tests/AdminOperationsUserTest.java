package tests;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class AdminOperationsTest extends BaseTest {
    
//    @Test(priority = 2)
//    public void testCreateUser() {
//        HashMap<String, String> data = new HashMap<>();
//        data.put("name", "Siam");
//        data.put("job", "QA");
//
//       id = given()
//                .header("Content-Type", "application/json")
//                .body(data)
//                .when()
//                .post("/users")
//                .jsonPath().getInt("id");
////                .then()
////                .statusCode(201)
////                .body("name", equalTo("Siam"))
////                .body("job", equalTo("QA"))
////                .log().all();
//    }
//
//    @Test(priority = 3,dependsOnMethods = {"testCreateUser"})
//    public void testUpdateUser() {
//        HashMap<String, String> data = new HashMap<>();
//        data.put("name", "Md.Mehedy Hasan Siam");
//        data.put("job", "QA automation Engineer");
//
//        given()
//                .header("Content-Type", "application/json")
//                .body(data)
//                .when()
//                .put("/users/"+id)
//                .then()
//                .statusCode(200)
//                .log().all();
//    }
//    
//    @Test(priority = 4)
//    public void testDeleteUser(){
//        given()
//                .when()
//                .delete("/users/"+id)
//                .then()
//                .statusCode(204)
//                .log().all();
//    }

    String id;
    private static String active_token;
    private static String imageUrl;
    //   private static final String id = "65887c1301915c7d0bb788e4";
    private static String token;

    //--------------Common operations Starts here

    @Test(priority = 0)
    public void testCreateUser() {
        try {
            // Upload image to Cloudinary
            String imagePath = "C:/Users/Admin/Pictures/Screenshots/a1qa.jpg";
            imageUrl = CloudinaryImageUpload.uploadImage(imagePath);

            // Validate that image URL is not null or empty
            if (imageUrl == null || imageUrl.isEmpty()) {
                throw new RuntimeException("Image upload failed or returned an empty URL.");
            }

            // Prepare the request payload
            HashMap<String, String> data = new HashMap<>();
            data.put("name", "Siam Hasan");
            data.put("email", "siam@example.com");
            data.put("password", "#MeHeDy#siam209902");
            data.put("phone", "01735233728");
            data.put("address", "Baganbari, Sirajgonj");
            data.put("image", imageUrl);

            System.out.println("Request Payload: " + data);
            
            Response response = given()
                    .header("Content-Type", "application/json")
                    .body(data)
                    .when()
                    .post("/users/process-register");

            // Print the response for debugging
            System.out.println("Response: " + response.body().asString());

            // Validate the response status
            response.then().statusCode(200);

            // Extract token from the response for activation
            active_token = response.jsonPath().getString("payload.token");
            if (active_token == null || active_token.isEmpty()) {
                throw new RuntimeException("Token is null or empty. Cannot proceed with account activation.");
            }

            System.out.println("User created with token: " + active_token);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("User creation failed.", e);
        }
    }

    @Test(priority = 1, dependsOnMethods = "testCreateUser")
    public void testActivateAccount() {
        try {
            // Validate that the token is available for activation
            if (active_token == null || active_token.isEmpty()) {
                throw new RuntimeException("Token is null or empty. Activation cannot proceed.");
            }

            // Ensure the image URL is available
            if (imageUrl == null || imageUrl.isEmpty()) {
                throw new RuntimeException("Image URL is null or empty. Cannot activate account.");
            }

            // Prepare the activation payload with both token and image
            HashMap<String, String> payload = new HashMap<>();
            payload.put("token", active_token);  // Add the token
            payload.put("image", imageUrl);  // Add the image URL (same as during user creation)

            // Send POST request to activate account
            Response response = given()
                    .header("Content-Type", "application/json")
                    .body(payload)
                    .when()
                    .post("/users/verify");

            // Print the response for debugging
            System.out.println("Activation Response: " + response.body().asString());

            // Validate the activation response
            response.then().statusCode(200)
                    .body("success", equalTo(true))
                    .body("message", equalTo("Account has been activated successfully."));

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Account activation failed.", e);
        }
    }
    
    
    
    @Test(priority = 1)
    public void testUserLogin(){
        HashMap<String, String> data = new HashMap<>();
        data.put("email", "mehedisiam11@gmail.com");
        data.put("password", "#MeHeDy#siam209902");

        Response response = given()
                .header("Content-Type", "application/json")
                .body(data)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .log().all()
                .extract().response();
        
        token = response.getCookie("access_token");
        String refreshToken = response.getCookie("refresh_token");

        System.out.println("Access Token: " + token);
        System.out.println("Refresh Token: " + refreshToken);
    }

    //--------------Common operations ends here

    //--------------Admin do users operations Starts here
    @Test(priority = 2, dependsOnMethods = {"testUserLogin"})
    public void testGetListOfUsers() {
        given()
                .cookie("access_token", token)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .log().all();
    }

    @Test(priority = 3, dependsOnMethods = {"testUserLogin"})
    public void testAdminGetUserById() {
        given()
                .cookie("access_token", token)
                .pathParam("id", id)
                .when()
                .get("/users/{id}")
                .then()
                .statusCode(200)
                .body("payload.user._id", equalTo(id))  
                .log().all();
    }

    @Test(priority = 4, dependsOnMethods = {"testUserLogin"})
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

    @Test(priority = 5, dependsOnMethods = {"testUserLogin"})
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

    @Test(priority = 6, dependsOnMethods = {"testUserLogin"})
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
    //--------------Admin do users operations ends here

}
