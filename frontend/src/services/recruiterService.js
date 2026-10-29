import apiClient from "./apiClient";

export const getRecruiterDashboard = async () => {
  const response = await apiClient.get("/recruiter/dashboard");
  return response.data;
};

export const getCompanyProfile = async () => {
  const response = await apiClient.get("/recruiter/company");
  return response.data;
};

export const updateCompanyProfile = async (data) => {
  // Map frontend field names to backend Company entity field names
  const payload = {
    id: data.id,
    name: data.name,
    industry: data.industry,
    description: data.description,
    website: data.website,
    // Frontend uses contactPerson/contactEmail/contactPhone,
    // but backend entity uses hrContactName/hrContactEmail/hrContactPhone
    hrContactName: data.contactPerson || data.hrContactName,
    hrContactEmail: data.contactEmail || data.hrContactEmail,
    hrContactPhone: data.contactPhone || data.hrContactPhone,
    city: data.location || data.city,
    state: data.state,
    address: data.address,
    logoUrl: data.logoUrl,
  };
  const response = await apiClient.put("/recruiter/company", payload);
  return response.data;
};

export const createDrive = async (driveData) => {
  // Transform frontend form shape to backend DriveCreateRequest DTO
  const payload = {
    jobTitle: driveData.title || driveData.jobTitle,
    jobDescription: driveData.description || driveData.jobDescription,
    packageLpa: driveData.salaryPackage || driveData.packageLpa,
    location: driveData.location,
    workMode: driveData.workMode || "ON_SITE",
    vacancies: driveData.vacancies,
    employmentType: driveData.employmentType || "FULL_TIME",
    applicationDeadline: driveData.applicationDeadline
      ? driveData.applicationDeadline.includes("T")
        ? driveData.applicationDeadline
        : driveData.applicationDeadline + "T23:59:59"
      : null,
    driveDate: driveData.driveDate || driveData.applicationDeadline || null,
    collegeId: driveData.collegeId || null,
    // Eligibility criteria (flatten from nested criteria object)
    allowedDepartments:
      driveData.criteria?.allowedDepartments ?? driveData.allowedDepartments ?? "",
    minCgpa: driveData.criteria?.minCgpa ?? driveData.minCgpa ?? 0,
    maxBacklogs: driveData.criteria?.maxBacklogs ?? driveData.maxBacklogs ?? 0,
    graduationYear:
      driveData.criteria?.graduationYear ?? driveData.graduationYear ?? new Date().getFullYear(),
    requiredSkills:
      driveData.criteria?.requiredSkills ?? driveData.requiredSkills ?? "",
    // Recruitment stages
    stages: (driveData.stages || []).map((s, idx) => ({
      stageOrder: s.roundOrder || s.stageOrder || idx + 1,
      stageName: s.stageName,
      stageType: s.stageType || "TECHNICAL_INTERVIEW",
      description: s.description || "",
      cutOffScore: s.cutOffScore || null,
      isFinalStage: s.isFinalStage || idx === (driveData.stages || []).length - 1,
    })),
  };
  const response = await apiClient.post("/recruiter/drives", payload);
  return response.data;
};

export const getRecruiterDrives = async () => {
  const response = await apiClient.get("/recruiter/drives");
  return response.data;
};

export const getDriveApplicants = async (driveId) => {
  const response = await apiClient.get(`/recruiter/drives/${driveId}/applicants`);
  return response.data;
};

export const updateApplicationStage = async (applicationId, stageData) => {
  // Backend uses @PostMapping with @RequestParam, not PUT with body
  const response = await apiClient.post(`/recruiter/applications/${applicationId}/stage`, null, {
    params: { status: stageData.status, remarks: stageData.remarks },
  });
  return response.data;
};

// NOTE: Backend endpoint for conflict check is not yet exposed as REST.
// This will be handled by the interview scheduling flow.
export const checkInterviewConflicts = async (checkData) => {
  const response = await apiClient.post("/recruiter/interviews/check-conflicts", checkData);
  return response.data;
};

export const scheduleInterview = async (interviewData) => {
  const response = await apiClient.post("/recruiter/interviews/schedule", interviewData);
  return response.data;
};

export const getScheduledInterviews = async () => {
  const response = await apiClient.get("/recruiter/interviews");
  return response.data;
};

export const submitEvaluation = async (evaluationData) => {
  const interviewId = evaluationData.interviewId;
  // Backend EvaluationRequest uses `comments` field, not `feedback`
  const payload = {
    technicalScore: evaluationData.technicalScore,
    communicationScore: evaluationData.communicationScore,
    problemSolvingScore: evaluationData.problemSolvingScore,
    overallRating: evaluationData.overallRating,
    decision: evaluationData.decision,
    comments: evaluationData.feedback || evaluationData.comments,
  };
  const response = await apiClient.post(
    `/recruiter/interviews/${interviewId}/evaluate`,
    payload
  );
  return response.data;
};

// NOTE: Backend GET endpoint for fetching evaluation by interview is not yet implemented
export const getInterviewEvaluation = async (interviewId) => {
  const response = await apiClient.get(`/recruiter/interviews/${interviewId}/evaluate`);
  return response.data;
};

export const createOffer = async (offerData) => {
  const response = await apiClient.post("/recruiter/offers", offerData);
  return response.data;
};

export const getRecruiterOffers = async () => {
  const response = await apiClient.get("/recruiter/offers");
  return response.data;
};

export const getPanels = async () => {
  const response = await apiClient.get("/recruiter/panels");
  return response.data;
};

export const createPanel = async (panelData) => {
  const payload = {
    name: panelData.name || panelData.panelName,
    panelName: panelData.panelName || panelData.name,
    description: panelData.description || "",
    interviewerIds: panelData.interviewerIds || [],
  };
  const response = await apiClient.post("/recruiter/panels", payload);
  return response.data;
};

export const getInterviewers = async () => {
  const response = await apiClient.get("/recruiter/interviewers");
  return response.data;
};

export const createInterviewer = async (interviewerData) => {
  const response = await apiClient.post("/recruiter/interviewers", interviewerData);
  return response.data;
};

export const getCollegeRecommendations = async (params) => {
  const response = await apiClient.get("/recruiter/recommendations/colleges", { params });
  return response.data;
};
