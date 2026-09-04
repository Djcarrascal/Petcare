package com.petcare.util;

import java.time.LocalDateTime;

// Prints console traces that look like HTTP calls, just to simulate request logging
public class HttpTraceLogger {

    private HttpTraceLogger() {
        // Utility class, no instances needed
    }

    public static void trace(String httpMethod, String path, int statusCode) {
        System.out.println("[" + LocalDateTime.now() + "] " + httpMethod + " " + path + " -> " + statusCode);
    }

    // Separate method for technical error details, kept apart from the friendly message shown to the user
    public static void error(String path, Exception e) {
        System.out.println("[" + LocalDateTime.now() + "] ERROR on " + path + ": " + e.getMessage());
        e.printStackTrace();
    }
}