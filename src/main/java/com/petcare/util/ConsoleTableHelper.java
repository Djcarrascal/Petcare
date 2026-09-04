package com.petcare.util;

// Builds simple aligned text tables to show inside JOptionPane, since it only renders plain text
public class ConsoleTableHelper {

    private ConsoleTableHelper() {
        // Utility class, no instances needed
    }

    // headers and each row must have the same number of columns
    public static String buildTable(String[] headers, String[][] rows) {
        int columnCount = headers.length;
        int[] columnWidths = new int[columnCount];

        // Starting each column width based on the header length
        for (int i = 0; i < columnCount; i++) {
            columnWidths[i] = headers[i].length();
        }

        // Growing each column width if any row's value is longer than the current header
        for (String[] row : rows) {
            for (int i = 0; i < columnCount; i++) {
                columnWidths[i] = Math.max(columnWidths[i], row[i].length());
            }
        }

        StringBuilder table = new StringBuilder();
        table.append(buildRow(headers, columnWidths));
        table.append(buildSeparator(columnWidths));

        for (String[] row : rows) {
            table.append(buildRow(row, columnWidths));
        }

        return table.toString();
    }

    private static String buildRow(String[] values, int[] columnWidths) {
        StringBuilder row = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            row.append(String.format("%-" + columnWidths[i] + "s  ", values[i]));
        }
        row.append("\n");
        return row.toString();
    }

    private static String buildSeparator(int[] columnWidths) {
        StringBuilder separator = new StringBuilder();
        for (int width : columnWidths) {
            separator.append("-".repeat(width)).append("  ");
        }
        separator.append("\n");
        return separator.toString();
    }
}