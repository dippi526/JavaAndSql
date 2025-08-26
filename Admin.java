package Project1c.java;

import java.io.Console;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Admin 
{
   int attempts = 0;
    int maxAttempts = 3;

    private Patient patient;
    private Doctors doctors;
    private Connection con;
    private Scanner sc;

    public Admin(Patient patient, Doctors doctors, Connection con, Scanner sc) 
    {
        this.patient = patient;
        this.doctors = doctors;
        this.con = con;
        this.sc = sc;
    }

    public int Admin_longIN() 
    {
        System.out.println(" ----- ADMIN LOGIN ----- ");

        int id=0;
        while (attempts < maxAttempts)  
       {
            System.out.print(" Enter Admin ID: ");
            id = getValidInt();
            sc.nextLine();

            Console console = System.console();
            char[] ch = console.readPassword("Enter Admin Password: ");
            String password = new String(ch); 
            boolean validationMessage = validatePassword(password);
            System.out.println(validationMessage);

            String login_query = "SELECT * FROM Admin WHERE Admin_Id = ? AND Admin_Password = ?";

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
                        attempts++;
                      System.out.println(" INVALID ADMIN ID AND PASSWORD ");
                    }
                }
            } 
            catch (SQLException e) 
            {
               System.out.println("Error in admin login.");
               e.printStackTrace();
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

    public void ViewAppointment() 
    {
        String query = "SELECT Patients.Patients_Id, Patients.Patients_Name, Patients.Patients_Age, Patients.Patients_Gender, "
                +
                "Doctors.Doctor_Id, Doctors.Doctor_name, Doctors.Doctor_Specialiazation, Appointment_date, Patient_disease, "
                +
                "Status, Bill  FROM Appointment " +
                "JOIN patients ON Appointment.Patients_id = Patients.Patients_Id " +
                "JOIN Doctors ON Appointment.Doctor_Id = Doctors.Doctor_Id";

        try (PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) 
           {
            System.out.println("+------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+");
            System.out.println("│                                                                               ***** APPOINTMENT LIST *****                                                                               │");
            System.out.println("+------------+-------------------+-------+------------+-----------+-------------------+-------------------+------------------+-------------------+-----------------+-----------------------+");
            System.out.println("│ Patient ID │ Patient Name      │ Age   │ Gender     │ Doctor ID │ Doctor Name       │ Specialization    │ Appointment Date │ Patient Disease   │     Status      │         Bill          │");
            System.out.println("+------------+-------------------+-------+------------+-----------+-------------------+-------------------+------------------+-------------------+-----------------+-----------------------+");

            while (rs.next()) 
            {
                int patientId = rs.getInt(1);
                String patientName = rs.getString(2);
                int patientAge = rs.getInt(3);
                String patientGender = rs.getString(4);
                int doctorId = rs.getInt(5);
                String doctorName = rs.getString(6);
                String doctorSpecialization = rs.getString(7);
                String appointmentDate = rs.getString(8);
                String patientDisease = rs.getString(9);
                String status = rs.getString(10);
                int bill = rs.getInt(11);

                System.out.printf("│ %-10d │ %-17s │ %-5d │ %-10s │ %-9d │ %-17s │ %-17s │ %-16s │ %-17s │ %-15s │ %-26d │\n",patientId, patientName, patientAge, patientGender, doctorId, doctorName, doctorSpecialization,appointmentDate, patientDisease, status, bill);
                System.out.println("+------------+-------------------+-------+------------+-----------+-------------------+-------------------+------------------+-------------------+-----------------+-----------------------+");
            }
        } 
        catch (SQLException e) 
        {
            System.out.println(" Error in view appointment.");
            e.printStackTrace();
        }
    }

    public void Patient_BookAppointment(int patient_id) 
    {
        System.out.println(" ----- BOOK PATIENT APPOINTMENT ----- ");

        int doctorId=0;
        while (attempts < maxAttempts)  
        {
            System.out.print(" Enter Doctor ID: ");
            doctorId = getValidInt();
            if (!doctors.doctor_get(doctorId)) 
            {
               
               System.out.println(" ----- PLEASE INSERT RIGHT DOCTOR ID ----- ");
               attempts++;
               //return;
            }
        }
        if (attempts == maxAttempts) 
        {
           System.out.println("Too many invalid attempts for Patient Password.");
           return ;
        }   
            sc.nextLine();

            System.out.print(" Enter Your Disease: ");
            String patientDisease = sc.nextLine();
 
            String appointmentDate;  
            while (true) 
           {
                System.out.print(" Enter Appointment Date (YYYY-MM-DD): ");
                appointmentDate = sc.nextLine();
                if (isValidDate(appointmentDate)) 
                {
                    LocalDate today = LocalDate.now();
                    LocalDate appointment = LocalDate.parse(appointmentDate);
                    if (!appointment.isBefore(today)) 
                    {
                     break;
                    } 
                    else 
                    {
                       System.out.println(" Invalid date. Please enter a future date.");
                    }
                } 
                else 
                {
                 System.out.println(" Invalid date format. Please use YYYY-MM-DD.");
                }
            }
 
            try 
            {
                if (patient.Patient_get(patient_id)) 
                {
                    if (doctors.doctor_get(doctorId)) 
                    {
                        if (Check_DoctorAvailability(doctorId, appointmentDate)) 
                        {
                            String query = "INSERT INTO Appointment(Patients_id, Doctor_Id, Patient_disease, Appointment_Date) VALUES (?, ?, ?, ?)";
                            try (PreparedStatement ps = con.prepareStatement(query)) 
                            {
                               ps.setInt(1, patient_id);
                               ps.setInt(2, doctorId);
                               ps.setString(3, patientDisease);
                               ps.setString(4, appointmentDate);
                               int rowAffected = ps.executeUpdate();
                               if (rowAffected > 0) 
                               {
                                 System.out.println(" ----- PATIENT APPOINTMENT Send ----- "); 
                               } 
                               else 
                               {
                                 System.out.println(" Patient Appointment Not Booked");
                               }
                            }
                        } 
                        else 
                       {
                           System.out.println(" ----- DOCTOR IS NOT AVAILABLE ON THIS DATE ----- ");
                       }
                    } 
                    else 
                    {
                       System.out.println(" PLEASE INSERT RIGHT DOCTOR ID ");
                    }
                } 
                else 
                {
                   System.out.println(" PLEASE INSERT RIGHT PATIENT ID ");
                }
            } 
            catch (SQLException e) 
            {
               System.out.println(" Error in book appointment.");
               e.printStackTrace();
            }
    }

    public boolean Check_DoctorAvailability(int doctorId, String date) 
    {
        String query = "SELECT COUNT(*) FROM Appointment WHERE Doctor_Id = ? AND Appointment_Date = ?";
        try (PreparedStatement ps = con.prepareStatement(query)) 
        {
            ps.setInt(1, doctorId);
            ps.setString(2, date);
            try (ResultSet rs = ps.executeQuery()) 
            {
                if (rs.next()) 
                {
                    int count = rs.getInt(1);
                    return count < 3;
                }
            }
        } 
        catch (SQLException e) 
        {
            System.out.println(" Eroor in doctor availability.");
            e.printStackTrace();
        }
        return false;
    }

    private int getValidInt() 
    {
        while (true) 
        {
            try 
            {
                return sc.nextInt();
            } 
            catch (InputMismatchException e) 
            {
                System.out.println(" Inputmismatch problem. please enter integer value.");
                sc.next(); 
            }
        }
    }

    private boolean isValidDate(String date) 
    {
        try 
        {
            LocalDate.parse(date);
            return true;
        } 
        catch (DateTimeParseException e) 
        {
            return false;
        }
    }
}
