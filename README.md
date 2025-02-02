Technologies Used:

Java,
RestAssured,
TestNG,
Cloudinary (for image processing),
JWT (for token extraction),
JSONPath (for API response parsing)

Test Modules:

1. AdminOperationsCategoryTest
2. 
User Login: Authenticates admin user and retrieves an access token.

Create Category: Adds a new category and extracts its slug.
\
Get Category by Slug: Fetches category details using the slug.

Get List of Categories: Retrieves all available categories.

Update Category by Slug: Modifies an existing category.

Delete Category: Removes a category from the system.

3. AdminOperationsProductTest

User Login: Authenticates admin user.

Create Product: Adds a new product with image upload and extracts its slug.

Get List of Products: Fetches all products.

Get Product by Slug: Retrieves a specific product.

Update Product by Slug: Updates an existing product's details.

Delete Product: Removes a product from the system.

3. AdminOperationsUserTest

User Login: Authenticates admin user.

User Registration: Creates a new user and triggers email verification.

Activate Account: Completes user registration.

Get List of Users: Fetches all users.

Get User by ID: Retrieves user details by ID.

Update User by ID: Modifies user details.

Ban User: Temporarily disables a user.

Unban User: Reactivates a banned user.

Delete User: Permanently removes a user.

User Logout: Logs out the admin.

4. CommonOperationsTest

User Login: Authenticates a regular user.

Update Password: Changes the user's password.

Forgot Password: Triggers a password reset request.

Reset Password: Completes the password reset process.

User Logout: Logs out the user.


