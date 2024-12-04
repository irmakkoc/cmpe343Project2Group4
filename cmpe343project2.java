package cmpe343project2group4;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * CMPE343 FALL 2024 PROJECT2 Firm Management Project.
 * Advanced Java Programming with Object-Oriented Paradigms and Database Integration.
 *
 * @author SerkanAçar
 * @author IrmakKoç
 * @author Aleynaİslamoğlu
 * @author ErenCanGünel
 * @author EmreŞaşmaz
 * @version 1.0
 */

/**
 * main class
 */
public class cmpe343project2group4 {

    private static final String URL = "jdbc:mysql://localhost:3306/firm_management";
    private static final String USER = "root";
    private static final String PASSWORD = "";


    /**
     * Establishes a connection to the MySQL database.
     *
     * @return a Connection object to interact with the database
     */
    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


    /**
     * Abstract class representing an employee.
     * Contains common fields and methods for all employee types.
     */
    public abstract class Employee {
        protected int employee_id;
        protected String name;
        protected String surname;
        protected String username;
        protected String role;

        /**
         * Constructs an Employee with the specified details.
         *
         * @param employee_id the ID of the employee
         * @param name the first name of the employee
         * @param surname the last name of the employee
         * @param username the username of the employee
         * @param role the role of the employee
         */

        public Employee(int employee_id, String name, String surname, String username, String role) {
            this.employee_id = employee_id;
            this.name = name;
            this.surname = surname;
            this.username = username;
            this.role = role;
        }

        /**
         * Displays the menu specific to the employee role.
         */
        public abstract void showMenu();
    }



    /**
     * Represents a manager in the firm management system.
     * Inherits common employee properties and adds manager-specific functionality.
     */
    public class Manager extends Employee {

        /**
         * Constructs a Manager with the specified details.
         *
         * @param employee_id the unique ID of the manager
         * @param name the first name of the manager
         * @param surname the last name of the manager
         * @param username the username of the manager
         */

        public Manager(int employee_id, String name, String surname, String username) {
            super(employee_id, name, surname, username, "manager");
        }

        /**
         * Displays the manager-specific menu.
         */
        @Override
        public void showMenu() {
            showManagerMenu(username);
        }
    }


    /**
     * Represents a regular employee in the firm management system.
     * Inherits common employee properties and adds regular employee functionality.
     */
    public class RegularEmployee extends Employee {

        /**
         * Constructs a RegularEmployee with the specified details.
         *
         * @param employee_id the unique ID of the employee
         * @param name the first name of the employee
         * @param surname the last name of the employee
         * @param username the username of the employee
         */
        public RegularEmployee(int employee_id, String name, String surname, String username) {
            super(employee_id, name, surname, username, "regular");
        }

        /**
         * Displays the regular employee-specific menu.
         */
        @Override
        public void showMenu() {

            showRegularMenu(username);
        }
    }



