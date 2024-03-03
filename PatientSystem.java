import java.sql.*;
import java.util.Scanner;
import java.util.UUID;

public class PatientSystem {
    public static void main(String[] args) throws SQLException {
        Connection conn = null;

        System.out.println("Starting Patient System... \n");
        conn = getConnection(conn);

        do{

            Scanner scanner = new Scanner(System.in);
            int userInput;
            while (true){
                userInput = getUserInput(scanner);
                if (userInput >=1 && userInput <=3){
                    break;
                }
                System.out.println("Please enter value btn 1 and 3");
            }

//        check if patients table exists
            boolean patientTableExists;
            patientTableExists = tableExists(conn);

            if (!patientTableExists){
                createPatientsTable(conn);
            }
            processUserInput(userInput, conn);

        } while (true);



    }

    private static boolean tableExists(Connection conn) throws SQLException {
        ResultSet resultSet = conn.getMetaData().getTables(null, null, "PATIENTS", null);
        return resultSet.next();
    }

    public static void createPatientsTable(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "CREATE TABLE patients " +
                "(ID TEXT    NOT NULL," +
                " NAME           TEXT    NOT NULL, " +
                " AGE            INT     NOT NULL, " +
                " SYMPTOMS         TEXT    NOT NULL)";
        stmt.executeUpdate(sql);
        stmt.close();
        System.out.println("Patients Table Created.");
    }

    public static void processUserInput(int userInput, Connection conn) throws SQLException {
        if (userInput == 1){
            askPatientDetails(conn);
        } else if (userInput == 2) {
            showPatientRecords(conn);
        } else if (userInput == 3){
            conn.close();
            System.out.println("Quiting system, Bye!");
            System.exit(0);
        }
    }

    public  static void askPatientDetails(Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        UUID uuid = UUID.randomUUID();
        String name;
        int age;
        String symptom;
        name = askPatientName(scanner);
        age = askPatientAge(scanner);
        symptom = askPatientSymptom(scanner);
        while (symptom.length() < 4){
            symptom = askPatientSymptom(scanner);
        }
        createPatientRecord(String.valueOf(uuid), name, age, symptom, conn);
        showPatientRecords(conn);
    }

    private static void createPatientRecord(
            String uuid, String name, int age, String symptom, Connection conn
    ) throws SQLException {
        System.out.println("Adding to database.");
        String insertSQL = "INSERT INTO patients (ID, NAME, AGE, SYMPTOMS) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(insertSQL);
        pstmt.setString(1, uuid);
        pstmt.setString(2, name);
        pstmt.setInt(3, age);
        pstmt.setString(4, symptom);
        pstmt.executeUpdate();
        pstmt.close();
    }

    public  static String askPatientName(Scanner scanner){
        System.out.print("Patient Name: ");
        return  scanner.nextLine();
    }
    public  static int askPatientAge(Scanner scanner){
        System.out.print("Age: ");
        return  scanner.nextInt();
    }
    public static String askPatientSymptom(Scanner scanner){
        System.out.print("Symptoms: ");
        return  scanner.nextLine();
    }

    public  static void showPatientRecords(Connection conn) throws SQLException {
        System.out.println("Fetching Patient records...");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM patients;");
        while (rs.next()) {
            String id = rs.getString("ID");
            String name = rs.getString("NAME");
            int age = rs.getInt("AGE");
            String symptoms = rs.getString("SYMPTOMS");

            System.out.println("ID = " + id);
            System.out.println("NAME = " + name);
            System.out.println("AGE = " + age);
            System.out.println("SYMPTOMS = " + symptoms);
            System.out.println();
        }
        rs.close();
        stmt.close();
    }


    public static int getUserInput(Scanner scanner){
        try {
            String enterRecord = "\t 1: Enter Record \n";
            String viewRecords = "\t 2: View Records \n";
            String exit = "\t 3: Exit";
            System.out.println("Press: \n" + enterRecord + viewRecords + exit);
            return scanner.nextInt();
        } catch ( Exception e){
            return 0;
        }
    }

    public static Connection getConnection(Connection conn){
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("Opened database successfully");
            return DriverManager.getConnection("jdbc:sqlite:patient.db");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
            return null;
        }
    }
}
