package Project1c.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Hospital_management 
{
    public static void main(String[] args) 
    {
        String Url = "jdbc:mysql://localhost:3306/Hospital_Appointment";
        String username = "root";
        String userpassword = "Dippi@2005";

        try 
        {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } 
        catch (ClassNotFoundException e) 
        {
            System.out.println("JDBC Driver not found.");
            e.printStackTrace();
            return;
        }

        try (Connection con = DriverManager.getConnection(Url, username, userpassword);
            Scanner sc = new Scanner(System.in)) 
        {
            Patient patient = new Patient(con, sc);
            Doctors doctor = new Doctors(con, sc);
            Doctor_request request = new Doctor_request(con, sc);
            Admin admin = new Admin(patient, doctor, con, sc);

            while (true) 
            {
                System.out.println(" ----- WELCOME TO HOSPITAL -----");
                System.out.println("1. Register Patient");
                System.out.println("2. Patient Log In");
                System.out.println("3. Doctor Log In");
                System.out.println("4. Admin Log In");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");

                int choice = getValidInt(sc);
                
                if(choice >=1 && choice <=5)
                {
                  switch (choice) 
                  {
                      case 1:// Register patient
                          patient.Patient_Register();
                      break;

                      case 2:// Patient log in
                         int patientId = patient.Patient_Login();
                          if (patientId != 0) 
                          {
                            System.out.println(" ----- PATIENT LOGGED IN SUCCESSFUL! ----- ");
 
                              int patientChoice = 0;
                              while (patientChoice != 4) 
                              {
                                  System.out.println(" -----PATIENT MENU-----");
                                  System.out.println("1. View Doctors");
                                  System.out.println("2. Book Appointment");
                                  System.out.println("3. View Appointments");
                                  System.out.println("4. Log Out");
                                  System.out.print("Select an option: ");
                                  patientChoice = getValidInt(sc);

                                  if(patientChoice>=1 && patientChoice<=4)
                                  {
                                     switch (patientChoice) 
                                     {
                                     case 1:
                                         doctor.ViewDoctors();
                                     break;

                                     case 2:
                                         admin.Patient_BookAppointment(patientId);
                                     break;

                                     case 3:
                                         Patient.View_PatientAppointment(patientId);
                                     break;

                                      case 4:
                                         System.out.println(" ----- PATIENT LOGGED OUT ----- ");
                                      break;

                                      default:
                                         System.out.println(" -----INVALID OPTION ----- ");
                                      break;
                                     }
                                    }
                                    else 
                                    {
                                        System.out.println(" Invalid choice. please enter valid choice.");
                                    }
                                }
                            } 
                            else 
                            {
                               System.out.println(" ---INVALID PATIENT DETAILS--- ");
                               System.out.println(" ");
                            }
                        break;

                    case 3:// Doctor work
                        int doctorId = doctor.Doctor_logIN();
                        if (doctorId != 0) 
                        {
                            System.out.println(" ----- DOCTOR LOGGED IN SUCCESSFUL! ----- ");

                            int doctorChoice = 0;
                            while (doctorChoice != 3) 
                            {
                                System.out.println(" -----DOCTOR MENU----- ");
                                System.out.println("1. View Patient Appointments");
                                System.out.println("2. Check Patient Requests");
                                System.out.println("3. Log Out");
                                System.out.print("Select an option: ");
                                doctorChoice = getValidInt(sc);

                                if(doctorChoice>=1 && doctorChoice<=3)
                                {
                                  switch (doctorChoice) 
                                  {
                                    case 1:
                                        doctor.ViewAppointment(doctorId);
                                    break;

                                    case 2:
                                        if (!request.Requst_Exits(doctorId)) 
                                        {
                                            System.out.println(" NO PATIENT REQUEST FOUND.... ");
                                            break;
                                        }
                                        request.PatientRequest(doctorId);
                                        System.out.println("1. ACCEPT PATIENT REQUEST ");
                                        System.out.println("2. REJECT PATIENT REQUEST ");
                                        System.out.print("Enter your choice: ");
                                        int requestChoice = getValidInt(sc);
   
                                        if(requestChoice>=1 && requestChoice<=2)
                                        {
                                           switch (requestChoice) 
                                           {
                                                case 1:
                                                  request.Patient_request_Accepted(doctorId);
                                                  System.out.println(" ");
                                                  System.out.println("1. APPOINTMENT IN MORNING ");
                                                  System.out.println("2. APPOINTMENT IN AFTERNOON ");
                                                  System.out.println("3. APPOINTMENT IN EVENING ");
                                                  int slotchoice = getValidInt(sc);
                                                
                                                  if(slotchoice>=1 && slotchoice<=3)
                                                  {
                                                        switch (slotchoice) 
                                                       {
                                                            case 1:// for morning slot
                                                              System.out.println(" PATIENT APPOINTMENT IS IN MORNING SLOT ");
                                                            break;

                                                            case 2:// for afternoon slot 
                                                             request.afternoon_slot(doctorId);
                                                            break;

                                                            case 3:// for evening slot
                                                             request.evening_slot(doctorId);
                                                            break;
                                                
                                                            default:
                                                             System.out.println(" -----INVALID OPTION----- ");
                                                            break;
                                                       }
                                                    }
                                                    else
                                                   {
                                                     System.out.println(" Invalid choice. please enter valid choice.");
                                                   }
        
                                                break;

                                                case 2:
                                                  request.Patient_request_rejected(doctorId);
                                                break;
                                            
                                                default:
                                                  System.out.println(" -----INVALID OPTION----- ");
                                                break;
                                           }
                                        }
                                        else
                                        {
                                            System.out.println(" Invalid choice. please enter valid choice.");
                                        }
                                    break;

                                    case 3:
                                        System.out.println(" ----- DOCTOR LOGGED OUT ----- ");
                                    break;

                                    default:
                                        System.out.println(" -----INVALID OPTION----- ");
                                    break;
                                  }
                                }
                                else
                                {
                                  System.out.println(" Invalid choice. please enter valid choice.");
                                }
                            }
                        } 
                        else 
                        {
                            System.out.println(" ---INVALID DOCTOR DETAILS--- ");
                        }
                    break;

                    case 4:// Admin work
                        int adminId = admin.Admin_longIN();
                        if (adminId != 0) 
                        {
                            System.out.println(" ----- ADMIN LOGGED IN SUCCESSFUL! ----- ");

                            int adminChoice = 0;
                            while (adminChoice != 6) 
                            {
                                System.out.println(" -----ADMIN MENU----- ");
                                System.out.println("1. View Appointment List");
                                System.out.println("2. View Patient List");
                                System.out.println("3. View All Doctor List");
                                System.out.println("4. Add New Doctor");
                                System.out.println("5. Delete Doctor");
                                System.out.println("6. Exit");
                                System.out.print("Select an option: ");
                                adminChoice = getValidInt(sc);
                                
                                if(adminChoice>=1 && adminChoice<=6)
                                {
                                   switch (adminChoice) 
                                   {
                                        case 1:
                                         admin.ViewAppointment();
                                        break;

                                        case 2:
                                         patient.viewPatients();
                                        break;

                                        case 3:
                                         doctor.ViewDoctors();
                                        break;

                                        case 4:
                                         doctor.addDoctor();
                                        break;

                                        case 5:
                                         doctor.delete_doctor();
                                        break;

                                        case 6:
                                         System.out.println(" ----- ADMIN LOGGED OUT ----- ");
                                        break;

                                        default:
                                         System.out.println(" ---INVALID OPTION--- ");
                                        break;
                                   }
                               }
                               else
                               {
                                 System.out.println(" Invalid choice. please enter valid choice.");
                               }
                            }
                        } 
                        else 
                        {
                            System.out.println(" ---INVALID ADMIN DETAILS--- ");
                        }
                    break;

                    case 5:
                        System.out.println(" ----- THANK YOU FOR VISITING ----- ");
                    return;

                    default:
                        System.out.println("-----INVALID CHOICE-----");
                    break;
                  }
                }
                else
                {
                  System.out.println(" invalid choice. please enter valid choice.");
                }
            }
        } 
        catch (SQLException e) 
        {
            System.out.println("Database error. Please check your database connection and query.");
            e.printStackTrace();
        }
    }

    // Method to get a valid integer input
    private static int getValidInt(Scanner sc) 
    {
        while (true) 
        {
            try 
            {
                return sc.nextInt();
            } 
            catch (InputMismatchException e) 
            {
                System.out.println(" Input mismatch problem. please insert integer value.");
                sc.next(); 
            }
        }
    }
}
