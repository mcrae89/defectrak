# Stage 1: Build React App using Node.js
FROM node:20 AS builder

WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Serve the React App with Nginx
FROM nginx:alpine

# Remove default static files and default configuration
RUN rm -rf /usr/share/nginx/html/*
RUN rm /etc/nginx/conf.d/default.conf

# Copy the build output
COPY --from=builder /app/build /usr/share/nginx/html

# Copy your custom Nginx configuration file (renamed to default.conf)
COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
