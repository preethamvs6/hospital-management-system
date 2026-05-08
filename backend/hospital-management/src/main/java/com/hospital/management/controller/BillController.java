package com.hospital.management.controller;

import com.hospital.management.dto.BillDTO;
import com.hospital.management.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    @Autowired
    private BillService billService;

    @GetMapping
    public ResponseEntity<List<BillDTO>> getAllBills() {
        return ResponseEntity.ok(billService.getAllBills());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillDTO> getBillById(@PathVariable Long id) {
        return ResponseEntity.ok(billService.getBillById(id));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<BillDTO>> getBillsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(billService.getBillsByPatientId(patientId));
    }

    @PostMapping
    public ResponseEntity<BillDTO> createBill(@RequestBody BillDTO dto) {
        return ResponseEntity.ok(billService.createBill(dto));
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<BillDTO> updatePayment(@PathVariable Long id,
                                                  @RequestBody Map<String, Object> payload) {
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());
        String method = payload.get("paymentMethod") != null ? payload.get("paymentMethod").toString() : null;
        return ResponseEntity.ok(billService.updatePayment(id, amount, method));
    }

    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenue() {
        Map<String, Object> revenue = Map.of(
                "totalRevenue", billService.getTotalRevenue(),
                "totalPaid", billService.getTotalPaidAmount()
        );
        return ResponseEntity.ok(revenue);
    }
}
