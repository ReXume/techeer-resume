# React 빌드 단계
FROM node:20-alpine as builder
WORKDIR /usr/src/app
COPY package.json yarn.lock ./

RUN yarn install

COPY .env ./
COPY ./ ./

RUN yarn run build
# 개발 서버 실행
CMD ["yarn", "run", "dev"]


EXPOSE 5173
# Nginx를 통한 배포
# FROM nginx:alpine

# COPY nginx/nginx.conf /etc/nginx/nginx.conf
# COPY nginx/mime.types /etc/nginx/mime.types

# COPY --from=builder /usr/src/app/dist /usr/share/nginx/html  
