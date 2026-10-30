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
import java.util.ArrayList;
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
        // Ensure all Coimbatore, Erode, and Chennai colleges (including BIT) are seeded with departments
        ensureAllRegionalColleges();
        College bit = collegeRepository.findByCode("BIT-SAT").orElseGet(() ->
            getOrSaveCollege("Bannari Amman Institute of Technology", "BIT-SAT", "Sathyamangalam", "Tamil Nadu", "placement@bitsathy.ac.in", "+91 4295 226000")
        );
        getOrSaveUser("tpo@bitsathy.ac.in", "Tpo@2025", Role.TPO, "Dr. S. BIT Placement Officer", "+91 9842200029", bit);

        // Idempotent guard: skip if already fully seeded with all required data
        if (collegeRepository.count() >= 59 && userRepository.existsByEmail("student@cb.com")) {
            if (companyHistoryRepository.findByCollegeId(bit.getId()).isEmpty()) {
                companyRepository.findByName("Zoho Corporation").ifPresent(z ->
                    companyHistoryRepository.save(new CompanyPlacementHistory(z, bit, "2024-2025", 92, 46, 22, "MTS, Product Developer", 8.6, 11.5))
                );
                companyRepository.findByName("Tata Consultancy Services").ifPresent(t ->
                    companyHistoryRepository.save(new CompanyPlacementHistory(t, bit, "2024-2025", 135, 72, 40, "Digital, Ninja", 6.8, 7.5))
                );
                companyRepository.findByName("Infosys Limited").ifPresent(inf ->
                    companyHistoryRepository.save(new CompanyPlacementHistory(inf, bit, "2024-2025", 115, 50, 24, "Specialist Programmer, SE", 8.0, 9.5))
                );
            }
            System.out.println(">>> CampusBridge database already fully seeded. Skipping.");
            return;
        }

        System.out.println(">>> Initializing CampusBridge expanded demo data...");

        // ===== 1. COLLEGES (21 total) =====
        College gct     = getOrSaveCollege("Government College of Technology",         "GCT-CBE", "Coimbatore",   "Tamil Nadu",    "tpo@gct.ac.in",            "+91 422 2432221");
        College psg     = getOrSaveCollege("PSG College of Technology",                "PSG-CBE", "Coimbatore",   "Tamil Nadu",    "placement@psgtech.edu",    "+91 422 4344777");
        College mit     = getOrSaveCollege("Madras Institute of Technology",           "MIT-CHN", "Chennai",      "Tamil Nadu",    "placement@mitindia.edu",   "+91 44 22516000");
        College anna    = getOrSaveCollege("Anna University",                          "AU-CHN",  "Chennai",      "Tamil Nadu",    "placement@annauniv.edu",   "+91 44 22358000");
        College vit     = getOrSaveCollege("VIT University",                           "VIT-VEL", "Vellore",      "Tamil Nadu",    "placement@vit.ac.in",      "+91 416 2202020");
        College srm     = getOrSaveCollege("SRM Institute of Science and Technology",  "SRM-KTR", "Kattankulathur","Tamil Nadu",   "placement@srmist.edu.in",  "+91 44 27417000");
        College bits    = getOrSaveCollege("BITS Pilani",                              "BITS-PIL","Pilani",       "Rajasthan",     "placement@bits-pilani.ac.in","+91 1596 242192");
        College iitm    = getOrSaveCollege("IIT Madras",                               "IITM-CHN","Chennai",      "Tamil Nadu",    "placement@iitm.ac.in",     "+91 44 22578000");
        College nit     = getOrSaveCollege("NIT Trichy",                               "NITT",    "Tiruchirappalli","Tamil Nadu",  "placement@nitt.edu",       "+91 431 2503000");
        College amrita  = getOrSaveCollege("Amrita Vishwa Vidyapeetham",               "AMRITA",  "Coimbatore",   "Tamil Nadu",    "placement@amrita.edu",     "+91 422 2685000");
        College ceg     = getOrSaveCollege("College of Engineering Guindy",            "CEG-CHN", "Chennai",      "Tamil Nadu",    "placement@ceg.annauniv.edu","+91 44 22200200");
        College kct     = getOrSaveCollege("Kumaraguru College of Technology",         "KCT-CBE", "Coimbatore",   "Tamil Nadu",    "placement@kct.ac.in",      "+91 422 2669401");
        College rvce    = getOrSaveCollege("RV College of Engineering",                "RVCE-BLR","Bangalore",    "Karnataka",     "placement@rvce.edu.in",    "+91 80 67178000");
        College pesu    = getOrSaveCollege("PES University",                           "PESU-BLR","Bangalore",    "Karnataka",     "placement@pes.edu",        "+91 80 26672178");
        College manipal = getOrSaveCollege("Manipal Institute of Technology",          "MIT-MNP", "Manipal",      "Karnataka",     "placement@manipal.edu",    "+91 820 2922000");
        College coep    = getOrSaveCollege("College of Engineering Pune",              "COEP-PNE","Pune",         "Maharashtra",   "placement@coep.ac.in",     "+91 20 25507000");
        College vjti    = getOrSaveCollege("Veermata Jijabai Technological Institute", "VJTI-MUM","Mumbai",       "Maharashtra",   "placement@vjti.ac.in",     "+91 22 24198000");
        College dtu     = getOrSaveCollege("Delhi Technological University",           "DTU-DLH", "Delhi",        "Delhi",         "placement@dtu.ac.in",      "+91 11 27871018");
        College lpu     = getOrSaveCollege("Lovely Professional University",           "LPU-PHG", "Phagwara",     "Punjab",        "placement@lpu.in",         "+91 1824 400000");
        College thapar  = getOrSaveCollege("Thapar Institute of Engineering",          "TIET-PTL","Patiala",      "Punjab",        "placement@thapar.edu",     "+91 175 2393021");

        // ===== 2. DEPARTMENTS =====
        ensureDepartments(gct);
        ensureDepartments(psg);
        ensureDepartments(vit);
        ensureDepartments(bit);

        // ===== 3. COMPANIES (50 total) =====
        Company zoho        = getOrSaveCompany("Zoho Corporation",                    "https://www.zohocorp.com",       "SaaS & Enterprise Cloud",          "Chennai",     "Tamil Nadu");
        Company tcs         = getOrSaveCompany("Tata Consultancy Services",           "https://www.tcs.com",            "IT Services & Consulting",         "Chennai",     "Tamil Nadu");
        Company infosys     = getOrSaveCompany("Infosys Limited",                     "https://www.infosys.com",        "Enterprise IT Services",           "Bangalore",   "Karnataka");
        Company wipro       = getOrSaveCompany("Wipro Technologies",                  "https://www.wipro.com",          "IT Services & BPO",                "Bangalore",   "Karnataka");
        Company hcl         = getOrSaveCompany("HCL Technologies",                    "https://www.hcltech.com",        "IT Services & Solutions",          "Noida",       "Uttar Pradesh");
        Company cognizant   = getOrSaveCompany("Cognizant Technology Solutions",      "https://www.cognizant.com",      "Digital Transformation",           "Chennai",     "Tamil Nadu");
        Company accenture   = getOrSaveCompany("Accenture India",                     "https://www.accenture.com",      "Management Consulting & IT",       "Mumbai",      "Maharashtra");
        Company amazon      = getOrSaveCompany("Amazon Development Centre",           "https://www.amazon.in",          "E-Commerce & Cloud (AWS)",         "Hyderabad",   "Telangana");
        Company google      = getOrSaveCompany("Google India",                        "https://careers.google.com",     "Technology & Search",              "Bangalore",   "Karnataka");
        Company microsoft   = getOrSaveCompany("Microsoft India",                     "https://www.microsoft.com",      "Software & Cloud (Azure)",         "Hyderabad",   "Telangana");
        Company deloitte    = getOrSaveCompany("Deloitte USI",                        "https://www2.deloitte.com",      "Management Consulting",            "Bangalore",   "Karnataka");
        Company capgemini   = getOrSaveCompany("Capgemini India",                     "https://www.capgemini.com",      "IT & Digital Services",            "Mumbai",      "Maharashtra");
        Company ibm         = getOrSaveCompany("IBM India",                           "https://www.ibm.com",            "Cloud & AI Solutions",             "Bangalore",   "Karnataka");
        Company oracle      = getOrSaveCompany("Oracle India",                        "https://www.oracle.com",         "Database & Cloud",                 "Bangalore",   "Karnataka");
        Company samsung     = getOrSaveCompany("Samsung R&D Institute India",         "https://research.samsung.com",   "Electronics & R&D",                "Bangalore",   "Karnataka");
        Company bosch       = getOrSaveCompany("Robert Bosch India",                  "https://www.bosch.in",           "Automotive & IoT",                 "Bangalore",   "Karnataka");
        Company adobe       = getOrSaveCompany("Adobe Systems India",                 "https://www.adobe.com",          "Creative & Digital Media",         "Noida",       "Uttar Pradesh");
        Company qualcomm    = getOrSaveCompany("Qualcomm India",                      "https://www.qualcomm.com",       "Semiconductors & Wireless",        "Hyderabad",   "Telangana");
        Company intuit      = getOrSaveCompany("Intuit India",                        "https://www.intuit.com",         "FinTech & Financial Software",     "Bangalore",   "Karnataka");
        Company flipkart    = getOrSaveCompany("Flipkart Internet",                   "https://www.flipkart.com",       "E-Commerce & Logistics",           "Bangalore",   "Karnataka");
        Company swiggy      = getOrSaveCompany("Swiggy",                              "https://www.swiggy.com",         "Food Delivery & Logistics",        "Bangalore",   "Karnataka");
        Company zomato      = getOrSaveCompany("Zomato",                              "https://www.zomato.com",         "Food Aggregator & Delivery",       "Gurugram",    "Haryana");
        Company ola         = getOrSaveCompany("Ola Electric Mobility",               "https://olaelectric.com",        "Electric Vehicles & EV Tech",      "Bangalore",   "Karnataka");
        Company paytm       = getOrSaveCompany("One97 Communications (Paytm)",        "https://paytm.com",              "Payments & FinTech",               "Noida",       "Uttar Pradesh");
        Company phonepe     = getOrSaveCompany("PhonePe",                             "https://www.phonepe.com",        "Digital Payments",                 "Bangalore",   "Karnataka");
        Company razorpay    = getOrSaveCompany("Razorpay",                            "https://razorpay.com",           "Payment Gateway & FinTech",        "Bangalore",   "Karnataka");
        Company freshworks  = getOrSaveCompany("Freshworks",                          "https://www.freshworks.com",     "SaaS & Customer Experience",       "Chennai",     "Tamil Nadu");
        Company chargebee   = getOrSaveCompany("Chargebee",                           "https://www.chargebee.com",      "Subscription Management SaaS",     "Chennai",     "Tamil Nadu");
        Company mindtree    = getOrSaveCompany("LTI-Mindtree",                        "https://www.ltimindtree.com",    "Digital Solutions",                "Bangalore",   "Karnataka");
        Company mphasis     = getOrSaveCompany("Mphasis",                             "https://www.mphasis.com",        "IT & BPO Services",                "Bangalore",   "Karnataka");
        Company hexaware    = getOrSaveCompany("Hexaware Technologies",               "https://hexaware.com",           "IT & BPO",                         "Mumbai",      "Maharashtra");
        Company zensar      = getOrSaveCompany("Zensar Technologies",                 "https://www.zensar.com",         "IT Services & Digital",            "Pune",        "Maharashtra");
        Company niit        = getOrSaveCompany("NIIT Technologies",                   "https://www.niit-tech.com",      "IT Solutions",                     "Noida",       "Uttar Pradesh");
        Company cyient      = getOrSaveCompany("Cyient",                              "https://www.cyient.com",         "Engineering & Technology",         "Hyderabad",   "Telangana");
        Company ltts        = getOrSaveCompany("L&T Technology Services",             "https://www.ltts.com",           "Engineering R&D",                  "Vadodara",    "Gujarat");
        Company persistent  = getOrSaveCompany("Persistent Systems",                  "https://www.persistent.com",     "Product Engineering & Cloud",      "Pune",        "Maharashtra");
        Company techmahindra= getOrSaveCompany("Tech Mahindra",                       "https://www.techmahindra.com",   "IT & Telecom Solutions",           "Pune",        "Maharashtra");
        Company kpmg        = getOrSaveCompany("KPMG India",                          "https://home.kpmg/in",           "Audit & Advisory",                 "Mumbai",      "Maharashtra");
        Company pwc         = getOrSaveCompany("PricewaterhouseCoopers India",        "https://www.pwc.in",             "Consulting & Audit",               "Mumbai",      "Maharashtra");
        Company ernst       = getOrSaveCompany("Ernst & Young India",                 "https://www.ey.com/en_in",       "Assurance & Advisory",             "Bangalore",   "Karnataka");
        Company thoughtworks= getOrSaveCompany("Thoughtworks India",                  "https://www.thoughtworks.com",   "Agile Consulting & Software",      "Bangalore",   "Karnataka");
        Company citi        = getOrSaveCompany("Citibank N.A. India",                 "https://www.citigroup.com",      "Banking & Financial Services",     "Mumbai",      "Maharashtra");
        Company jpmorgan    = getOrSaveCompany("JPMorgan Chase India",                "https://www.jpmorganchase.com",  "Investment Banking & Technology",  "Mumbai",      "Maharashtra");
        Company goldman     = getOrSaveCompany("Goldman Sachs India",                 "https://www.goldmansachs.com",   "Financial Technology",             "Bangalore",   "Karnataka");
        Company bny         = getOrSaveCompany("BNY Mellon India",                    "https://www.bnymellon.com",      "Financial Services & IT",          "Pune",        "Maharashtra");
        Company sasken      = getOrSaveCompany("Sasken Technologies",                 "https://www.sasken.com",         "Embedded & IoT",                   "Bangalore",   "Karnataka");
        Company tataelxsi   = getOrSaveCompany("Tata Elxsi",                          "https://www.tataelxsi.com",      "Design & Technology",              "Bangalore",   "Karnataka");
        Company mediaspark  = getOrSaveCompany("Mediaspark Technologies",             "https://mediaspark.io",          "Media & AdTech",                   "Chennai",     "Tamil Nadu");
        Company kaleidofin  = getOrSaveCompany("Kaleidofin",                          "https://kaleidofin.com",         "Rural FinTech",                    "Chennai",     "Tamil Nadu");

        // ===== 4. EXISTING USERS (keep original accounts) =====
        User tpoUserGct = getOrSaveUser("tpo@gct.ac.in",             "Tpo@123",      Role.TPO,       "Dr. K. Rangarajan",   "+91 9842100001", gct);
        User zohoUser   = getOrSaveUser("recruiter.zoho@zoho.com",   "Zoho@123",     Role.RECRUITER, "Priya Sharma",        "+91 9842100002", null);
        getOrSaveRecruiter(zohoUser, zoho, "Lead Technical Recruiter");
        User tcsUser    = getOrSaveUser("recruiter.tcs@tcs.com",     "Tcs@123",      Role.RECRUITER, "Rajesh Kumar",        "+91 9842100003", null);
        getOrSaveRecruiter(tcsUser, tcs, "Campus Talent Acquisition Lead");
        User infyUser   = getOrSaveUser("recruiter.infosys@infosys.com", "Infy@123", Role.RECRUITER, "Ananya Sen",          "+91 9842100004", null);
        getOrSaveRecruiter(infyUser, infosys, "University Relations Manager");

        User u1 = getOrSaveUser("arun.student@gct.ac.in",  "Student@123", Role.STUDENT, "Arun Prakash",    "+91 9876543210", gct);
        User u2 = getOrSaveUser("kavya.student@gct.ac.in", "Student@123", Role.STUDENT, "Kavya Murugan",   "+91 9876543211", gct);
        User u3 = getOrSaveUser("suresh.student@gct.ac.in","Student@123", Role.STUDENT, "Suresh Balan",    "+91 9876543212", gct);
        User u4 = getOrSaveUser("deepa.student@gct.ac.in", "Student@123", Role.STUDENT, "Deepa Sundaram",  "+91 9876543213", gct);
        User u5 = getOrSaveUser("vijay.student@gct.ac.in", "Student@123", Role.STUDENT, "Vijay Anand",     "+91 9876543214", gct);

        Student s1 = null, s2 = null;
        if (!studentRepository.existsByRollNumber("22CS014")) {
            s1 = new Student(u1, gct, "22CS014", "CSE", "3rd Year", 2026, 8.75, 0, "Java, Spring Boot, MySQL, React, Data Structures");
            s1.setCertifications("Oracle Certified Java Associate, AWS Cloud Practitioner");
            s1.setResumeUrl("/resumes/Resume_22CS014.pdf"); s1.setResumeFileName("Resume_22CS014.pdf");

            s2 = new Student(u2, gct, "22IT028", "IT", "3rd Year", 2026, 9.10, 0, "Python, Machine Learning, SQL, React, Node.js");
            s2.setCertifications("Google Data Analytics Professional, Python Institute PCAP");
            s2.setResumeUrl("/resumes/Resume_22IT028.pdf"); s2.setResumeFileName("Resume_22IT028.pdf");
            s2.setPlacementStatus(PlacementStatus.PLACED);

            Student s3 = new Student(u3, gct, "22EC045", "ECE", "3rd Year", 2026, 7.20, 1, "C++, Embedded Systems, Python, SQL");
            Student s4 = new Student(u4, gct, "22CS052", "CSE", "3rd Year", 2026, 8.40, 0, "Java, React, SQL, Cloud Computing");
            Student s5 = new Student(u5, gct, "22ME012", "MECH", "3rd Year", 2026, 6.80, 2, "AutoCAD, SolidWorks, Python Basics");
            studentRepository.saveAll(Arrays.asList(s1, s2, s3, s4, s5));
        } else {
            s1 = studentRepository.findByRollNumber("22CS014").orElse(null);
            s2 = studentRepository.findByRollNumber("22IT028").orElse(null);
        }

        // ===== 5. DEMO ACCOUNTS =====
        User demoStudent  = getOrSaveUser("student@cb.com",   "student28", Role.STUDENT,   "Demo Student",      "+91 9000000001", gct);
        if (!studentRepository.existsByRollNumber("DEMO-STU-001")) {
            Student ds = new Student(demoStudent, gct, "DEMO-STU-001", "CSE", "3rd Year", 2026, 8.5, 0, "Java, Python, React, SQL");
            ds.setCertifications("AWS Cloud Practitioner");
            studentRepository.save(ds);
        }
        getOrSaveUser("tpo@cb.com",       "tpo28",      Role.TPO,       "Demo TPO Officer",  "+91 9000000002", gct);
        User demoRec = getOrSaveUser("recruiter@cb.com", "rec28", Role.RECRUITER, "Demo Recruiter",    "+91 9000000003", null);
        getOrSaveRecruiter(demoRec, zoho, "Campus Recruiter");

        // ===== 6. ADDITIONAL TPO USERS (28 more = 30 total including existing + demo) =====
        Object[][] tpoEntries = {
            {"tpo@psgtech.edu",          "Dr. R. Sureshkumar",       "+91 9842200001", psg},
            {"tpo@srm.edu",              "Prof. A. Meenakshi",        "+91 9842200002", srm},
            {"tpo@vit.ac.in",            "Dr. P. Krishnamurthy",      "+91 9842200003", vit},
            {"tpo@amrita.edu",           "Prof. S. Vijayalakshmi",    "+91 9842200004", amrita},
            {"tpo@kct.ac.in",            "Dr. M. Chandrasekaran",     "+91 9842200005", kct},
            {"tpo@annauniv.edu",         "Prof. T. Radhakrishnan",    "+91 9842200006", anna},
            {"tpo@nitt.edu",             "Dr. V. Gopalakrishnan",     "+91 9842200007", nit},
            {"tpo@iitm.ac.in",           "Prof. K. Subramanian",      "+91 9842200008", iitm},
            {"tpo@bits-pilani.ac.in",    "Dr. N. Joshi",              "+91 9842200009", bits},
            {"tpo@rvce.edu.in",          "Prof. S. Nandakumar",       "+91 9842200010", rvce},
            {"tpo@pesu.edu",             "Dr. R. Venkataraman",       "+91 9842200011", pesu},
            {"tpo@manipal.edu",          "Prof. G. Hegde",            "+91 9842200012", manipal},
            {"tpo@coep.ac.in",           "Dr. P. Kulkarni",           "+91 9842200013", coep},
            {"tpo@vjti.ac.in",           "Prof. A. Joshi",            "+91 9842200014", vjti},
            {"tpo@dtu.ac.in",            "Dr. S. Sharma",             "+91 9842200015", dtu},
            {"tpo@lpu.in",               "Prof. H. Kaur",             "+91 9842200016", lpu},
            {"tpo@thapar.edu",           "Dr. G. Singh",              "+91 9842200017", thapar},
            {"tpo@mitindia.edu",         "Prof. C. Ramesh",           "+91 9842200018", mit},
            {"tpo@ceg.ac.in",            "Dr. D. Palanivel",          "+91 9842200019", ceg},
            {"tpo2@gct.ac.in",           "Dr. L. Arumugam",           "+91 9842200020", gct},
            {"tpo2@psgtech.edu",         "Prof. B. Manickam",         "+91 9842200021", psg},
            {"tpo2@vit.ac.in",           "Dr. F. Karthikeyan",        "+91 9842200022", vit},
            {"tpo2@srm.edu",             "Prof. J. Selvam",           "+91 9842200023", srm},
            {"tpo2@amrita.edu",          "Dr. I. Sundaresan",         "+91 9842200024", amrita},
            {"tpo@kctcbe.ac.in",         "Prof. E. Balasubramanian",  "+91 9842200025", kct},
            {"tpo2@nitt.edu",            "Dr. N. Venkatesh",          "+91 9842200026", nit},
            {"tpo2@manipal.edu",         "Prof. O. Shetty",           "+91 9842200027", manipal},
            {"tpo@mitmangalore.edu",     "Dr. Q. Rao",                "+91 9842200028", mit},
            {"tpo@bitsathy.ac.in",       "Dr. S. BIT Placement Officer", "+91 9842200029", bit}
        };
        for (Object[] entry : tpoEntries) {
            getOrSaveUser((String)entry[0], "Tpo@2025", Role.TPO, (String)entry[1], (String)entry[2], (College)entry[3]);
        }

        // ===== 7. ADDITIONAL RECRUITERS (47 more = 50 total) =====
        Company[] rCompanies = {wipro, hcl, cognizant, accenture, amazon, google, microsoft, deloitte,
            capgemini, ibm, oracle, samsung, bosch, adobe, qualcomm, intuit, flipkart, swiggy,
            zomato, ola, paytm, phonepe, razorpay, freshworks, chargebee, mindtree, mphasis,
            hexaware, zensar, niit, cyient, ltts, persistent, techmahindra, kpmg, pwc, ernst,
            thoughtworks, citi, jpmorgan, goldman, bny, sasken, tataelxsi, mediaspark, kaleidofin, zoho};
        String[][] recruiterEntries = {
            {"recruiter.wipro@wipro.com",               "Arjun Nair",             "+91 9843300001", "Campus HR Head"},
            {"recruiter.hcl@hcltech.com",               "Sunitha Rao",            "+91 9843300002", "Talent Acquisition"},
            {"recruiter.cognizant@cognizant.com",       "Venkat Ramesh",          "+91 9843300003", "University Relations"},
            {"recruiter.accenture@accenture.com",       "Neha Gupta",             "+91 9843300004", "Campus Lead"},
            {"recruiter.amazon@amazon.in",              "Rohit Sharma",           "+91 9843300005", "SDE Recruiter"},
            {"recruiter.google@google.com",             "Preethi Nair",           "+91 9843300006", "University Programs"},
            {"recruiter.microsoft@microsoft.com",       "Aakash Mehta",           "+91 9843300007", "Campus Recruiter"},
            {"recruiter.deloitte@deloitte.com",         "Kavitha Suresh",         "+91 9843300008", "Talent Consultant"},
            {"recruiter.capgemini@capgemini.com",       "Suresh Babu",            "+91 9843300009", "Campus HR"},
            {"recruiter.ibm@ibm.com",                   "Meera Krishnan",         "+91 9843300010", "Technical Recruiter"},
            {"recruiter.oracle@oracle.com",             "Dinesh Patel",           "+91 9843300011", "Campus Specialist"},
            {"recruiter.samsung@samsung.com",           "Shalini Iyer",           "+91 9843300012", "R&D Recruiter"},
            {"recruiter.bosch@bosch.com",               "Karthik Rajan",          "+91 9843300013", "Engineering Talent"},
            {"recruiter.adobe@adobe.com",               "Anjali Mishra",          "+91 9843300014", "Product Recruiter"},
            {"recruiter.qualcomm@qualcomm.com",         "Naveen Kumar",           "+91 9843300015", "VLSI Recruiter"},
            {"recruiter.intuit@intuit.com",             "Ritu Singh",             "+91 9843300016", "FinTech Talent"},
            {"recruiter.flipkart@flipkart.com",         "Arun Balaji",            "+91 9843300017", "SDE Campus Lead"},
            {"recruiter.swiggy@swiggy.com",             "Deepika Menon",          "+91 9843300018", "Tech Talent"},
            {"recruiter.zomato@zomato.com",             "Vikram Reddy",           "+91 9843300019", "Campus Recruiter"},
            {"recruiter.ola@olaelectric.com",           "Priya Venkat",           "+91 9843300020", "EV Tech Recruiter"},
            {"recruiter.paytm@paytm.com",               "Sandeep Joshi",          "+91 9843300021", "FinTech Campus"},
            {"recruiter.phonepe@phonepe.com",           "Lakshmi Rao",            "+91 9843300022", "Payments Tech"},
            {"recruiter.razorpay@razorpay.com",         "Harish Kumar",           "+91 9843300023", "Campus Lead"},
            {"recruiter.freshworks@freshworks.com",     "Sowmya Parthasarathy",   "+91 9843300024", "Product Recruiter"},
            {"recruiter.chargebee@chargebee.com",       "Balachandran S",         "+91 9843300025", "SaaS Talent"},
            {"recruiter.mindtree@ltimindtree.com",      "Anitha Srinivasan",      "+91 9843300026", "Digital Talent"},
            {"recruiter.mphasis@mphasis.com",           "Ganesh Subramanian",     "+91 9843300027", "IT Recruiter"},
            {"recruiter.hexaware@hexaware.com",         "Priya Chandrasekaran",   "+91 9843300028", "BPO Talent"},
            {"recruiter.zensar@zensar.com",             "Vijay Kulkarni",         "+91 9843300029", "Digital Recruiter"},
            {"recruiter.niit@niit-tech.com",            "Sneha Patil",            "+91 9843300030", "IT Campus"},
            {"recruiter.cyient@cyient.com",             "Mahesh Rao",             "+91 9843300031", "Engineering Recruiter"},
            {"recruiter.ltts@ltts.com",                 "Divya Sharma",           "+91 9843300032", "R&D Talent"},
            {"recruiter.persistent@persistent.com",     "Amit Joshi",             "+91 9843300033", "Cloud Recruiter"},
            {"recruiter.techmahindra@techmahindra.com", "Rekha Pillai",           "+91 9843300034", "Telecom Talent"},
            {"recruiter.kpmg@kpmg.com",                 "Sunil Mehta",            "+91 9843300035", "Advisory Recruiter"},
            {"recruiter.pwc@pwc.com",                   "Nisha Kapoor",           "+91 9843300036", "Consulting Talent"},
            {"recruiter.ey@ey.com",                     "Raghav Sridharan",       "+91 9843300037", "Assurance Recruiter"},
            {"recruiter.thoughtworks@thoughtworks.com", "Kiran Oommen",           "+91 9843300038", "Agile Coach"},
            {"recruiter.citi@citi.com",                 "Pooja Agarwal",          "+91 9843300039", "Banking Tech"},
            {"recruiter.jpmorgan@jpmorgan.com",         "Srinivas Reddy",         "+91 9843300040", "IB Tech Campus"},
            {"recruiter.goldman@goldmansachs.com",      "Madhavi Krishnamurthy",  "+91 9843300041", "Strats Recruiter"},
            {"recruiter.bny@bnymellon.com",             "Jayaprakash Nair",       "+91 9843300042", "FS Tech Talent"},
            {"recruiter.sasken@sasken.com",             "Balaji Venkataraman",    "+91 9843300043", "Embedded Talent"},
            {"recruiter.tataelxsi@tataelxsi.com",       "Swathi Nair",            "+91 9843300044", "Design Tech"},
            {"recruiter.mediaspark@mediaspark.io",      "Chitra Balakumar",       "+91 9843300045", "AdTech Recruiter"},
            {"recruiter.kaleidofin@kaleidofin.com",     "Dinesh Arumugam",        "+91 9843300046", "Rural FinTech"},
            {"recruiter2.zoho@zoho.com",                "Karthiga Mohan",         "+91 9843300047", "Product Recruiter"}
        };
        for (int i = 0; i < recruiterEntries.length; i++) {
            User ru = getOrSaveUser(recruiterEntries[i][0], "Recruiter@2025", Role.RECRUITER,
                    recruiterEntries[i][1], recruiterEntries[i][2], null);
            getOrSaveRecruiter(ru, rCompanies[i % rCompanies.length], recruiterEntries[i][3]);
        }

        // ===== 8. BULK 300 STUDENTS =====
        String[] firstNames = {"Aakash","Abirami","Aditi","Ajay","Akash","Akila","Akshay","Amith","Ananya",
            "Anbuchelvan","Anjali","Anand","Aravind","Arjun","Arulmurugan","Arun","Athira","Bala","Balaji",
            "Bhavana","Charan","Chandrika","Deepak","Deepika","Devaraj","Dharani","Dhivya","Dilip","Dinesh",
            "Divya","Elavarasi","Elakkiya","Gokul","Gokulnath","Gowtham","Guru","Gurubaran","Harini","Harisankar",
            "Hemavathi","Ilakkiya","Indira","Jayakumar","Janani","Karthik","Kavitha","Keerthana","Kishore",
            "Kokila","Krishnapriya","Kumaresan","Lakshmi","Lavanya","Madhan","Maheswari","Manikandan","Meena",
            "Meenakshi","Mohamed","Monisha","Murali","Nandakumar","Nandha","Nandhini","Narendran","Naveen",
            "Navya","Nithya","Oviya","Padma","Palani","Parvathi","Pavithra","Ponmalar","Pooja","Pradeep",
            "Prasanth","Preethi","Priya","Priyadarshini","Punithan","Ragavendar","Ragul","Rahul","Raja",
            "Rajesh","Ramesh","Ranjith","Ravi","Revathi","Rohini","Sabari","Sakthi","Sanjay","Saranya",
            "Saravanan","Satheesh","Selvaraj","Senthil","Shakthi","Shanmugapriya","Siva","Sivanesh","Sowmiya",
            "Sree","Sudha","Suganth","Sundar","Suresh","Suriya","Swetha","Tamil","Tamilarasi","Tharani",
            "Thirumala","Udhaya","Uma","Umarani","Vaishnavi","Vasanth","Venkat","Vijayalakshmi","Vinoth",
            "Viswanath","Yamuna","Yuva"};
        String[] lastNames = {"Chandrasekaran","Gopalakrishnan","Muthukrishnan","Palanisamy","Rajakumar",
            "Ramachandran","Ramamoorthy","Selvaraj","Shanmugam","Soundararajan","Subramaniam","Sundarajan",
            "Thangavel","Thirumoorthy","Venkatesan","Vijayakumar","Arumugam","Balamurugan","Balasundaram",
            "Karuppiah","Kumaresan","Manickam","Murugan","Natarajan","Pandian","Periyasamy","Pillai",
            "Prabhu","Raj","Rajagopal"};
        String[] depts    = {"CSE","IT","ECE","EEE","MECH","AIDS","CIVIL","CHEM"};
        String[] years    = {"1st Year","2nd Year","3rd Year","Final Year"};
        String[] skillSets= {"Java, Spring Boot, SQL, React",
            "Python, ML, TensorFlow, SQL",
            "C++, Data Structures, Algorithms",
            "JavaScript, React, Node.js, MongoDB",
            "Embedded C, RTOS, Linux",
            "AutoCAD, SolidWorks, MATLAB",
            "Java, Android, Kotlin, Firebase",
            "Python, Django, REST API, PostgreSQL",
            "Networking, CCNA, Linux Administration",
            "Cloud (AWS), Docker, Kubernetes, CI/CD"};
        College[] sCols   = {gct,psg,vit,srm,amrita,kct,anna,nit,rvce,pesu,
            manipal,bits,iitm,coep,dtu,lpu,thapar,mit,ceg,vjti,bit};

        List<User> newUsers = new ArrayList<>();
        List<int[]> indices  = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            String rollNum = String.format("CB%04d", 1000 + i);
            if (studentRepository.existsByRollNumber(rollNum)) continue;
            String fn = firstNames[i % firstNames.length];
            String ln = lastNames[i % lastNames.length];
            String email = fn.toLowerCase() + "." + ln.toLowerCase() + i + "@campus.edu";
            email = email.replaceAll("[^a-z0-9.@]", "");
            if (userRepository.existsByEmail(email)) email = "cbstudent" + i + "@campus.edu";
            College col = sCols[i % sCols.length];
            String phone = String.format("+91 98765%05d", 10000 + i);
            User u = new User(email, passwordEncoder.encode("Student@2025"), Role.STUDENT, fn + " " + ln, phone, col);
            newUsers.add(u);
            indices.add(new int[]{i});
        }
        if (!newUsers.isEmpty()) {
            List<User> savedUsers = userRepository.saveAll(newUsers);
            List<Student> newStudents = new ArrayList<>();
            for (int idx = 0; idx < savedUsers.size(); idx++) {
                int i = indices.get(idx)[0];
                String rollNum = String.format("CB%04d", 1000 + i);
                College col = sCols[i % sCols.length];
                String dept = depts[i % depts.length];
                String year = years[i % years.length];
                int gradYear = year.equals("Final Year") ? 2025 : (year.equals("3rd Year") ? 2026 : 2027);
                double cgpa = Math.round((6.0 + ((i % 40) * 0.1)) * 10.0) / 10.0;
                int backlogs = i % 10 < 7 ? 0 : (i % 3);
                String skills = skillSets[i % skillSets.length];
                PlacementStatus ps = i % 5 == 0 ? PlacementStatus.PLACED : PlacementStatus.UNPLACED;
                Student st = new Student(savedUsers.get(idx), col, rollNum, dept, year, gradYear, cgpa, backlogs, skills);
                st.setPlacementStatus(ps);
                newStudents.add(st);
            }
            studentRepository.saveAll(newStudents);
        }

        // ===== 9. PLACEMENT DRIVES (only if none exist) =====
        if (driveRepository.count() == 0) {
            PlacementDrive d1 = new PlacementDrive();
            d1.setCompany(zoho); d1.setCollege(gct);
            d1.setJobTitle("Member Technical Staff (MTS)");
            d1.setJobDescription("Develop high-scale cloud applications and microservices using Java, C++, and modern web technologies.");
            d1.setPackageLpa(8.5); d1.setLocation("Chennai"); d1.setWorkMode(WorkMode.ON_SITE); d1.setVacancies(25);
            d1.setEmploymentType(EmploymentType.FULL_TIME);
            d1.setApplicationDeadline(LocalDateTime.now().plusDays(14)); d1.setDriveDate(LocalDate.now().plusDays(20));
            d1.setStatus(DriveStatus.ACTIVE); d1.setTpoApprovalNotes("Approved by TPO Dr. K. Rangarajan for CSE, IT, ECE."); d1.setCreatedBy(zohoUser);
            driveRepository.save(d1);
            DriveEligibilityCriteria c1 = new DriveEligibilityCriteria(d1, "CSE,IT,ECE", 7.5, 0, 2026, "Java, Data Structures, Algorithms, SQL");
            criteriaRepository.save(c1);
            RecruitmentStage s1_1 = new RecruitmentStage(d1, 1, "Aptitude & Basic Programming", StageType.APTITUDE, "Quantitative aptitude and basic C/Java logic", false);
            RecruitmentStage s1_2 = new RecruitmentStage(d1, 2, "Advanced Coding Challenge", StageType.CODING, "Hands-on data structures and problem solving", false);
            RecruitmentStage s1_3 = new RecruitmentStage(d1, 3, "Technical Round 1", StageType.TECHNICAL_INTERVIEW, "System design, core CS, and live coding", false);
            RecruitmentStage s1_4 = new RecruitmentStage(d1, 4, "HR & Cultural Fit", StageType.HR_INTERVIEW, "Personal interview and role discussion", true);
            stageRepository.saveAll(Arrays.asList(s1_1, s1_2, s1_3, s1_4));

            PlacementDrive d2 = new PlacementDrive();
            d2.setCompany(tcs); d2.setCollege(gct);
            d2.setJobTitle("Digital Specialist Engineer");
            d2.setJobDescription("Work on cutting-edge technologies including Cloud, AI/ML, Cyber Security, and Big Data Analytics.");
            d2.setPackageLpa(7.0); d2.setLocation("Chennai / Bangalore"); d2.setWorkMode(WorkMode.HYBRID); d2.setVacancies(50);
            d2.setEmploymentType(EmploymentType.FULL_TIME);
            d2.setApplicationDeadline(LocalDateTime.now().plusDays(10)); d2.setDriveDate(LocalDate.now().plusDays(15));
            d2.setStatus(DriveStatus.ACTIVE); d2.setTpoApprovalNotes("Approved for all engineering branches meeting 7.0 CGPA."); d2.setCreatedBy(tcsUser);
            driveRepository.save(d2);
            DriveEligibilityCriteria c2 = new DriveEligibilityCriteria(d2, "CSE,IT,ECE,EEE,MECH,AIDS", 7.0, 1, 2026, "Java, Python, SQL");
            criteriaRepository.save(c2);
            RecruitmentStage s2_1 = new RecruitmentStage(d2, 1, "TCS NQT / Digital Assessment", StageType.APTITUDE, "Advanced cognitive and hands-on coding", false);
            RecruitmentStage s2_2 = new RecruitmentStage(d2, 2, "Technical Interview", StageType.TECHNICAL_INTERVIEW, "Tech panel interview on project and fundamentals", false);
            RecruitmentStage s2_3 = new RecruitmentStage(d2, 3, "Managerial & HR Round", StageType.HR_INTERVIEW, "Leadership, adaptability, and background checks", true);
            stageRepository.saveAll(Arrays.asList(s2_1, s2_2, s2_3));

            PlacementDrive d3 = new PlacementDrive();
            d3.setCompany(infosys); d3.setCollege(gct);
            d3.setJobTitle("Specialist Programmer (SP)");
            d3.setJobDescription("High-impact algorithmic engineering role focused on enterprise digital architecture.");
            d3.setPackageLpa(9.5); d3.setLocation("Bangalore"); d3.setWorkMode(WorkMode.ON_SITE); d3.setVacancies(15);
            d3.setEmploymentType(EmploymentType.FULL_TIME);
            d3.setApplicationDeadline(LocalDateTime.now().plusDays(7)); d3.setDriveDate(LocalDate.now().plusDays(12));
            d3.setStatus(DriveStatus.ACTIVE); d3.setTpoApprovalNotes("Approved high-CTC drive."); d3.setCreatedBy(infyUser);
            driveRepository.save(d3);
            DriveEligibilityCriteria c3 = new DriveEligibilityCriteria(d3, "CSE,IT,AIDS", 8.0, 0, 2026, "Data Structures, Algorithms, Python, Java");
            criteriaRepository.save(c3);
            RecruitmentStage s3_1 = new RecruitmentStage(d3, 1, "HackWithInfy / Coding Round", StageType.CODING, "Algorithmic programming", false);
            RecruitmentStage s3_2 = new RecruitmentStage(d3, 2, "Technical Discussion", StageType.TECHNICAL_INTERVIEW, "In-depth code walkthrough", false);
            RecruitmentStage s3_3 = new RecruitmentStage(d3, 3, "HR Discussion", StageType.HR_INTERVIEW, "Compensation and joining formalization", true);
            stageRepository.saveAll(Arrays.asList(s3_1, s3_2, s3_3));

            PlacementDrive d4 = new PlacementDrive();
            d4.setCompany(zoho); d4.setCollege(gct);
            d4.setJobTitle("Associate Quality Assurance Engineer");
            d4.setJobDescription("Ensure world-class product quality through test automation and CI/CD pipeline testing.");
            d4.setPackageLpa(5.5); d4.setLocation("Chennai"); d4.setWorkMode(WorkMode.ON_SITE); d4.setVacancies(20);
            d4.setEmploymentType(EmploymentType.FULL_TIME);
            d4.setApplicationDeadline(LocalDateTime.now().plusDays(18)); d4.setDriveDate(LocalDate.now().plusDays(25));
            d4.setStatus(DriveStatus.PENDING_APPROVAL); d4.setCreatedBy(zohoUser);
            driveRepository.save(d4);
            criteriaRepository.save(new DriveEligibilityCriteria(d4, "CSE,IT,ECE,EEE", 6.5, 2, 2026, "Testing, Java, Python, SQL"));

            if (s1 != null && s2 != null) {
                Application app1 = new Application(d1, s1, ApplicationStatus.INTERVIEW, s1_3);
                Application app2 = new Application(d2, s1, ApplicationStatus.INTERVIEW, s2_2);
                Application app3 = new Application(d1, s2, ApplicationStatus.INTERVIEW, s1_4);
                Application app4 = new Application(d3, s2, ApplicationStatus.SELECTED, s3_3);
                applicationRepository.saveAll(Arrays.asList(app1, app2, app3, app4));

                appHistoryRepository.save(new ApplicationStatusHistory(app1, "INTERVIEW", "Technical Round 1", "Cleared coding round with 92% score", "Priya Sharma"));
                appHistoryRepository.save(new ApplicationStatusHistory(app4, "SELECTED", "HR Discussion", "Selected for Specialist Programmer role at 9.5 LPA", "Ananya Sen"));

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

                LocalDate demoDate = LocalDate.now().plusDays(2);
                Interview iZoho = new Interview();
                iZoho.setDrive(d1); iZoho.setStage(s1_3); iZoho.setStudent(s1);
                iZoho.setInterviewDate(demoDate); iZoho.setStartTime(LocalTime.of(10,0)); iZoho.setEndTime(LocalTime.of(11,0));
                iZoho.setMode("ONLINE"); iZoho.setMeetingLink("https://meet.zoho.com/join/cb-zoho-tech-22cs014");
                iZoho.setPanel(zohoPanel); iZoho.setStatus(InterviewStatus.SCHEDULED);
                iZoho.setInstructions("Please have your IDE ready with Java 17/21 installed.");
                iZoho.setRequiredDocuments("College ID Card, Resume, GitHub Profile");
                interviewRepository.save(iZoho);

                Interview iTcs = new Interview();
                iTcs.setDrive(d2); iTcs.setStage(s2_2); iTcs.setStudent(s1);
                iTcs.setInterviewDate(demoDate); iTcs.setStartTime(LocalTime.of(10,30)); iTcs.setEndTime(LocalTime.of(11,30));
                iTcs.setMode("ONLINE"); iTcs.setMeetingLink("https://teams.microsoft.com/l/meetup-join/cb-tcs-digital-22cs014");
                iTcs.setPanel(tcsPanel); iTcs.setStatus(InterviewStatus.RESCHEDULE_REQUESTED);
                iTcs.setInstructions("Be prepared for domain questions on Cloud Architecture & Data Structures.");
                iTcs.setRequiredDocuments("College ID, Grade Sheets, Resume");
                interviewRepository.save(iTcs);

                RescheduleRequest reschedule = new RescheduleRequest(iTcs, s1,
                    "Overlapping interview conflict: Zoho MTS Technical Interview is scheduled on the same date at 10:00 - 11:00 AM.",
                    "Same date afternoon after 02:00 PM or next day morning");
                reschedule.setStatus(RescheduleStatus.PENDING);
                rescheduleRepository.save(reschedule);

                Offer offerKavya = new Offer(app4, s2, infosys, d3, "Specialist Programmer", 9.5, "Bangalore", LocalDate.now().plusMonths(8));
                offerKavya.setOfferStatus(OfferStatus.ACCEPTED);
                offerKavya.setOfferLetterUrl("/documents/Offer_Infosys_22IT028.pdf");
                offerRepository.save(offerKavya);
            }
        }

        // ===== 10. ANNOUNCEMENTS =====
        if (announcementRepository.count() == 0) {
            announcementRepository.save(new Announcement(gct,
                "Naan Mudhalvan Placement Drive & Industry Connect Initiative",
                "Under the Naan Mudhalvan initiative, unified multi-company recruitment drive coordinators have partnered with top tech companies including Zoho, TCS, and Infosys.",
                TargetAudience.ALL, null, null, "Dr. K. Rangarajan (TPO)"));
            announcementRepository.save(new Announcement(gct,
                "Mandatory Resume & CGPA Verification for S5 / Final Year Batches",
                "All registered students are requested to verify their uploaded resumes and CGPA records in the CampusBridge portal before applying to upcoming product drives.",
                TargetAudience.STUDENTS, "CSE,IT,ECE", null, "Placement Coordinator Office"));
        }

        // ===== 11. COMPANY PLACEMENT HISTORY =====
        if (companyHistoryRepository.count() == 0) {
            companyHistoryRepository.saveAll(Arrays.asList(
                new CompanyPlacementHistory(zoho, gct, "2024-2025", 85, 38, 18, "MTS, QA Engineer", 8.2, 8.5),
                new CompanyPlacementHistory(zoho, gct, "2023-2024", 78, 32, 14, "MTS", 7.8, 8.0),
                new CompanyPlacementHistory(zoho, psg, "2024-2025", 110, 52, 26, "MTS, Product Developer", 8.8, 12.0),
                new CompanyPlacementHistory(zoho, mit, "2024-2025", 95, 44, 21, "MTS", 8.4, 9.5),
                new CompanyPlacementHistory(zoho, bit, "2024-2025", 92, 46, 22, "MTS, Product Developer", 8.6, 11.5),
                new CompanyPlacementHistory(tcs, gct, "2024-2025", 140, 75, 42, "Digital, Ninja", 6.8, 7.5),
                new CompanyPlacementHistory(tcs, gct, "2023-2024", 130, 68, 38, "Digital, Ninja", 6.5, 7.0),
                new CompanyPlacementHistory(tcs, bit, "2024-2025", 135, 72, 40, "Digital, Ninja", 6.8, 7.5),
                new CompanyPlacementHistory(infosys, gct, "2024-2025", 120, 48, 22, "Specialist Programmer, SE", 8.1, 9.5),
                new CompanyPlacementHistory(infosys, psg, "2024-2025", 135, 60, 31, "SP, DSE", 8.6, 9.5),
                new CompanyPlacementHistory(infosys, bit, "2024-2025", 115, 50, 24, "Specialist Programmer, SE", 8.0, 9.5)
            ));
        }

        System.out.println(">>> CampusBridge data initialization complete!");
    }

    // ===== HELPER METHODS =====
    private College getOrSaveCollege(String name, String code, String city, String state, String email, String phone) {
        return collegeRepository.findByCode(code)
            .or(() -> collegeRepository.findByName(name))
            .orElseGet(() -> collegeRepository.save(new College(name, code, city, state, email, phone)));
    }

    private Company getOrSaveCompany(String name, String website, String industry, String city, String state) {
        return companyRepository.findByName(name)
            .orElseGet(() -> {
                String cleaned = name.toLowerCase().replaceAll("[^a-z0-9]", "");
                String emailLocal = cleaned.substring(0, Math.min(cleaned.length(), 20));
                Company c = new Company(name, website, industry, name + " - " + industry,
                    city, state, "HR Team",
                    "hr@" + emailLocal + ".com",
                    "+91 80 00000000");
                c.setVerified(true);
                return companyRepository.save(c);
            });
    }

    private User getOrSaveUser(String email, String rawPwd, Role role, String fullName, String phone, College college) {
        return userRepository.findByEmail(email)
            .orElseGet(() -> userRepository.save(
                new User(email, passwordEncoder.encode(rawPwd), role, fullName, phone, college)));
    }

    private void getOrSaveRecruiter(User user, Company company, String designation) {
        if (!recruiterRepository.existsByUserId(user.getId())) {
            recruiterRepository.save(new Recruiter(user, company, designation));
        }
    }

    private void ensureDepartments(College college) {
        if (departmentRepository.findByCollegeId(college.getId()).isEmpty()) {
            departmentRepository.saveAll(Arrays.asList(
                new Department(college, "Computer Science & Engineering", "CSE"),
                new Department(college, "Information Technology", "IT"),
                new Department(college, "Electronics & Communication Engineering", "ECE"),
                new Department(college, "Electrical & Electronics Engineering", "EEE"),
                new Department(college, "Mechanical Engineering", "MECH"),
                new Department(college, "Artificial Intelligence & Data Science", "AIDS")
            ));
        }
    }

    private void ensureAllRegionalColleges() {
        String[][] collegesData = {
            // Erode Region
            {"Bannari Amman Institute of Technology", "BIT-SAT", "Sathyamangalam", "Tamil Nadu", "placement@bitsathy.ac.in", "+91 4295 226000"},
            {"Kongu Engineering College", "KEC-PER", "Perundurai", "Tamil Nadu", "placement@kongu.edu", "+91 4294 226555"},
            {"Velalar College of Engineering and Technology", "VCET-ERD", "Erode", "Tamil Nadu", "placement@velalarengg.ac.in", "+91 424 2244201"},
            {"Nandha Engineering College", "NEC-ERD", "Erode", "Tamil Nadu", "placement@nandhaengg.org", "+91 4294 225585"},
            {"Nandha College of Technology", "NCT-ERD", "Erode", "Tamil Nadu", "placement@nandhatech.org", "+91 4294 223434"},
            {"Erode Sengunthar Engineering College", "ESEC-ERD", "Thudupathi", "Tamil Nadu", "placement@erodesengunthar.ac.in", "+91 4294 232701"},
            {"Al-Ameen Engineering College", "AEC-ERD", "Erode", "Tamil Nadu", "placement@alameen.ac.in", "+91 424 2500354"},
            {"M.P. Nachimuthu M. Jaganathan Engineering College", "MPNMJ-ERD", "Erode", "Tamil Nadu", "placement@mpnmjec.ac.in", "+91 424 2357009"},
            {"Surya Engineering College", "SEC-ERD", "Erode", "Tamil Nadu", "placement@surya.ac.in", "+91 424 2555018"},

            // Coimbatore Region
            {"Government College of Technology", "GCT-CBE", "Coimbatore", "Tamil Nadu", "tpo@gct.ac.in", "+91 422 2432221"},
            {"PSG College of Technology", "PSG-CBE", "Coimbatore", "Tamil Nadu", "placement@psgtech.edu", "+91 422 4344777"},
            {"Kumaraguru College of Technology", "KCT-CBE", "Coimbatore", "Tamil Nadu", "placement@kct.ac.in", "+91 422 2669401"},
            {"Coimbatore Institute of Technology", "CIT-CBE", "Coimbatore", "Tamil Nadu", "placement@cit.edu.in", "+91 422 2574071"},
            {"PSG Institute of Technology and Applied Research", "PSG-ITECH", "Coimbatore", "Tamil Nadu", "placement@psgitech.ac.in", "+91 422 3933666"},
            {"Sri Krishna College of Engineering and Technology", "SKCET-CBE", "Coimbatore", "Tamil Nadu", "placement@skcet.ac.in", "+91 422 2678001"},
            {"Sri Krishna College of Technology", "SKCT-CBE", "Coimbatore", "Tamil Nadu", "placement@skct.edu.in", "+91 422 2604567"},
            {"Sri Ramakrishna Engineering College", "SREC-CBE", "Coimbatore", "Tamil Nadu", "placement@srec.ac.in", "+91 422 2460088"},
            {"Sri Ramakrishna Institute of Technology", "SRIT-CBE", "Coimbatore", "Tamil Nadu", "placement@srit.org", "+91 422 2605577"},
            {"Dr. Mahalingam College of Engineering and Technology", "MCET-PLM", "Pollachi", "Tamil Nadu", "placement@drmcet.ac.in", "+91 4259 236030"},
            {"Karpagam College of Engineering", "KCE-CBE", "Coimbatore", "Tamil Nadu", "placement@kce.ac.in", "+91 422 2619047"},
            {"Hindusthan College of Engineering and Technology", "HICET-CBE", "Coimbatore", "Tamil Nadu", "placement@hindusthan.net", "+91 422 2930216"},
            {"SNS College of Technology", "SNS-CBE", "Coimbatore", "Tamil Nadu", "placement@snsct.org", "+91 422 2666264"},
            {"Rathinam Technical Campus", "RTC-CBE", "Coimbatore", "Tamil Nadu", "placement@rathinam.in", "+91 422 4040909"},
            {"Coimbatore Institute of Engineering and Technology", "CIET-CBE", "Coimbatore", "Tamil Nadu", "placement@cietcbe.edu.in", "+91 422 2651188"},
            {"KPR Institute of Engineering and Technology", "KPRIET-CBE", "Coimbatore", "Tamil Nadu", "placement@kpriet.ac.in", "+91 422 2635600"},
            {"Sri Shakthi Institute of Engineering and Technology", "SIET-CBE", "Coimbatore", "Tamil Nadu", "placement@siet.ac.in", "+91 422 2383838"},
            {"Amrita Vishwa Vidyapeetham", "AMRITA", "Coimbatore", "Tamil Nadu", "placement@amrita.edu", "+91 422 2685000"},

            // Chennai Region
            {"College of Engineering Guindy", "CEG-CHN", "Chennai", "Tamil Nadu", "placement@ceg.annauniv.edu", "+91 44 22200200"},
            {"Madras Institute of Technology", "MIT-CHN", "Chennai", "Tamil Nadu", "placement@mitindia.edu", "+91 44 22516000"},
            {"Anna University", "AU-CHN", "Chennai", "Tamil Nadu", "placement@annauniv.edu", "+91 44 22358000"},
            {"IIT Madras", "IITM-CHN", "Chennai", "Tamil Nadu", "placement@iitm.ac.in", "+91 44 22578000"},
            {"SSN College of Engineering", "SSN-CHN", "Chennai", "Tamil Nadu", "placement@ssn.edu.in", "+91 44 27469700"},
            {"Sri Venkateswara College of Engineering", "SVCE-CHN", "Sriperumbudur", "Tamil Nadu", "placement@svce.ac.in", "+91 44 27152000"},
            {"Rajalakshmi Engineering College", "REC-CHN", "Chennai", "Tamil Nadu", "placement@rajalakshmi.edu.in", "+91 44 27156750"},
            {"Rajalakshmi Institute of Technology", "RIT-CHN", "Chennai", "Tamil Nadu", "placement@ritchennai.edu.in", "+91 44 67181600"},
            {"Chennai Institute of Technology", "CIT-CHN", "Chennai", "Tamil Nadu", "placement@citchennai.net", "+91 44 71119111"},
            {"St. Joseph's College of Engineering", "SJCE-CHN", "Chennai", "Tamil Nadu", "placement@stjosephs.ac.in", "+91 44 24501060"},
            {"St. Joseph's Institute of Technology", "SJIT-CHN", "Chennai", "Tamil Nadu", "placement@stjosefstechnology.ac.in", "+91 44 24503132"},
            {"Panimalar Engineering College", "PEC-CHN", "Chennai", "Tamil Nadu", "placement@panimalar.ac.in", "+91 44 26490404"},
            {"RMK Engineering College", "RMK-CHN", "Thiruvallur", "Tamil Nadu", "placement@rmkec.ac.in", "+91 44 67906790"},
            {"RMD Engineering College", "RMD-CHN", "Thiruvallur", "Tamil Nadu", "placement@rmd.ac.in", "+91 44 67919191"},
            {"Sri Sairam Engineering College", "SAIRAM-CHN", "Chennai", "Tamil Nadu", "placement@sairam.edu.in", "+91 44 22512222"},
            {"Easwari Engineering College", "EEC-CHN", "Chennai", "Tamil Nadu", "placement@eec.srmrmp.edu.in", "+91 44 43923041"},
            {"Meenakshi Sundararajan Engineering College", "MSEC-CHN", "Chennai", "Tamil Nadu", "placement@msec.edu.in", "+91 44 24801636"},
            {"Loyola-ICAM College of Engineering and Technology", "LICET-CHN", "Chennai", "Tamil Nadu", "placement@licet.ac.in", "+91 44 28178490"},
            {"Saveetha Engineering College", "SAV-CHN", "Chennai", "Tamil Nadu", "placement@saveetha.ac.in", "+91 44 66311322"},
            {"SRM Institute of Science and Technology", "SRM-KTR", "Kattankulathur", "Tamil Nadu", "placement@srmist.edu.in", "+91 44 27417000"},
            {"SRM Valliammai Engineering College", "VEC-KTR", "Kattankulathur", "Tamil Nadu", "placement@valliammai.co.in", "+91 44 27454784"}
        };

        for (String[] row : collegesData) {
            College c = getOrSaveCollege(row[0], row[1], row[2], row[3], row[4], row[5]);
            ensureDepartments(c);
        }
    }
}
