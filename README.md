# 🚀 HelloCICD — Java CI/CD Pipeline with Git, Maven, Jenkins & Docker

A complete, end-to-end **CI/CD pipeline** implementation for a Java Maven application (**CP08 – Computer Science Practical**). This repository automates source code checkout, compilation, JUnit 5 unit testing, JAR packaging, Docker image creation, and container deployment via a declarative `Jenkinsfile`.

---

## 🔄 Pipeline Workflow

```text
GitHub Push ──▶ Jenkins Checkout ──▶ Maven Compile & JUnit 5 Tests ──▶ Package JAR ──▶ Docker Build ──▶ Container Deployment
```

1. **Git Checkout:** Pulls the latest source code from the `main` branch.
2. **Maven Build:** Compiles the Java 17 source code (`mvn clean compile`).
3. **Automated Testing:** Runs JUnit 5 test cases (`mvn test`) and publishes XML reports to Jenkins.
4. **Package Application:** Packages the executable `HelloCICD-1.0.jar` (`mvn package -DskipTests`).
5. **Docker Build:** Builds a lightweight container image (`hellocicd`) using `eclipse-temurin:17-jre`.
6. **Deployment:** Cleans up any existing container and runs a fresh `hellocicd-container`.

---

## 📁 Repository Structure

```text
HelloCICD/
├── Dockerfile               # Container definition using Eclipse Temurin JRE 17
├── Jenkinsfile              # Declarative CI/CD pipeline script
├── pom.xml                  # Maven configuration (Java 17, JUnit 5.10.2, executable JAR)
├── README.md                # Setup and execution guide
└── src/
    ├── main/java/com/example/App.java        # Main Java application
    └── test/java/com/example/AppTest.java    # Automated JUnit 5 test suite
```

---

## 🛠️ Prerequisites & Server Setup (Ubuntu 24.04 / AWS EC2)

> **AWS EC2 Tip:** If you are using a `t2.micro` or `t3.micro` (1 GB RAM), add a **2 GB swap file** first so Maven and Jenkins don't freeze the instance during builds. Also ensure **TCP Port `8080`** is open in your EC2 Security Group.

Run the following setup script on your Ubuntu server to install **Java 21** (required by Jenkins 2.580+), **Maven**, **Git**, **Docker**, and **Jenkins**:

```bash
# 1. (Recommended on 1 GB RAM EC2) Create 2GB Swap Space
sudo fallocate -l 2G /swapfile && sudo chmod 600 /swapfile && sudo mkswap /swapfile && sudo swapon /swapfile

# 2. Install JDK 21, Maven, Git, Docker, and wget
sudo apt update && sudo apt install -y openjdk-21-jdk maven git docker.io wget

# 3. Add the 2026 Jenkins Repository Key & Install Jenkins
sudo wget -O /etc/apt/keyrings/jenkins-keyring.asc https://pkg.jenkins.io/debian-stable/jenkins.io-2026.key
echo "deb [signed-by=/etc/apt/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/" | sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null
sudo apt update && sudo apt install -y jenkins

# 4. Enable Docker & Jenkins, and grant Jenkins permission to run Docker
sudo systemctl enable --now docker jenkins
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins

# 5. Get your initial Jenkins unlock password
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

1. Open `http://<YOUR-SERVER-IP>:8080` in your browser.
2. Paste the password above, select **Install Suggested Plugins**, and create your admin account.

---

## ⚡ How to Use This Repository in Jenkins

### Option A: Run Directly (Read-Only Demo)
If you just want to test the pipeline without modifying code:
1. Open Jenkins and click **New Item**.
2. Name the job `HelloCICD-Pipeline`, select **Pipeline**, and click **OK**.
3. Under **Pipeline**, set:
   - **Definition:** `Pipeline script from SCM`
   - **SCM:** `Git`
   - **Repository URL:** `https://github.com/StevefromSpace/HelloCICD.git`
   - **Branch Specifier:** `*/main`
   - **Script Path:** `Jenkinsfile`
4. Click **Save** and hit **Build Now**.

### Option B: Fork & Customize (Recommended for Lab Evaluation)
To push your own code changes or run the troubleshooting assessment:
1. Click **Fork** at the top-right of this repository to copy it to your own GitHub account.
2. Open `Jenkinsfile` in your fork and replace `StevefromSpace` with your GitHub username:
   ```groovy
   git branch: 'main',
   url: 'https://github.com/<YOUR-GITHUB-USERNAME>/HelloCICD.git'
   ```
3. Point your Jenkins Pipeline job to your forked repository URL and click **Build Now**.

---

## 🔍 Verification & Troubleshooting Drill

### Verify Deployment & Logs
Once all stages turn green in Jenkins, verify the container output on your server terminal:
```bash
docker images
docker ps -a
docker logs hellocicd-container
```
Expected output:
```text
Welcome to CI/CD Pipeline!
Application deployed successfully!
```
*(Note: Because `App.java` is a console application, `hellocicd-container` prints its output and exits with code `0`—this is expected behavior.)*

### Practical Troubleshooting Assessment (Green → Red → Green)
To demonstrate failure detection and recovery:
1. Edit `src/test/java/com/example/AppTest.java` and change `assertEquals(30, result);` to `assertEquals(99, result);`.
2. Commit and push the change—Jenkins will fail at the **Automated Testing** stage and log the JUnit error.
3. Revert `99` back to `30`, push again, and re-run the build to restore a green pipeline.