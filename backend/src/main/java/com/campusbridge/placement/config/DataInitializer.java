package com.campusbridge.placement.config;

import com.campusbridge.placement.entity.*;
import com.campusbridge.placement.entity.enums.*;
import com.campusbridge.placement.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final StudentRepository studentRepository;
    private final RecruiterRepository recruiterRepository;
    private final PlacementDriveRepository driveRepository;
    private final DriveEligibilityCriteriaRepository criteriaRepository;
    private final RecruitmentStageRepository stageRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusHistoryRepository appHistoryRepository;
    private final InterviewerRepository interviewerRepository;
    private final InterviewPanelRepository panelRepository;
    private final InterviewRepository interviewRepository;
    private final RescheduleRequestRepository rescheduleRepository;
    private final OfferRepository offerRepository;
    private final AnnouncementRepository announcementRepository;
    private final CompanyPlacementHistoryRepository companyHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            CollegeRepository collegeRepository,
            DepartmentRepository departmentRepository,
            UserRepository userRepository,
            CompanyRepository companyRepository,
            StudentRepository studentRepository,
            RecruiterRepository recruiterRepository,
            PlacementDriveRepository driveRepository,
            DriveEligibilityCriteriaRepository criteriaRepository,
            RecruitmentStageRepository stageRepository,
            ApplicationRepository applicationRepository,
            ApplicationStatusHistoryRepository appHistoryRepository,
            InterviewerRepository interviewerRepository,
            InterviewPanelRepository panelRepository,
            InterviewRepository interviewRepository,
            RescheduleRequestRepository rescheduleRepository,
            OfferRepository offerRepository,
            AnnouncementRepository announcementRepository,
            CompanyPlacementHistoryRepository companyHistoryRepository,
            PasswordEncoder passwordEncoder) {
        this.collegeRepository = collegeRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.studentRepository = studentRepository;
        this.recruiterRepository = recruiterRepository;
        this.driveRepository = driveRepository;
        this.criteriaRepository = criteriaRepository;
        this.stageRepository = stageRepository;
        this.applicationRepository = applicationRepository;
        this.appHistoryRepository = appHistoryRepository;
        this.interviewerRepository = interviewerRepository;
        this.panelRepository = panelRepository;
        this.interviewRepository = interviewRepository;
        this.rescheduleRepository = rescheduleRepository;
        this.offerRepository = offerRepository;
        this.announcementRepository = announcementRepository;
        this.companyHistoryRepository = companyHistoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            System.out.println(">>> CampusBridge database already initialized with demo data. Skipping seeding.");
            return;
        }

        System.out.println(">>> Initializing CampusBridge synthetic demo data...");

        // 1. Colleges
        College gct = new College("Government College of Technology", "GCT-CBE", "Coimbatore", "Tamil Nadu", "tpo@gct.ac.in", "+91 422 2432221");
        College psg = new College("PSG College of Technology", "PSG-CBE", "Coimbatore", "Tamil Nadu", "placement@psgtech.edu", "+91 422 4344777");
        College mit = new College("Madras Institute of Technology", "MIT-CHN", "Chennai", "Tamil Nadu", "placement@mitindia.edu", "+91 44 22516000");
        collegeRepository.saveAll(Arrays.asList(gct, psg, mit));

        // 2. Departments
        List<Department> departments = Arrays.asList(
                new Department(gct, "Computer Science & Engineering", "CSE"),
                new Department(gct, "Information Technology", "IT"),
                new Department(gct, "Electronics & Communication Engineering", "ECE"),
                new Department(gct, "Electrical & Electronics Engineering", "EEE"),
                new Department(gct, "Mechanical Engineering", "MECH"),
                new Department(gct, "Artificial Intelligence & Data Science", "AIDS")
        );
        departmentRepository.saveAll(departments);

        // 3. Companies
        Company zoho = new Company("Zoho Corporation", "https://www.zohocorp.com", "SaaS & Enterprise Cloud",
                "Leading cloud software suite and SaaS pioneer founded in Tamil Nadu.", "Chennai", "Tamil Nadu",
                "Priya Sharma", "recruiter.zoho@zoho.com", "+91 44 67447000");
        zoho.setLogoUrl("https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=100&auto=format&fit=crop&q=60");

        Company tcs = new Company("Tata Consultancy Services", "https://www.tcs.com", "IT Services & Consulting",
                "Global leader in IT services, consulting, and business solutions.", "Chennai", "Tamil Nadu",
                "Rajesh Kumar", "recruiter.tcs@tcs.com", "+91 44 66168888");
        tcs.setLogoUrl("https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=100&auto=format&fit=crop&q=60");

        Company infosys = new Company("Infosys Limited", "https://www.infosys.com", "Enterprise IT Services",
                "Global leader in next-generation digital services and consulting.", "Bangalore", "Karnataka",
                "Ananya Sen", "recruiter.infosys@infosys.com", "+91 80 28520261");
        infosys.setLogoUrl("https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=100&auto=format&fit=crop&q=60");

        companyRepository.saveAll(Arrays.asList(zoho, tcs, infosys));

        // 4. Users & Profiles
        // TPO User
        User tpoUser = new User("tpo@gct.ac.in", passwordEncoder.encode("Tpo@123"), Role.TPO, "Dr. K. Rangarajan", "+91 9842100001", gct);
        userRepository.save(tpoUser);

        // Recruiter Users
        User zohoUser = new User("recruiter.zoho@zoho.com", passwordEncoder.encode("Zoho@123"), Role.RECRUITER, "Priya Sharma", "+91 9842100002", null);
        userRepository.save(zohoUser);
        Recruiter zohoRecruiter = new Recruiter(zohoUser, zoho, "Lead Technical Recruiter");
        recruiterRepository.save(zohoRecruiter);

        User tcsUser = new User("recruiter.tcs@tcs.com", passwordEncoder.encode("Tcs@123"), Role.RECRUITER, "Rajesh Kumar", "+91 9842100003", null);
        userRepository.save(tcsUser);
        Recruiter tcsRecruiter = new Recruiter(tcsUser, tcs, "Campus Talent Acquisition Lead");
        recruiterRepository.save(tcsRecruiter);

        User infyUser = new User("recruiter.infosys@infosys.com", passwordEncoder.encode("Infy@123"), Role.RECRUITER, "Ananya Sen", "+91 9842100004", null);
        userRepository.save(infyUser);
        Recruiter infyRecruiter = new Recruiter(infyUser, infosys, "University Relations Manager");
        recruiterRepository.save(infyRecruiter);

        // Student Users
        User u1 = new User("arun.student@gct.ac.in", passwordEncoder.encode("Student@123"), Role.STUDENT, "Arun Prakash", "+91 9876543210", gct);
        User u2 = new User("kavya.student@gct.ac.in", passwordEncoder.encode("Student@123"), Role.STUDENT, "Kavya Murugan", "+91 9876543211", gct);
        User u3 = new User("suresh.student@gct.ac.in", passwordEncoder.encode("Student@123"), Role.STUDENT, "Suresh Balan", "+91 9876543212", gct);
        User u4 = new User("deepa.student@gct.ac.in", passwordEncoder.encode("Student@123"), Role.STUDENT, "Deepa Sundaram", "+91 9876543213", gct);
        User u5 = new User("vijay.student@gct.ac.in", passwordEncoder.encode("Student@123"), Role.STUDENT, "Vijay Anand", "+91 9876543214", gct);
        userRepository.saveAll(Arrays.asList(u1, u2, u3, u4, u5));

        Student s1 = new Student(u1, gct, "22CS014", "CSE", "3rd Year", 2026, 8.75, 0, "Java, Spring Boot, MySQL, React, Data Structures");
        s1.setCertifications("Oracle Certified Java Associate, AWS Cloud Practitioner");
        s1.setResumeUrl("/resumes/Resume_22CS014.pdf");
        s1.setResumeFileName("Resume_22CS014.pdf");

        Student s2 = new Student(u2, gct, "22IT028", "IT", "3rd Year", 2026, 9.10, 0, "Python, Machine Learning, SQL, React, Node.js");
        s2.setCertifications("Google Data Analytics Professional, Python Institute PCAP");
        s2.setResumeUrl("/resumes/Resume_22IT028.pdf");
        s2.setResumeFileName("Resume_22IT028.pdf");
        s2.setPlacementStatus(PlacementStatus.PLACED);

        Student s3 = new Student(u3, gct, "22EC045", "ECE", "3rd Year", 2026, 7.20, 1, "C++, Embedded Systems, Python, SQL");
        Student s4 = new Student(u4, gct, "22CS052", "CSE", "3rd Year", 2026, 8.40, 0, "Java, React, SQL, Cloud Computing");
        Student s5 = new Student(u5, gct, "22ME012", "MECH", "3rd Year", 2026, 6.80, 2, "AutoCAD, SolidWorks, Python Basics");

        studentRepository.saveAll(Arrays.asList(s1, s2, s3, s4, s5));

        // 5. Placement Drives
        // Drive 1: Zoho MTS (Active)
        PlacementDrive d1 = new PlacementDrive();
        d1.setCompany(zoho);
        d1.setCollege(gct);
        d1.setJobTitle("Member Technical Staff (MTS)");
        d1.setJobDescription("Develop high-scale cloud applications and microservices using Java, C++, and modern web technologies. You will work on real-time collaboration engines and distributed storage.");
        d1.setPackageLpa(8.5);
        d1.setLocation("Chennai");
        d1.setWorkMode(WorkMode.ON_SITE);
        d1.setVacancies(25);
        d1.setEmploymentType(EmploymentType.FULL_TIME);
        d1.setApplicationDeadline(LocalDateTime.now().plusDays(14));
        d1.setDriveDate(LocalDate.now().plusDays(20));
        d1.setStatus(DriveStatus.ACTIVE);
        d1.setTpoApprovalNotes("Approved by TPO Dr. K. Rangarajan for CSE, IT, ECE.");
        d1.setCreatedBy(zohoUser);
        driveRepository.save(d1);

        DriveEligibilityCriteria c1 = new DriveEligibilityCriteria(d1, "CSE,IT,ECE", 7.5, 0, 2026, "Java, Data Structures, Algorithms, SQL");
        criteriaRepository.save(c1);

        RecruitmentStage s1_1 = new RecruitmentStage(d1, 1, "Aptitude & Basic Programming", StageType.APTITUDE, "Quantitative aptitude and basic C/Java logic", false);
        RecruitmentStage s1_2 = new RecruitmentStage(d1, 2, "Advanced Coding Challenge", StageType.CODING, "Hands-on data structures and problem solving", false);
        RecruitmentStage s1_3 = new RecruitmentStage(d1, 3, "Technical Round 1", StageType.TECHNICAL_INTERVIEW, "System design, core CS, and live coding", false);
        RecruitmentStage s1_4 = new RecruitmentStage(d1, 4, "HR & Cultural Fit", StageType.HR_INTERVIEW, "Personal interview and role discussion", true);
        stageRepository.saveAll(Arrays.asList(s1_1, s1_2, s1_3, s1_4));

        // Drive 2: TCS Digital (Active)
        PlacementDrive d2 = new PlacementDrive();
        d2.setCompany(tcs);
        d2.setCollege(gct);
        d2.setJobTitle("Digital Specialist Engineer");
        d2.setJobDescription("Work on cutting-edge technologies including Cloud, AI/ML, Cyber Security, and Big Data Analytics across enterprise client projects.");
        d2.setPackageLpa(7.0);
        d2.setLocation("Chennai / Bangalore");
        d2.setWorkMode(WorkMode.HYBRID);
        d2.setVacancies(50);
        d2.setEmploymentType(EmploymentType.FULL_TIME);
        d2.setApplicationDeadline(LocalDateTime.now().plusDays(10));
        d2.setDriveDate(LocalDate.now().plusDays(15));
        d2.setStatus(DriveStatus.ACTIVE);
        d2.setTpoApprovalNotes("Approved for all engineering branches meeting 7.0 CGPA.");
        d2.setCreatedBy(tcsUser);
        driveRepository.save(d2);

        DriveEligibilityCriteria c2 = new DriveEligibilityCriteria(d2, "CSE,IT,ECE,EEE,MECH,AIDS", 7.0, 1, 2026, "Java, Python, SQL");
        criteriaRepository.save(c2);

        RecruitmentStage s2_1 = new RecruitmentStage(d2, 1, "TCS NQT / Digital Assessment", StageType.APTITUDE, "Advanced cognitive and hands-on coding", false);
        RecruitmentStage s2_2 = new RecruitmentStage(d2, 2, "Technical Interview", StageType.TECHNICAL_INTERVIEW, "Tech panel interview on project and fundamentals", false);
        RecruitmentStage s2_3 = new RecruitmentStage(d2, 3, "Managerial & HR Round", StageType.HR_INTERVIEW, "Leadership, adaptability, and background checks", true);
        stageRepository.saveAll(Arrays.asList(s2_1, s2_2, s2_3));

        // Drive 3: Infosys Specialist Programmer (Active)
        PlacementDrive d3 = new PlacementDrive();
        d3.setCompany(infosys);
        d3.setCollege(gct);
        d3.setJobTitle("Specialist Programmer (SP)");
        d3.setJobDescription("High-impact algorithmic engineering role focused on enterprise digital architecture, full-stack development, and complex algorithm optimization.");
        d3.setPackageLpa(9.5);
        d3.setLocation("Bangalore");
        d3.setWorkMode(WorkMode.ON_SITE);
        d3.setVacancies(15);
        d3.setEmploymentType(EmploymentType.FULL_TIME);
        d3.setApplicationDeadline(LocalDateTime.now().plusDays(7));
        d3.setDriveDate(LocalDate.now().plusDays(12));
        d3.setStatus(DriveStatus.ACTIVE);
        d3.setTpoApprovalNotes("Approved high-CTC drive.");
        d3.setCreatedBy(infyUser);
        driveRepository.save(d3);

        DriveEligibilityCriteria c3 = new DriveEligibilityCriteria(d3, "CSE,IT,AIDS", 8.0, 0, 2026, "Data Structures, Algorithms, Python, Java");
        criteriaRepository.save(c3);

        RecruitmentStage s3_1 = new RecruitmentStage(d3, 1, "HackWithInfy / Coding Round", StageType.CODING, "Algorithmic programming", false);
        RecruitmentStage s3_2 = new RecruitmentStage(d3, 2, "Technical Discussion", StageType.TECHNICAL_INTERVIEW, "In-depth code walkthrough", false);
        RecruitmentStage s3_3 = new RecruitmentStage(d3, 3, "HR Discussion", StageType.HR_INTERVIEW, "Compensation and joining formalization", true);
        stageRepository.saveAll(Arrays.asList(s3_1, s3_2, s3_3));

        // Drive 4: Zoho Quality Analyst (Pending Approval - for demo workflow)
        PlacementDrive d4 = new PlacementDrive();
        d4.setCompany(zoho);
        d4.setCollege(gct);
        d4.setJobTitle("Associate Quality Assurance Engineer");
        d4.setJobDescription("Ensure world-class product quality through test automation, performance profiling, and continuous integration pipeline testing.");
        d4.setPackageLpa(5.5);
        d4.setLocation("Chennai");
        d4.setWorkMode(WorkMode.ON_SITE);
        d4.setVacancies(20);
        d4.setEmploymentType(EmploymentType.FULL_TIME);
        d4.setApplicationDeadline(LocalDateTime.now().plusDays(18));
        d4.setDriveDate(LocalDate.now().plusDays(25));
        d4.setStatus(DriveStatus.PENDING_APPROVAL); // Visible to TPO for approval!
        d4.setCreatedBy(zohoUser);
        driveRepository.save(d4);

        DriveEligibilityCriteria c4 = new DriveEligibilityCriteria(d4, "CSE,IT,ECE,EEE", 6.5, 2, 2026, "Testing, Java, Python, SQL");
        criteriaRepository.save(c4);

        // 6. Applications
        // Student 1 (Arun) applied to Zoho and TCS
        Application app1 = new Application(d1, s1, ApplicationStatus.INTERVIEW, s1_3);
        Application app2 = new Application(d2, s1, ApplicationStatus.INTERVIEW, s2_2);

        // Student 2 (Kavya) applied to Zoho (HR) and Infosys (Selected)
        Application app3 = new Application(d1, s2, ApplicationStatus.INTERVIEW, s1_4);
        Application app4 = new Application(d3, s2, ApplicationStatus.SELECTED, s3_3);

        // Student 3 (Suresh) applied to TCS (Under Review)
        Application app5 = new Application(d2, s3, ApplicationStatus.UNDER_REVIEW, s2_1);

        // Student 4 (Deepa) applied to TCS (Interview)
        Application app6 = new Application(d2, s4, ApplicationStatus.INTERVIEW, s2_2);

        applicationRepository.saveAll(Arrays.asList(app1, app2, app3, app4, app5, app6));

        appHistoryRepository.save(new ApplicationStatusHistory(app1, "INTERVIEW", "Technical Round 1", "Cleared coding round with 92% score", "Priya Sharma"));
        appHistoryRepository.save(new ApplicationStatusHistory(app2, "INTERVIEW", "Technical Interview", "Qualified through TCS Digital NQT", "Rajesh Kumar"));
        appHistoryRepository.save(new ApplicationStatusHistory(app4, "SELECTED", "HR Discussion", "Selected for Specialist Programmer role at 9.5 LPA", "Ananya Sen"));

        // 7. Interview Panels
        InterviewPanel zohoPanel = new InterviewPanel(zoho, "Zoho Cloud Core Panel A", "Core product engineering panel");
        panelRepository.save(zohoPanel);
        Interviewer iv1 = new Interviewer(zoho, "Manojkumar S", "manoj@zohocorp.com", "Principal Architect", "+91 9443100001");
        interviewerRepository.save(iv1);
        zohoPanel.getMembers().add(iv1);
        panelRepository.save(zohoPanel);

        InterviewPanel tcsPanel = new InterviewPanel(tcs, "TCS Digital Special Panel 2", "Digital initiatives interview panel");
        panelRepository.save(tcsPanel);
        Interviewer iv2 = new Interviewer(tcs, "Gokulnath R", "gokul@tcs.com", "Solution Architect", "+91 9443100002");
        interviewerRepository.save(iv2);
        tcsPanel.getMembers().add(iv2);
        panelRepository.save(tcsPanel);

        // 8. INTERVIEWS WITH PRE-CONFIGURED CONFLICT FOR FIRST REVIEW DEMONSTRATION!
        // Fixed upcoming demo date: 2026-10-20
        LocalDate demoInterviewDate = LocalDate.now().plusDays(2);

        // Interview 1: Arun Prakash with Zoho Technical Round: 10:00:00 to 11:00:00
        Interview interviewZoho = new Interview();
        interviewZoho.setDrive(d1);
        interviewZoho.setStage(s1_3);
        interviewZoho.setStudent(s1);
        interviewZoho.setInterviewDate(demoInterviewDate);
        interviewZoho.setStartTime(LocalTime.of(10, 0));
        interviewZoho.setEndTime(LocalTime.of(11, 0));
        interviewZoho.setMode("ONLINE");
        interviewZoho.setMeetingLink("https://meet.zoho.com/join/cb-zoho-tech-22cs014");
        interviewZoho.setPanel(zohoPanel);
        interviewZoho.setStatus(InterviewStatus.SCHEDULED);
        interviewZoho.setInstructions("Please have your IDE ready with Java 17/21 installed.");
        interviewZoho.setRequiredDocuments("College ID Card, Resume, GitHub Profile");
        interviewRepository.save(interviewZoho);

        // Interview 2: Arun Prakash with TCS Technical Round: 10:30:00 to 11:30:00
        // OVERLAPS with Zoho interview interval [10:30, 11:00]!
        Interview interviewTcs = new Interview();
        interviewTcs.setDrive(d2);
        interviewTcs.setStage(s2_2);
        interviewTcs.setStudent(s1);
        interviewTcs.setInterviewDate(demoInterviewDate);
        interviewTcs.setStartTime(LocalTime.of(10, 30));
        interviewTcs.setEndTime(LocalTime.of(11, 30));
        interviewTcs.setMode("ONLINE");
        interviewTcs.setMeetingLink("https://teams.microsoft.com/l/meetup-join/cb-tcs-digital-22cs014");
        interviewTcs.setPanel(tcsPanel);
        interviewTcs.setStatus(InterviewStatus.RESCHEDULE_REQUESTED); // Student has submitted reschedule request!
        interviewTcs.setInstructions("Be prepared for domain questions on Cloud Architecture & Data Structures.");
        interviewTcs.setRequiredDocuments("College ID, Grade Sheets, Resume");
        interviewRepository.save(interviewTcs);

        // Reschedule Request pre-seeded for demo workflow
        RescheduleRequest reschedule = new RescheduleRequest(
                interviewTcs, s1,
                "Overlapping interview conflict: Zoho MTS Technical Interview is scheduled on the same date at 10:00 - 11:00 AM.",
                "Same date afternoon after 02:00 PM or next day morning"
        );
        reschedule.setStatus(RescheduleStatus.PENDING);
        rescheduleRepository.save(reschedule);

        // Interview 3: Kavya with Zoho HR Round
        Interview interviewKavya = new Interview();
        interviewKavya.setDrive(d1);
        interviewKavya.setStage(s1_4);
        interviewKavya.setStudent(s2);
        interviewKavya.setInterviewDate(demoInterviewDate.plusDays(1));
        interviewKavya.setStartTime(LocalTime.of(14, 0));
        interviewKavya.setEndTime(LocalTime.of(14, 45));
        interviewKavya.setMode("ONLINE");
        interviewKavya.setMeetingLink("https://meet.zoho.com/join/cb-zoho-hr-22it028");
        interviewKavya.setStatus(InterviewStatus.SCHEDULED);
        interviewRepository.save(interviewKavya);

        // 9. Offers
        Offer offerKavya = new Offer(app4, s2, infosys, d3, "Specialist Programmer", 9.5, "Bangalore", LocalDate.now().plusMonths(8));
        offerKavya.setOfferStatus(OfferStatus.ACCEPTED);
        offerKavya.setOfferLetterUrl("/documents/Offer_Infosys_22IT028.pdf");
        offerRepository.save(offerKavya);

        // 10. Announcements
        announcementRepository.save(new Announcement(
                gct,
                "Naan Mudhalvan Placement Drive & Industry Connect Initiative",
                "Under the Naan Mudhalvan initiative, unified multi-company recruitment drive coordinators have partnered with top tech companies including Zoho, TCS, and Infosys. Students must keep their profiles updated and report scheduling conflicts through CampusBridge.",
                TargetAudience.ALL,
                null,
                null,
                "Dr. K. Rangarajan (TPO)"
        ));

        announcementRepository.save(new Announcement(
                gct,
                "Mandatory Resume & CGPA Verification for S5 / Final Year Batches",
                "All registered students are requested to verify their uploaded resumes and CGPA records in the CampusBridge portal before applying to upcoming product drives.",
                TargetAudience.STUDENTS,
                "CSE,IT,ECE",
                null,
                "Placement Coordinator Office"
        ));

        // 11. Company Placement History (for explainable recommendations & college comparison)
        companyHistoryRepository.saveAll(Arrays.asList(
                new CompanyPlacementHistory(zoho, gct, "2024-2025", 85, 38, 18, "MTS, QA Engineer", 8.2, 8.5),
                new CompanyPlacementHistory(zoho, gct, "2023-2024", 78, 32, 14, "MTS", 7.8, 8.0),
                new CompanyPlacementHistory(zoho, psg, "2024-2025", 110, 52, 26, "MTS, Product Developer", 8.8, 12.0),
                new CompanyPlacementHistory(zoho, mit, "2024-2025", 95, 44, 21, "MTS", 8.4, 9.5),
                new CompanyPlacementHistory(tcs, gct, "2024-2025", 140, 75, 42, "Digital, Ninja", 6.8, 7.5),
                new CompanyPlacementHistory(tcs, gct, "2023-2024", 130, 68, 38, "Digital, Ninja", 6.5, 7.0),
                new CompanyPlacementHistory(infosys, gct, "2024-2025", 120, 48, 22, "Specialist Programmer, SE", 8.1, 9.5),
                new CompanyPlacementHistory(infosys, psg, "2024-2025", 135, 60, 31, "SP, DSE", 8.6, 9.5)
        ));

        System.out.println(">>> CampusBridge synthetic demo data initialization completed successfully!");
    }
}
