import java.io.Console;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Patient 
{
   int attempts = 0;
    int maxAttempts = 3;

    private static Connection connection;
    private Scanner sc;

    public Patient(Connection connection, Scanner scanner) 
    {
        Patient.connection = connection;
        this.sc = scanner;
    }

    public void Patient_Register() 
    {
        sc.nextLine();
        
        // Validate Patient Name
        String name=null;
        while (attempts < maxAttempts) 
        {
            System.out.print("Enter Patient Name: ");
            name = sc.nextLine();
            if (!name.matches(".*\\d.*")) 
            {
                break;
            } 
            else 
            {
                attempts++;
                System.out.println("Invalid name. Name should not contain numbers.");
            }
        }
        if (attempts == maxAttempts) 
        {
            System.out.println("Too many invalid attempts for Patient Name.");
            return; 
        }
        
        // Validate Patient Number
        String number = null;      
        while (attempts < maxAttempts) 
        {
            System.out.print("Enter Patient Number (10 digits, starts with 6 or more): ");
            number = sc.nextLine();
            if (number.matches("\\d{10}") && Integer.parseInt(number.substring(0, 2)) >= 60) 
            {
                break;
            } 
            else 
            {
                attempts++;
                System.out.println("Invalid number. Please enter exactly 10 digits and the number should start with 6 or more.");
            }
        }
        if (attempts == maxAttempts) 
        {
            System.out.println("Too many invalid attempts for Patient Name.");
            return; 
        }
        
        // Validate Patient Age
        int age = 0;
        while (attempts < maxAttempts) 
        {
             
            System.out.print("Enter Patient Age: ");
            if (sc.hasNextInt()) 
            {
                 
                age = sc.nextInt();

                if (age > 0 && age <= 120) 
                {
                    break;
                }
                else 
                {
                    attempts++;
                    System.out.println("Age must be a positive number or valid age."+attempts);
                }

            } 
            // else 
            // {
            //     System.out.println("5th");

            //     attempts++;
            //     System.out.println("Invalid input. Please enter a valid integer for age. "+attempts);
            //     sc.next(); 
            // }
        }
        if (attempts == maxAttempts) 
        {
            System.out.println("Too many invalid attempts for Patient Name.");
            return; 
        }
        sc.nextLine();

        // Validate Patient Gender
        String gender = null;
        while (attempts < maxAttempts) 
        {
            System.out.print("Enter Patient Gender (M/F/O): ");
            gender = sc.nextLine();
            if (gender.matches("[MFoO||mfoO]")) 
            {
                break;
            } 
            else 
            {
                attempts++;
                System.out.println("Invalid gender. Please enter M, F, or O.");
            }
        }
        if (attempts == maxAttempts) 
        {
            System.out.println("Too many invalid attempts for Patient Name.");
            return; 
        }
        
        String password = null;
        Console console = System.console();
       
        while (attempts < maxAttempts)
        {
           char[] ch = console.readPassword("Enter Patient Password: ");
           password = new String(ch); 
           if (validatePassword(password)) 
            {
              System.out.println("Password is valid.");
              break;
            }
           else 
           {
              attempts++;
              System.out.println("Password is invalid. It must meet the following criteria:");
              System.out.println("- At least 8 characters long");
              System.out.println("- At least one uppercase letter");
              System.out.println("- At least one lowercase letter");
              System.out.println("- At least one digit");
              System.out.println("- At least one special character (e.g., !@#$%^&*())");
            }
        }
        if (attempts == maxAttempts) 
        {
            System.out.println("Too many invalid attempts for Patient Password.");
            return; 
        }  
        Random random = new Random();
        char[] digits = new char[6];
        digits[0] = (char) (random.nextInt(9) + '1');
        for (int i = 1; i < 6; i++) 
        {
           digits[i] = (char) (random.nextInt(10) + '0');
        }
        int id = Integer.parseInt(new String(digits));
        System.out.println("\nYOUR MEMBERSHIP ID = " + id + "\n");

        if (Patient_Exist(id)) 
        {
           System.out.println(" ---PATIENT ALREADY EXIST OF THIS ID--- ");
           return;
        }

        try 
        {
            String query = "INSERT INTO patients (Patients_Id, Patients_Name, Patients_Number, Patients_Age, Patients_Gender, PassWord) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, number);
            preparedStatement.setInt(4, age);
            preparedStatement.setString(5, gender);
            preparedStatement.setString(6, password);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) 
            {
               System.out.println(" PATIENT REGISTRATION SUCCESSFUL!\n");
            } 
            else 
            {
               System.out.println(" FAILED TO REGISTER PATIENT!\n");
            }
        } 
        catch (SQLException e) 
        {
            System.out.println(" Error in patient registration.");
            e.printStackTrace();
        } 
    }

    public boolean Patient_Exist(int id) 
    {
        //System.out.println("khvx");
        String query = "SELECT * FROM patients WHERE Patients_Id = ?";
        //System.out.println("222");

        try 
        {
        //System.out.println("33333");

            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            //System.out.println("4444");
            return resultSet.next();

        } 
        catch (SQLException e) 
        {
            System.out.println(" Error in patient existing.");
            e.printStackTrace();
        }
        return true;
    }

    public boolean validatePassword(String password) 
    {
        final String PASSWORD_REGEX ="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&*()!+=-])(?=.{8,}).*$";
        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        return pattern.matcher(password).matches();
    }

    public int Patient_Login() 
    {
        int id = 0;
        while (attempts < maxAttempts)  
        {             
            System.out.print("Enter Patient ID: ");
            id = getValidInt();
           
            if (sc.hasNextInt() ) 
            {
                id=sc.nextInt();

                Console console = System.console();
                char[] ch = console.readPassword("Enter Patient Password: ");
                String password = new String(ch); 
                boolean validationMessage = validatePassword(password);
                System.out.println(validationMessage);
                
                if (!Patient_Exist(id)) 
                {  
                    attempts++ ;
                    System.out.println(" ---INVALID PATIENT ID AND PASSWORD--- ");
                }   
                
                String loginQuery = "SELECT * FROM patients WHERE Patients_Id = ? AND PassWord = ?";
                try 
                {
                    PreparedStatement preparedStatement = connection.prepareStatement(loginQuery);
                    preparedStatement.setInt(1, id);
                    preparedStatement.setString(2, password);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) 
                    {
                        return id;
                    }
                } 
                catch (SQLException e) 
                {
                    System.out.println(" Error in patient logging.");
                    e.printStackTrace();
                }
            } 
            else 
            {
                attempts++;
                sc.next(); 
            }
        }
        if (attempts == maxAttempts) 
        {
           System.out.println("Too many invalid attempts for Patient Password.");
           return 0 ;
        }
    return 0;
    }

    public void viewPatients() 
    {
        String query = "SELECT * FROM patients";
        try 
        {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            
            System.out.println("+----------------------------------------------------------------------------+");
            System.out.println("|                            *** Patient LIST ***                            |");
            System.out.println("+------------+------------------+--------------------+----------+------------+");
            System.out.println("| Patient ID | Patient Name     | Mobile Number      | Age      | Gender     |");
            System.out.println("+------------+------------------+--------------------+----------+------------+");
            while (resultSet.next()) 
            {
                int id = resultSet.getInt("Patients_Id");
                String name = resultSet.getString("Patients_Name");
                String number = resultSet.getString("Patients_Number");
                int age = resultSet.getInt("Patients_Age");
                String gender = resultSet.getString("Patients_Gender");
                System.out.printf("| %-10d | %-16s | %-18s | %-8d | %-10s |\n", id, name, number, age, gender);
            }
            System.out.println("+------------+------------------+--------------------+----------+------------+");
        } 
        catch (SQLException e) 
        {
            System.out.println(" Error in view patient.");
            e.printStackTrace();
        }
    }

    public boolean Patient_get(int id) 
    {
        String query = "SELECT * FROM patients WHERE Patients_Id = ?";
        try 
        {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            return resultSet.next();
        } 
        catch (SQLException e) 
        {
            System.out.println(" Error in getting patient from database.");
            e.printStackTrace();
        }
        return false;
    }

    public static void View_PatientAppointment(int patient_id) throws SQLException 
    {
        String query = "SELECT Patients.Patients_Id, Patients.Patients_Name, Doctors.Doctor_Id, Doctors.Doctor_Name, Appointment_Date, Status, Bill " +
                       "FROM Appointment " +
                       "JOIN Patients ON Appointment.Patients_Id = Patients.Patients_Id " +
                       "JOIN Doctors ON Appointment.Doctor_Id = Doctors.Doctor_Id " +
                       "WHERE Patients.Patients_Id = ?";
        try 
        {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, patient_id);
            ResultSet rs = ps.executeQuery();
            System.out.println("+---------------------------------------------------------------------------------------------------------------------+");
            System.out.println("|                                         ***** APPOINTMENT INFORMATION *****                                         |");
            System.out.println("+------------+------------------+-----------+-------------------+------------------+------------------+---------------+");
            System.out.println("| Patient ID |   Patient Name   | Doctor ID |    Doctor Name    | Appointment Date |      Status      |  Patient Bill |");
            System.out.println("+------------+------------------+-----------+-------------------+------------------+------------------+---------------+");
            while (rs.next()) 
            {
                int patientId = rs.getInt(1);
                String patientName = rs.getString(2);
                int doctorId = rs.getInt(3);
                String doctorName = rs.getString(4);
                String appointmentDate = rs.getString(5);
                String status = rs.getString(6);
                int bill = rs.getInt(7);
                System.out.printf("| %-10d | %-16s | %-9d | %-17s | %-16s | %-16s | %-13d |\n",patientId, patientName, doctorId, doctorName, appointmentDate, status, bill);
            }
            System.out.println("+------------+------------------+-----------+-------------------+------------------+------------------+---------------+\n");
        } 
        catch (SQLException e) 
        {
            System.out.println(" Error in view patient patient appointment.");
            e.printStackTrace();
        }
    }
    private int getValidInt() 
    {    
        while (true) 
        {    
            try 
            { 
                return 0;
            }
            catch (InputMismatchException e) 
            {
                System.out.println(" Inputmismatch problem. pease enter integer value.");
                sc.next(); 
            }
        }
    }
}
