package com.poc.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.poc.domain.Customer;
import com.poc.repository.CustomerRepository;
import com.poc.utils.ExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExcelFileServices {

    @Autowired
    private CustomerRepository customerRepository;

    // Store File Data to Database
    public void store(MultipartFile file) {
        try {
            List<Customer> lstCustomers = ExcelUtils.parseExcelFile(file.getInputStream());
            // Save Customers to DataBase
            lstCustomers.forEach(customer -> {
                customer.setId(UUID.randomUUID());
            });
            customerRepository.saveAll(lstCustomers);
        } catch (IOException e) {
            throw new RuntimeException("FAIL! -> message = " + e.getMessage());
        }
    }

    // Load Data to Excel File
    public ByteArrayInputStream loadFile() {
        List<Customer> customers = (List<Customer>) customerRepository.findAll();

        try {
            return ExcelUtils.customersToExcel(customers);
        } catch (IOException e) {

        }
        return null;
    }
}