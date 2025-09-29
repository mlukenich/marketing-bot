# Affiliate Agent Wiki & Setup Guide

## 1. Overview

(This section remains the same)

---

## 2. Prerequisites

- **Docker Desktop:** For running the application locally.
- **A Cloud Hosting Platform:** Such as Render, for production deployment.

---

## 3. Required Accounts & Credentials

(This section remains the same)

---

## 4. Security Configuration

This application is secured by **HTTP Basic Authentication**. All API endpoints and the web UI are protected by default. You must provide a username and password to access the application.

These credentials are set via environment variables:
- `APP_USER`: The username for the application.
- `APP_PASSWORD`: The password for the application.

--- 

## 5. Deployment & Running

This application is designed to be deployed as a Docker container. All secrets and configuration are provided via environment variables for maximum security.

### 5.1. Environment Variables

To run the application, you must set the following environment variables in your hosting environment (e.g., in Render's "Environment" settings).

**Application Credentials:**
```
APP_USER=your_admin_username
APP_PASSWORD=a_very_strong_and_secret_password
```

**API Keys & Tokens:**
```
BITLY_API_TOKEN=your_bitly_token
OPENAI_API_KEY=your_openai_api_key
TWITTER_CONSUMER_KEY=your_twitter_consumer_key
TWITTER_CONSUMER_SECRET=your_twitter_consumer_secret
TWITTER_ACCESS_TOKEN=your_twitter_access_token
TWITTER_ACCESS_TOKEN_SECRET=your_twitter_access_token_secret
FACEBOOK_PAGE_ID=your_facebook_page_id
FACEBOOK_PAGE_ACCESS_TOKEN=your_facebook_page_access_token
PINTEREST_ACCESS_TOKEN=your_pinterest_access_token
PINTEREST_BOARD_ID=your_pinterest_board_id
```

**Affiliate & App Configuration:**
```
AMAZON_AFFILIATE_TAG=youraffiliatename-20
APP_BASE_URL=https://your-app-name.onrender.com
RESEARCH_URL=https://www.amazon.com/bestsellers
```

**Scheduling (Optional - can use defaults):**
```
RESEARCH_CRON=0 0 3 * * ?
MARKETING_CRON=0 */15 * * * ?
POSTING_CRON=0 * * * * ?
STAGGER_MINUTES=120
POSTING_WINDOW_START=9
POSTING_WINDOW_END=17
RECURRING_ENABLED=true
RECURRING_CRON=0 0 4 * * ?
REPOST_DAYS=30
MIN_CLICKS=10
```

### 5.2. Deploying to Render

1.  Create a new **"Web Service"** on Render and connect it to your GitHub repository.
2.  Set the **Runtime** to **"Docker"**.
3.  Go to the **"Environment"** tab and add all the environment variables listed above with your actual secret values.
4.  Render will automatically build the `Dockerfile` and deploy your application.
5.  Your agent will be live and fully secured at the URL Render provides.

### 5.3. Running Locally with Docker

For local testing, you can use the `application.properties` file.

1.  **Fill out `application.properties`:** Add your credentials to the `application.properties` file as default values (e.g., `BITLY_API_TOKEN:your_token`).
2.  **Run Docker Compose:**
    ```sh
    docker-compose up --build
    ```
3.  **Access the UI:** Navigate to `http://localhost:8085`. You will be prompted for the username and password you set in the properties file.

---

## 6. How It Works & Database

(These sections remain the same)
