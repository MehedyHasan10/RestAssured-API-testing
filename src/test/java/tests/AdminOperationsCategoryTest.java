package tests;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

public class AdminOperationsCategoryTest extends BaseTest{
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
    public void testCreateCategory() {
        String categoryName = "create category";
        HashMap<String, String> data = new HashMap<>();
        data.put("name", categoryName);

        Response response = given()
                .cookie("access_token", token)
                .header("Content-Type", "application/json")
                .body(data)
                .when()
                .post("/categories")
                .then()
                .log().all()
                .statusCode(201)
                .body("success", equalTo(true))
                .body("message", equalTo("Category is created successfully"))
                .body("payload.name", equalTo(categoryName))
                .extract().response();

        slug = response.jsonPath().getString("payload.slug");
    }

    @Test(priority = 3, dependsOnMethods = {"testUserLogin", "testCreateCategory"})
    public void testGetCategoryBySlug() {
        given()
                .cookie("access_token", token)
                .when()
                .get("/categories/" + slug)
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Category was returned successfully"))
                .body("payload.slug", equalTo(slug));
    }

    @Test(priority = 4, dependsOnMethods = {"testUserLogin"})
    public void testGetListOfCategories() {
        given()
                .cookie("access_token", token)
                .when()
                .get("/categories")
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Categories was returned successfully"))
                .body("payload.size()", greaterThan(0)); 
    }

    @Test(priority = 5, dependsOnMethods = {"testUserLogin", "testCreateCategory"})
    public void testUpdateCategoryBySlug() {
        String categoryUpdate = "update Category";
        HashMap<String, String> updateData = new HashMap<>();
        updateData.put("name", categoryUpdate);

        given()
                .cookie("access_token", token)
                .header("Content-Type", "application/json")
                .body(updateData)
                .log().all()
                .when()
                .put("/categories/" + slug)
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Category is updated successfully"))
                .body("payload.name", equalTo(categoryUpdate));  
    }

    @Test(priority = 6, dependsOnMethods = {"testUserLogin", "testCreateCategory", "testGetCategoryBySlug", "testGetListOfCategories"})
    public void testDeleteCategory() {
        given()
                .cookie("access_token", token)
                .log().all()
                .when()
                .delete("/categories/" + slug)
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Category is deleted successfully"));
    }
}
