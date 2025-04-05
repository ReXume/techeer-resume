import http from "k6/http";
import { sleep, check } from "k6";

export const options = {
  stages: [
    { duration: "30s", target: 20 }, // Ramp up to 20 users
    { duration: "1m", target: 20 }, // Stay at 20 users for 1 minute
    { duration: "30s", target: 0 }, // Ramp down to 0 users
  ],
  thresholds: {
    http_req_duration: ["p(95)<500"], // 95% of requests must complete below 500ms
    http_req_failed: ["rate<0.1"], // Less than 10% of requests can fail
  },
};

const BASE_URL = "https://rexume.site"; // Replace with your API base URL

export default function () {
  // Test main API endpoints
  const responses = http.batch([
    ["GET", `${BASE_URL}/api/v1/health`],
    // Add more endpoints as needed
  ]);

  // Check responses
  responses.forEach((response) => {
    check(response, {
      "status is 200": (r) => r.status === 200,
      "response time < 500ms": (r) => r.timings.duration < 500,
    });
  });

  sleep(1);
}
