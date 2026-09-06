package com.learnvault.learnvaultbackend.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfService {

    public String extractText(MultipartFile file) throws IOException {

        byte[] pdfBytes = file.getBytes();

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    public List<TextSegment> extractAndChunk(MultipartFile file) throws IOException {
        String text = extractText(file);
        Document document = Document.from(text);

        var splitter = DocumentSplitters.recursive(
                500,
                50
        );

        return splitter.split(document);
    }

    public List<PageChunk> extractAndChunkWithPages(MultipartFile file) throws IOException {

        List<PageChunk> result = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(file.getBytes())) {

            PDFTextStripper stripper = new PDFTextStripper();

            for (int page = 1; page <= document.getNumberOfPages(); page++) {

                stripper.setStartPage(page);
                stripper.setEndPage(page);

                String pageText = stripper.getText(document);

                if (pageText == null || pageText.isBlank()) {
                    continue;
                }

                Document langchainDocument = Document.from(pageText);

                var splitter = DocumentSplitters.recursive(
                        500,
                        50
                );

                List<TextSegment> chunks = splitter.split(langchainDocument);

                for (TextSegment chunk: chunks) {
                    result.add(new PageChunk(chunk.text(), page));
                }
            }
        }

        return result;
    }
}
