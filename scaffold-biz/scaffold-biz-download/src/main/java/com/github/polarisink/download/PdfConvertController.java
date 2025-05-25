package com.github.polarisink.download;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@RestController
public class PdfConvertController {
    @Autowired
    DocxToExcelConverter excelConverter;

    @PostMapping("/convert")
    public ResponseEntity<?> convert(@RequestParam MultipartFile file) throws Exception {
        Path tempDir = Files.createTempDirectory("pdf-convert");
        Path pdf = tempDir.resolve("input.pdf");
        Path docx = tempDir.resolve("output.docx");
        Path xlsx = tempDir.resolve("output.xlsx");

        file.transferTo(pdf.toFile());
        AcrobatPdfToWordConverter.convertPdfToDocx(pdf, docx);

        return ResponseEntity.ok(Map.of("excelPath", xlsx.toAbsolutePath().toString()));
    }
}
