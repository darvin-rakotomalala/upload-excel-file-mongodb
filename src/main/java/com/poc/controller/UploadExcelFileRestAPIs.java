package com.poc.controller;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.poc.message.Message;
import com.poc.message.Response;
import com.poc.services.ExcelFileServices;
import com.poc.utils.ExcelUtils;

@RestController
@RequestMapping(path = "customers")
public class UploadExcelFileRestAPIs {

    @Autowired
    private ExcelFileServices fileServices;

    @PostMapping("/upload/excel-single")
    public Response uploadSingleExcelFile(@RequestParam("excelFile") MultipartFile uploadFile) {

        Response response = new Response();

        if (StringUtils.isEmpty(uploadFile.getOriginalFilename())) {
            response.addMessage(new Message(uploadFile.getOriginalFilename(),
                    "No selected file to upload! Please do the checking", "fail"));

            return response;
        }

        if (!ExcelUtils.isExcelFile(uploadFile)) {
            response.addMessage(
                    new Message(uploadFile.getOriginalFilename(), "Error: this is not a Excel file!", "fail"));
            return response;
        }

        try {
            // save file data to MongoDB
            fileServices.store(uploadFile);
            response.addMessage(new Message(uploadFile.getOriginalFilename(), "Upload File Successfully!", "ok"));
        } catch (Exception e) {
            response.addMessage(new Message(uploadFile.getOriginalFilename(), e.getMessage(), "fail"));
        }

        return response;
    }

    @PostMapping("/upload/excel-multiple")
    public Response uploadFileMulti(@RequestParam("excelFiles") MultipartFile[] uploadFiles) {

        Response response = new Response();

        MultipartFile[] readyUploadedFiles = Arrays.stream(uploadFiles)
                .filter(x -> !StringUtils.isEmpty(x.getOriginalFilename())).toArray(MultipartFile[]::new);

        /*
         * Checking whether having at least one file had been selected for uploading
         */
        if (readyUploadedFiles.length == 0) {
            response.addMessage(new Message("", "No selected file to upload!", "fail"));
            return response;
        }

        /*
         * Checking uploaded files are Excel files or NOT
         */
        String notExcelFiles = Arrays.stream(uploadFiles).filter(x -> !ExcelUtils.isExcelFile(x))
                .map(MultipartFile::getOriginalFilename).collect(Collectors.joining(" , "));

        if (!StringUtils.isEmpty(notExcelFiles)) {
            response.addMessage(new Message(notExcelFiles, "Not Excel Files", "fail"));
            return response;
        }

        for (MultipartFile file : uploadFiles) {
            try {
                fileServices.store(file);
                response.addMessage(new Message(file.getOriginalFilename(), "Upload Successfully!", "ok"));
            } catch (Exception e) {
                response.addMessage(new Message(file.getOriginalFilename(), e.getMessage(), "fail"));
            }
        }

        return response;
    }
}