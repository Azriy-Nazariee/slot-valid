import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class validateSlot {
    private static final String csvFile = "exampleResponse.csv"; // Replace with your CSV file path
    private static final String csvSplitBy = ","; // Change the delimiter if necessary

    private static final Map<String, Integer> amClassCapacities = initializeAmClassCapacities();
    private static final Map<String, Integer> pmClassCapacities = initializePmClassCapacities();

    private static final List<Student> excessStudentsForAMSession = new ArrayList<>();
    private static final List<Student> excessStudentsForPMSession = new ArrayList<>();

    //arraylist for duplicate students
    private static final List<Student> duplicateStudents = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            // Welcome to SPKG2023 Slot Registration
            System.out.println();
            System.out.println("Welcome to SPKG2023 Slot Registration");
            System.out.println("=====================================");
            displayMenu();
            System.out.println("=====================================");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            System.out.println();

            switch (choice) {
                case 1:
                    printDuplicateStudents();
                    System.out.println();
                    break;
                case 2:
                    printNameListForAMSession();
                    System.out.println();
                    break;
                case 3:
                    printNameListForPMSession();
                    System.out.println();
                    break;
                case 4:
                    printExcessStudentsForAMSession();
                    System.out.println();
                    break;
                case 5:
                    printExcessStudentsForPMSession();
                    System.out.println();
                    break;
                case 6:
                    summarizeRegistrationProcess();
                    System.out.println();
                    break;
                case 7:
                    System.out.println("Exiting...");
                    System.out.println();
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 7);

        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("1) Print Duplicate Students");
        System.out.println("2) Print Name List for AM Session");
        System.out.println("3) Print Name List for PM Session");
        System.out.println("4) Print Excess Students for AM Session");
        System.out.println("5) Print Excess Students for PM Session");
        System.out.println("6) Summarize Registration Process");
        System.out.println("7) Exit");
    }

    private static void printDuplicateStudents() {
        duplicateStudents.clear(); // Clear the list before populating it again
    
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Skip the header line if it exists
            br.readLine();
    
            String line;
    
            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);
    
                if (data.length >= 8) { // Ensure the array has enough elements
                    String amSlot = data[6].trim(); // Assuming AM Slot Selection is in the seventh column
                    String pmSlot = data[7].trim(); // Assuming PM Slot Selection is in the eighth column
                    String studentclass = data[3].trim(); // Assuming Class is in the 4th column
    
                    if (amSlot.equals(pmSlot)) {
                        String fullName = data[2].trim(); // Assuming Full Name is in the third column
                        duplicateStudents.add(new Student(fullName, studentclass, "", "", ""));
                    }
                }
            }
    
            System.out.println("Number of Duplicate Students: " + duplicateStudents.size());
            System.out.println();
            System.out.println("Duplicate Students:");
            for (Student student : duplicateStudents) {
                System.out.println("Name: " + student.getName());
                System.out.println("Class: " + student.getStudentclass());
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }    

    private static boolean isSlotAvailable(String slot, Map<String, Integer> classCapacities) {
    int count = Collections.frequency(excessStudentsForAMSession, slot);
    int capacity = classCapacities.getOrDefault(slot, 0);
    return count < capacity;
}

    private static void printNameListForAMSession() {
        Map<String, List<Student>> amSlotMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Skip the header line if it exists
            br.readLine();

            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);

                if (data.length >= 8) { // Ensure the array has enough elements
                    String fullName = data[2].trim(); // Assuming Full Name is in the third column
                    String amSlot = data[6].trim(); // Assuming AM Slot Selection is in the seventh column
                    String timestamp = data[0].trim(); // Assuming Timestamp is in the first column
                    String studentclass = data[3].trim(); // Assuming Class is in the 4th column

                    if (isSlotAvailable(amSlot, amClassCapacities)) {
                        Student student = new Student(fullName, studentclass, amSlot, "", timestamp);
                        amSlotMap.computeIfAbsent(amSlot, k -> new ArrayList<>()).add(student);
                        reduceSlotCapacity(amSlot, amClassCapacities);
                    } else {
                        excessStudentsForAMSession.add(new Student(fullName, studentclass, amSlot, "", timestamp));
                    }
                }
            }

            System.out.println("Name List for AM Session:");
            System.out.println();

            for (Map.Entry<String, List<Student>> entry : amSlotMap.entrySet()) {
                String amSlot = entry.getKey();
                List<Student> students = entry.getValue();
                System.out.println("AM Slot: " + amSlot);
                System.out.println("Number of students in this class: " + students.size());
                for (Student student : students) {
                    System.out.println("Name: " + student.getName());
                    System.out.println("Class: " + student.getStudentclass());
                    System.out.println("Timestamp: " + student.getTimestamp());
                    System.out.println();
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printNameListForPMSession() {
        Map<String, List<Student>> pmSlotMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // Skip the header line if it exists
            br.readLine();

            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);

                if (data.length >= 8) { // Ensure the array has enough elements
                    String fullName = data[2].trim(); // Assuming Full Name is in the third column
                    String pmSlot = data[7].trim(); // Assuming PM Slot Selection is in the eighth column
                    String studentclass = data[3].trim(); // Assuming Class is in the 4th column
                    String timestamp = data[0].trim(); // Assuming Timestamp is in the first column

                    if (!duplicateStudents.contains(fullName) && isSlotAvailable(pmSlot, pmClassCapacities)) {
                        Student student = new Student(fullName, studentclass, "", pmSlot, timestamp);
                        pmSlotMap.computeIfAbsent(pmSlot, k -> new ArrayList<>()).add(student);
                        reduceSlotCapacity(pmSlot, pmClassCapacities);
                    } else {
                        excessStudentsForPMSession.add(new Student(fullName, studentclass, "", pmSlot, timestamp));
                    }
                }
            }

            System.out.println("Name List for PM Session:");
            System.out.println();

            for (Map.Entry<String, List<Student>> entry : pmSlotMap.entrySet()) {
                String pmSlot = entry.getKey();
                List<Student> students = entry.getValue();
                System.out.println("PM Slot: " + pmSlot);
                System.out.println("Number of students in this class: " + students.size());
                for (Student student : students) {
                    System.out.println("Name: " + student.getName());
                    System.out.println("Class: " + student.getStudentclass());
                    System.out.println("Timestamp: " + student.getTimestamp());
                    System.out.println();
                }
                System.out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void reduceSlotCapacity(String slot, Map<String, Integer> classCapacities) {
        int currentCapacity = classCapacities.getOrDefault(slot, 0);
        classCapacities.put(slot, currentCapacity - 1);
    }

    private static void printExcessStudentsForAMSession() {
    System.out.println("Excess Students for AM Session:");
    // Group students by AM slot
    Map<String, List<Student>> studentsByAMSlot = excessStudentsForAMSession.stream()
            .collect(Collectors.groupingBy(Student::getAmSlot));

    int totalExcessStudentsAM = 0;

    for (Map.Entry<String, List<Student>> entry : studentsByAMSlot.entrySet()) {
        String amSlot = entry.getKey();
        List<Student> students = entry.getValue();

        System.out.println("AM Slot: " + amSlot);
        int numExcessStudentsAM = students.size();
        totalExcessStudentsAM += numExcessStudentsAM;
        System.out.println("Number of excess students for this slot: " + numExcessStudentsAM);
        System.out.println();

        for (Student student : students) {
            System.out.println("Name: " + student.getName());
            System.out.println("Class: " + student.getStudentclass());
            System.out.println("Timestamp: " + student.getTimestamp());
            System.out.println();
        }
    }

    System.out.println("Total Excess Students for AM Session: " + totalExcessStudentsAM);
    System.out.println();
    }

    private static void printExcessStudentsForPMSession() {
        System.out.println("Excess Students for PM Session:");
        // Group students by PM slot
        Map<String, List<Student>> studentsByPMSlot = excessStudentsForPMSession.stream()
                .collect(Collectors.groupingBy(Student::getPmSlot));

        int totalExcessStudentsPM = 0;

        for (Map.Entry<String, List<Student>> entry : studentsByPMSlot.entrySet()) {
            String pmSlot = entry.getKey();
            List<Student> students = entry.getValue();

            System.out.println("PM Slot: " + pmSlot);
            int numExcessStudentsPM = students.size();
            totalExcessStudentsPM += numExcessStudentsPM;
            System.out.println("Number of excess students for this slot: " + numExcessStudentsPM);
            System.out.println();

            for (Student student : students) {
                System.out.println("Name: " + student.getName());
                System.out.println("Class: " + student.getStudentclass());
                System.out.println("Timestamp: " + student.getTimestamp());
                System.out.println();
            }
        }

        System.out.println("Total Excess Students for PM Session: " + totalExcessStudentsPM);
        System.out.println();
    }


    private static Map<String, Integer> initializeAmClassCapacities() {
        Map<String, Integer> amClassCapacities = new HashMap<>();
        amClassCapacities.put("Academics/Lecturer/Scientist/Zoologist/Botanist/Astrophysics", 20);
        amClassCapacities.put("Architecture/Interior Design/Arts & Design", 20);
        amClassCapacities.put("Business/Entrepreneurship/Accounting/Banking/Finance", 20);
        amClassCapacities.put("Computer Science/IT", 20); // Updated capacity to 20
        amClassCapacities.put("Engineering", 40);
        amClassCapacities.put("Entertainment/Film/Music/Modeling/Multimedia", 20);
        amClassCapacities.put("Lawyer/Legal", 20);
        amClassCapacities.put("Medical/Specialist/Dentistry/Pharmacy/Forensic/Vet/Psychology", 60);
        amClassCapacities.put("Others/Undecided", 20);
        return amClassCapacities;
    }

    private static Map<String, Integer> initializePmClassCapacities() {
        Map<String, Integer> pmClassCapacities = new HashMap<>();
        pmClassCapacities.put("Academics/Lecturer/Scientist/Zoologist/Botanist/Astrophysics", 20);
        pmClassCapacities.put("Architecture/Interior Design/Arts & Design", 20);
        pmClassCapacities.put("Business/Entrepreneurship/Accounting/Banking/Finance", 20);
        pmClassCapacities.put("Computer Science/IT", 20); // Updated capacity to 20
        pmClassCapacities.put("Engineering", 40);
        pmClassCapacities.put("Entertainment/Film/Music/Modeling/Multimedia", 20);
        pmClassCapacities.put("Lawyer/Legal", 20);
        pmClassCapacities.put("Medical/Specialist/Dentistry/Pharmacy/Forensic/Vet/Psychology", 60);
        pmClassCapacities.put("Others/Undecided", 20);
        return pmClassCapacities;
    }

    // Student class to store student information including timestamp for sorting
    private static class Student {
        private String name;
        private String amSlot;
        private String pmSlot;
        private String timestamp;
        private String studentclass;

        public Student(String name, String studentclass, String amSlot, String pmSlot, String timestamp) {
            this.name = name;
            this.studentclass = studentclass;
            this.amSlot = amSlot;
            this.pmSlot = pmSlot;
            this.timestamp = timestamp;
        }

        public String getName() {
            return name;
        }

        public String getStudentclass() {
            return studentclass;
        }

        public String getAmSlot() {
            return amSlot;
        }

        public String getPmSlot() {
            return pmSlot;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

    private static void summarizeRegistrationProcess() {
        System.out.println("Registration Process Summary");
        System.out.println("============================");

        // Duplicate Students
        
        System.out.println("Duplicate Students:");
        System.out.println("-------------------");
        System.out.println();
        //print the number of duplicate students
        System.out.println("Number of duplicate students: " + duplicateStudents.size());
        System.out.println();
        //the name of the duplicate students
        for (Student student : duplicateStudents) {
            System.out.println("Name: " + student.getName());
            System.out.println("Class: " + student.getStudentclass());
            System.out.println();
        }
    
        // AM Session Summary
        System.out.println("AM Session:");
        System.out.println("-----------");
        System.out.println("Slot Status:");
        System.out.println();
    
        for (String slot : amClassCapacities.keySet()) {
            int capacity = amClassCapacities.get(slot);
            int availableSpots = getAvailableSpotsAM(slot);
            System.out.println(slot + ": " + (availableSpots > 0 ? "Available (" + availableSpots + " spots left)" : "Full"));
            System.out.println();
        }
    
        System.out.println();
    
        // Excess Students for AM Session
        System.out.println("Excess Students:");
        System.out.println("----------------");
        System.out.println();
    
        if (excessStudentsForAMSession.isEmpty()) {
            System.out.println("No excess students for the AM session.");
        } else {
            System.out.println("Number of excess students: " + excessStudentsForAMSession.size());
            System.out.println();
            System.out.println("Excess Students by Slot:");
            System.out.println();
    
            Map<String, List<Student>> excessStudentsBySlotAM = excessStudentsForAMSession.stream()
                    .collect(Collectors.groupingBy(Student::getAmSlot));
    
            for (Map.Entry<String, List<Student>> entry : excessStudentsBySlotAM.entrySet()) {
                String slot = entry.getKey();
                List<Student> students = entry.getValue();
    
                System.out.println("Slot: " + slot);
                System.out.println("Number of excess students: " + students.size());
                System.out.println();
            }
        }
    
        System.out.println();
    
        // PM Session Summary
        System.out.println("PM Session:");
        System.out.println("-----------");
        System.out.println("Slot Status:");
        System.out.println();
    
        for (String slot : pmClassCapacities.keySet()) {
            int capacity = pmClassCapacities.get(slot);
            int availableSpots = getAvailableSpotsPM(slot);
            System.out.println(slot + ": " + (availableSpots > 0 ? "Available (" + availableSpots + " spots left)" : "Full"));
            System.out.println();
        }
    
        System.out.println();
    
        // Excess Students for PM Session
        System.out.println("Excess Students:");
        System.out.println("----------------");
    
        if (excessStudentsForPMSession.isEmpty()) {
            System.out.println("No excess students for the PM session.");
        } else {
            System.out.println("Number of excess students: " + excessStudentsForPMSession.size());
            System.out.println();
            System.out.println("Excess Students by Slot:");
            System.out.println();
    
            Map<String, List<Student>> excessStudentsBySlotPM = excessStudentsForPMSession.stream()
                    .collect(Collectors.groupingBy(Student::getPmSlot));
    
            for (Map.Entry<String, List<Student>> entry : excessStudentsBySlotPM.entrySet()) {
                String slot = entry.getKey();
                List<Student> students = entry.getValue();
    
                System.out.println("Slot: " + slot);
                System.out.println("Number of excess students: " + students.size());
                System.out.println();
            }
        }
    }
    
    private static int getAvailableSpotsAM(String slot) {
        int capacity = amClassCapacities.getOrDefault(slot, 0);
        int excessStudents = (int) excessStudentsForAMSession.stream()
                .filter(student -> student.getAmSlot().equals(slot))
                .count();
        return Math.max(0, capacity - excessStudents);
    }
    
    private static int getAvailableSpotsPM(String slot) {
        int capacity = pmClassCapacities.getOrDefault(slot, 0);
        int excessStudents = (int) excessStudentsForPMSession.stream()
                .filter(student -> student.getPmSlot().equals(slot))
                .count();
        return Math.max(0, capacity - excessStudents);
    }    
}
