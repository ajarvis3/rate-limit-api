package com.ratelimit.billing.controller;

import com.ratelimit.billing.dto.InvoiceResponseDTO;
import com.ratelimit.billing.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

	private final InvoiceService service;

	public InvoiceController(InvoiceService service) {
		this.service = service;
	}

	@GetMapping("/{id}")
	public ResponseEntity<InvoiceResponseDTO> getById(@PathVariable("id") Long id) {
		// service.getById will throw InvoiceNotFoundException (mapped to 404) when missing
		return ResponseEntity.ok(service.getById(id));
	}

	@GetMapping("/recent")
	public ResponseEntity<InvoiceResponseDTO> getMostRecent(@RequestParam("userId") String userId) {
		// service throws InvoiceNotFoundException if none found
		return ResponseEntity.ok(service.getMostRecentByUserId(userId));
	}

	@GetMapping
	public ResponseEntity<List<InvoiceResponseDTO>> getForUserBetween(
			@RequestParam("userId") String userId,
			@RequestParam("start") long start,
			@RequestParam("end") long end) {
		List<InvoiceResponseDTO> list = service.getInvoicesForUserBetween(userId, start, end);
		return ResponseEntity.ok(list);
	}
}

