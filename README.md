## Restaurant Management Application

An Android application for managing restaurant operations, built with Java and Android Studio. The application supports two user roles: **Staff** and **Guests**, each with role-specific functionality for menu management and table reservations.

## Features

### Guest Features
- **User Authentication**: Secure signup and login with enhanced password requirements
- **Browse Menu**: View restaurant menu items with search and category filtering
- **Make Reservations**: Book tables with date/time selection and guest count
- **Manage Reservations**: View, edit, and cancel existing reservations
- **Real-time Notifications**: Receive updates when reservations are modified or cancelled
- **Customizable Settings**: Manage notification preferences and account settings

### Staff Features
- **User Authentication**: Secure staff account creation with authorization code [IN THIS DEMO ITS 'STAFF2025']
- **Menu Management**: Add, edit, and delete menu items with images and categories
- **Reservation Management**: View all customer reservations and modify/cancel as needed
- **Dashboard Analytics**: Overview of today's reservations and menu statistics
- **Notification System**: Receive alerts for new and modified reservations
- **Category Organization**: Organize menu items into categories (Starters, Mains, Desserts, Drinks, Pasta, Other)

## Architecture & Design Patterns

### Design Patterns Implemented
- **Singleton Pattern**: DatabaseHelper, SessionManager, NotificationHelper, ApiService
- **MVC (Model-View-Controller)**: Clear separation of data models, UI, and logic
- **Adapter Pattern**: RecyclerView adapters for menu items and reservations
- **Observer Pattern**: Notification system for real-time updates

### SOLID Principles
- **Single Responsibility**: Each class has one clear purpose
- **Open/Closed**: Extensible design without modifying existing code
- **Dependency Inversion**: Depends on abstractions (ApiService, DatabaseHelper)

### HCI Principles
- **Visibility**: Clear labels and intuitive navigation
- **Feedback**: Warning messages, toasts, and visual confirmation
- **Error Prevention**: Input validation before submission
- **Consistency**: Uniform design patterns across all screens
- **Constraint**: Disabled buttons, date/time restrictions

## Technical 

- **Language**: Java
- **IDE**: Android Studio
- **Database**: SQLite (local storage)
- **API**: RESTful API for user authentication
- **Threading**: ExecutorService for background operations

## User Authentication

### Guest Account
- Email format validation
- Username requirements: min 3 characters, no spaces
- Password requirements:
  - Minimum 8 characters
  - At least 1 uppercase letter
  - At least 1 lowercase letter
  - At least 1 number
  - At least 1 special character
  - No common weak passwords

### Staff Account
- Same password requirements as Guest
- Requires valid staff authorization code
- Creates account with "staff" user type

## Third-Party Resources Used

### External Libraries

1. **Volley (com.android.volley)**
   - Purpose: HTTP networking library for API communication
   - Used for: Creating and managing network requests to the RESTful API
   - Documentation: https://google.github.io/volley/

2. **Gson (com.google.gson)**
   - Purpose: JSON serialization/deserialization library
   - Used for: Converting Java objects to JSON and vice versa
   - Documentation: https://github.com/google/gson

### Android Standard Libraries (AndroidX)

- **RecyclerView** - For efficient list rendering (menus, reservations)
- **CardView** - For card-based UI components
- **AppCompat** - For backward compatibility and modern UI components
- **NotificationCompat** - For building notifications

### External APIs

- **COMP2000 RESTful API**
  - Base URL: http://10.240.72.69/comp2000/coursework
  - Student ID: 10921081
  - Purpose: User authentication and data storage
  - API Documentation: Provided via DLE

### Built-in Android Components

- **SQLite** - Local database storage
- **SharedPreferences** - Session and preference management
- **NotificationManager** - System notification service
- **DatePickerDialog** - Date selection UI
- **FileProvider** - Image file handling

### Custom Resources

All drawable resources (icons, backgrounds, logo) are custom-created or part of the application's original design.
