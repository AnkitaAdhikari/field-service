package com.example.field_service.field_service.controller;

import com.example.field_service.field_service.dto.InvoiceResponse;
import com.example.field_service.field_service.service.InvoiceService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/{id}")
    public InvoiceResponse getById(@PathVariable Long id) {
        return invoiceService.getById(id);
    }

    @GetMapping("/me/as-customer")
    public List<InvoiceResponse> getMyInvoicesAsCustomer() {
        return invoiceService.getMyInvoicesAsCustomer();
    }

    @GetMapping("/me/as-technician")
    public List<InvoiceResponse> getMyInvoicesAsTechnician() {
        return invoiceService.getMyInvoicesAsTechnician();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        byte[] pdf = invoiceService.generatePdf(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + id + ".pdf")
                .body(pdf);
    }
}