package com.lxgolovin.sandbox.file.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
@UtilityClass
public final class TextToFile {

    public static void write(Path path, String text) {
        Optional.ofNullable(path).orElseThrow(() -> new IllegalArgumentException("Path to file should not be null"));
        Optional.ofNullable(text).map(t-> writeToFile(path, t)).orElseThrow(() -> new IllegalArgumentException("Passed text cannot be null"));
    }

    public static String read(Path path) {
        return Optional.ofNullable(path)
                .map(TextToFile::readFromFile)
                .orElseThrow(() -> new IllegalArgumentException("Path to file should not be null"));
    }

    private static String writeToFile(Path path, String text) {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            bufferedWriter.write(text);
        } catch (IOException e) {
            log.error("Cannot write to file {}", path, e);
        }
        return text;
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
