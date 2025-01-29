package tests;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static utils.CloudinaryImageUpload.convertToPng;

public class AdminOperationsProductTest extends BaseTest{
    private static String token;
    private static String slug;

    @Test(priority = 1)
    public void testUserLogin() {
        HashMap<String, String> data = new HashMap<>();
        data.put("email", "admin_email");
        data.put("password", "admin_password");

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
    
    @Test(priority = 2, dependsOnMethods = {"testUserLogin"})
    public void testCreateProduct() {
        try {
            String imagePath = "C:/Users/Admin/Pictures/Screenshots/a1qa.jpg";
            File imageFile = convertToPng(imagePath);

            HashMap<String, String> data = new HashMap<>();
            data.put("name", "try product");
            data.put("price", "3000");
            data.put("quantity", "40");
            data.put("size", "22 X 38");
            data.put("shipping", "0");
            data.put("category", "6575fd9709f466d0b7fc9d7e");
            data.put("description", "This is a sample product.");

            Response response = given()
                    .cookie("access_token", token)
                    .header("Content-Type", "multipart/form-data")
                    .multiPart("image", imageFile, "image/png")
                    .formParams(data)
                    .when()
                    .post("/products");

            response.then()
                    .statusCode(200)  
                    .body("success", equalTo(true))  
                    .body("message", equalTo("Product was created successfully"));

            System.out.println("Response: " + response.body().asString());
            slug = response.jsonPath().getString("payload.slug");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(priority = 3, dependsOnMethods = {"testUserLogin"})
    public void testGetListOfProducts() {
        given()
                .cookie("access_token", token)
                .when()
                .get("/products")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .body("message", equalTo("Product was returned sucessfully......"))
                .log().all();
    }

    @Test(priority = 4, dependsOnMethods = {"testUserLogin","testCreateProduct"})
    public void testGetProductBySlug() {
        given()
                .cookie("access_token", token)
                .when()
                .get("/products/" + slug)
                .then()
                .statusCode(200)
                .body("message", equalTo("Return product.."))
                .log().all();
    }

    @Test(priority = 5, dependsOnMethods = {"testUserLogin","testCreateProduct"})
    public void testUpdateProductBySlug() {
        HashMap<String, String> updateData = new HashMap<>();
        updateData.put("description","This is a sample product.");
        updateData.put("size", "21 X 32");
        updateData.put("price", "3333");

        given()
                .cookie("access_token", token)
                .header("Content-Type", "application/json")
                .body(updateData)
                .log().all()
                .when()
                .put("/products/" + slug)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message",equalTo("This product updated sucessfully.."))
                .log().all() ;
    }
    
    @Test(priority = 6,dependsOnMethods = {"testUserLogin","testCreateProduct","testGetListOfProducts","testGetProductBySlug","testUpdateProductBySlug"})
    public void testDeleteProduct() {
        given()
                .cookie("access_token", token)
                .log().all()
                .when()
                .delete("/products/" + slug)
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("This product delete sucessfully.."));
    } 
}
