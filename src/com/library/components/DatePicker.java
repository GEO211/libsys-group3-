package com.library.components;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class DatePicker extends JPanel {
    private JTextField dateField;
    private JButton pickButton;
    private Date selectedDate;
    private Date minimumDate;
    private SimpleDateFormat dateFormat;
    private JPopupMenu contextMenu;
    
    public DatePicker() {
        this(new Date());
    }
    
    public DatePicker(Date initialDate) {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        selectedDate = initialDate;
        minimumDate = null;  // No minimum date by default
        
        setLayout(new BorderLayout(5, 0));
        
        dateField = new JTextField();
        dateField.setEditable(false);
        dateField.setText(dateFormat.format(selectedDate));
        
        pickButton = new JButton("...");
        pickButton.addActionListener(e -> showCalendarDialog());
        
        // Create context menu
        createContextMenu();
        
        // Add mouse listener for right-click
        dateField.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    contextMenu.show(dateField, e.getX(), e.getY());
                }
            }
        });
        
        add(dateField, BorderLayout.CENTER);
        add(pickButton, BorderLayout.EAST);
    }
    
    private void createContextMenu() {
        contextMenu = new JPopupMenu();
        
        // View Details option
        JMenuItem viewDetails = new JMenuItem("View Details");
        viewDetails.addActionListener(e -> showDetailsDialog());
        
        // Print option
        JMenuItem print = new JMenuItem("Print");
        print.addActionListener(e -> printDate());
        
        // Edit option
        JMenuItem edit = new JMenuItem("Edit");
        edit.addActionListener(e -> showCalendarDialog());
        
        contextMenu.add(viewDetails);
        contextMenu.add(print);
        contextMenu.add(edit);
    }
    
    private void showDetailsDialog() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog;
        
        if (parentWindow instanceof Frame) {
            dialog = new JDialog((Frame) parentWindow, "Date Details", true);
        } else if (parentWindow instanceof Dialog) {
            dialog = new JDialog((Dialog) parentWindow, "Date Details", true);
        } else {
            dialog = new JDialog((Frame) null, "Date Details", true);
        }
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create a detailed view of the date
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);
        
        SimpleDateFormat fullFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
        JLabel fullDate = new JLabel(fullFormat.format(selectedDate));
        fullDate.setFont(fullDate.getFont().deriveFont(14f));
        
        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        detailsPanel.add(new JLabel("Day of Week:"));
        detailsPanel.add(new JLabel(new SimpleDateFormat("EEEE").format(selectedDate)));
        
        detailsPanel.add(new JLabel("Day of Month:"));
        detailsPanel.add(new JLabel(String.valueOf(cal.get(Calendar.DAY_OF_MONTH))));
        
        detailsPanel.add(new JLabel("Month:"));
        detailsPanel.add(new JLabel(new SimpleDateFormat("MMMM").format(selectedDate)));
        
        detailsPanel.add(new JLabel("Year:"));
        detailsPanel.add(new JLabel(String.valueOf(cal.get(Calendar.YEAR))));
        
        detailsPanel.add(new JLabel("Day of Year:"));
        detailsPanel.add(new JLabel(String.valueOf(cal.get(Calendar.DAY_OF_YEAR))));
        
        detailsPanel.add(new JLabel("Week of Year:"));
        detailsPanel.add(new JLabel(String.valueOf(cal.get(Calendar.WEEK_OF_YEAR))));
        
        panel.add(fullDate, BorderLayout.NORTH);
        panel.add(detailsPanel, BorderLayout.CENTER);
        
        // Add close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private void printDate() {
        // Create a formatted string for printing
        SimpleDateFormat fullFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
        String dateToPrint = "Date: " + fullFormat.format(selectedDate);
        
        // Create a simple print job
        try {
            java.awt.print.PrinterJob job = java.awt.print.PrinterJob.getPrinterJob();
            job.setPrintable((graphics, pageFormat, pageIndex) -> {
                if (pageIndex > 0) {
                    return java.awt.print.Printable.NO_SUCH_PAGE;
                }
                
                graphics.setFont(new java.awt.Font("Serif", java.awt.Font.PLAIN, 12));
                graphics.drawString(dateToPrint, 100, 100);
                
                return java.awt.print.Printable.PAGE_EXISTS;
            });
            
            if (job.printDialog()) {
                job.print();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error printing: " + ex.getMessage(),
                "Print Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void setMinimumDate(Date minDate) {
        this.minimumDate = minDate;
        // If current selected date is before minimum date, update it
        if (minimumDate != null && selectedDate.before(minimumDate)) {
            setDate(minimumDate);
        }
    }
    
    private void showCalendarDialog() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog dialog;
        if (parentWindow instanceof Frame) {
            dialog = new JDialog((Frame) parentWindow, "Select Date", true);
        } else if (parentWindow instanceof Dialog) {
            dialog = new JDialog((Dialog) parentWindow, "Select Date", true);
        } else {
            dialog = new JDialog((Frame) null, "Select Date", true);
        }
        dialog.setLayout(new BorderLayout());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        JPanel calendarPanel = new JPanel(new BorderLayout());
        JPanel daysPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        JPanel headerPanel = new JPanel(new FlowLayout());
        JButton prevMonth = new JButton("<<");
        JButton nextMonth = new JButton(">>");
        JLabel monthLabel = new JLabel(new SimpleDateFormat("MMMM").format(calendar.getTime()));
        JLabel yearLabel = new JLabel(String.valueOf(calendar.get(Calendar.YEAR)));
        monthLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        yearLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        // Month picker
        monthLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String[] months = new SimpleDateFormat().getDateFormatSymbols().getMonths();
                String selected = (String) JOptionPane.showInputDialog(dialog, "Select Month", "Month",
                        JOptionPane.PLAIN_MESSAGE, null, months, months[calendar.get(Calendar.MONTH)]);
                if (selected != null) {
                    for (int i = 0; i < months.length; i++) {
                        if (months[i].equals(selected)) {
                            calendar.set(Calendar.MONTH, i);
                            break;
                        }
                    }
                    monthLabel.setText(new SimpleDateFormat("MMMM").format(calendar.getTime()));
                    updateCalendarDays(daysPanel, calendar);
                }
            }
        });
        // Year picker
        yearLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                int startYear = currentYear - 50;
                int endYear = currentYear + 10;
                Integer[] years = new Integer[endYear - startYear + 1];
                for (int i = 0; i < years.length; i++) years[i] = startYear + i;
                Integer selected = (Integer) JOptionPane.showInputDialog(dialog, "Select Year", "Year",
                        JOptionPane.PLAIN_MESSAGE, null, years, calendar.get(Calendar.YEAR));
                if (selected != null) {
                    calendar.set(Calendar.YEAR, selected);
                    yearLabel.setText(String.valueOf(selected));
                    updateCalendarDays(daysPanel, calendar);
                }
            }
        });
        headerPanel.add(prevMonth);
        headerPanel.add(monthLabel);
        headerPanel.add(yearLabel);
        headerPanel.add(nextMonth);
        updateCalendarDays(daysPanel, calendar);
        prevMonth.addActionListener(e -> {
            calendar.add(Calendar.MONTH, -1);
            monthLabel.setText(new SimpleDateFormat("MMMM").format(calendar.getTime()));
            yearLabel.setText(String.valueOf(calendar.get(Calendar.YEAR)));
            updateCalendarDays(daysPanel, calendar);
        });
        nextMonth.addActionListener(e -> {
            calendar.add(Calendar.MONTH, 1);
            monthLabel.setText(new SimpleDateFormat("MMMM").format(calendar.getTime()));
            yearLabel.setText(String.valueOf(calendar.get(Calendar.YEAR)));
            updateCalendarDays(daysPanel, calendar);
        });
        calendarPanel.add(headerPanel, BorderLayout.NORTH);
        calendarPanel.add(daysPanel, BorderLayout.CENTER);
        dialog.add(calendarPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    private String getMonthYearString(Calendar calendar) {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy");
        return monthFormat.format(calendar.getTime());
    }
    
    private void updateCalendarDays(JPanel daysPanel, Calendar calendar) {
        daysPanel.removeAll();
        
        // Add day names
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String dayName : dayNames) {
            JLabel label = new JLabel(dayName, SwingConstants.CENTER);
            daysPanel.add(label);
        }
        
        // Get first day of month and total days
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDay = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Add empty labels for days before first day of month
        for (int i = 0; i < firstDay; i++) {
            daysPanel.add(new JLabel());
        }
        
        // Add buttons for each day
        ButtonGroup buttonGroup = new ButtonGroup();
        for (int i = 1; i <= maxDays; i++) {
            JToggleButton dayButton = new JToggleButton(String.valueOf(i));
            buttonGroup.add(dayButton);
            
            // Check if this date should be enabled
            Calendar tempCal = (Calendar) calendar.clone();
            tempCal.set(Calendar.DAY_OF_MONTH, i);
            boolean isEnabled = minimumDate == null || !tempCal.getTime().before(minimumDate);
            dayButton.setEnabled(isEnabled);
            
            if (i == calendar.get(Calendar.DAY_OF_MONTH) && 
                calendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH) &&
                calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
                dayButton.setSelected(true);
            }
            
            dayButton.addActionListener(e -> {
                calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayButton.getText()));
                Date newDate = calendar.getTime();
                
                // Only update if the date is valid
                if (minimumDate == null || !newDate.before(minimumDate)) {
                    selectedDate = newDate;
                    dateField.setText(dateFormat.format(selectedDate));
                    Window window = SwingUtilities.getWindowAncestor(dayButton);
                    if (window != null) {
                        window.dispose();
                    }
                }
            });
            
            daysPanel.add(dayButton);
        }
        
        daysPanel.revalidate();
        daysPanel.repaint();
    }
    
    public Date getDate() {
        return selectedDate;
    }
    
    public void setDate(Date date) {
        selectedDate = date;
        dateField.setText(dateFormat.format(date));
    }
    
    public String getFormattedDate() {
        return dateField.getText();
    }
} 