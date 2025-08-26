package Project1c.java;

import java.io.Console;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Doctors 
{
   int attempts = 0;
    int maxAttempts = 3;

    private Connection con;
    private Scanner sc;

    public Doctors(Connection con, Scanner sc) 
    {
        this.con = con;
        this.sc = sc;
    }

    public int Doctor_logIN() 
    {
        System.out.println(" ----- DOCTOR LOGIN ----- ");

        int id = 0;
        while (attempts < maxAttempts)  
        {
            System.out.print(" Enter Doctor ID: ");
            id = getValidInt();

            if (sc.hasNextInt() ) 
            {
                id=sc.nextInt();

                Console console = System.console();
                char[] ch = console.readPassword("Enter Doctor Password: ");
                String password = new String(ch); 
                boolean validationMessage = validatePassword(password);
                System.out.println(validationMessage);
          
                if (!doctor_extis(id)) 
                {
                    attempts++;
                    System.out.println(" ---INVALID DOCTOR ID--- ");
                }
              
                String login_query = "SELECT * FROM Doctors WHERE Doctor_ID = ? AND Doctor_password = ?";
                try (PreparedStatement preparedStatement = con.prepareStatement(login_query)) 
                {
                  preparedStatement.setInt(1, id);
                  preparedStatement.setString(2, password);
    
                  try (ResultSet resultSet = preparedStatement.executeQuery()) 
                   {
                      if (resultSet.next()) 
                       {
                          return id;
                       } 
                      else 
                       {
                         System.out.println(" ---INVALID DOCTOR PASSWORD--- ");
                         // return 0;
                       }
                   }
                } 
                catch (SQLException e) 
                {
                  System.out.println(" Error in doctor login.");
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
   
    public boolean validatePassword(String password) 
    {
        final String PASSWORD_REGEX ="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&*()!+=-])(?=.{8,}).*$";
        Pattern pattern = Pattern.compile(PASSWORD_REGEX);
        return pattern.matcher(password).matches();
    }
    
    public void ViewDoctors() 
    {
        String query = "SELECT * FROM Doctors";
        try (PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) 
        {
            System.out.println("+----------------------------------------------------+");
            System.out.println("│               ***** DOCTOR LIST *****              │");
            System.out.println("+------------+-------------------+-------------------+");
            System.out.println("│ Doctor ID  │ Doctor Name       │ Specialization    │");
            System.out.println("+------------+-------------------+-------------------+");

            while (rs.next()) 
            {
                int id = rs.getInt("Doctor_ID");
                String name = rs.getString("Doctor_Name");
                String specialization = rs.getString("Doctor_Specialiazation");
                System.out.printf("│ %-10d │ %-17s │ %-17s │\n", id, name, specialization);
                System.out.println("+------------+-------------------+-------------------+");

            } 
        } 
        catch (SQLException e) 
        {
            System.out.println(" Error in view doctor.");
            e.printStackTrace();
        }
    }

    public boolean doctor_get(int id) 
    {
        String query = "SELECT * FROM Doctors WHERE Doctor_ID = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) 
        {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) 
            {
                return resultSet.next();
            }
        } 
        catch (SQLException e) 
        {
            System.out.println(" Error in getting doctor id from database.");
            e.printStackTrace();
        }
        return false;
    }

    public void addDoctor() 
    {
        System.out.println(" ----- ADD NEW DOCTOR ----- ");
        System.out.print(" Enter Doctor ID: ");
        int id = getValidInt();
        if (doctor_extis(id)) 
        {
            System.out.println(" THIS DOCTOR ID ALREADY EXISTS. TRY AGAIN. ");
            return;
        }
        sc.nextLine();
        System.out.print(" Enter Doctor Name: ");
        String name = sc.nextLine();
        System.out.print(" Enter Doctor Specialization: ");
        String specialization = sc.nextLine();
        System.out.print(" Enter Doctor Password: ");
        String password = sc.nextLine();

        String addDoctorQuery = "INSERT INTO Doctors(Doctor_ID, Doctor_Name, Doctor_Specialiazation, Doctor_password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(addDoctorQuery)) 
        {
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, specialization);
            ps.setString(4, password);
            int rowAffected = ps.executeUpdate();
            if (rowAffected > 0) 
            {
                System.out.println(" ----- NEW DOCTOR ADDED SUCCESSFULLY ----- ");
            } 
            else 
            {
                System.out.println(" ----- FAILED TO ADD NEW DOCTOR ----- ");
            }
        } 
        catch (SQLException e) 
        {
            System.out.println("Error in adding doctor");
            e.printStackTrace();
        }
    }

    public boolean doctor_extis(int id) 
    {
        String query = "SELECT * FROM Doctors WHERE Doctor_ID = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) 
        {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) 
            {
                return resultSet.next();
            }
        } 
        catch (SQLException e) 
        {
            System.out.println("Error in doctor existing. ");
            e.printStackTrace();
        }
        return false;
    }

    public void delete_doctor() 
    {
        System.out.println(" ----- DELETE DOCTOR ----- ");

        int id=0;
        while (attempts < maxAttempts)  
        {
            System.out.print(" Enter Doctor ID: ");
            id = getValidInt();
            sc.nextInt();

            if (!doctor_extis(id)) 
            {
                attempts++;
                System.out.println(" DOCTOR ID DOES NOT EXIST. TRY AGAIN. ");
              // return;
            }
        }   
        if (attempts == maxAttempts) 
        {
           System.out.println("Too many invalid attempts for Patient Password.");
        }         

            String deleteQuery = "DELETE FROM Doctors WHERE Doctor_ID = ?";
            try (PreparedStatement ps = con.prepareStatement(deleteQuery)) 
            {
              ps.setInt(1, id);
              int rowAffected = ps.executeUpdate();
              if (rowAffected > 0) 
               {
                 System.out.println(" ----- DOCTOR REMOVED SUCCESSFULLY ----- ");
               } 
               else 
               {
                  System.out.println(" ----- FAILED TO REMOVE DOCTOR ----- ");
               }
            } 
            catch (SQLException e) 
            {
               System.out.println(" Error in removing doctor.");
              e.printStackTrace();
            } 
        
    }

    public void ViewAppointment(int doctor_id) 
    {
        String query = "SELECT Patients.Patients_Id, Patients.Patients_Name, Patient_disease, Appointment_date, Status, Bill " +
                       "FROM Appointment " +
                       "JOIN Patients ON Appointment.Patients_id = Patients.Patients_Id " +
                       "JOIN Doctors ON Appointment.Doctor_Id = Doctors.Doctor_Id " +
                       "WHERE Doctors.Doctor_Id = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) 
        {
            ps.setInt(1, doctor_id);
            try (ResultSet rs = ps.executeQuery()) 
            {
                System.out.println("+-----------------------------------------------------------------------------------------------------------------+");
                System.out.println("│                                     ***  APPOINTMENT INFORMATION ***                                            │");
                System.out.println("+------------+-------------------+-------------------+-------------------+-------------------+--------------------+");
                System.out.println("│ Patient ID │   Patient Name    │     Disease       │ Appointment Date  │      Status       │  Amount Received   │");
                System.out.println("+------------+-------------------+-------------------+-------------------+-------------------+--------------------+");
                while (rs.next()) 
                {
                    int patientId = rs.getInt(1);
                    String patientName = rs.getString(2);
                    String disease = rs.getString(3);
                    String appointmentDate = rs.getString(4);
                    String status = rs.getString(5);
                    int bill = rs.getInt(6);
                    System.out.printf("│ %-10d │ %-17s │ %-17s │ %-17s │ %-17s │ %-18d │\n",patientId, patientName, disease, appointmentDate, status, bill);
                    System.out.println("+------------+-------------------+-------------------+-------------------+-------------------+--------------------+");
                }
            }
        } 
        catch (SQLException e) 
        {
            System.out.println("Error in view appointment.");
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
