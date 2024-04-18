package com.example.exam.upload.service;

import com.example.exam.exceptions.FailedImportFileException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import static java.io.File.createTempFile;

@Service
@RequiredArgsConstructor
public class ImportFileService {

    private final CsvFileImportService csvFileImportService;

    public void importFile(MultipartFile multipartFile, String taskId) throws IOException {
        if (!isCsvFile(multipartFile)) {
            throw new FailedImportFileException("Unsupported file type: " + multipartFile.getContentType());
        }
        File file = multipartFileToFileConverter(multipartFile);
        csvFileImportService.importCsv(file, taskId);
    }

    private File multipartFileToFileConverter(MultipartFile multipartFile) throws IOException {
        File file = createTempFile("temp", null);
        multipartFile.transferTo(file);
        file.deleteOnExit();
        return file;
    }

    private boolean isCsvFile(MultipartFile file) {
        return file.getContentType() != null && (file.getContentType().equals(MediaType.TEXT_PLAIN_VALUE) || file.getContentType().equals("text/csv"));
    }

}
