package com.smartclinic.controller;

import com.smartclinic.common.ApiResponse;
import com.smartclinic.common.PagedResponse;
import com.smartclinic.dto.lab.LabReportCreateDto;
import com.smartclinic.dto.lab.LabReportResponseDto;
import com.smartclinic.service.LabReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/lab-reports")
@RequiredArgsConstructor
@Tag(name = "Lab Reports", description = "Endpoints for diagnostic test reports and document uploads")
public class LabReportController {

    private final LabReportService labReportService;

    @GetMapping
    @Operation(summary = "List diagnostic lab reports")
    public ResponseEntity<ApiResponse<PagedResponse<LabReportResponseDto>>> getLabReports(
            @RequestParam(required = false) Long patientId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(labReportService.getLabReports(patientId, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lab report details by ID")
    public ResponseEntity<ApiResponse<LabReportResponseDto>> getLabReportById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(labReportService.getLabReportById(id)));
    }

    @PostMapping
    @Operation(summary = "Create a diagnostic lab report entry")
    public ResponseEntity<ApiResponse<LabReportResponseDto>> createLabReport(@Valid @RequestBody LabReportCreateDto dto) {
        LabReportResponseDto created = labReportService.createLabReport(dto);
        return ResponseEntity.ok(ApiResponse.ok("Lab report created successfully", created));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload lab report document (PDF, PNG, JPG)")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadLabFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileUrl = labReportService.uploadLabFile(file);
        return ResponseEntity.ok(ApiResponse.ok("File uploaded successfully", Map.of("fileUrl", fileUrl)));
    }
}
