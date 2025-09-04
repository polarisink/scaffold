package com.github.polarisink.download;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AcrobatPdfToWordConverter {
    public static void convertPdfToDocx(Path pdfPath, Path docxPath) throws IOException, InterruptedException {
        Path jsTemplate = Path.of("C:\\Users\\lqsgo\\IdeaProjects\\scaffold\\scaffold-biz\\scaffold-biz-download\\src\\main\\resources\\ex.js");
        String script = Files.readString(jsTemplate)
                .replace("{INPUT_PDF}", pdfPath.toString().replace("\\", "/"))
                .replace("{OUTPUT_DOCX}", docxPath.toString().replace("\\", "/"));
        Path scriptPath = Files.createTempFile("acrobat-", ".js");
        Files.writeString(scriptPath, script);

        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "C:\\Users\\lqsgo\\IdeaProjects\\scaffold\\scaffold-biz\\scaffold-biz-download\\src\\main\\resources\\convert-pdf.bat");
        pb.directory(new File("."));
        Process process = pb.start();
        process.waitFor();
    }

    public static void main(String[] args) {
        try {
            Path pdfPath = Path.of("C:\\Users\\lqsgo\\Desktop\\111.pdf");
            Path docxPath = Path.of("C:\\Users\\lqsgo\\Desktop\\res.docx");
            convertPdfToDocx(pdfPath, docxPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
