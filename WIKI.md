# Affiliate Agent Wiki & Setup Guide

## 1. Overview

This project is a fully automated Affiliate Marketing Agent. Its purpose is to discover marketing opportunities, generate promotional content, and post it to multiple social media platforms with trackable affiliate links. The entire process, from research to posting, is designed to be hands-off after initial configuration.

### Core Features

- **Automated Research:** Periodically scrapes a configured URL to find new products to market.
- **AI Content Generation:** Uses OpenAI to generate promotional content (Tweets, Blog Posts, etc.) for discovered products.
- **Link Shortening & Tracking:** Shortens links using Bitly and tracks clicks on all posted links.
- **Multi-Platform Social Media Posting:** Automatically posts generated content to configured social media accounts (Twitter, Facebook, Pinterest).
- **Scheduling:** Allows for both immediate and scheduled posting of content.
- **Web UI:** A simple dashboard to manually create links and view all created links and research results.
- **Persistent Storage:** Uses a file-based database to ensure all data persists between application restarts.

---

## 2. Prerequisites

To build and run this application, you will need:

- **Java Development Kit (JDK):** Version 17 or higher.
- **Apache Maven:** A build automation tool used to manage project dependencies and build the application.

---

## 3. Required Accounts & Credentials

Before running the application, you must create accounts with the following services and obtain the necessary API keys and tokens. This is the most critical part of the setup.

### 3.1. Bitly
- **Purpose:** To shorten the long affiliate URLs.
- **Account:** Create an account at [https://bitly.com/](https://bitly.com/).
- **Credentials Needed:**
    - `API Token`: Generate this from your Bitly account settings.

### 3.2. OpenAI
- **Purpose:** To generate promotional content.
- **Account:** Create an account at [https://platform.openai.com/](https://platform.openai.com/).
- **Credentials Needed:**
    - `API Key`: Generate this from your OpenAI dashboard.

### 3.3. Twitter
- **Purpose:** To post promotional content to a Twitter account.
- **Account:** You need a Twitter Developer account. Apply for one at [https://developer.twitter.com/](https://developer.twitter.com/).
- **Credentials Needed (from your Twitter App's 'Keys and tokens' section):**
    - `Consumer Key`
    - `Consumer Secret`
    - `Access Token`
    - `Access Token Secret`

### 3.4. Facebook
- **Purpose:** To post promotional content to a Facebook Page.
- **Account:** You need a Meta for Developers account ([https://developers.facebook.com/](https://developers.facebook.com/)) and a Facebook Page you manage.
- **Credentials Needed:**
    - `Page ID`: The unique ID of your Facebook Page.
    - `Page Access Token`: Generate this from your App in the Meta Developer Portal. It requires the `pages_manage_posts` and `pages_read_engagement` permissions.

### 3.5. Pinterest
- **Purpose:** To post promotional content (Pins) to a Pinterest Board.
- **Account:** You need a Pinterest Developers account ([https://developers.pinterest.com/](https://developers.pinterest.com/)).
- **Credentials Needed:**
    - `Access Token`: Generate this from your Pinterest App. It requires `pins:read`, `pins:write`, `boards:read`, and `boards:write` scopes.
    - `Board ID`: The unique ID of the Pinterest Board you want to post to (found in the board's URL).

---

## 4. Configuration

All configuration is done in the `src/main/resources/application.properties` file. Open this file and fill in the placeholder values with the credentials you obtained in the previous step.

```properties
# Bitly API Token
bitly.api.token=YOUR_BITLY_API_TOKEN

# OpenAI API Key
openai.api.url=https://api.openai.com/v1/chat/completions
api-keys.openai=YOUR_OPENAI_API_KEY

# Twitter API Credentials
twitter.oauth.consumerKey=YOUR_CONSUMER_KEY
twitter.oauth.consumerSecret=YOUR_CONSUMER_SECRET
twitter.oauth.accessToken=YOUR_ACCESS_TOKEN
twitter.oauth.accessTokenSecret=YOUR_ACCESS_TOKEN_SECRET

# Facebook API Credentials
facebook.page.id=YOUR_FACEBOOK_PAGE_ID
facebook.page.accessToken=YOUR_PAGE_ACCESS_TOKEN

# Pinterest API Credentials
pinterest.accessToken=YOUR_PINTEREST_ACCESS_TOKEN
pinterest.boardId=YOUR_PINTEREST_BOARD_ID

# Application Port
server.port=8085

# Research Feature Configuration
# IMPORTANT: Replace this example URL with a real, scrapeable page.
research.trending.url=https://www.amazon.com/bestsellers

# Application Base URL for Trackable Links
application.base-url=http://localhost:8085
```

---

## 5. Build & Run

1.  **Build the Application:**
    Open a terminal in the project's root directory (`affiliate-agent`) and run the following Maven command:
    ```sh
    mvn clean install
    ```
    This will compile the code and package it into a JAR file in the `/target` directory.

2.  **Run the Application:**
    Once the build is successful, run the application with the following command:
    ```sh
    java -jar target/affiliate-agent-0.0.1-SNAPSHOT.jar
    ```
    The application will start, and the server will be running on port `8085`.

---

## 6. How It Works

- **Web UI:** You can access the user interface by navigating to `http://localhost:8085` in your web browser.
- **Automated Schedulers:** The application has three core schedulers that run automatically:
    - `ResearchScheduler`: Runs every hour to find new products.
    - `MarketingScheduler`: Runs every 5 minutes to process new research and create marketing content.
    - `PostingScheduler`: Runs every 60 seconds to publish scheduled posts to all configured social media platforms.

---

## 7. Database

The application uses a file-based H2 database to ensure all data persists between restarts. The database file is created automatically and is located at `[PROJECT_ROOT]/data/affiliateagentdb.mv.db`.
