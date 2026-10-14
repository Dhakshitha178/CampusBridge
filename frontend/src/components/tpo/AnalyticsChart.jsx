import React from "react";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import { Bar, Pie, Line } from "react-chartjs-2";

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  PointElement,
  LineElement,
  ArcElement,
  Title,
  Tooltip,
  Legend
);

const AnalyticsChart = ({ type = "bar", data, options = {}, title }) => {
  if (!data) return null;

  const defaultOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: "top",
      },
      title: {
        display: Boolean(title),
        text: title || "",
      },
    },
    ...options,
  };

  return (
    <div style={{ height: "300px", width: "100%" }}>
      {type === "bar" && <Bar data={data} options={defaultOptions} />}
      {type === "pie" && <Pie data={data} options={defaultOptions} />}
      {type === "line" && <Line data={data} options={defaultOptions} />}
    </div>
  );
};

export default AnalyticsChart;
