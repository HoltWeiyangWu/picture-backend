# Picture Cloud Backend

## Overview

This project (https://www.holtwywpicloud.me) is a full-stack web application built as a personal learning exercise to explore modern web development technologies. The application features a Spring Boot backend connected to a MySQL database for managing and storing data, and utilises Vue and Ant Design for frontend interaction and visuals.

The project allows users to store pictures in both public space, private space and team space. Real-time collaborative editing on pictures is allowed within team space. The website is deployed with Heroku using AWS S3 for picture storage.

## Technologies
- Java Springboot
- Amazon Web Service(AWS) S3
- Redis
- WebSocket
- MyBatis
- MySQL
- OpenAPI (development only)


## Backend Features
### Key Features
- Authentication across different storage spaces based on different roles
- Storage service with AWS S3 and performance optimised by Redis
- Collaborative-editing feature in team space via WebSocket

### User Management
- Users can register accounts and login
- Users can edit their profiles
- Admins can view/edit user roles and their information

### Picture Management
- Users can upload pictures to all types of storage space via url or direct upload
- Users can change rotation/sizes of their pictures
- Users can download pictures and share their QR code links
- Users can search pictures based on conditions
- Admins can pass or reject a picture upload request to public space
- Admins can fetch pictures online in batches
- Admins can view/edit/delete all pictures in public space

### Storage Space Management
- Users can analyse picture storage space in different dimensions
- Users can edit pictures collaboratively in team space
- Users can assign access level in their created team space
- Admins can manage all storage spaces
- Admins can upgrade storage space level

### Others
- Authentication system using framework and annotation
- Custom interceptors, config management
- Image storage with AWS S3
- Heroku deployment with MySQL

## Usage

Please refer to https://github.com/HoltWeiyangWu/picture-frontend for detailed information.

## Quick Start

- **Set up:** Download Java. Use Oracle OpenJDK 22.0.1.
- **Environment variable:** Add environment variables specified  in application.yaml (e.g. url, AWS S3)
- **Build:** Build Maven dependencies and build java application.
- **Start:** Run "PictureApplication" main function with environment variables.