    /**
     * Used for the login process for users.
     * Validates username and password against the database
     * and redirects users to the appropriate menu based on their role.
     *
     * @throws SQLException if a database error occurs.
     */
    public void login() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            String query = "SELECT * FROM employees WHERE username = ? AND password = ?";
            try (Connection connection = connect();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    int employee_id = resultSet.getInt("employee_id");
                    String name = resultSet.getString("name");
                    String surname = resultSet.getString("surname");
                    String role = resultSet.getString("role");
                    boolean first_login = resultSet.getBoolean("first_login");

                    System.out.println("Welcome, " + name + " " + surname);

                    if (first_login) {
                        System.out.println("This is your first login. You must update your password.");
                        updatePasswordOnFirstLogin(username);
                    }


                    Employee employee;
                    if ("manager".equals(role)) {
                        employee = new Manager(employee_id, name, surname, username);
                    } else {
                        employee = new RegularEmployee(employee_id, name, surname, username);
                    }

                    employee.showMenu();
                    return;
                } else {
                    System.out.println("Incorrect username and/or password. Try again.");
                }
            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }
        }
    }


    /**
     * Displays the manager menu contain operations that a regular employee cannot perform.
     *
     * @param loggedInUsername The username of the logged-in manager.
     */
    public static void showManagerMenu(String loggedInUsername) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Manager Menu ---");
            System.out.println("1. Update Own Profile");
            System.out.println("2. Display All Employees");
            System.out.println("3. Display Employees with the Role");
            System.out.println("4. Display Employee with Username");
            System.out.println("5. Update Employee Non-Profile Fields");
            System.out.println("6. Hire Employee");
            System.out.println("7. Fire Employee");
            System.out.println("8. Run Algorithms");
            System.out.println("9. Logout");
            System.out.print("Your choice: ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        updateProfile(loggedInUsername);
                        break;
                    case 2:
                        displayAllEmployees();
                        break;
                    case 3:
                        displayEmployeesWithRole();
                        break;
                    case 4:
                        displayEmployeeWithUsername();
                        break;
                    case 5:
                        updateEmployeeNonProfileFields();
                        break;
                    case 6:
                        hireEmployee();
                        break;
                    case 7:
                        fireEmployee(loggedInUsername);
                        break;
                    case 8:
                        runAlgorithms();
                        break;
                    case 9:
                        logout();
                        return;
                    default:
                        System.out.println("Invalid choice. Please select a valid option.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }


    /**
     * Displays the regular employee menu and handles user input for profile-related operations.
     *
     * @param username The username of the logged-in regular employee.
     */
    public static void showRegularMenu(String username) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Regular Menu ---");
            System.out.println("1. Display Profile");
            System.out.println("2. Update Profile");
            System.out.println("3. Logout");
            System.out.print("Your choice: ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        displayProfile(username);
                        break;
                    case 2:
                        updateProfile(username);
                        break;
                    case 3:
                        logout();
                        return;
                    default:
                        System.out.println("Invalid Choice. Please select a valid option.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }


    /**
     * Handles the logout process.
     * Asks the user to confirm exiting the program or returning to the login screen.
     */
    public static void logout() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n--- Logout ---");
        while (true) {
            System.out.print("Do you want to exit? (Y/N): ");
            String exitChoice = scanner.nextLine().toUpperCase();

            if (exitChoice.equals("Y")) {
                System.out.println("Exiting the program. Goodbye!");
                System.exit(0);
            } else if (exitChoice.equals("N")) {
                System.out.println("Returning to the login screen...");
                cmpe343project2group4 instance = new cmpe343project2group4();
                instance.login();
                return;
            } else {
                System.out.println("Invalid choice. Please enter 'Y' or 'N'.");
            }
        }
    }


    /**
     * Updates the profile of the specified user by allowing them to change their phone number, email, and password.
     * @param username The username of the employee whose profile is being updated.
     */
    private static void updateProfile1(String username) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nUpdate Profile");
        System.out.print("New Phone Number: ");
        String newPhone = scanner.nextLine();

        System.out.print("New Email: ");
        String newEmail = scanner.nextLine();

        System.out.print("New Password: ");
        String newPassword = scanner.nextLine();

        String query = "UPDATE employees SET phone_no = ?, email = ?, password = ? WHERE username = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, newPhone);
            stmt.setString(2, newEmail);
            stmt.setString(3, newPassword);
            stmt.setString(4, username);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Profile updated successfully!");
            } else {
                System.out.println("Profile update failed. User not found.");
            }

        } catch (SQLException e) {
            System.err.println("Error during profile update: " + e.getMessage());
        }
    }

    /**
     * Displays a list of all employees in the database with their information.
     */
    private static void displayAllEmployees() {
        String query = "SELECT * FROM employees";
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            System.out.println("\n--- All Employees ---");
            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("employee_id"));
                System.out.println("Name: " + resultSet.getString("name"));
                System.out.println("Surname: " + resultSet.getString("surname"));
                System.out.println("Role: " + resultSet.getString("role"));
                System.out.println("--------------------");
            }
        } catch (SQLException e) {
            System.out.println("Errror: " + e.getMessage());
        }
    }

    /**
     * Displays employees filtered by a specific role entered by the user, "manager", "engineer", "technician", "intern".
     */
    private static void displayEmployeesWithRole() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter role to filter by (e.g., manager, engineer): ");
        String role = scanner.nextLine().toLowerCase();

        String query = "SELECT * FROM employees WHERE role = ?";
        try (Connection connection = connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();


            boolean hasEmployees = false;

            System.out.println("\n--- Employees with Role: " + role + " ---");
            while (rs.next()) {
                hasEmployees = true;
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Surname: " + rs.getString("surname"));
                System.out.println("Username: " + rs.getString("username"));
                System.out.println("Phone number: " + rs.getString("phone_no"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("--------------------");
            }

            if (!hasEmployees) {
                System.out.println("No employees found with the role: " + role);
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Displays the information of an employee based on their username entered by the user.
     */
    private static void displayEmployeeWithUsername() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = scanner.nextLine();

        String query = "SELECT * FROM employees WHERE username = ?";
        try (Connection connection = connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                System.out.println("\n--- Employee Details ---");
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Surname: " + rs.getString("surname"));
                System.out.println("Role: " + rs.getString("role"));
                System.out.println("Phone: " + rs.getString("phone_no"));
                System.out.println("Email: " + rs.getString("email"));
            } else {
                System.out.println("Employee not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }



    /**
     * Updates non-profile fields (e.g., name, surname, role, date of birth, date of start) for an employee.
     */
    private static void updateEmployeeNonProfileFields() {
        Scanner scanner = new Scanner(System.in);

        String targetUsername = null;
        while (true) {
            System.out.print("Enter the username of the employee to update: ");
            targetUsername = scanner.nextLine();

            String checkQuery = "SELECT * FROM employees WHERE username = ?";
            try (Connection connection = connect();
                 PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {

                checkStmt.setString(1, targetUsername);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    System.out.println("Employee found: " + rs.getString("name") + " " + rs.getString("surname"));
                    break;
                } else {
                    System.out.println("No employee found with the given username. Please try again.");
                }
            } catch (SQLException e) {
                System.out.println("Error while checking user existence: " + e.getMessage());
                return;
            }
        }

        boolean continueUpdate = true;
        while (continueUpdate) {
            System.out.println("\n--- Update Employee Non-Profile Fields ---");
            System.out.println("Enter 1 to Update Name");
            System.out.println("Enter 2 to Update Surname");
            System.out.println("Enter 3 to Update Role");
            System.out.println("Enter 4 to Update Date of Birth (YYYY-MM-DD)");
            System.out.println("Enter 5 to Update Date of Start (YYYY-MM-DD)");
            System.out.println("Enter 6 to Go Back to Main Menu");

            System.out.print("Your choice: ");
            System.out.print("Your choice: ");
            int choice = -1;

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input from the scanner
                continue;
            }

            String fieldName = null;
            String newValue = null;

            switch (choice) {
                case 1:
                    while (true) {
                        try {
                            System.out.print("Enter New Name: ");
                            newValue = scanner.nextLine();
                            if (!newValue.matches("[a-zA-ZğüşıöçĞÜŞİÖÇ]+")) {
                                throw new IllegalArgumentException("Name must contain only letters.");
                            }
                            break;
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    fieldName = "name";
                    break;
                case 2:
                    while (true) {
                        try {
                            System.out.print("Enter New Surname: ");
                            newValue = scanner.nextLine();
                            if (!newValue.matches("[a-zA-ZğüşıöçĞÜŞİÖÇ]+")) {
                                throw new IllegalArgumentException("Surname must contain only letters.");
                            }
                            break;
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    fieldName = "surname";
                    break;
                case 3:
                    while (true) {
                        System.out.print("Enter new Role (manager, engineer, technician, intern): ");
                        newValue = scanner.nextLine().toLowerCase();
                        if (newValue.equals("manager") || newValue.equals("engineer") || newValue.equals("technician") || newValue.equals("intern")) {
                            fieldName = "role";
                            break;
                        } else {
                            System.out.println("Invalid role. Please enter one of the following: manager, engineer, technician, intern.");
                        }
                    }
                    break;
                case 4:
                    while (true) {
                        try {
                            System.out.print("Enter new Date of Birth (YYYY-MM-DD): ");
                            newValue = scanner.nextLine();
                            if (!isValidDate(newValue)) {
                                throw new IllegalArgumentException("Invalid date format. Please use YYYY-MM-DD.");
                            }
                            fieldName = "date_of_birth";
                            break;
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    break;
                case 5:
                    while (true) {
                        try {
                            System.out.print("Enter new Date of Start (YYYY-MM-DD): ");
                            newValue = scanner.nextLine();
                            if (!isValidDate(newValue)) {
                                throw new IllegalArgumentException("Invalid date format. Please use YYYY-MM-DD.");
                            }
                            fieldName = "date_of_start";
                            break;
                        } catch (IllegalArgumentException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    break;
                case 6:
                    System.out.println("Returning to the previous menu...");
                    continueUpdate = false;
                    continue;
                default:
                    System.out.println("Invalid choice. Try again.");
                    continue;
            }

            if (fieldName != null && newValue != null) {
                String updateQuery = "UPDATE employees SET " + fieldName + " = ? WHERE username = ?";
                try (Connection connection = connect();
                     PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {

                    updateStmt.setString(1, newValue);
                    updateStmt.setString(2, targetUsername);

                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println(fieldName + " updated successfully for " + targetUsername + "!");
                    } else {
                        System.out.println("Failed to update the field.");
                    }
                } catch (SQLException e) {
                    System.out.println("Error while updating " + fieldName + ": " + e.getMessage());
                }
            }
        }
    }



    /**
     * Checks if the date matches the format YYYY-MM-DD.
     *
     * @param date The date string to be validated.
     * @return true if the date matches the format, false otherwise.
     */
    private static boolean isValidDate(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }



    /**
     * Handles the hiring process of a new employee, demands necessary information from the manager
     * such as name, surname, username, role, date of birth, start date, email, and phone number.
     * Inserts the new employee's data into the database.
     */
    private static void hireEmployee() {
        Scanner scanner = new Scanner(System.in);

        String name = null;
        while (true) {
            try {
                System.out.print("Enter name: ");
                name = scanner.nextLine();
                if (!name.matches("[a-zA-ZğüşıöçĞÜŞİÖÇ]+")) {
                    throw new IllegalArgumentException("Name must contain only letters.");
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        String surname = null;
        while (true) {
            try {
                System.out.print("Enter surname: ");
                surname = scanner.nextLine();
                if (!surname.matches("[a-zA-ZğüşıöçĞÜŞİÖÇ]+")) {
                    throw new IllegalArgumentException("Surname must contain only letters.");
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        String username = null;
        while (true) {
            System.out.print("Enter username: ");
            username = scanner.nextLine();
            if (isUsernameTaken(username)) {
                System.out.println("This username is already taken. Please try another.");
            } else {
                break;
            }
        }

        String role = null;
        while (true) {
            System.out.print("Enter role (manager, engineer, technician, intern): ");
            role = scanner.nextLine().toLowerCase();
            if (role.equals("manager") || role.equals("engineer") || role.equals("technician") || role.equals("intern")) {
                break;
            } else {
                System.out.println("Invalid role. Please enter one of the following: manager, engineer, technician, intern.");
            }
        }

        String date_of_birth = null;
        while (true) {
            try {
                System.out.print("Enter date of birth (YYYY-MM-DD): ");
                date_of_birth = scanner.nextLine();
                if (!isValidDate(date_of_birth)) {
                    throw new IllegalArgumentException("Invalid date format. Please use YYYY-MM-DD.");
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        String date_of_start = null;
        while (true) {
            try {
                System.out.print("Enter date of start (YYYY-MM-DD): ");
                date_of_start = scanner.nextLine();
                if (!isValidDate(date_of_start)) {
                    throw new IllegalArgumentException("Invalid date format. Please use YYYY-MM-DD.");
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        String email = null;
        while (true) {
            System.out.print("Enter email: ");
            email = scanner.nextLine();

            if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                break;
            } else {
                System.out.println("Invalid email format. Please enter a valid email (e.g., user@example.com).");
            }
        }

        String phone_no = null;
        while (true) {
            System.out.print("Enter phone number: ");
            phone_no = scanner.nextLine();
            if (phone_no.matches("\\d+")) {
                break;
            } else {
                System.out.println("Invalid phone number. Please enter digits only.");
            }
        }

        String query = "INSERT INTO employees (name, surname, username, role, date_of_birth, date_of_start, email, phone_no, password, first_login) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'default123', 1)";

        try (Connection connection = connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setString(2, surname);
            stmt.setString(3, username);
            stmt.setString(4, role);
            stmt.setString(5, date_of_birth);
            stmt.setString(6, date_of_start);
            stmt.setString(7, email);
            stmt.setString(8, phone_no);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("New employee hired successfully.");
            } else {
                System.out.println("Failed to hire employee.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Checks if the given username already exists in the database.
     *
     * @param username The username to check.
     * @return true if the username is already taken, false otherwise.
     */
    private static boolean isUsernameTaken(String username) {
        String query = "SELECT COUNT(*) FROM employees WHERE username = ?";
        try (Connection connection = connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error checking username: " + e.getMessage());
        }
        return false;
    }

    /**
     * Allows the manager to fire an employee by providing the employee's username.
     * Ensures that the user cannot fire themselves.
     * Deletes the user from the database
     *
     * @param loggedInUsername The username of the currently logged-in user.
     */
    private static void fireEmployee(String loggedInUsername) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter employee username to fire: ");
        String username = scanner.nextLine();


        if (loggedInUsername.equals(username)) {
            System.out.println("You cannot fire yourself!");
            return;
        }

        String query = "DELETE FROM employees WHERE username = ?";
        try (Connection connection = connect();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Employee fired successfully.");
            } else {
                System.out.println("No employee found with the given username.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    /**
     * Displays the profile information of an employee based on the provided username.
     * Retrieves and prints details such as username, role, name, surname, date of birth, and date of start.
     *
     * @param username The username of the employee whose profile is to be displayed.
     */
    private static void displayProfile(String username) {
        String query = "SELECT username, role, name, surname, date_of_birth, date_of_start FROM employees WHERE username = ?";
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("\n--- Profile Information ---");
                System.out.println("Username: " + resultSet.getString("username"));
                System.out.println("Role: " + resultSet.getString("role"));
                System.out.println("Name: " + resultSet.getString("name"));
                System.out.println("Surname: " + resultSet.getString("surname"));
                System.out.println("Date of Birth: " + resultSet.getString("date_of_birth"));
                System.out.println("Date of Start: " + resultSet.getString("date_of_start"));
            } else {
                System.out.println("Couldn't find the profile.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Updates the profile information for a user based on their choice.
     * The user can update their password, phone number, or email,
     * or return to the main menu.
     *
     * @param username The username of the user whose profile is being updated.
     */
    private static void updateProfile(String username) {
        Scanner scanner = new Scanner(System.in);
        boolean continueUpdate = true;
        while (continueUpdate) {
            System.out.println("\n--- Update Profile ---");
            System.out.println("Enter 1 to Update Password");
            System.out.println("Enter 2 to Update Phone Number");
            System.out.println("Enter 3 to Update E-mail");
            System.out.println("Enter 4 to Go Back to Main Menu");

            int choice = -1;

            try {
                System.out.print("Your choice: ");
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Choice invalid! Please enter a number between 1 and 4.");
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter Your New Password: ");
                    String newPassword = scanner.nextLine();
                    updateField(username, "password", newPassword);
                    break;
                case 2:
                    try {
                        System.out.print("Enter your new Phone Number: ");
                        String phoneNumber = scanner.nextLine();

                        if (phoneNumber.matches(".*[a-zA-Z]+.*")) {
                            throw new IllegalArgumentException("Phone number can only contain digits.");
                        }

                        updateField(username, "phone_no", phoneNumber);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 3:
                    while (true) {
                        System.out.print("Enter Your New E-mail: ");
                        String newEmail = scanner.nextLine();


                        if (newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                            updateField(username, "email", newEmail);
                            break;
                        } else {
                            System.out.println("Invalid e-mail format. Please try again.");
                        }
                    }
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }

        }
    }

    /**
     * Updates the password for a user if they log in for the first time.
     * The password change is applied to the database and sets the 'first_login' flag to false.
     *
     * @param username The username of the user whose password is being updated.
     */
    private static void updatePasswordOnFirstLogin(String username) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your new password: ");
        String newPassword = scanner.nextLine();

        String query = "UPDATE employees SET password = ?, first_login = FALSE WHERE username = ?";
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, username);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Password updated successfully. Proceeding to the menu.");
            } else {
                System.out.println("Failed to update password. Contact the administrator.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }



    /**
     * Updates a specific field in the user's profile in the database.
     *
     * @param username The username of the user whose profile is being updated.
     * @param fieldName The name of the field being updated (e.g., "password", "phone_no", "email").
     * @param newValue The new value for the specified field.
     */
    private static void updateField(String username, String fieldName, String newValue) {
        String query = "UPDATE employees SET " + fieldName + " = ? WHERE username = ?";
        try (Connection connection = connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, newValue);
            preparedStatement.setString(2, username);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(fieldName + " updated successfully.");
            } else {
                System.out.println("Update failed.");
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Displays an ASCII art.
     * The art is styled using color codes.
     */
    public static void displayLaptopAsciiArt() {

        String reset = "\u001B[0m";
        String white = "\u001B[37m";
        String blue = "\u001B[34m";
        String green = "\u001B[32m";
        String yellow = "\u001B[33m";
        String cyan = "\u001B[36m";
        String red = "\u001B[31m";

        System.out.println();
        System.out.println(cyan + "█████████████████████████████████████████████████████████████" + reset);
        System.out.println(cyan + "██                                                         ██" + reset);
        System.out.println(cyan + "██  " + red + "   ██   ██ ███████  ███     ███      █████████       " +cyan + "  ██" + reset);
        System.out.println(cyan + "██  " + red + "   ██   ██ ██       ███     ███      ██     ██       " + cyan + "  ██" + reset);
        System.out.println(cyan + "██  " + red + "   ███████ ██████   ███     ███      ██     ██       " + cyan + "  ██" + reset);
        System.out.println(cyan + "██  " + red + "   ██   ██ ██       ███     ███      ██     ██       " + cyan + "  ██" + reset);
        System.out.println(cyan + "██  " + red + "   ██   ██ ███████  ███████ ████████ █████████       " + cyan + "  ██" + reset);
        System.out.println(cyan + "██  " + red + "                                                     " + cyan + "  ██" + reset);
        System.out.println(cyan + "██                                                         ██" + reset);
        System.out.println(cyan + "█████████████████████████████████████████████████████████████" + reset);
        System.out.println(cyan + " ██                                                         ██" + reset);
        System.out.println(cyan + "  ██      " + yellow + "███████████████████████████████████████" + cyan + "            ██" + reset);
        System.out.println(cyan + "   ██      " + yellow + "██                                   ██" + cyan + "            ██" + reset);
        System.out.println(cyan + "    ██      " + yellow + "██   Welcome to the Firm Management  ██" + cyan + "            ██" + reset);
        System.out.println(cyan + "     ██      " + yellow + "██                                   ██" + cyan + "            ██" + reset);
        System.out.println(cyan + "      ██      " + yellow + "███████████████████████████████████████" + cyan + "            ██" + reset);
        System.out.println(cyan + "       ██                                                         ██" + reset);
        System.out.println(cyan + "        █████████████████████████████████████████████████████████████" + reset);
        System.out.println(white + "           ██████████████████████████████████████████████████" + reset);
        System.out.println(white + "           ██████████████████████████████████████████████████" + reset);
        System.out.println(white + "           ██████████████████████████████████████████████████" + reset);
        System.out.println();
    }

    private static int[] radixArray, shellArray, heapArray, insertionArray;

    /**
     * Runs multiple sorting algorithms (Radix Sort, Shell Sort, Heap Sort, and Insertion Sort)
     * on arrays of random integers and calculates their execution times.
     */
    public static void runAlgorithms() {
        Comparator<Integer> ascendingComparator = getAscendingComparator();
        int valid;
        long exeStartTime, exeEndTime;
        Scanner scanner = new Scanner(System.in);
        do {
            valid = 1; //for exiting this should turn 0
            int size = readInputAlgo(scanner);
            exeStartTime = System.nanoTime();

            int[] randomArray = generateRandomArray(size);
            radixArray = new int[size];
            shellArray = new int[size];
            heapArray = new int[size];
            insertionArray = new int[size];
            copyArrays(randomArray);
            List<Integer> arrayList = arrToList(randomArray);
            calculateDuration(radixArray, shellArray, heapArray, insertionArray, arrayList, ascendingComparator);
            exeEndTime = System.nanoTime();
            System.out.println("Execution Duration: " + (exeEndTime - exeStartTime) + " ns");
            valid = readInput(scanner);
        } while(valid !=0 );

    }
    /**
     * Generates an array of random integers within a specified range.
     * The size of the array should be between 1000 and 10000.
     *
     * @param size The size of the array to be generated, which must be between 1000 and 10000.
     * @return An array containing random integers within the range of -10000 to 10000.
     */
    public static int[] generateRandomArray(int size) // size 1000-10000 arasinda olacak.
    {
        Random rand = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++)
        {
            array[i] = rand.nextInt(20001) - 10000;
        }
        return array;
    }

    /**
     * Sorts an array using the Counting Sort algorithm for a specific digit place.
     *
     * @param array The array to be sorted.
     * @param place The digit place to be considered for sorting.
     */
    private static void countingSort(int[] array, int place) {
        int[] result = new int[array.length];
        int[] count = new int[20];

        for (int i = 0; i < array.length; i++) {
            int index = (array[i] / place % 10) + 9;
            count[index]++;
        }

        for (int i = 1; i < 20; i++) {
            count[i] += count[i - 1];
        }

        for (int i = array.length - 1; i >= 0; i--) {
            int index = (array[i] / place % 10) + 9;
            result[count[index] - 1] = array[i];
            count[index]--;
        }

        for (int i = 0; i < array.length; i++) {
            array[i] = result[i];
        }
    }

    /**
     * Sorts an array using the Radix Sort algorithm.
     *
     * @param array The array to be sorted.
     */
    private static void radixSort(int array[]) {
        int max = array[0];
        for (int i = 1; i < array.length; i++)
        {
            if (array[i] > max)
            {
                max = array[i];
            }
        }

        for (int place = 1; max / place > 0; place *= 10)
        {
            countingSort(array, place);
        }
    }

    /**
     * Sorts an array using the Shell Sort algorithm.
     *
     * @param array The array to be sorted.
     */
    private static void shellSort(int array[]) {
        for (int interval = array.length / 2; interval > 0; interval = interval / 2)
        {
            for (int i = interval; i < array.length; i += 1)
            {
                int temp = array[i];
                int j;
                for (j = i; j >= interval && array[j - interval] > temp; j -= interval)
                {
                    array[j] = array[j - interval];
                }
                array[j] = temp;
            }
        }
    }

    /**
     * Sorts an array using the Heap Sort algorithm.
     *
     * @param arr The array to be sorted.
     */
    private static void heapSort(int arr[]) {

        for (int i = arr.length / 2 - 1; i >= 0; i--) {
            heapify(arr, arr.length, i);
        }

        for (int i = arr.length - 1; i >= 0; i--) {
            int temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;

            heapify(arr, i, 0);
        }
    }

    /**
     * Maintains the heap property of a subarray by recursively reordering it.
     *
     * @param arr The array to be heapified.
     * @param size The size of the heap.
     * @param i The index of the element to be heapified.
     */
    private static void heapify(int arr[], int size, int i) {
        int largest = i;
        int l = 2 * i + 1;
        int r = 2 * i + 2;

        if (l < size && arr[l] > arr[largest])
            largest = l;

        if (r < size && arr[r] > arr[largest])
            largest = r;

        if (largest != i)
        {
            int swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;

            heapify(arr, size, largest);
        }
    }

    /**
     * Sorts an array using the Insertion Sort algorithm.
     *
     * @param array The array to be sorted.
     */
    private static void insertionSort(int array[]) {

        for (int step = 1; step < array.length; step++)
        {
            int key = array[step];
            int j = step - 1;

            while (j >= 0 && key < array[j])
            {
                array[j + 1] = array[j];
                --j;
            }

            array[j + 1] = key;
        }
    }

    /**
     * Returns a Comparator for sorting integers in ascending order.
     *
     * @return A Comparator that sorts integers in ascending order.
     */
    private static Comparator<Integer> getAscendingComparator() {
        return new Comparator<>() {
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        };
    };

    /**
     * Reads an integer input from the user and ensures it is between 1000 and 10000.
     *
     * @param scanner The Scanner object for reading user input.
     * @return The integer input from the user within the valid range.
     */
    private static int readInputAlgo(Scanner scanner) {
        int result = 0;
        boolean input = true;
        while (input)
        {
            System.out.print("Enter number between 1000 and 10000:");
            try {
                result = scanner.nextInt();
                if (result < 1000 || result > 10000)
                {
                    System.out.println("Entered number should be between 1000 and 10000!");
                }
                else
                {
                    input = false;
                }
            } catch (InputMismatchException e) {
                System.out.println("Enter valid number!");
                scanner.nextLine();
            }
        }
        return result;
    }

    /**
     * Reads an integer input from the user to decide whether to exit or continue.
     *
     * @param scanner The Scanner object for reading user input.
     * @return The integer input from the user, which can be 0 for exit or 1 to continue.
     */
    private static int readInput(Scanner scanner) {
        int result = 0;
        while (true) {
            System.out.print("Enter 0 for exiting or 1 for continue:");
            try {
                result = scanner.nextInt();
                if (result == 0) {
                    break;
                }
                else if (result == 1)
                {
                    break;
                }
                else {
                    System.out.println("Enter valid number!");
                }
            } catch (InputMismatchException e) {
                System.out.println("Enter valid number!");
                scanner.next();
            }
        }
        return result;
    }

    /**
     * Copies the elements of an input array to the sorting arrays used in runAlgorithms().
     *
     * @param array The array to be copied.
     */
    private static void copyArrays(int[] array) {
        for (int i = 0; i < array.length; i++)
        {
            radixArray[i] = array[i];
            shellArray[i] = array[i];
            heapArray[i] = array[i];
            insertionArray[i] = array[i];
        }
    }
    /**
     * Calculates and prints the execution duration of each sorting algorithm
     * and Java's built-in Collections.sort() method.
     *
     * @param radixArray The array to be sorted using Radix Sort.
     * @param shellArray The array to be sorted using Shell Sort.
     * @param heapArray The array to be sorted using Heap Sort.
     * @param insertionArray The array to be sorted using Insertion Sort.
     * @param arrayList The list to be sorted using Java's built-in sorting method.
     * @param ascendingComparator The Comparator for sorting in ascending order.
     */

    private static void calculateDuration(int[] radixArray, int[] shellArray, int[] heapArray, int[] insertionArray, List<Integer> arrayList, Comparator<Integer> ascendingComparator) {
        long startTime, endTime;
        startTime = System.nanoTime();
        radixSort(radixArray);
        endTime = System.nanoTime();
        System.out.println("Radix Sort Duration: " + (endTime - startTime) + " ns");

        startTime = System.nanoTime();
        shellSort(shellArray);
        endTime = System.nanoTime();
        System.out.println("Shell Sort Duration: " + (endTime - startTime) + " ns");

        startTime = System.nanoTime();
        heapSort(heapArray);
        endTime = System.nanoTime();
        System.out.println("Heap Sort Duration: " + (endTime - startTime) + " ns");

        startTime = System.nanoTime();
        insertionSort(insertionArray);
        endTime = System.nanoTime();
        System.out.println("Insertion Sort Duration: " + (endTime - startTime) + " ns");

        startTime = System.nanoTime();
        Collections.sort(arrayList, ascendingComparator);
        endTime = System.nanoTime();
        System.out.println("Collection.sort() Duration: " + (endTime - startTime) + " ns");
    }

    /**
     * Converts an array of integers into a List of integers.
     *
     * @param array The array to be converted.
     * @return A List containing the elements of the input array.
     */
    private static List<Integer> arrToList(int[] array) {
        List<Integer> list = new ArrayList<>();
        for (int num : array) {
            list.add(num);
        }
        return list;
    }

    /**
     * The main method to start the project, displaying ASCII art and initiating the login process.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        displayLaptopAsciiArt();
        cmpe343project2group4 instance = new cmpe343project2group4();
        instance.login();
    }
}