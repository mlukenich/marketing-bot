# Affiliate Agent Wiki & Setup Guide

## 1. Overview

This project is a fully automated Affiliate Marketing Agent. Its purpose is to discover marketing opportunities, generate promotional content for A/B testing, and post it to multiple social media platforms with trackable affiliate links. The entire process, from research to posting and recycling successful content, is designed to be hands-off after initial configuration.

### Core Features

- **Automated Research:** Periodically scrapes configured websites to find new products to market using a modular, extensible `ProductSource` architecture.
- **AI-Powered A/B Testing:** Uses OpenAI to generate multiple, distinct ad copy variations for each product.
- **Performance Analytics:** Tracks clicks for each specific ad variation, providing the data needed to identify winning content.
- **Automated Content Recycling:** A recurring post scheduler automatically identifies top-performing content and re-queues it for future promotion, creating a powerful self-optimizing feedback loop.
- **Monetization Engine:** Automatically transforms product URLs into valid affiliate links using a modular system that can support multiple affiliate networks.
- **Multi-Platform Broadcasting:** Automatically posts generated content to configured social media accounts (Twitter, Facebook, Pinterest) using a flexible `SocialMediaService` design.
- **Advanced & Strategic Scheduling:** Uses `cron` jobs for precise timing, intelligently staggers new posts to create a clean queue, and respects configurable "posting windows" to maximize audience engagement.
- **Comprehensive UI Dashboard:** A web interface to manually create links, monitor all research and marketing activity, view detailed A/B testing analytics, and manually trigger the agent's core processes.
- **Persistent & Reliable:** Uses a file-based database to ensure all data persists between restarts and includes a suite of unit tests to ensure core logic is reliable.
- **Secure & Deployable:** Secured with HTTP Basic Authentication and fully "dockerized" for easy, secure deployment to any modern cloud platform.

---

## 2. Prerequisites

- **Docker Desktop:** The recommended way to run the application. Download it from [https://www.docker.com/products/docker-desktop/](https://www.docker.com/products/docker-desktop/).
- **Java Development Kit (JDK):** Version 17 or higher (for manual builds).
- **Apache Maven:** For manual builds.

---

## 3. Required Accounts & Credentials

Before running the application, you must create accounts with the following services and obtain the necessary API keys and tokens. This is the most critical part of the setup.

- **Bitly:** For shortening URLs (`API Token`).
- **OpenAI:** For generating content (`API Key`).
- **Twitter:** For posting tweets (`Consumer Key`, `Consumer Secret`, `Access Token`, `Access Token Secret`).
- **Facebook:** For posting to a Page (`Page ID`, `Page Access Token` with `pages_manage_posts` permission).
- **Pinterest:** For creating Pins on a Board (`Access Token` with `pins:write` and `boards:write` scopes, `Board ID`).

---

## 4. Security

This application is secured by **HTTP Basic Authentication**. All API endpoints and the web UI are protected by default. You must provide a username and password to access the application. These credentials are set via environment variables (`APP_USER` and `APP_PASSWORD`).

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

For local testing, you can use the `application.properties` file, which contains default values.

1.  **Fill out `application.properties`:** It's recommended to replace the default placeholder values (like `YOUR_BITLY_API_TOKEN`) with your actual credentials for local testing.
2.  **Run Docker Compose:**
    ```sh
    docker-compose up --build
    ```
3.  **Access the UI:** Navigate to `http://localhost:8085`. You will be prompted for the username and password (`admin` / `P@ssw0rd123!` by default).

---

## 6. How It Works: The Automated Pipeline

- **Web UI:** Access the dashboard at `http://localhost:8085`.
- **Automated Schedulers:** The application has four core schedulers that create a continuous, self-optimizing loop:
    1.  `ResearchScheduler`: Runs on a `cron` schedule (e.g., daily at 3 AM) to find new products from all configured sources.
    2.  `MarketingScheduler`: Runs on a `cron` schedule (e.g., every 15 minutes) to find unprocessed research. It creates a full marketing campaign (generates A/B test content, creates affiliate links) and schedules it at the end of the current content queue, respecting the staggering and posting window rules.
    3.  `PostingScheduler`: Runs frequently (e.g., every minute) to find the next due content variation and broadcast it to all configured social media platforms.
    4.  `RecurringPostScheduler`: Runs periodically (e.g., daily at 4 AM) to find old, successful posts. It identifies the **single best-performing ad variation** for that link and re-queues it for promotion, creating a powerful learning feedback loop.

---

## 7. Database Persistence

- **When running with Docker (Recommended):** The `docker-compose.yml` file is configured to use a named Docker volume (`agent-data`). This ensures your database is safely stored and persists even if the container is removed or rebuilt.
- **When running manually:** The application uses a file-based H2 database. The data file is created automatically at `[PROJECT_ROOT]/data/affiliateagentdb.mv.db`.
