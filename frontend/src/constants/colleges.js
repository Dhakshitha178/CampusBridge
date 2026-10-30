// Master list of pre-configured institutions across Coimbatore, Erode, and Chennai regions.
// Bannari Amman Institute of Technology is included as a primary institution.
export const REGIONAL_COLLEGES = [
  // --- Erode Region ---
  { id: 40, name: "Bannari Amman Institute of Technology", code: "BIT-SAT", city: "Sathyamangalam", state: "Tamil Nadu" },
  { id: 55, name: "Kongu Engineering College", code: "KEC-PER", city: "Perundurai", state: "Tamil Nadu" },
  { id: 56, name: "Velalar College of Engineering and Technology", code: "VCET-ERD", city: "Erode", state: "Tamil Nadu" },
  { id: 57, name: "Nandha Engineering College", code: "NEC-ERD", city: "Erode", state: "Tamil Nadu" },
  { id: 58, name: "Nandha College of Technology", code: "NCT-ERD", city: "Erode", state: "Tamil Nadu" },
  { id: 59, name: "Erode Sengunthar Engineering College", code: "ESEC-ERD", city: "Thudupathi", state: "Tamil Nadu" },
  { id: 60, name: "Al-Ameen Engineering College", code: "AEC-ERD", city: "Erode", state: "Tamil Nadu" },
  { id: 61, name: "M.P. Nachimuthu M. Jaganathan Engineering College", code: "MPNMJ-ERD", city: "Erode", state: "Tamil Nadu" },
  { id: 62, name: "Surya Engineering College", code: "SEC-ERD", city: "Erode", state: "Tamil Nadu" },

  // --- Coimbatore Region ---
  { id: 1, name: "Government College of Technology", code: "GCT-CBE", city: "Coimbatore", state: "Tamil Nadu" },
  { id: 2, name: "PSG College of Technology", code: "PSG-CBE", city: "Coimbatore", state: "Tamil Nadu" },
  { id: 29, name: "Kumaraguru College of Technology", code: "KCT-CBE", city: "Coimbatore", state: "Tamil Nadu" },
  { id: 41, name: "Coimbatore Institute of Technology", code: "CIT-CBE", city: "Coimbatore", state: "Tamil Nadu" },
  { id: 42, name: "PSG Institute of Technology and Applied Research", code: "PSG-ITECH", city: "Coimbatore", state: "Tamil Nadu" },
  { id: 43, name: "Sri Krishna College of Engineering and Technology", code: "SKCET-CBE", city: "Coimbatore", state: "Tamil Nadu" },
  { id: 44, name: "Sri Krishna College of Technology", code: "SKCT-CBE", city: "Coimbatore", state: "Tamil Nadu" },
  { id: 45, name: "Sri Ramakrishna Engineering College", code: "SREC-CBE", city: "Coimbatore", state: "Tamil Nadu" },
  { id: 46, name: "Sri Ramakrishna Institute of Technology", code: "SRIT-CBE", city: "Coimbatore", state: "Tamil Nadu" },
  { id: 47, name: "Dr. Mahalingam College of Engineering and Technology", code: "MCET-PLM", city: "Pollachi", state: "Tamil Nadu" },
  { id: 48, name: "Karpagam College of Engineering", code: "KCE-CBE", city: "Coimbatore", state: "Tamil Nadu" },
  { id: 49, name: "Hindusthan College of Engineering and Technology", code: "HICET-CBE", city: "Coimbatore", state: "Tamil Nadu" },
  { id: 50, name: "SNS College of Technology", code: "SNS-CBE", city: "Coimbatore", state: "Tamil Nadu" },
  { id: 51, name: "Rathinam Technical Campus", code: "RTC-CBE", city: "Coimbatore", state: "Tamil Nadu" },
  { id: 52, name: "Coimbatore Institute of Engineering and Technology", code: "CIET-CBE", city: "Coimbatore", state: "Tamil Nadu" },
  { id: 53, name: "KPR Institute of Engineering and Technology", code: "KPRIET-CBE", city: "Coimbatore", state: "Tamil Nadu" },
  { id: 54, name: "Sri Shakthi Institute of Engineering and Technology", code: "SIET-CBE", city: "Coimbatore", state: "Tamil Nadu" },
  { id: 27, name: "Amrita Vishwa Vidyapeetham", code: "AMRITA", city: "Coimbatore", state: "Tamil Nadu" },

  // --- Chennai Region ---
  { id: 28, name: "College of Engineering Guindy", code: "CEG-CHN", city: "Chennai", state: "Tamil Nadu" },
  { id: 3, name: "Madras Institute of Technology", code: "MIT-CHN", city: "Chennai", state: "Tamil Nadu" },
  { id: 21, name: "Anna University", code: "AU-CHN", city: "Chennai", state: "Tamil Nadu" },
  { id: 25, name: "IIT Madras", code: "IITM-CHN", city: "Chennai", state: "Tamil Nadu" },
  { id: 63, name: "SSN College of Engineering", code: "SSN-CHN", city: "Chennai", state: "Tamil Nadu" },
  { id: 64, name: "Sri Venkateswara College of Engineering", code: "SVCE-CHN", city: "Sriperumbudur", state: "Tamil Nadu" },
  { id: 65, name: "Rajalakshmi Engineering College", code: "REC-CHN", city: "Chennai", state: "Tamil Nadu" },
  { id: 66, name: "Rajalakshmi Institute of Technology", code: "RIT-CHN", city: "Chennai", state: "Tamil Nadu" },
  { id: 67, name: "Chennai Institute of Technology", code: "CIT-CHN", city: "Chennai", state: "Tamil Nadu" },
  { id: 68, name: "St. Joseph's College of Engineering", code: "SJCE-CHN", city: "Chennai", state: "Tamil Nadu" },
  { id: 69, name: "St. Joseph's Institute of Technology", code: "SJIT-CHN", city: "Chennai", state: "Tamil Nadu" },
  { id: 70, name: "Panimalar Engineering College", code: "PEC-CHN", city: "Chennai", state: "Tamil Nadu" },
  { id: 71, name: "RMK Engineering College", code: "RMK-CHN", city: "Thiruvallur", state: "Tamil Nadu" },
  { id: 72, name: "RMD Engineering College", code: "RMD-CHN", city: "Thiruvallur", state: "Tamil Nadu" },
  { id: 73, name: "Sri Sairam Engineering College", code: "SAIRAM-CHN", city: "Chennai", state: "Tamil Nadu" },
  { id: 74, name: "Easwari Engineering College", code: "EEC-CHN", city: "Chennai", state: "Tamil Nadu" },
  { id: 75, name: "Meenakshi Sundararajan Engineering College", code: "MSEC-CHN", city: "Chennai", state: "Tamil Nadu" },
  { id: 76, name: "Loyola-ICAM College of Engineering and Technology", code: "LICET-CHN", city: "Chennai", state: "Tamil Nadu" },
  { id: 77, name: "Saveetha Engineering College", code: "SAV-CHN", city: "Chennai", state: "Tamil Nadu" },
  { id: 23, name: "SRM Institute of Science and Technology", code: "SRM-KTR", city: "Kattankulathur", state: "Tamil Nadu" },
  { id: 78, name: "SRM Valliammai Engineering College", code: "VEC-KTR", city: "Kattankulathur", state: "Tamil Nadu" }
];

/**
 * Merge API-fetched colleges with regional presets without duplicates.
 * Guarantees Bannari Amman Institute of Technology and all Coimbatore,
 * Erode, and Chennai colleges are always selectable.
 */
export const mergeColleges = (apiColleges = []) => {
  const map = new Map();

  // 1. Add regional defaults first
  REGIONAL_COLLEGES.forEach((c) => {
    const key = (c.name || "").trim().toLowerCase();
    map.set(key, { ...c });
  });

  // 2. Overlay / augment with API colleges (preserves database IDs)
  if (Array.isArray(apiColleges)) {
    apiColleges.forEach((c) => {
      if (!c || !c.name) return;
      const key = c.name.trim().toLowerCase();
      const existing = map.get(key);
      if (existing) {
        map.set(key, { ...existing, ...c });
      } else {
        map.set(key, { ...c });
      }
    });
  }

  // 3. Return sorted alphabetically by college name
  return Array.from(map.values()).sort((a, b) => a.name.localeCompare(b.name));
};
