# API Automation Framework (Java + Maven + Cucumber + JUnit)

A lightweight and scalable **API automation testing framework** built using:

* **Java 17+**
* **Maven**
* **Cucumber BDD**
* **JUnit Platform**
* **RestAssured**
* **Logback**
* **Allure Reporting (optional)**

This framework supports:

* API test automation (CRUD, status validation, schema validation)
* BDD-style feature files
* Hooks, step definitions, reusable API utilities
* HTML + JSON Cucumber report generation

---

## 📁 Project Structure

```
project
│   pom.xml
│   README.md
│
├── .idea/                  # IntelliJ project files (ignored in git)
├── .mvn/                   # Maven wrapper files
│
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── api/        # API helpers (RestAssured wrappers)
│   │   │   └── utils/      # Reusable utilities (ConfigReader, LoggerUtil)
│   │   └── resources/
│   │       └── logback.xml # Logging config
│   │
│   └── test
│       ├── java
│       │   ├── hooks/      # Cucumber hooks
│       │   ├── runners/    # Test runners
│       │   └── steps/      # Step definitions
│       └── resources
│           ├── config.properties
│           ├── junit-platform.properties
│           └── features/   # Cucumber feature files
│
└── target/
        # Maven build output, HTML reports, compiled classes, etc.
```

---

## 🚀 Getting Started

### **Prerequisites**

Ensure you have the following installed:

* Java 17+
* Maven 3.8+
* IntelliJ IDEA / VS Code (recommended)
* Git

---

## 🔧 Installation

Clone the repository:

```bash
git clone <your-repo-url>
cd project-folder
```

Install dependencies:

```bash
mvn clean install
```

---

## ▶️ Running the Tests

### **Run all tests**

```bash
mvn clean test
```

### **Run tests with a specific tag**

```bash
mvn clean test -Dcucumber.filter.tags="@smoke"
```

### **Run a specific feature**

```bash
mvn clean test -Dcucumber.features=src/test/resources/features/get_user.feature
```

---

## 📊 Reports

### **Cucumber Report**

After execution, HTML reports will appear at:

```
target/cucumber-report.html
target/cucumber-html-reports/
```

### **JUnit XML Reports**

```
target/surefire-reports/
```

### **Allure Report (optional)**

If integrated:

Generate report:

```bash
allure generate target/allure-results --clean
```

Open report:

```bash
allure open target/allure-report
```

---

## ⚙️ Configuration

Edit environment settings in:

```
src/test/resources/config.properties
```

Typical config values:

```
base.url=https://api.example.com
timeout=5000
token=your_token_here
```

---

## 🧱 Framework Components

### **1. API Layer (`api/`)**

Contains reusable wrappers for HTTP operations (GET, POST, PUT, DELETE) using RestAssured.

### **2. Utilities (`utils/`)**

* `ConfigReader` – Loads environment configs
* `LoggerUtil` – Centralized logging

### **3. BDD Layer**

* `features/` – Gherkin test scenarios
* `steps/` – Step definitions
* `hooks/` – Before/After hooks

### **4. Runner Classes**

Located under `/runners`
Controls:

* Glue path
* Plugins (JSON, HTML reports)
* Feature file paths

---

## 🧪 Example Feature File

```gherkin
Feature: Get user details
  Scenario: Verify user information
    Given I hit the get user API
    Then I should receive status code 200
    And the user response should contain valid data
```

---

## 🧩 Example Step Definition

```java
@Given("I hit the get user API")
public void hitGetUserApi() {
    response = UserApi.getUser();
}
```

---

## 🤝 Contributing

1. Create a new branch
2. Commit your changes
3. Push and raise a PR

---

## 📜 License

This project is for learning, testing, and automation development purposes.

---

## ✨ Author

**Vivek Varma Maddu**
QA Automation Engineer & Test Lead
