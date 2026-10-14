package com.campusbridge.placement.controller.recruiter;

import com.campusbridge.placement.dto.response.ApiResponse;
import com.campusbridge.placement.dto.response.DriveResponse;
import com.campusbridge.placement.entity.RecruitmentStage;
import com.campusbridge.placement.repository.RecruitmentStageRepository;
import com.campusbridge.placement.service.recruiter.DriveService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/placement-drives")
public class DriveController {

    private final DriveService driveService;
    private final RecruitmentStageRepository stageRepository;

    public DriveController(DriveService driveService, RecruitmentStageRepository stageRepository) {
        this.driveService = driveService;
        this.stageRepository = stageRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DriveResponse>>> getDrives(
            @RequestParam(required = false) Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(driveService.getActiveDrivesForStudent(studentId)));
    }

    @GetMapping("/{driveId}")
    public ResponseEntity<ApiResponse<DriveResponse>> getDriveById(
            @PathVariable Long driveId,
            @RequestParam(required = false) Long studentId) {
        return ResponseEntity.ok(ApiResponse.ok(driveService.getDriveById(driveId, studentId)));
    }

    @GetMapping("/{driveId}/stages")
    public ResponseEntity<ApiResponse<List<RecruitmentStage>>> getStages(@PathVariable Long driveId) {
        return ResponseEntity.ok(ApiResponse.ok(stageRepository.findByDriveIdOrderByStageOrderAsc(driveId)));
    }
}
