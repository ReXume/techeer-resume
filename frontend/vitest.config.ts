import { defineConfig } from "vitest/config";

export default defineConfig({
  test: {
    globals: true,
    environment: "jsdom",
    setupFiles: "./setupTests.ts",
    include: ["src/__test__/**/*.test.tsx"],
    coverage: {
      provider: "istanbul",
      reporter: ["text", "html"],
      exclude: ["node_modules/", "dist/", "setupTests.ts"],
      include: ["src/components/**/*.tsx"], // 커버리지 대상 파일
    },
  },
});
