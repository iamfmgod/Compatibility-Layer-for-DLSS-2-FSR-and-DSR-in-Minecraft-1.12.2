package com.fattie.compatlayer.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PerformanceLogger {
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private static BufferedWriter writer;
    private static File logFile;
    private static int frameCount = 0;
    private static double totalFrameTime = 0;

    public static void init() {
        if (writer != null) return;

        try {
            Path logDir = new File("compatlayer_logs").toPath(); // Java 8 compatible
            Files.createDirectories(logDir);

            String timestamp = LocalDateTime.now().format(timestampFormat);
            logFile = logDir.resolve("perf_" + timestamp + ".csv").toFile();
            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write("Frame,FrameTime(ms),Width,Height\n");
        } catch (IOException e) {
            System.err.println("[CompatLayer] Failed to initialize performance logger: " + e.getMessage());
        }
    }

    public static void log(float frameTimeMs, int width, int height) {
        if (writer == null) return;

        try {
            frameCount++;
            totalFrameTime += frameTimeMs;
            writer.write(frameCount + "," + df.format(frameTimeMs) + "," + width + "," + height + "\n");
        } catch (IOException e) {
            System.err.println("[CompatLayer] Failed to log frame: " + e.getMessage());
        }
    }

    public static void shutdown() {
        if (writer == null) return;

        try {
            writer.flush();
            writer.close();

            double avgFrameTime = totalFrameTime / frameCount;
            double avgFPS = 1000.0 / avgFrameTime;

            File summary = new File(logFile.getParent(), "summary.txt");
            try (BufferedWriter summaryWriter = new BufferedWriter(new FileWriter(summary, true))) {
                summaryWriter.write("Session: " + logFile.getName() + "\n");
                summaryWriter.write("Frames: " + frameCount + "\n");
                summaryWriter.write("Avg Frame Time: " + df.format(avgFrameTime) + " ms\n");
                summaryWriter.write("Avg FPS: " + df.format(avgFPS) + "\n\n");
            }

            System.out.println("[CompatLayer] Performance log saved to: " + logFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("[CompatLayer] Failed to finalize performance log: " + e.getMessage());
        }
    }
}