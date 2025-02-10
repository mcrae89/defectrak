# Stage 1: Build React App using Node.js
FROM node:20 AS builder

WORKDIR /app

# Copy package files first to leverage Docker caching
COPY package.json package-lock.json ./

# Install dependencies
RUN npm install

# Copy the rest of the application files
COPY . .

# Build the React app
RUN npm run build

# Stage 2: Serve the app with Nginx
FROM nginx:alpine

# Remove default Nginx static assets and copy our built React app
RUN rm -rf /usr/share/nginx/html/*
COPY --from=builder /app/build /usr/share/nginx/html

# Expose the port Nginx will serve on
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
