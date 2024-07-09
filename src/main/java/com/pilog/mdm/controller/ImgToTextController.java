package com.pilog.mdm.controller;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping(value = "/${api-version}")
public class ImgToTextController {
    @PostMapping("/imgTotext")
    public ResponseEntity<String> convertImageToText(@RequestParam("image") MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            return new ResponseEntity<>("Please upload an image file.", HttpStatus.BAD_REQUEST);
        }
        try {
            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + imageFile.getOriginalFilename());
            imageFile.transferTo(convFile);
            ITesseract tesseract = new Tesseract();
            File tempTessdataDir = new File(System.getProperty("java.io.tmpdir") + "/tessdata");
            if (!tempTessdataDir.exists()) {
                tempTessdataDir.mkdirs();
            }
            ClassPathResource tessdataResource = new ClassPathResource("tessdata/eng.traineddata");
            Files.copy(tessdataResource.getInputStream(),
                    new File(tempTessdataDir, "eng.traineddata").toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            tesseract.setDatapath(tempTessdataDir.getAbsolutePath());

            String text = tesseract.doOCR(convFile);
            convFile.delete();

            return new ResponseEntity<>(text, HttpStatus.OK);
        } catch (IOException | TesseractException e) {
            return new ResponseEntity<>("Failed to process the image: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
