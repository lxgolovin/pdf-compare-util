package com.lxgolovin.sandbox.file.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@UtilityClass
public final class TextToFile {

    public static void write(@NonNull Path path, @NonNull String text) {
        TextToFile.writeToFile(path, text);
    }

    public static String read(@NonNull Path path) {
        return TextToFile.readFromFile(path);
    }

    private static void writeToFile(Path path, String text) {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            bufferedWriter.write(text);
        } catch (IOException e) {
            log.error("Cannot write to file {}", path, e);
        }
    }

    private static String readFromFile(Path path) {
        try {
            return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Cannot read file by specified path {}", path, e);
            return "";
        }
    }
}
