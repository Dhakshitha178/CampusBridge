import React, { useState } from "react";

const AnnouncementForm = ({ onSubmit, loading = false }) => {
  const [formData, setFormData] = useState({
    title: "",
    content: "",
    targetAudience: "ALL",
    targetDepartment: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!formData.title || !formData.content) return;
    onSubmit(formData);
    setFormData({
      title: "",
      content: "",
      targetAudience: "ALL",
      targetDepartment: "",
    });
  };

  return (
    <form onSubmit={handleSubmit} className="p-3 bg-light rounded-3 border">
      <div className="mb-3">
        <label className="form-label fw-semibold small">Title</label>
        <input
          type="text"
          name="title"
          value={formData.title}
          onChange={handleChange}
          required
          placeholder="e.g., Campus Drive Alert"
          className="form-control"
        />
      </div>

      <div className="row g-2 mb-3">
        <div className="col-md-6">
          <label className="form-label fw-semibold small">Target Audience</label>
          <select
            name="targetAudience"
            value={formData.targetAudience}
            onChange={handleChange}
            className="form-select"
          >
            <option value="ALL">All Users</option>
            <option value="STUDENTS">Students Only</option>
            <option value="RECRUITERS">Recruiters Only</option>
            <option value="DEPARTMENT">Department Specific</option>
          </select>
        </div>
        {formData.targetAudience === "DEPARTMENT" && (
          <div className="col-md-6">
            <label className="form-label fw-semibold small">Department</label>
            <input
              type="text"
              name="targetDepartment"
              value={formData.targetDepartment}
              onChange={handleChange}
              placeholder="e.g., Computer Science"
              className="form-control"
              required
            />
          </div>
        )}
      </div>

      <div className="mb-3">
        <label className="form-label fw-semibold small">Announcement Content</label>
        <textarea
          name="content"
          value={formData.content}
          onChange={handleChange}
          required
          rows={4}
          placeholder="Detailed announcement details..."
          className="form-control"
        ></textarea>
      </div>

      <button type="submit" disabled={loading} className="btn btn-primary btn-sm">
        {loading ? "Publishing..." : "Publish Announcement"}
      </button>
    </form>
  );
};

export default AnnouncementForm;
