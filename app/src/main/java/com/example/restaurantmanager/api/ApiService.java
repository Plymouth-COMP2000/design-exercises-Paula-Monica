package com.example.restaurantmanager.api;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.restaurantmanager.models.User;
import com.google.gson.Gson;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 ApiService - Handles all API communication
 Design Pattern: Singleton
 Threading: All API calls run on worker threads
 SOLID: Single Responsibility - Only handles API operations
 */
public class ApiService {

    private static final String TAG = "ApiService";

    // API Configuration
    private static final String BASE_URL = "http://10.240.72.69/comp2000/coursework";
    private static final String STUDENT_ID = "10921081"; // Your student ID

    // Singleton instance
    private static ApiService instance;

    // Request queue
    private RequestQueue requestQueue;

    // Gson for JSON parsing
    private Gson gson;

    // Executor for background threads
    private ExecutorService executorService;

    // Handler for main thread callbacks
    private Handler mainThreadHandler;

    //Private constructor
    private ApiService(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        gson = new Gson();
        executorService = Executors.newFixedThreadPool(4); // 4 worker threads
        mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    //Get singleton instance
    public static synchronized ApiService getInstance(Context context) {
        if (instance == null) {
            instance = new ApiService(context);
        }
        return instance;
    }

    //CALLBACK INTERFACES

    //Callback for API responses
    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    //Callback for user operations
    public interface UserCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    //API ENDPOINTS

    public void createStudentDatabase(final ApiCallback callback) {
        // Run on worker thread
        executorService.execute(() -> {
            String url = BASE_URL + "/create_student/" + STUDENT_ID;

            Log.d(TAG, "Creating student database: " + url);

            // Create POST request
            StringRequest request = new StringRequest(
                    Request.Method.POST,
                    url,
                    response -> {
                        Log.d(TAG, "Database created successfully: " + response);
                        // Callback on main thread
                        mainThreadHandler.post(() -> callback.onSuccess(response));
                    },
                    error -> {
                        String errorMsg = "Failed to create database: " + error.getMessage();
                        Log.e(TAG, errorMsg);
                        // Callback on main thread
                        mainThreadHandler.post(() -> callback.onError(errorMsg));
                    }
            );

            requestQueue.add(request);
        });
    }

    //Create new user (Sign Up)
    //Runs on worker thread

    public void createUser(User user, final ApiCallback callback) {
        executorService.execute(() -> {
            String url = BASE_URL + "/create_user/" + STUDENT_ID;

            Log.d(TAG, "Creating user: " + url);

            try {
                // Convert User to JSON
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("username", user.getUsername());
                jsonBody.put("password", user.getPassword());
                jsonBody.put("firstname", user.getFirstname());
                jsonBody.put("lastname", user.getLastname());
                jsonBody.put("email", user.getEmail());
                jsonBody.put("contact", user.getContact());
                jsonBody.put("usertype", user.getUsertype());

                Log.d(TAG, "Request body: " + jsonBody.toString());

                // Create POST request
                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        jsonBody,
                        response -> {
                            Log.d(TAG, "User created successfully: " + response.toString());
                            mainThreadHandler.post(() -> callback.onSuccess(response.toString()));
                        },
                        error -> {
                            final String errorMsg;

                            if (error.networkResponse != null) {
                                errorMsg = "Failed to create user: " + new String(error.networkResponse.data);
                            } else {
                                errorMsg = "Failed to create user: " + error.getMessage();
                            }

                            Log.e(TAG, errorMsg);
                            mainThreadHandler.post(() -> callback.onError(errorMsg));
                        }
                );

                requestQueue.add(request);

            } catch (JSONException e) {
                Log.e(TAG, "JSON Error: " + e.getMessage());
                mainThreadHandler.post(() -> callback.onError("Invalid user data: " + e.getMessage()));
            }
        });
    }

    //Login user (authenticate)
    //Fetches user by username and verifies password
    public void loginUser(String username, String password, final UserCallback callback) {
        executorService.execute(() -> {
            String url = BASE_URL + "/read_user/" + STUDENT_ID + "/" + username;

            Log.d(TAG, "Logging in user: " + url);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    response -> {
                        try {
                            Log.d(TAG, "Login response: " + response.toString());

                            // Parse user from response
                            JSONObject userJson = response.getJSONObject("user");

                            String storedPassword = userJson.getString("password");

                            // Verify password
                            if (storedPassword.equals(password)) {
                                // Password correct - create User object
                                User user = new User(
                                        userJson.getString("username"),
                                        userJson.getString("password"),
                                        userJson.getString("firstname"),
                                        userJson.getString("lastname"),
                                        userJson.getString("email"),
                                        userJson.getString("contact"),
                                        userJson.getString("usertype")
                                );

                                Log.d(TAG, "Login successful for user: " + user.getUsername());
                                mainThreadHandler.post(() -> callback.onSuccess(user));
                            } else {
                                // Password incorrect
                                Log.e(TAG, "Incorrect password");
                                mainThreadHandler.post(() -> callback.onError("Incorrect password"));
                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage());
                            mainThreadHandler.post(() -> callback.onError("Error parsing user data"));
                        }
                    },
                    error -> {
                        final String errorMsg;

                        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                            errorMsg = "User not found";
                        } else if (error.networkResponse != null) {
                            errorMsg = "Login failed: " + new String(error.networkResponse.data);
                        } else {
                            errorMsg = "Login failed: " + error.getMessage();
                        }

                        Log.e(TAG, errorMsg);
                        mainThreadHandler.post(() -> callback.onError(errorMsg));
                    }
            );

            requestQueue.add(request);
        });
    }

    //Check if user exists
    public void checkUserExists(String username, final ApiCallback callback) {
        executorService.execute(() -> {
            String url = BASE_URL + "/read_user/" + STUDENT_ID + "/" + username;

            Log.d(TAG, "Checking if user exists: " + url);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    response -> {
                        // User exists
                        mainThreadHandler.post(() -> callback.onSuccess("User exists"));
                    },
                    error -> {
                        if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                            // User doesn't exist
                            mainThreadHandler.post(() -> callback.onError("User not found"));
                        } else {
                            // Other error
                            mainThreadHandler.post(() -> callback.onError("Error checking user"));
                        }
                    }
            );

            requestQueue.add(request);
        });
    }

    //Update user information
    public void updateUser(String username, User updatedUser, final ApiCallback callback) {
        executorService.execute(() -> {
            String url = BASE_URL + "/update_user/" + STUDENT_ID + "/" + username;

            Log.d(TAG, "Updating user: " + url);

            try {
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("username", updatedUser.getUsername());
                jsonBody.put("password", updatedUser.getPassword());
                jsonBody.put("firstname", updatedUser.getFirstname());
                jsonBody.put("lastname", updatedUser.getLastname());
                jsonBody.put("email", updatedUser.getEmail());
                jsonBody.put("contact", updatedUser.getContact());
                jsonBody.put("usertype", updatedUser.getUsertype());

                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.PUT,
                        url,
                        jsonBody,
                        response -> {
                            Log.d(TAG, "User updated successfully");
                            mainThreadHandler.post(() -> callback.onSuccess(response.toString()));
                        },
                        error -> {
                            final String errorMsg = "Failed to update user: " + error.getMessage();
                            Log.e(TAG, errorMsg);
                            mainThreadHandler.post(() -> callback.onError(errorMsg));
                        }
                );

                requestQueue.add(request);

            } catch (JSONException e) {
                mainThreadHandler.post(() -> callback.onError("Invalid data: " + e.getMessage()));
            }
        });
    }
}