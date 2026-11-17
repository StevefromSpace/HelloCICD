# 🚀 HelloCICD — Java CI/CD Pipeline with Git, Maven, Jenkins & Docker

A complete, end-to-end **CI/CD pipeline** implementation for a Java Maven application (**CP08 – Computer Science Practical**). This repository automates source code checkout, compilation, JUnit 5 unit testing, JAR packaging, Docker image creation, and container deployment via a declarative `Jenkinsfile`.

---

## 🔄 Pipeline Workflow

```text
GitHub Push ──▶ Jenkins Checkout ──▶ Maven Compile & JUnit 5 Tests ──▶ Package JAR ──▶ Docker Build ──▶ Container Deployment
```

1. **Git Checkout:** Pulls the latest source code from the target branch.
2. **Maven Build:** Compiles the Java 17 source code (`mvn clean compile`).
3. **Automated Testing:** Runs JUnit 5 test cases (`mvn test`) and publishes XML reports to Jenkins.
4. **Package Application:** Packages the executable `HelloCICD-1.0.jar` (`mvn package -DskipTests`).
5. **Docker Build:** Builds a lightweight container image (`hellocicd`) using `eclipse-temurin:17-jre`.
6. **Deployment:** Removes any previous container and runs a fresh `hellocicd-container`.

---

## 📁 Repository Structure

```text
HelloCICD/
├── Dockerfile               # Container definition using Eclipse Temurin JRE 17
├── Jenkinsfile              # Declarative CI/CD pipeline script
├── pom.xml                  # Maven configuration (Java 17, JUnit 5)
├── README.md                # Setup, deployment, and troubleshooting guide
└── src/
    ├── main/java/com/example/App.java        # Main Java application
    └── test/java/com/example/AppTest.java    # Automated JUnit 5 test suite
```

---

## 🛠️ Prerequisites & Server Setup (Ubuntu 24.04 / AWS EC2)

### 1. AWS EC2 Security Group Ports
Ensure the following inbound TCP ports are open in your EC2 Security Group:
* **Port `22` (SSH):** Terminal access to your EC2 instance.
* **Port `8080` (Custom TCP):** Jenkins Web Dashboard.
* **Port `8085` (Custom TCP):** Live web application & `/actuator/health` endpoint *(if running the Spring Boot browser deployment upgrade)*.

### 2. One-Shot Ubuntu / EC2 Installation
> **1 GB RAM EC2 Tip (`t2.micro` / `t3.micro`):** Add a **2 GB swap file** and cap Jenkins heap memory so Maven builds and Docker don't freeze the server.

```bash
# 1. Create 2GB Swap Space (prevents OOM freezes on 1GB RAM instances)
sudo fallocate -l 2G /swapfile && sudo chmod 600 /swapfile && sudo mkswap /swapfile && sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab

# 2. Install JDK 21 (required by Jenkins 2.580+), Maven, Git, Docker, and wget
sudo apt update && sudo apt install -y openjdk-21-jdk maven git docker.io wget

# 3. Add the 2026 Jenkins Repository Key & Install Jenkins
sudo wget -O /etc/apt/keyrings/jenkins-keyring.asc https://pkg.jenkins.io/debian-stable/jenkins.io-2026.key
echo "deb [signed-by=/etc/apt/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/" | sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null
sudo apt update && sudo apt install -y jenkins

# 4. Cap Jenkins JVM heap for low-RAM EC2 instances & start services
sudo mkdir -p /etc/systemd/system/jenkins.service.d
cat << 'EOF' | sudo tee /etc/systemd/system/jenkins.service.d/override.conf > /dev/null
[Service]
Environment="JAVA_OPTS=-Djava.awt.headless=true -Xms256m -Xmx512m"
EOF

sudo systemctl daemon-reload
sudo systemctl enable --now docker jenkins
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins

# 5. Print your initial Jenkins unlock password
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

1. Open `http://<YOUR-EC2-PUBLIC-IP>:8080` in your browser.
2. Paste the unlock password above, click **Install Suggested Plugins**, and create your admin user.

---

## ⚡ How to Use This Repository in Jenkins

### Option A: Run Directly (Read-Only Demo)
1. Open Jenkins and click **New Item**.
2. Name the job `HelloCICD-Pipeline`, select **Pipeline**, and click **OK**.
3. Under **Pipeline**, configure:
   * **Definition:** `Pipeline script from SCM`
   * **SCM:** `Git`
   * **Repository URL:** `https://github.com/StevefromSpace/HelloCICD.git`
   * **Branch Specifier:** `*/main`
   * **Script Path:** `Jenkinsfile`
4. Click **Save** and hit **Build Now**.

