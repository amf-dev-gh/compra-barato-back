FROM openjdk:21-jdk-slim

RUN apt-get update && apt-get install -y \
    curl \
    wget \
    gnupg \
    ca-certificates \
    fonts-liberation \
    libasound2 \
    libatk-bridge2.0-0 \
    libatk1.0-0 \
    libatspi2.0-0 \
    libcups2 \
    libdbus-1-3 \
    libdrm2 \
    libgtk-3-0 \
    libnspr4 \
    libnss3 \
    libwayland-client0 \
    libx11-6 \
    libx11-xcb1 \
    libxcb1 \
    libxcomposite1 \
    libxdamage1 \
    libxext6 \
    libxfixes3 \
    libxrandr2 \
    libxss1 \
    libxtst6 \
    xdg-utils \
    libglib2.0-0 \
    libnss3-dev \
    libatk-bridge2.0-dev \
    libdrm-dev \
    libxss-dev \
    libgconf-2-4 \
    libxrandr-dev \
    libasound2-dev \
    libpangocairo-1.0-0 \
    libatk1.0-dev \
    libcairo-gobject2 \
    libgtk-3-dev \
    libgdk-pixbuf2.0-dev \
    && rm -rf /var/lib/apt/lists/*

RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - \
    && apt-get install -y nodejs

RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

RUN mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install chrome" \
    && mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install-deps"

COPY target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xmx1024m -Xms512m"
ENV PLAYWRIGHT_BROWSERS_PATH=/ms-playwright

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]