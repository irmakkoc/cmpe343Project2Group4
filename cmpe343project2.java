package cmpe343project2group4;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;


	public class cmpe343project2group4 {

	    private static final String URL = "jdbc:mysql://localhost:3306/firm_management"; 
	    private static final String USER = "root"; 
	    private static final String PASSWORD = "";
	    
	    private static Connection connect() throws SQLException {
	        return DriverManager.getConnection(URL, USER, PASSWORD);
	    }

	    public abstract class Employee {
	        protected int employee_id;
	        protected String name;
	        protected String surname;
	        protected String username;
	        protected String role;

	        public Employee(int employee_id, String name, String surname, String username, String role) {
	            this.employee_id = employee_id;
	            this.name = name;
	            this.surname = surname;
	            this.username = username;
	            this.role = role;
	        }

	        public abstract void showMenu();
	    }
	    
	   

	    public class Manager extends Employee {
	        public Manager(int employee_id, String name, String surname, String username) {
	            super(employee_id, name, surname, username, "manager");
	        }

	        @Override
	        public void showMenu() {
	           
	            showManagerMenu(username);
	        }
	    }
	    

	    public class RegularEmployee extends Employee {
	        public RegularEmployee(int employee_id, String name, String surname, String username) {
	            super(employee_id, name, surname, username, "regular");
	        }

	        @Override
	        public void showMenu() {
	           
	            showRegularMenu(username);
	        }
	    }




	    public void login() {
	        Scanner scanner = new Scanner(System.in);
	        
	        while (true) {
	            System.out.print("Do you want to exit? (Y/N): ");
	            String exitChoice = scanner.nextLine().toUpperCase();
	            
	            if (exitChoice.equals("Y")) {
	            	System.out.println("Exiting the program. Goodbye!");
	                System.exit(0); 
	            } else if(exitChoice.equals("N")) {
	            	break;
	            } else {
	                System.out.println("Couldn't understand your choice. Please enter 'Y' for exit or 'N' to continue.");
	            }
	        }
	        
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
	                    System.out.println("Invalid credentials. Try again.");
	                }
	            } catch (SQLException e) {
	                System.out.println("Database error: " + e.getMessage());
	            }
	        }
	    }


	    public static void showManagerMenu(String username) {
	        Scanner scanner = new Scanner(System.in);
	        while (true) {
	            System.out.println("\n--- Manager Menu ---");
	            System.out.println("1. Display All Employees");
	            System.out.println("2. Update Employee Non-Profile Fields");
	            System.out.println("3. Hire Employee");
	            System.out.println("4. Fire Employee");
	            System.out.println("5. Logout");
	            System.out.print("Seçiminiz: ");

	            int choice = scanner.nextInt();
	            scanner.nextLine(); 

	            switch (choice) {
	                case 1:
	                    displayAllEmployees();
	                    break;
	                case 2:
	                    updateEmployeeNonProfileFields();
	                    break;
	                case 3:
	                    hireEmployee();
	                    break;
	                case 4:
	                    fireEmployee();
	                    break;
	                case 5:
	                    logout();
	                    return; 
	                default:
	                    System.out.println("Invalid cohice. Try again.");
	            }
	        }
	    }

	    
	    public static void showRegularMenu(String username) {
	        Scanner scanner = new Scanner(System.in);
	        while (true) {
	            System.out.println("\n--- Regular Menu ---");
	            System.out.println("1. Display Profile");
	            System.out.println("2. Update Profile");
	            System.out.println("3. Logout");
	            System.out.print("Seçiminiz: ");

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
	                    System.out.println("Geçersiz seçim. Tekrar deneyin.");
	            }
	        }
	    }

	    
	    public static void logout() {
	        System.out.println("Sistemden çıkış yapılıyor...\n");
	        cmpe343project2group4 instance = new cmpe343project2group4(); 
	        instance.login();
	    }

	    private static void displayAllEmployees() {
	        String query = "SELECT * FROM employees";
	        try (Connection connection = connect();
	             PreparedStatement preparedStatement = connection.prepareStatement(query);
	             ResultSet resultSet = preparedStatement.executeQuery()) {

	            System.out.println("\n--- Tüm Çalışanlar ---");
	            while (resultSet.next()) {
	                System.out.println("ID: " + resultSet.getInt("employee_id"));
	                System.out.println("Name: " + resultSet.getString("name"));
	                System.out.println("Surname: " + resultSet.getString("surname"));
	                System.out.println("Role: " + resultSet.getString("role"));
	                System.out.println("--------------------");
	            }
	        } catch (SQLException e) {
	            System.out.println("Hata: " + e.getMessage());
	        }
	    }

	    private static void updateEmployeeNonProfileFields() {
	        System.out.println("Çalışan bilgileri güncelleniyor...");
	    }

	    private static void hireEmployee() {
	        System.out.println("Yeni çalışan ekleniyor...");
	    }

	    private static void fireEmployee() {
	        System.out.println("Çalışan çıkarılıyor...");
	    }

	    private static void displayProfile(String username) {
	        String query = "SELECT password, phone_no, email FROM employees WHERE username = ?";
	        try (Connection connection = connect();
	             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

	            preparedStatement.setString(1, username);
	            ResultSet resultSet = preparedStatement.executeQuery();

	            if (resultSet.next()) {
	                System.out.println("\n--- Profile Information ---");
	                System.out.println("Password: " + resultSet.getString("password"));
	                System.out.println("Phone Number: " + resultSet.getString("phone_no"));
	                System.out.println("E-mail: " + resultSet.getString("email"));
	            } else {
	                System.out.println("Profil bilgisi bulunamadı.");
	            }
	        } catch (SQLException e) {
	            System.out.println("Hata: " + e.getMessage());
	        }
	    }

	    private static void updateProfile(String username) {
	    	Scanner scanner = new Scanner(System.in);
	    	boolean continueUpdate = true;
	        while (continueUpdate) {
	            System.out.println("\n--- Update Profile ---");
	            System.out.println("Enter 1 to Update Password");
	            System.out.println("Enter 2 to Update Phone Number");
	            System.out.println("Enter 3 to Update E-mail");
	            System.out.println("Enter 4 to Go Back to Main Menu");
	            
	            int choice = scanner.nextInt();
	            scanner.nextLine();
	            
	            switch (choice) {
	                case 1:
	                    System.out.print("Enter Your New Password: ");
	                    String newPassword = scanner.nextLine();
	                    updateField(username, "password", newPassword);
	                    break;
	                case 2:
	                    System.out.print("Enter Your New Phone Number: ");
	                    String newPhoneNo = scanner.nextLine();
	                    updateField(username, "phone_no", newPhoneNo);
	                    break;
	                case 3:
	                    System.out.print("Enter Your new E-mail: ");
	                    String newEmail = scanner.nextLine();
	                    updateField(username, "email", newEmail);
	                    break;
	                case 4:
	                    return; 
	                default:
	                    System.out.println("Geçersiz seçim. Tekrar deneyin.");
	            }
	           
	        }
	    }
	    
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

	    public static void main(String[] args) {
	    	cmpe343project2group4 instance = new cmpe343project2group4(); 
	        instance.login();
	    }
	}
