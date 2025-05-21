package com.library.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.library.models.Student;

public class ExcelTemplateUtil {

    private static final String[] HEADERS = {
        "First Name*",
        "Last Name*",
        "Course",
        "Council",
        "Contact Number",
        "Email",
        "School Year"
    };

    public static void generateStudentTemplate() {
        try (Workbook workbook = new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Student Import Template");

            // Create header styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle noteStyle = createNoteStyle(workbook);

            // Create headers
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 256 * 20);
            }

            // Add sample data
            addSampleData(sheet);

            // Add notes
            addNotes(sheet, noteStyle);

            // Save template
            saveTemplate(workbook);

        } catch (IOException e) {
            // e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error creating template: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private static CellStyle createNoteStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setItalic(true);
        style.setFont(font);
        return style;
    }

    private static void addSampleData(Sheet sheet) {
        Row sampleRow = sheet.createRow(1);
        sampleRow.createCell(0).setCellValue("Juan");
        sampleRow.createCell(1).setCellValue("Dela Cruz");
        sampleRow.createCell(2).setCellValue("BS Computer Science");
        sampleRow.createCell(3).setCellValue("IT Council");
        sampleRow.createCell(4).setCellValue("09123456789");
        sampleRow.createCell(5).setCellValue("juan.delacruz@email.com");
        sampleRow.createCell(6).setCellValue("2023-2024");
    }

    private static void addNotes(Sheet sheet, CellStyle noteStyle) {
        Row noteRow = sheet.createRow(3);
        Cell noteCell = noteRow.createCell(0);
        noteCell.setCellValue("* Required fields");
        noteCell.setCellStyle(noteStyle);

        Row formatRow = sheet.createRow(4);
        formatRow.createCell(0).setCellValue("Note: Please maintain the column order and format");
    }

    private static void saveTemplate(Workbook workbook) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Excel Template");
        fileChooser.setSelectedFile(new File("student_import_template.xls"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xls"));

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().endsWith(".xls")) {
                file = new File(file.getAbsolutePath() + ".xls");
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
                JOptionPane.showMessageDialog(null,
                        "Template exported successfully to:\n" + file.getAbsolutePath(),
                        "Export Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public static void exportStudentList(List<Student> students) {
        try (Workbook workbook = new HSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Student List");

            // Create header style
            CellStyle headerStyle = createHeaderStyle(workbook);

            // Create headers
            String[] headers = {
                "ID Number", "First Name", "Last Name", "Course",
                "Council", "School Year", "Contact Number",
                "Email", "Registration Date", "Status"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 256 * 20);
            }

            // Add data
            int rowNum = 1;
            for (Student student : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getIdNumber());
                row.createCell(1).setCellValue(student.getFirstName());
                row.createCell(2).setCellValue(student.getLastName());
                row.createCell(3).setCellValue(student.getCourseName());
                row.createCell(4).setCellValue(student.getCouncilName());
                row.createCell(5).setCellValue(student.getSchoolYear());
                row.createCell(6).setCellValue(student.getContactNumber());
                row.createCell(7).setCellValue(student.getEmail());
                row.createCell(8).setCellValue(student.getRegistrationDate() != null
                        ? student.getRegistrationDate().toString() : "");
                row.createCell(9).setCellValue(student.getStatus());
            }

            // Save file
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Student List");
            fileChooser.setSelectedFile(new File("student_list.xls"));
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xls"));

            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".xls")) {
                    file = new File(file.getAbsolutePath() + ".xls");
                }

                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                    JOptionPane.showMessageDialog(null,
                            "Student list exported successfully to:\n" + file.getAbsolutePath(),
                            "Export Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error exporting student list: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
