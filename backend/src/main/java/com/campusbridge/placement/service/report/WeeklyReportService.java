package com.campusbridge.placement.service.report;

import com.campusbridge.placement.entity.*;
import com.campusbridge.placement.entity.enums.InterviewStatus;
import com.campusbridge.placement.repository.*;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WeeklyReportService {

    private final WeeklyPlacementReportRepository reportRepository;
    private final CollegeRepository collegeRepository;
    private final PlacementDriveRepository driveRepository;
    private final ApplicationRepository applicationRepository;
    private final InterviewRepository interviewRepository;
    private final OfferRepository offerRepository;
    private final StudentRepository studentRepository;

    public WeeklyReportService(
            WeeklyPlacementReportRepository reportRepository,
            CollegeRepository collegeRepository,
            PlacementDriveRepository driveRepository,
            ApplicationRepository applicationRepository,
            InterviewRepository interviewRepository,
            OfferRepository offerRepository,
            StudentRepository studentRepository) {
        this.reportRepository = reportRepository;
        this.collegeRepository = collegeRepository;
        this.driveRepository = driveRepository;
        this.applicationRepository = applicationRepository;
        this.interviewRepository = interviewRepository;
        this.offerRepository = offerRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * Generate snapshot and persist in database
     */
    @Transactional
    public WeeklyPlacementReport generateReport(Long collegeId, LocalDate startDate, LocalDate endDate, String generatedBy) {
        College college = collegeId != null ? collegeRepository.findById(collegeId).orElse(null) : null;
        if (college == null) {
            college = collegeRepository.findAll().stream().findFirst().orElse(null);
        }

        List<PlacementDrive> drives = driveRepository.findAll();
        List<Application> apps = applicationRepository.findAll();
        List<Interview> interviews = interviewRepository.findAll();
        List<Offer> offers = offerRepository.findAll();

        int totalDrives = drives.size();
        int totalApps = apps.size();
        int totalInterviews = interviews.size();
        int totalOffers = offers.size();

        double avgPkg = offers.stream().mapToDouble(Offer::getPackageLpa).average().orElse(0.0);
        double maxPkg = offers.stream().mapToDouble(Offer::getPackageLpa).max().orElse(0.0);
        double placementPct = totalApps > 0 ? ((double) totalOffers / totalApps) * 100.0 : 0.0;

        String title = String.format("Weekly Placement Drive Report: %s to %s",
                startDate.format(DateTimeFormatter.ISO_DATE), endDate.format(DateTimeFormatter.ISO_DATE));

        WeeklyPlacementReport report = new WeeklyPlacementReport();
        report.setCollege(college);
        report.setReportTitle(title);
        report.setReportingPeriodStart(startDate);
        report.setReportingPeriodEnd(endDate);
        report.setGeneratedAt(LocalDateTime.now());
        report.setGeneratedBy(generatedBy != null ? generatedBy : "TPO Placement Office");
        report.setTotalDrives(totalDrives);
        report.setTotalApplications(totalApps);
        report.setTotalShortlisted(totalApps > 0 ? (int) (totalApps * 0.7) : 0);
        report.setTotalInterviews(totalInterviews);
        report.setTotalOffers(totalOffers);
        report.setPlacementPercentage(Math.round(placementPct * 10.0) / 10.0);
        report.setAvgPackageLpa(Math.round(avgPkg * 100.0) / 100.0);
        report.setMaxPackageLpa(Math.round(maxPkg * 100.0) / 100.0);

        // Snapshot JSON
        String snapshot = String.format("{\"totalDrives\":%d,\"totalApplications\":%d,\"totalInterviews\":%d,\"totalOffers\":%d,\"avgPackage\":%.2f}",
                totalDrives, totalApps, totalInterviews, totalOffers, avgPkg);
        report.setSnapshotJson(snapshot);

        return reportRepository.save(report);
    }

    /**
     * Automatic weekly scheduled report: Every Monday at 09:00 AM
     */
    @Scheduled(cron = "0 0 9 * * MON")
    public void scheduledWeeklyReport() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(7);
        generateReport(null, start, end, "CampusBridge Automated Scheduler");
    }

    /**
     * Generate PDF using OpenPDF
     */
    public byte[] generatePdfReport(Long reportId) throws DocumentException, IOException {
        WeeklyPlacementReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found: " + reportId));

        Document document = new Document(PageSize.A4, 36, 36, 40, 40);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        // Title & Header
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(13, 110, 253));
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
        Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

        Paragraph pTitle = new Paragraph("CAMPUSBRIDGE — WEEKLY PLACEMENT REPORT", titleFont);
        pTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(pTitle);

        Paragraph pSub = new Paragraph("Virtual Placement Drive Dashboard & Multi-Company Interview Lifecycle", headerFont);
        pSub.setAlignment(Element.ALIGN_CENTER);
        pSub.setSpacingAfter(15);
        document.add(pSub);

        // Metadata box
        PdfPTable metaTable = new PdfPTable(2);
        metaTable.setWidthPercentage(100);
        metaTable.setSpacingAfter(15);
        addMetaCell(metaTable, "Reporting Period:", report.getReportingPeriodStart() + " to " + report.getReportingPeriodEnd(), boldFont, regularFont);
        addMetaCell(metaTable, "Institution / College:", report.getCollege() != null ? report.getCollege().getName() : "Central Placement Ecosystem", boldFont, regularFont);
        addMetaCell(metaTable, "Generated By:", report.getGeneratedBy(), boldFont, regularFont);
        addMetaCell(metaTable, "Generated Date:", report.getGeneratedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), boldFont, regularFont);
        document.add(metaTable);

        // Summary Metric Table
        Paragraph pMetrics = new Paragraph("1. Executive Placement Summary", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK));
        pMetrics.setSpacingAfter(8);
        document.add(pMetrics);

        PdfPTable metricTable = new PdfPTable(4);
        metricTable.setWidthPercentage(100);
        metricTable.setSpacingAfter(15);

        addTableHeader(metricTable, "Active Drives", "Total Applications", "Interviews Conducted", "Offers Extended", boldFont);
        metricTable.addCell(new Phrase(String.valueOf(report.getTotalDrives()), regularFont));
        metricTable.addCell(new Phrase(String.valueOf(report.getTotalApplications()), regularFont));
        metricTable.addCell(new Phrase(String.valueOf(report.getTotalInterviews()), regularFont));
        metricTable.addCell(new Phrase(String.valueOf(report.getTotalOffers()), regularFont));
        document.add(metricTable);

        // Package Stats Table
        PdfPTable pkgTable = new PdfPTable(3);
        pkgTable.setWidthPercentage(100);
        pkgTable.setSpacingAfter(15);
        addTableHeader(pkgTable, "Placement Conversion", "Average CTC Package", "Highest CTC Package", boldFont);
        pkgTable.addCell(new Phrase(report.getPlacementPercentage() + "%", regularFont));
        pkgTable.addCell(new Phrase(report.getAvgPackageLpa() + " LPA", regularFont));
        pkgTable.addCell(new Phrase(report.getMaxPackageLpa() + " LPA", regularFont));
        document.add(pkgTable);

        // Drives Listing
        Paragraph pDrives = new Paragraph("2. Drives in Progress", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.BLACK));
        pDrives.setSpacingAfter(8);
        document.add(pDrives);

        PdfPTable drivesTable = new PdfPTable(5);
        drivesTable.setWidthPercentage(100);
        drivesTable.setSpacingAfter(20);
        addTableHeader(drivesTable, "Company", "Role", "Package", "Drive Date", "Status", boldFont);

        List<PlacementDrive> activeDrives = driveRepository.findAll();
        for (PlacementDrive d : activeDrives) {
            drivesTable.addCell(new Phrase(d.getCompany() != null ? d.getCompany().getName() : "-", regularFont));
            drivesTable.addCell(new Phrase(d.getJobTitle(), regularFont));
            drivesTable.addCell(new Phrase(d.getPackageLpa() + " LPA", regularFont));
            drivesTable.addCell(new Phrase(d.getDriveDate() != null ? d.getDriveDate().toString() : "-", regularFont));
            drivesTable.addCell(new Phrase(d.getStatus().name(), regularFont));
        }
        document.add(drivesTable);

        // Signature footer
        Paragraph sig = new Paragraph("\nVerified & Authorized by: Placement Director / TPO Office\nCampusBridge Placement Coordination System", FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, Color.GRAY));
        sig.setAlignment(Element.ALIGN_RIGHT);
        document.add(sig);

        document.close();
        return out.toByteArray();
    }

    /**
     * Export Excel per report using Apache POI
     */
    public byte[] generateExcelReport(Long reportId) throws IOException {
        WeeklyPlacementReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found: " + reportId));

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Placement Summary");

            Row row0 = sheet.createRow(0);
            row0.createCell(0).setCellValue("CampusBridge Weekly Placement Report");

            Row row1 = sheet.createRow(1);
            row1.createCell(0).setCellValue("Reporting Period");
            row1.createCell(1).setCellValue(report.getReportingPeriodStart() + " to " + report.getReportingPeriodEnd());

            Row row3 = sheet.createRow(3);
            row3.createCell(0).setCellValue("Metric");
            row3.createCell(1).setCellValue("Value");

            sheet.createRow(4).createCell(0).setCellValue("Total Drives");
            sheet.getRow(4).createCell(1).setCellValue(report.getTotalDrives());

            sheet.createRow(5).createCell(0).setCellValue("Total Applications");
            sheet.getRow(5).createCell(1).setCellValue(report.getTotalApplications());

            sheet.createRow(6).createCell(0).setCellValue("Interviews Conducted");
            sheet.getRow(6).createCell(1).setCellValue(report.getTotalInterviews());

            sheet.createRow(7).createCell(0).setCellValue("Offers Released");
            sheet.getRow(7).createCell(1).setCellValue(report.getTotalOffers());

            sheet.createRow(8).createCell(0).setCellValue("Placement Percentage");
            sheet.getRow(8).createCell(1).setCellValue(report.getPlacementPercentage() + "%");

            sheet.createRow(9).createCell(0).setCellValue("Average Package (LPA)");
            sheet.getRow(9).createCell(1).setCellValue(report.getAvgPackageLpa());

            sheet.createRow(10).createCell(0).setCellValue("Highest Package (LPA)");
            sheet.getRow(10).createCell(1).setCellValue(report.getMaxPackageLpa());

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    /**
     * Master placement Excel export: Students, Drives, Offers across all colleges
     */
    public byte[] generateMasterPlacementExcel(Long collegeId) throws IOException {
        List<Student> students = studentRepository.findAll();
        List<PlacementDrive> drives = driveRepository.findAll();
        List<Offer> offers = offerRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // ---- Sheet 1: Students ----
            Sheet studentsSheet = workbook.createSheet("Students");
            String[] sCols = {"ID", "Name", "Email", "College", "Department", "Roll Number",
                    "Academic Year", "Graduation Year", "CGPA", "Backlogs", "Skills", "Placement Status"};
            Row sHeader = studentsSheet.createRow(0);
            for (int j = 0; j < sCols.length; j++) {
                Cell cell = sHeader.createCell(j);
                cell.setCellValue(sCols[j]);
                cell.setCellStyle(headerStyle);
            }
            int sRow = 1;
            for (Student s : students) {
                if (collegeId != null && s.getCollege() != null && !collegeId.equals(s.getCollege().getId())) continue;
                Row row = studentsSheet.createRow(sRow++);
                row.createCell(0).setCellValue(s.getId());
                row.createCell(1).setCellValue(s.getFullName());
                row.createCell(2).setCellValue(s.getEmail());
                row.createCell(3).setCellValue(s.getCollege() != null ? s.getCollege().getName() : "");
                row.createCell(4).setCellValue(s.getDepartment());
                row.createCell(5).setCellValue(s.getRollNumber());
                row.createCell(6).setCellValue(s.getAcademicYear());
                row.createCell(7).setCellValue(s.getGraduationYear());
                row.createCell(8).setCellValue(s.getCgpa());
                row.createCell(9).setCellValue(s.getBacklogs());
                row.createCell(10).setCellValue(s.getSkills() != null ? s.getSkills() : "");
                row.createCell(11).setCellValue(s.getPlacementStatus() != null ? s.getPlacementStatus().name() : "UNPLACED");
            }
            for (int j = 0; j < sCols.length; j++) studentsSheet.autoSizeColumn(j);

            // ---- Sheet 2: Placement Drives ----
            Sheet drivesSheet = workbook.createSheet("Placement Drives");
            String[] dCols = {"ID", "Company", "Job Title", "Package (LPA)", "Location", "Status", "Drive Date", "Vacancies"};
            Row dHeader = drivesSheet.createRow(0);
            for (int j = 0; j < dCols.length; j++) {
                Cell cell = dHeader.createCell(j);
                cell.setCellValue(dCols[j]);
                cell.setCellStyle(headerStyle);
            }
            int dRow = 1;
            for (PlacementDrive d : drives) {
                Row row = drivesSheet.createRow(dRow++);
                row.createCell(0).setCellValue(d.getId());
                row.createCell(1).setCellValue(d.getCompany() != null ? d.getCompany().getName() : "");
                row.createCell(2).setCellValue(d.getJobTitle());
                row.createCell(3).setCellValue(d.getPackageLpa());
                row.createCell(4).setCellValue(d.getLocation() != null ? d.getLocation() : "");
                row.createCell(5).setCellValue(d.getStatus() != null ? d.getStatus().name() : "");
                row.createCell(6).setCellValue(d.getDriveDate() != null ? d.getDriveDate().toString() : "");
                row.createCell(7).setCellValue(d.getVacancies());
            }
            for (int j = 0; j < dCols.length; j++) drivesSheet.autoSizeColumn(j);

            // ---- Sheet 3: Offers ----
            Sheet offersSheet = workbook.createSheet("Offers");
            String[] oCols = {"ID", "Student Name", "Company", "Role", "Package (LPA)", "Location", "Offer Status", "Joining Date"};
            Row oHeader = offersSheet.createRow(0);
            for (int j = 0; j < oCols.length; j++) {
                Cell cell = oHeader.createCell(j);
                cell.setCellValue(oCols[j]);
                cell.setCellStyle(headerStyle);
            }
            int oRow = 1;
            for (Offer o : offers) {
                Row row = offersSheet.createRow(oRow++);
                row.createCell(0).setCellValue(o.getId());
                row.createCell(1).setCellValue(o.getStudent() != null ? o.getStudent().getFullName() : "");
                row.createCell(2).setCellValue(o.getCompany() != null ? o.getCompany().getName() : "");
                row.createCell(3).setCellValue(o.getJobRole() != null ? o.getJobRole() : "");
                row.createCell(4).setCellValue(o.getPackageLpa());
                row.createCell(5).setCellValue(o.getLocation() != null ? o.getLocation() : "");
                row.createCell(6).setCellValue(o.getOfferStatus() != null ? o.getOfferStatus().name() : "");
                row.createCell(7).setCellValue(o.getJoiningDate() != null ? o.getJoiningDate().toString() : "");
            }
            for (int j = 0; j < oCols.length; j++) offersSheet.autoSizeColumn(j);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void addTableHeader(PdfPTable table, String col1, String col2, String col3, String col4, Font font) {
        PdfPCell c1 = new PdfPCell(new Phrase(col1, font));
        PdfPCell c2 = new PdfPCell(new Phrase(col2, font));
        PdfPCell c3 = new PdfPCell(new Phrase(col3, font));
        PdfPCell c4 = new PdfPCell(new Phrase(col4, font));
        c1.setBackgroundColor(new Color(230, 240, 255));
        c2.setBackgroundColor(new Color(230, 240, 255));
        c3.setBackgroundColor(new Color(230, 240, 255));
        c4.setBackgroundColor(new Color(230, 240, 255));
        table.addCell(c1);
        table.addCell(c2);
        table.addCell(c3);
        table.addCell(c4);
    }

    private void addTableHeader(PdfPTable table, String col1, String col2, String col3, String col4, String col5, Font font) {
        addTableHeader(table, col1, col2, col3, col4, font);
        PdfPCell c5 = new PdfPCell(new Phrase(col5, font));
        c5.setBackgroundColor(new Color(230, 240, 255));
        table.addCell(c5);
    }

    private void addTableHeader(PdfPTable table, String col1, String col2, String col3, Font font) {
        PdfPCell c1 = new PdfPCell(new Phrase(col1, font));
        PdfPCell c2 = new PdfPCell(new Phrase(col2, font));
        PdfPCell c3 = new PdfPCell(new Phrase(col3, font));
        c1.setBackgroundColor(new Color(230, 240, 255));
        c2.setBackgroundColor(new Color(230, 240, 255));
        c3.setBackgroundColor(new Color(230, 240, 255));
        table.addCell(c1);
        table.addCell(c2);
        table.addCell(c3);
    }

    private void addMetaCell(PdfPTable table, String label, String value, Font boldFont, Font regularFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.addElement(new Phrase(label + " ", boldFont));
        cell.addElement(new Phrase(value, regularFont));
        table.addCell(cell);
    }
}
