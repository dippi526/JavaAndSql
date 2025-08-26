package Project1c.java;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Doctor_request 
{
   private Connection con;
    private Scanner sc;

    public Doctor_request(Connection con, Scanner sc) 
    {
        this.con = con;
        this.sc = sc;
    }

    public void PatientRequest(int doctorId) throws SQLException 
    {
        String query = "SELECT Patients.Patients_Id, Patients.Patients_Name, Patient_disease, Appointment_date, Status " +
                       "FROM Appointment " +
                       "JOIN Patients ON Appointment.Patients_id = Patients.Patients_Id " +
                       "JOIN Doctors ON Appointment.Doctor_Id = Doctors.Doctor_Id " +
                       "WHERE Appointment.Doctor_Id = ? AND Status = 'Pending'";
        try (PreparedStatement ps = con.prepareStatement(query)) 
        {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) 
            {
                System.out.println("+--------------------------------------------------------------------------------------------+");
                System.out.println("│                                  ***** Patient LIST *****                                  │");
                System.out.println("+------------+-------------------+-------------------+-------------------+-------------------+");
                System.out.println("│ Patient ID │   Patient Name    │     Disease       │ Appointment Date  │     Status        │");
                System.out.println("+------------+-------------------+-------------------+-------------------+-------------------+");
                while (rs.next()) 
                {
                    int patientId = rs.getInt("Patients_Id");
                    String patientName = rs.getString("Patients_Name");
                    String disease = rs.getString("Patient_disease");
                    String appointmentDate = rs.getString("Appointment_date");
                    String status = rs.getString("Status");
                    System.out.printf("│ %-10d │ %-17s │ %-17s │ %-17s │ %-17s │\n",patientId, patientName, disease, appointmentDate, status);
                    System.out.println("+------------+-------------------+-------------------+-------------------+-------------------+");
                }
            }
        } 
        catch (SQLException e) 
        {
            System.out.println(" Error in patient request.");
           e.printStackTrace();
        }
    }

    public void Patient_request_Accepted(int doctorId) throws SQLException 
    {
        System.out.println();
        int patientId = getIntInput("Enter the Patient ID to accept the request: ");

        String query = "UPDATE Appointment SET Status = 'Accepted' WHERE Patients_id = ? AND Doctor_Id = ? AND Status = 'Pending' LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(query)) 
        {
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            int rowAffected = ps.executeUpdate();
            if (rowAffected > 0) 
            {
                System.out.println(" ----- Patient Request Accepted Successfully ------ ");
                Doctor_Bill(patientId, doctorId);
            } 
            else 
            {
                System.out.println(" ----- Problem with Patient Request ----- ");
            }
        } 
        catch (SQLException e) 
        {
            System.out.println(" Error in patient request accepted.");
           e.printStackTrace();
        }
    }

    public void afternoon_slot(int doctorId) throws SQLException
    {
       System.out.println(" ");
       int patientId = getIntInput("Enter the Patient ID for bookig the appointment slot: ");

       String query =" UPDATE Appointment SET Slot = 'Afternoon' WHERE  Patients_id = ? AND Doctor_Id = ? AND Slot = 'Morning' LIMIT 1  ";
       try (PreparedStatement ps = con.prepareStatement(query)) 
       {
           ps.setInt(1, patientId);
           ps.setInt(2, doctorId);
           int rowAffected = ps.executeUpdate();
           if (rowAffected > 0) 
           {
               System.out.println(" ----- PATIENT APPOINTMENT IS IN AFTERNOON SLOT  ------ ");
               Doctor_Bill(patientId, doctorId);
           } 
           else 
           {
               System.out.println(" ----- Problem with afternoon slot ----- ");
           }
       } 
       catch (SQLException e) 
       {
           System.out.println(" Error in afternoon slot.");
          e.printStackTrace();
       }
    }

    public void evening_slot(int doctorId) throws SQLException
    {
       System.out.println(" ");
       int patientId = getIntInput("Enter the Patient ID for bookig the appointment slot: ");

       String query =" UPDATE Appointment SET Slot = 'Evening' WHERE  Patients_id = ? AND Doctor_Id = ? AND Slot = 'Morning' LIMIT 1  ";
       try (PreparedStatement ps = con.prepareStatement(query)) 
       {
           ps.setInt(1, patientId);
           ps.setInt(2, doctorId);
           int rowAffected = ps.executeUpdate();
           if (rowAffected > 0) 
           {
               System.out.println(" ----- PATIENT APPOINTMENT IS IN EVENING SLOT ------ ");
               Doctor_Bill(patientId, doctorId);
           } 
           else 
           {
               System.out.println(" ----- Problem with evening slot ----- ");
           }
       } 
       catch (SQLException e) 
       {
           System.out.println(" Error in evening slot.");
          e.printStackTrace();
       }
    }

    public void Patient_request_rejected(int doctorId) throws SQLException 
    {
        System.out.println();
        int patientId = getIntInput("Enter the Patient ID to reject the request: ");
        
        String query = "UPDATE Appointment SET Status = 'Rejected' WHERE Patients_id = ? AND Doctor_Id = ? AND Status = 'Pending' LIMIT 1";
        try (PreparedStatement ps = con.prepareStatement(query)) 
        {
            ps.setInt(1, patientId);
            ps.setInt(2, doctorId);
            int rowAffected = ps.executeUpdate();
            if (rowAffected > 0) 
            {
                System.out.println(" ----- Patient Request Rejected Successfully ----- ");
            } 
            else 
            {
                System.out.println(" ----- Problem with Patient Request ----- ");
            }
        } 
        catch (SQLException e) 
        {
            System.out.println(" Error in patient request rejected.");
           e.printStackTrace();
        }
    }

    public boolean Requst_Exits(int doctorId) throws SQLException 
    {
        String query = "SELECT * FROM Appointment WHERE Doctor_Id = ? AND Status = 'Pending'";
        try (PreparedStatement ps = con.prepareStatement(query)) 
        {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) 
            {
                return rs.next();
            }
        } 
        catch (SQLException e) 
        {
            System.out.println(" Error in id existing.");
            e.printStackTrace();
        }
        return false;
    }

    public void Doctor_Bill(int patientId, int doctorId) throws SQLException 
    {

      System.out.println();
      int doctorBill = getIntInput("Enter Doctor Payment Amount: ");
    
        String query = "UPDATE Appointment SET Bill = ? WHERE Patients_id = ? AND Doctor_Id = ? AND Bill = 0";
    
      try (PreparedStatement ps = con.prepareStatement(query)) 
      {
        ps.setInt(1, doctorBill);
        ps.setInt(2, patientId);
        ps.setInt(3, doctorId);
        
        int rowAffected = ps.executeUpdate();
        
        if (rowAffected > 0) 
        {
            System.out.println(" Patient Bill Updated Successfully ");
            System.out.println(" Amount: " + doctorBill + "  ");
        } 
        else 
        {
            System.out.println(" ----- Problem with Bill Update ----- ");
        }
    } 
    catch (SQLException e) 
    {
        System.out.println(" Error in bill.");
        e.printStackTrace();  
    }
}

    private int getIntInput(String prompt) 
    {
        int value = -1;
        boolean valid = false;
        while (!valid) 
        {
            System.out.print(prompt);
            try 
            {
                value = sc.nextInt();
                sc.nextLine(); 
                valid = true;
            } 
            catch (InputMismatchException e) 
            {
                System.out.println(" Inputmismatch problem. please enter integer value.");
                sc.next(); 
            }
        }
        return value;
    }
}
