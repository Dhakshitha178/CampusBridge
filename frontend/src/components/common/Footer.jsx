import React from "react";

const Footer = () => {
  const year = new Date().getFullYear();
  return (
    <footer className="portal-footer">
      <div className="container d-flex flex-wrap justify-content-between align-items-center gap-2">
        <div>
          <strong>CampusBridge</strong> &mdash; Connecting Talent with Opportunity
        </div>
        <div className="text-secondary small">
          © {year} CampusBridge &bull; Placement &amp; Career Management Portal
        </div>
      </div>
    </footer>
  );
};

export default Footer;
