# Stage 1: Build React App using Node.js
FROM node:20 AS builder

WORKDIR /app
COPY package.json package-lock.json ./
RUN npm config set registry https://registry.npmjs.org/ && \
    npm cache clean --force && \
    npm install --unsafe-perm
COPY . .

ARG REACT_APP_SYNCFUSION_LICENSE
ENV REACT_APP_SYNCFUSION_LICENSE=$REACT_APP_SYNCFUSION_LICENSE

RUN REACT_APP_SYNCFUSION_LICENSE=$REACT_APP_SYNCFUSION_LICENSE npm run build

# Stage 2: Serve the React App with Nginx
FROM nginx:alpine
RUN rm -rf /usr/share/nginx/html/* && \
    rm /etc/nginx/conf.d/default.conf
COPY --from=builder /app/build /usr/share/nginx/html
COPY nginx/frontend-nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
