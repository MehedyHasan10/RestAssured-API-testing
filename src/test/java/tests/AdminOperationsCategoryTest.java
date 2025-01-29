package tests;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

public class AdminOperationsCategoriesTest extends BaseTest{

    private static String token;
    private static String slug;
   
    
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
    }
    
    @Test(priority = 2, dependsOnMethods = {"testUserLogin"})
    public void testCreateCategory(){
        HashMap<String, String> data = new HashMap<>();
        data.put("name", "Siam Hasan");

        Response response = given()
                .cookie("access_token", token)
                .header("Content-Type", "application/json")
                .body(data)
                .when()
                .post("/categories");

        slug = response.jsonPath().getString("payload.slug");
        System.out.println("Category Slug: " + slug);
        response.then().statusCode(201);
    }
    
    @Test(priority = 3, dependsOnMethods = {"testUserLogin","testCreateCategory"})
    public void testGetCategoryBySlug() {
       given()
                .cookie("access_token", token)
                .when()
                .get("/categories/" + slug)  
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("payload.slug", equalTo(slug))
                .log().all();
       
    }

    @Test(priority = 3, dependsOnMethods = {"testUserLogin"})
    public void testGetListOfCategories() {
        given()
                .cookie("access_token", token)
                .when()
                .get("/categories")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0))
                .log().all();
    }

    @Test(priority = 4, dependsOnMethods = {"testUserLogin", "testCreateCategory"})
    public void testUpdateCategoryBySlug() {
        HashMap<String, String> updateData = new HashMap<>();
        updateData.put("name", "test Category");

        given()
                .cookie("access_token", token)  
                .header("Content-Type", "application/json")  
                .body(updateData)  
                .log().all() 
                .when()
                .put("/categories/" + slug)  
                .then()
                .statusCode(200)
                .log().all()  ;
    }

    @Test(priority = 4, dependsOnMethods = {"testUserLogin", "testCreateCategory","testGetCategoryBySlug","testGetListOfCategories"})
    public void testDeleteCategory() {
        given()
                .cookie("access_token", token) 
                .log().all()  
                .when()
                .delete("/categories/" + slug)  
                .then()
                .statusCode(200)
                .log().all() ;
    }
}
