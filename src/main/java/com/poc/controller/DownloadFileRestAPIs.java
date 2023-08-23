package com.poc.controller;

import com.poc.services.ExcelFileServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "customers")
public class DownloadFileRestAPIs {

    @Autowired
    private ExcelFileServices fileServices;

    @GetMapping("/download/excel")
    public ResponseEntity<InputStreamResource> downloadFile() {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=customers.xlsx");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(fileServices.loadFile()));
    }

}