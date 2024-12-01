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
	    private static final String PASSWORD = "277353MYSQL.";
	    
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
	            // Regular Employee için özel menü fonksiyonunu çağırın
	            showRegularMenu(username);
	        }
	    }




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
		
						// Create the correct employee instance based on role
						Employee employee;
						if ("manager".equals(role)) {
							employee = new Manager(employee_id, name, surname, username);
						} else {
							employee = new RegularEmployee(employee_id, name, surname, username);
						}
		
						employee.showMenu(); // This will call the correct menu based on the role
						return; // Exit the loop after showing the menu
					} else {
						System.out.println("Incorrect username and/or password. Try again.");
					}
				} catch (SQLException e) {
					System.out.println("Database error: " + e.getMessage());
				}
			}
		}
		


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
                System.out.print("Seçiminiz: ");

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
                        System.out.println("Invalid choice. Try again.");
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
	                    return; // return to login
	                default:
	                    System.out.println("Geçersiz seçim. Tekrar deneyin.");
	            }
	        }
	    }

	    
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
		

	    // Manager menu operations
	    private static void updateProfile1(String username) {
	        Scanner scanner = new Scanner(System.in);

	        System.out.println("\nProfil Güncelleme");
	        System.out.print("Yeni Telefon Numarası: ");
	        String newPhone = scanner.nextLine();

	        System.out.print("Yeni E-posta: ");
	        String newEmail = scanner.nextLine();

	        System.out.print("Yeni Şifre: ");
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
	                System.out.println("Profil başarıyla güncellendi!");
	            } else {
	                System.out.println("Profil güncellenemedi. Kullanıcı bulunamadı.");
	            }

	        } catch (SQLException e) {
	            System.err.println("Profil güncelleme sırasında hata: " + e.getMessage());
	        }
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

	    private static void displayEmployeesWithRole() {
	        Scanner scanner = new Scanner(System.in);
	        System.out.print("Enter role to filter by (e.g., manager, engineer): ");
	        String role = scanner.nextLine();

	        String query = "SELECT * FROM employees WHERE role = ?";
	        try (Connection connection = connect();
	             PreparedStatement stmt = connection.prepareStatement(query)) {

	            stmt.setString(1, role);
	            ResultSet rs = stmt.executeQuery();

	            System.out.println("\n--- Employees with Role: " + role + " ---");
	            while (rs.next()) {
	                System.out.println("Name: " + rs.getString("name"));
	                System.out.println("Username: " + rs.getString("username"));
	                System.out.println("--------------------");
	            }
	        } catch (SQLException e) {
	            System.out.println("Error: " + e.getMessage());
	        }
	    }

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
        
	    
	    
	    private static void updateEmployeeNonProfileFields() {
	        Scanner scanner = new Scanner(System.in);

	        System.out.print("Enter the username of the employee to update: ");
	        String username = scanner.nextLine();

	        String checkQuery = "SELECT * FROM employees WHERE username = ?";
	        try (Connection connection = connect();
	             PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {

	            checkStmt.setString(1, username);
	            ResultSet rs = checkStmt.executeQuery();

	            if (!rs.next()) {
	                System.out.println("No employee found with the given username.");
	                return;
	            }
	        } catch (SQLException e) {
	            System.out.println("Error while checking user existence: " + e.getMessage());
	            return;
	        }

	        System.out.println("\nWhich field do you want to update?");
	        System.out.println("[1] Name");
	        System.out.println("[2] Surname");
	        System.out.println("[3] Role");
	        System.out.print("Your choice: ");
	        int choice = scanner.nextInt();
	        scanner.nextLine(); 

	        String query = null;
	        String newValue = null;

	        switch (choice) {
	            case 1:
	                System.out.print("Enter new Name: ");
	                newValue = scanner.nextLine();
	                query = "UPDATE employees SET name = ? WHERE username = ?";
	                break;
	            case 2:
	                System.out.print("Enter new Surname: ");
	                newValue = scanner.nextLine();
	                query = "UPDATE employees SET surname = ? WHERE username = ?";
	                break;
	            case 3:
	                System.out.print("Enter new Role: ");
	                newValue = scanner.nextLine();
	                query = "UPDATE employees SET role = ? WHERE username = ?";
	                break;
	            default:
	                System.out.println("Invalid choice. No changes made.");
	                return;
	        }

	        try (Connection connection = connect();
	             PreparedStatement stmt = connection.prepareStatement(query)) {

	            stmt.setString(1, newValue);
	            stmt.setString(2, username);

	            int rowsAffected = stmt.executeUpdate();
	            if (rowsAffected > 0) {
	                System.out.println("Field updated successfully!");
	            } else {
	                System.out.println("No employee found with the given username.");
	            }

	        } catch (SQLException e) {
	            System.out.println("Error while updating employee: " + e.getMessage());
	        }
	    }



	    private static void hireEmployee() {
	        Scanner scanner = new Scanner(System.in);
	        System.out.print("Enter name: ");
	        String name = scanner.nextLine();
	        System.out.print("Enter surname: ");
	        String surname = scanner.nextLine();
	        System.out.print("Enter username: ");
	        String username = scanner.nextLine();
	        System.out.print("Enter role: ");
	        String role = scanner.nextLine();

	        String query = "INSERT INTO employees (name, surname, username, role, password) VALUES (?, ?, ?, ?, 'default123')";
	        try (Connection connection = connect();
	             PreparedStatement stmt = connection.prepareStatement(query)) {

	            stmt.setString(1, name);
	            stmt.setString(2, surname);
	            stmt.setString(3, username);
	            stmt.setString(4, role);
	            stmt.executeUpdate();

	            System.out.println("New employee hired successfully.");
	        } catch (SQLException e) {
	            System.out.println("Error: " + e.getMessage());
	        }
	    }

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

	    // Regular Employee Menu Operations
	    private static void displayProfile(String username) {
	        String query = "SELECT password, phone_no, email, role, date_of_birth FROM employees WHERE username = ?";
	        try (Connection connection = connect();
	             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

	            preparedStatement.setString(1, username);
	            ResultSet resultSet = preparedStatement.executeQuery();

	            if (resultSet.next()) {
	                System.out.println("\n--- Profile Information ---");
	                System.out.println("Password: " + resultSet.getString("password"));
	                System.out.println("Phone Number: " + resultSet.getString("phone_no"));
	                System.out.println("E-mail: " + resultSet.getString("email"));
	                System.out.println("Role: " + resultSet.getString("role"));
	                System.out.println("Date of birth: " + resultSet.getString("date_of_birth"));
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
	                    try {
	                        System.out.print("Telefon numaranızı girin: ");
	                        String phoneNumber = scanner.nextLine();

	                        if (phoneNumber.matches(".*[a-zA-Z]+.*")) {
	                            throw new IllegalArgumentException("Telefon numarası harf içeremez.");
	                        }

	                        updateField(username, "phone_no", phoneNumber);
	                    } catch (IllegalArgumentException e) {
	                        System.out.println(e.getMessage());
	                    } catch (Exception e) {
	                        System.out.println("Bir hata oluştu: " + e.getMessage());
	                    }
	                    break;

	                case 3:
	                    System.out.print("Enter Your new E-mail: ");
	                    String newEmail = scanner.nextLine();
	                    updateField(username, "email", newEmail);
	                    break;
	                case 4:
	                    return; 
	                default:
	                    System.out.println("Invalid choice. Try again.");
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


	    public static void main(String[] args) {
	    	displayLaptopAsciiArt();
	    	cmpe343project2group4 instance = new cmpe343project2group4(); 
	        instance.login();
	    }
	}
