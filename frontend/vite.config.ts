import { sentryVitePlugin } from "@sentry/vite-plugin";
import { defineConfig } from "vite";
import react from "@vitejs/plugin-react-swc";

export default defineConfig({
  plugins: [react(), sentryVitePlugin({
    org: "rexume-7b",
    project: "javascript-react"
  })],

  build: {
    sourcemap: true
  }
});