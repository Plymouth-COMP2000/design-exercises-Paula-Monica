package com.example.restaurantmanager.activities.staff;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.example.restaurantmanager.R;
import com.example.restaurantmanager.database.DatabaseHelper;
import com.example.restaurantmanager.models.MenuItem;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;

/**
 StaffAddEditMenuItemActivity - Add or edit menu items
 Design Pattern: MVC (Controller)
 SOLID: Single Responsibility - handles menu item form
 */
public class StaffAddEditMenuItemActivity extends AppCompatActivity {

    // UI Components
    private ImageView backArrow;
    private TextView titleText;
    private ImageView menuItemImagePreview;
    private Button btnSelectFromGallery;
    private Button btnTakePhoto;
    private EditText editItemName;
    private EditText editItemPrice;
    private EditText editItemDescription;

    private Spinner categorySpinner;
    private Button btnSave;

    // Services
    private DatabaseHelper databaseHelper;

    // State
    private boolean isEditMode = false;
    private int editItemId = -1;
    private String currentImagePath = null;
    private Uri photoUri;

    //Categories
    private static final String[] CATEGORIES = {
            "Starters",
            "Mains",
            "Desserts",
            "Drinks",
            "Pasta",
            "Other"
    };

    // Permission request code
    private static final int CAMERA_PERMISSION_CODE = 100;

    // Activity result launchers
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_add_edit_menu_item);

        // Initialize services
        databaseHelper = DatabaseHelper.getInstance(this);

        // Check if edit mode
        Intent intent = getIntent();
        if (intent.hasExtra("ITEM_ID")) {
            isEditMode = true;
            editItemId = intent.getIntExtra("ITEM_ID", -1);
        }

        initializeViews();
        setupActivityLaunchers();
        setupListeners();

        if (isEditMode) {
            loadExistingItem();
        }
    }

    //Initialize view components
    private void initializeViews() {
        backArrow = findViewById(R.id.backArrow);
        titleText = findViewById(R.id.titleText);
        menuItemImagePreview = findViewById(R.id.menuItemImagePreview);
        btnSelectFromGallery = findViewById(R.id.btnSelectFromGallery);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        editItemName = findViewById(R.id.editItemName);
        editItemPrice = findViewById(R.id.editItemPrice);
        editItemDescription = findViewById(R.id.editItemDescription);
        categorySpinner = findViewById(R.id.categorySpinner);
        btnSave = findViewById(R.id.btnSave);

        // Set title based on mode
        titleText.setText(isEditMode ? "Edit Menu Item" : "Add Menu Item");
        btnSave.setText(isEditMode ? "Update Menu Item" : "Save Menu Item");

        //Set up category spinner
        setupCategorySpinner();
    }

    //Setup activity result launchers for image selection

    private void setupActivityLaunchers() {
        // Gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        if (selectedImage != null) {
                            handleImageSelected(selectedImage);
                        }
                    }
                });

        // Camera launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (photoUri != null) {
                            handleImageSelected(photoUri);
                        }
                    }
                });
    }

    //Setup click listener
    private void setupListeners() {
        backArrow.setOnClickListener(v -> finish());

        btnSelectFromGallery.setOnClickListener(v -> openGallery());

        btnTakePhoto.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openCamera();
            } else {
                requestCameraPermission();
            }
        });

        btnSave.setOnClickListener(v -> saveMenuItem());
    }

    //Load existing item data (edit mode)
    private void loadExistingItem() {
        Intent intent = getIntent();
        editItemName.setText(intent.getStringExtra("ITEM_NAME"));
        editItemPrice.setText(String.valueOf(intent.getDoubleExtra("ITEM_PRICE", 0.0)));
        editItemDescription.setText(intent.getStringExtra("ITEM_DESCRIPTION"));

        currentImagePath = intent.getStringExtra("ITEM_IMAGE");
        if (currentImagePath != null && !currentImagePath.isEmpty()) {
            loadImageIntoPreview(currentImagePath);
        }

        // Load category
        String category = intent.getStringExtra("ITEM_CATEGORY");
        if (category != null) {
            int position = Arrays.asList(CATEGORIES).indexOf(category);
            if (position >= 0) {
                categorySpinner.setSelection(position);
            }
        }
    }

    //Open gallery for image selection
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    //Open camera for photo capture

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create file for photo
        File photoFile = new File(getExternalFilesDir(null), "menu_item_" + System.currentTimeMillis() + ".jpg");
        photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        cameraLauncher.launch(intent);
    }

    //Handle selected/captured image
    private void handleImageSelected(Uri imageUri) {
        try {
            // Save image to internal storage
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();

                // Save to internal storage
                String filename = "menu_" + System.currentTimeMillis() + ".jpg";
                File file = new File(getFilesDir(), filename);

                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

                currentImagePath = file.getAbsolutePath();

                // Display in preview
                menuItemImagePreview.setImageBitmap(bitmap);

                Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Load image into preview

    private void loadImageIntoPreview(String imagePath) {
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                menuItemImagePreview.setImageBitmap(bitmap);
            }
        } catch (Exception e) {
            // Keep default image
        }
    }

    //Save menu item to database
    private void saveMenuItem() {
        // Validate inputs
        String name = editItemName.getText().toString().trim();
        String priceStr = editItemPrice.getText().toString().trim();
        String description = editItemDescription.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (name.isEmpty()) {
            editItemName.setError("Name is required");
            editItemName.requestFocus();
            return;
        }

        if (priceStr.isEmpty()) {
            editItemPrice.setError("Price is required");
            editItemPrice.requestFocus();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) {
                editItemPrice.setError("Price must be greater than 0");
                editItemPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            editItemPrice.setError("Invalid price format");
            editItemPrice.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            editItemDescription.setError("Description is required");
            editItemDescription.requestFocus();
            return;
        }

        // Create or update menu item
        MenuItem menuItem = new MenuItem(
                isEditMode ? editItemId : 0,
                name,
                price,
                currentImagePath != null ? currentImagePath : "placeholder",
                description,
                category
        );

        if (isEditMode) {
            int result = databaseHelper.updateMenuItem(menuItem);
            if (result > 0) {
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to update menu item", Toast.LENGTH_SHORT).show();
            }
        } else {
            long result = databaseHelper.addMenuItem(menuItem);
            if (result > 0) {
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Failed to add menu item", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Setup category spinner with predefined categories
     */
    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                CATEGORIES
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        // Set default selection to "Other"
        categorySpinner.setSelection(CATEGORIES.length - 1);
    }

    //Check camera permission
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    //Request camera permission
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