### Option B: Fork & Customize (Recommended for Lab Evaluation)
1. Click **Fork** at the top-right of this repository to copy it to your own GitHub account.
2. Edit `Jenkinsfile` in your fork and replace `StevefromSpace` with your GitHub username:
   ```groovy
   git branch: 'main',
   url: 'https://github.com/<YOUR-GITHUB-USERNAME>/HelloCICD.git'
   ```
3. Point your Jenkins job to your forked URL and click **Build Now**.
4. *(Optional Auto-Trigger)* Under **Build Triggers**, enable **Poll SCM** with schedule `* * * * *` so every `git push` automatically triggers a build.

---

## 🌐 Console App vs. Live Browser Deployment (Port `8085`)

* **Standard CP08 Console Mode (`main`):** Runs `App.java` as a standalone CLI program. The container prints two confirmation lines and exits (`Exited (0)`). Verify it using:
  ```bash
  docker logs hellocicd-container
  ```
* **Live Browser Web Upgrade (Port `8085`):** When upgraded to a Spring Boot web service (`server.port=8085` with `spring-boot-starter-actuator`), the container runs in detached mode (`docker run -d -p 8085:8085 --name hellocicd-container hellocicd`) and exposes:
  * **Live Web UI:** `http://<YOUR-EC2-PUBLIC-IP>:8085`
  * **Health Check Endpoint:** `http://<YOUR-EC2-PUBLIC-IP>:8085/actuator/health` (`{"status":"UP"}`)

---

## 🛠️ Troubleshooting Guide

### Part 1: Official CP08 Practical Failure Scenarios
To complete the Practical Assessment (**Green → Red → Green**), intentionally trigger a failure, inspect the Jenkins **Console Output**, fix it, and re-run:

| # | Intentional Error | Failed Stage & Error Message | Fix |
| :--- | :--- | :--- | :--- |
| **1** | Wrong GitHub URL in job/Jenkinsfile | **Git Checkout:** `fatal: repository not found` | Verify the repository URL, branch name (`main`), and visibility/credentials. |
| **2** | Java syntax error in `App.java` | **Maven Build:** `COMPILATION ERROR` | Fix the Java syntax error and push the corrected code. |
| **3** | Wrong expected value in `AppTest.java` (e.g., `assertEquals(99, 10 + 20)`) | **Automated Testing:** `Tests run: 2, Failures: 1` | Fix the test assertion or application logic and re-run. |
| **4** | Wrong JAR name in `Dockerfile` | **Docker Build:** `COPY failed: file not found` | Ensure `COPY target/HelloCICD-1.0.jar app.jar` matches `<artifactId>-<version>.jar` in `pom.xml`. |
| **5** | Jenkins lacks Docker permissions | **Deployment:** `permission denied while trying to connect to the Docker daemon` | Run `sudo usermod -aG docker jenkins && sudo systemctl restart jenkins`. |
| **6** | Container name already exists | **Deployment:** `Conflict. The container name "/hellocicd-container" is already in use` | Include `docker rm -f hellocicd-container || true` before `docker run`. |

### Part 2: Real-World AWS EC2 & Windows Gotchas

* **Jenkins `apt update` GPG Key Error (`NO_PUBKEY 7198F4B714ABFC68`):**
  Older guides reference `jenkins.io-2023.key`, which fails signature verification on Ubuntu 24.04. Use the updated `jenkins.io-2026.key` shown in the setup script above.
* **`jenkins.service` Fails to Start on Ubuntu:**
  Modern Jenkins (`2.580+`) requires **Java 21** to run the controller, even though our Maven project targets Java 17. Install `openjdk-21-jdk` and verify with `java -version` (or switch via `sudo update-alternatives --config java`).
* **EC2 Freezes / SSH Disconnects During `mvn compile`:**
  A 1 GB RAM instance (`t2.micro` / `t3.micro`) will run out of memory running Jenkins + Maven + Docker simultaneously. Always enable the **2 GB swap file** and set `-Xmx512m` in `JAVA_OPTS`.
* **Windows Hidden `.txt` File Extensions:**
  Saving `Dockerfile` or `Jenkinsfile` in Windows Notepad often creates `Dockerfile.txt` or `Jenkinsfile.txt`, causing Docker/Jenkins to fail with `"file not found"`. Verify in PowerShell with `dir` and rename if needed:
  ```powershell
  ren Dockerfile.txt Dockerfile
  ren Jenkinsfile.txt Jenkinsfile
  ```
* **Jenkins `Deployment` Stage Hangs Forever (Web App Mode):**
  If running a Spring Boot web server on port `8085` instead of the exiting console app, make sure `docker run` includes `-d` (detached mode) so Jenkins doesn't wait indefinitely for the web server process to exit.