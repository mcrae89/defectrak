DefecTrak

DefecTrak is a robust and user-friendly defect tracking system designed to streamline the process of reporting, tracking, managing, and resolving software defects. Built with a Spring Boot backend and a ReactJS frontend, DefecTrak provides a comprehensive solution for software teams seeking improved quality assurance and efficient collaboration.

Technology Stack

Backend: Spring Boot (Java)

Frontend: ReactJS

Database: PostgreSQL

Containerization: Docker

Web Server: Nginx (Reverse Proxy)

Hosting: AWS EC2

Key Features

Defect Management: Full CRUD (Create, Read, Update, Delete) operations for efficient defect tracking.

Role-Based Access: Secure user management with role-specific permissions.

Real-time Notifications: Immediate updates on defect status and changes.

Reporting & Analytics: Built-in analytics to monitor defect trends and enhance quality assurance.

Getting Started

Prerequisites

Docker & Docker Compose

AWS EC2 Instance

Git

Installation

Clone the repository:

git clone https://github.com/mcrae89/defectrak.git
cd defectrak

Build and run the Docker containers:

docker compose up --build

Your application will now be accessible at:

http://<your-ec2-instance-ip>

Project Structure

defectrak/
├── defectrak-backend/       # Spring Boot REST API
├── defectrak-frontend/      # ReactJS frontend application
├── nginx/                   # Nginx configuration for reverse proxy
├── compose.yaml             # Docker Compose file
└── README.md

Contributing

Feel free to open issues or submit pull requests for enhancements or bug fixes. Contributions are always welcome!

COMING SOON:
OpenAI integration
RabbitMQ Integration
And More!
