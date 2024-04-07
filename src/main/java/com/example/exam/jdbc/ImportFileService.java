package com.example.exam.jdbc;

import com.example.exam.exceptions.FailedImportFileException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static java.io.File.createTempFile;

@Service
@RequiredArgsConstructor
public class ImportFileService {

    private final CsvFileImportService csvFileImportService;

    public void importFile(MultipartFile multipartFile, String taskId) {
        try {

            File file;
            file = multipartFileToFileConverter(multipartFile);
            csvFileImportService.importCsv(file, taskId);
        } catch (RuntimeException e) {
            throw new RuntimeException();
        }
    }

    private File multipartFileToFileConverter(MultipartFile multipartFile){
        try {
            File file = createTempFile("temp", null);
            multipartFile.transferTo(file);
            file.deleteOnExit();
            return file;
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException();
        }
    }
}