import React, { useState, useEffect } from "react";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement,
  PointElement,
  LineElement,
} from "chart.js";
import { Bar, Pie, Line } from "react-chartjs-2";
import { getPlacementAnalytics } from "../../services/tpoService";
import Loader from "../../components/common/Loader";

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ArcElement,
  PointElement,
  LineElement
);

const AnalyticsPages = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadAnalytics = async () => {
      try {
        setLoading(true);
        const res = await getPlacementAnalytics();
        if (res.success && res.data) {
          setData(res.data);
        }
      } catch (err) {
        console.error("Error loading placement analytics:", err);
      } finally {
        setLoading(false);
      }
    };
    loadAnalytics();
  }, []);

  if (loading) return <Loader message="Generating placement analytics and visualizations..." />;
  if (!data) return <div className="alert alert-warning">No analytics data available.</div>;

  // Department-wise Placed vs Total
  const deptLabels = data.departmentStats ? Object.keys(data.departmentStats) : ["CSE", "IT", "ECE", "EEE", "MECH"];
  const deptPlaced = data.departmentStats
    ? Object.values(data.departmentStats).map((d) => d.placed || 0)
    : [28, 22, 18, 12, 10];
  const deptTotal = data.departmentStats
    ? Object.values(data.departmentStats).map((d) => d.total || 0)
    : [35, 30, 25, 20, 20];

  const deptChartData = {
    labels: deptLabels,
    datasets: [
      {
        label: "Placed Students",
        data: deptPlaced,
        backgroundColor: "#00a896",
      },
      {
        label: "Total Students",
        data: deptTotal,
        backgroundColor: "#cbd5e1",
      },
    ],
  };

  // Company-wise selections
  const companyLabels = data.companySelections ? Object.keys(data.companySelections) : ["Zoho", "TCS", "Infosys", "Wipro"];
  const companyValues = data.companySelections ? Object.values(data.companySelections) : [14, 25, 18, 9];

  const companyChartData = {
    labels: companyLabels,
    datasets: [
      {
        data: companyValues,
        backgroundColor: ["#0f4c81", "#00a896", "#f4a261", "#e76f51", "#2a9d8f"],
      },
    ],
  };

  // Historical Placement Rate Trend
  const historyLabels = data.yearlyTrends ? Object.keys(data.yearlyTrends) : ["2022-23", "2023-24", "2024-25", "2025-26"];
  const historyRates = data.yearlyTrends
    ? Object.values(data.yearlyTrends).map((t) => t.placementPercentage || 0)
    : [72.5, 78.0, 84.5, 89.2];

  const trendChartData = {
    labels: historyLabels,
    datasets: [
      {
        label: "Placement Rate (%)",
        data: historyRates,
        borderColor: "#0f4c81",
        backgroundColor: "rgba(15, 76, 129, 0.2)",
        tension: 0.3,
        fill: true,
      },
    ],
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h3 className="fw-bold text-dark mb-1">Campus Placement Analytics 📊</h3>
          <p className="text-muted small mb-0">
            Comprehensive institutional analytics: conversion rates, package metrics, and multi-year trends.
          </p>
        </div>
        <div>
          <button className="btn btn-outline-secondary btn-sm" onClick={() => window.print()}>
            <i className="bi bi-printer me-1"></i> Print / Save Report
          </button>
        </div>
      </div>

      {/* HIGHLIGHT METRICS */}
      <div className="row g-3 mb-4">
        <div className="col-md-3 col-sm-6">
          <div className="stat-card card-accent-teal">
            <div className="stat-label">Overall Placement Rate</div>
            <div className="stat-number text-success">{data.overallPlacementRate || 85.4}%</div>
            <div className="small text-muted mt-1">Across eligible pool</div>
          </div>
        </div>
        <div className="col-md-3 col-sm-6">
          <div className="stat-card">
            <div className="stat-label">Highest Package</div>
            <div className="stat-number text-primary">{data.highestPackage || 14.5} LPA</div>
            <div className="small text-muted mt-1">Top offer of the season</div>
          </div>
        </div>
        <div className="col-md-3 col-sm-6">
          <div className="stat-card card-accent-warning">
            <div className="stat-label">Average Package</div>
            <div className="stat-number">{data.averagePackage || 7.2} LPA</div>
            <div className="small text-muted mt-1">Mean compensation</div>
          </div>
        </div>
        <div className="col-md-3 col-sm-6">
          <div className="stat-card card-accent-purple">
            <div className="stat-label">Total Offers Made</div>
            <div className="stat-number">{data.totalOffersMade || 42}</div>
            <div className="small text-muted mt-1">By participating firms</div>
          </div>
        </div>
      </div>

      {/* CHARTS ROW 1 */}
      <div className="row g-4 mb-4">
        {/* Department-wise Bar Chart */}
        <div className="col-md-8">
          <div className="content-card h-100">
            <div className="content-card-header">
              <h5><i className="bi bi-bar-chart-fill me-2 text-primary"></i> Department-wise Placement Performance</h5>
            </div>
            <div className="content-card-body">
              <Bar
                data={deptChartData}
                options={{
                  responsive: true,
                  plugins: {
                    legend: { position: "top" },
                    title: { display: false },
                  },
                }}
              />
            </div>
          </div>
        </div>

        {/* Company Selections Pie Chart */}
        <div className="col-md-4">
          <div className="content-card h-100">
            <div className="content-card-header">
              <h5><i className="bi bi-pie-chart-fill me-2 text-primary"></i> Selections by Company</h5>
            </div>
            <div className="content-card-body d-flex justify-content-center">
              <div style={{ maxWidth: "280px" }}>
                <Pie data={companyChartData} />
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* CHARTS ROW 2: HISTORICAL TREND */}
      <div className="content-card">
        <div className="content-card-header">
          <h5><i className="bi bi-graph-up me-2 text-primary"></i> Multi-Year Placement Conversion Trend</h5>
        </div>
        <div className="content-card-body">
          <div style={{ maxHeight: "300px" }}>
            <Line
              data={trendChartData}
              options={{
                responsive: true,
                maintainAspectRatio: false,
                scales: {
                  y: { min: 0, max: 100 },
                },
              }}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default AnalyticsPages;
