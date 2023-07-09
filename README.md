# SPKG2023 Slot Registration Validation

This code is designed to validate slot registrations for a specific event or program. It reads student information from a CSV file, checks for duplicate registrations, verify slot availability, and prints various reports related to the registration process.

## Getting Started

1. Clone the repository or download the code files.

2. Ensure you have Java Development Kit (JDK) installed on your system.

3. Update the `csvFile` variable in the `validateSlot` class to the path of your CSV file containing the slot registration data.

4. Compile the Java source file using the following command:
   ```
   javac validateSlot.java
   ```
   
6. Run the compiled program using the following command:
   ```
   java validateSlot
   ```
   
## Functionality

The program provides the following functionality:

1. **Print Duplicate Students:** This option checks for duplicate registrations where a student has selected the same slot for both the AM and PM sessions. It prints the number of duplicate students found and lists their names.

2. **Print Name List for AM Session:** This option generates a name list for the AM session, grouped by the selected slots. It ensures that the slot capacities are respected and prints the names, class, and timestamp of each student.

3. **Print Name List for PM Session:** This option generates a name list for the PM session, grouped by the selected slots. It checks for duplicate registrations and slot availability. It prints the names, class, and timestamp of each student.

4. **Print Excess Students for AM Session:** This option identifies the students who have selected the AM session but were not assigned a slot due to exceeding the capacity. It prints the number of excess students and lists their names, class, AM slot, and timestamp.

5. **Print Excess Students for PM Session:** This option identifies the students who have selected the PM session but were not assigned a slot due to exceeding the capacity. It prints the number of excess students and lists their names, class, PM slot, and timestamp.

6. **Exit:** This option exits the program.

## CSV File Format

The CSV file should follow the specified format:

```
Timestamp,Email address,Full Name,Class,College Number,Mobile Number,(AM) Slot Selection,(PM) Slot Selection,My Ambition
```

- **Timestamp:** The timestamp when the registration was made.
- **Email address:** The email address of the student.
- **Full Name:** The full name of the student.
- **Class:** The class or course the student belongs to.
- **College Number:** The college number or ID of the student.
- **Mobile Number:** The mobile number of the student.
- **(AM) Slot Selection:** The selected slot for the AM session.
- **(PM) Slot Selection:** The selected slot for the PM session.
- **My Ambition:** The ambition or career aspiration of the student.

Ensure that the CSV file path is correctly specified in the `csvFile` variable.

## Slot Capacities

The program includes predefined capacities for different slot categories. These capacities can be customized by updating the `initializeAmClassCapacities` and `initializePmClassCapacities` methods in the code.

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, feel free to submit a pull request or open an issue.

