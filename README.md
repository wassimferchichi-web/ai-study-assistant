# 🎓 AI Study Assistant
## 🎬 Demo









<div align="center">

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.2-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![LLaMA](https://img.shields.io/badge/LLaMA_3-Groq-FF6B35?style=for-the-badge&logo=meta&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

**An AI-powered study assistant that summarizes your notes, generates quizzes, and lets you chat with an AI tutor — all from a beautiful dark interface.**

[Features](#-features) • [Tech Stack](#-tech-stack) • [Getting Started](#-getting-started) • [API Reference](#-api-reference) • [Screenshots](#-screenshots)

</div>

---

## ✨ Features

| Feature | Description |
|--------|-------------|
| 📝 **Note Management** | Create, view, and delete study notes with full CRUD support |
| 📁 **File Upload** | Import `.txt` and `.pdf` files directly as notes |
| 🤖 **AI Summarization** | Get instant AI-generated summaries with key points and definitions |
| 🎯 **Quiz Generation** | Auto-generate 10 multiple-choice questions from any note |
| 💬 **AI Chat** | Chat with LLaMA 3 using your notes as context |
| 🎨 **Beautiful UI** | Premium dark interface with smooth animations |

---

## 🛠️ Tech Stack

### Backend
- **Java 21** — Latest LTS version
- **Spring Boot 3.3.2** — REST API framework
- **Spring Data JPA** — Database ORM
- **H2 Database** — In-memory database for development
- **Apache PDFBox 2.0** — PDF text extraction

### AI
- **LLaMA 3 (llama-3.1-8b-instant)** — AI model
- **Groq API** — Ultra-fast AI inference (free tier available)

### Frontend
- **HTML5 + CSS3 + Vanilla JavaScript** — No framework needed
- **Google Fonts** — Playfair Display + DM Sans
- **Responsive Dark UI** — Fully custom design

---

## 🚀 Getting Started

### Prerequisites

Make sure you have these installed:

```bash
java -version    # Java 21+
mvn -version     # Maven 3.9+
```

You also need a **free Groq API key** from [console.groq.com](https://console.groq.com)

---

### Installation

**1. Clone the repository**

```bash
git clone https://github.com/wassimferchichi-web/ai-study-assistant.git
cd ai-study-assistant
```

**2. Configure the application**

Create the file `src/main/resources/application.properties` using the provided template:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Then open it and add your Groq API key:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

server.port=8080

groq.api.key=YOUR_GROQ_KEY_HERE

spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=20MB
```

**3. Run the application**

```bash
mvn spring-boot:run
```

**4. Open in your browser**

```
http://localhost:8080/index.html
```

---

## 📁 Project Structure

```
ai-study-assistant/
│
├── src/
│   └── main/
│       ├── java/com/example/ai_study_assistant/
│       │   ├── AiStudyAssistantApplication.java   # Entry point
│       │   ├── controller/
│       │   │   └── NoteController.java            # REST endpoints
│       │   ├── model/
│       │   │   └── Note.java                      # Note entity
│       │   ├── repository/
│       │   │   └── NoteRepository.java            # Data layer
│       │   └── service/
│       │       ├── AiService.java                 # Groq AI integration
│       │       └── NoteService.java               # Business logic
│       └── resources/
│           ├── static/
│           │   └── index.html                     # Frontend UI
│           ├── application.properties.example     # Config template
│           └── application.properties             # Your config (not committed)
│
├── pom.xml                                        # Maven dependencies
└── README.md
```

---

## 📡 API Reference

### Notes

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/notes` | Get all notes |
| `POST` | `/api/notes` | Create a new note |
| `GET` | `/api/notes/{id}` | Get note by ID |
| `DELETE` | `/api/notes/{id}` | Delete a note |

### AI Features

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/notes/{id}/summarize` | Summarize a note with AI |
| `POST` | `/api/notes/{id}/quiz` | Generate 10 quiz questions |
| `POST` | `/api/chat` | Chat with AI (with optional note context) |
| `POST` | `/api/upload` | Upload .txt or .pdf file as a note |

### Example Requests

**Create a note:**
```bash
curl -X POST http://localhost:8080/api/notes \
  -H "Content-Type: application/json" \
  -d '{"title": "Photosynthesis", "content": "Photosynthesis is the process..."}'
```

**Summarize a note:**
```bash
curl -X POST http://localhost:8080/api/notes/1/summarize
```

**Chat with AI:**
```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Explain the key concepts", "context": "optional note content here"}'
```

---

## 🗺️ Roadmap

- [x] Note CRUD
- [x] PDF & TXT file upload
- [x] AI summarization
- [x] Quiz generation (10 questions)
- [x] AI chat with note context
- [x] Beautiful dark UI
- [ ] PostgreSQL persistent database
- [ ] User authentication (JWT)
- [ ] Online deployment
- [ ] Progress tracking & spaced repetition
- [ ] Support for more file types (DOCX, PPTX)

---

## 🤝 Contributing

Contributions are welcome! Feel free to:

1. Fork the project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

**Wassim Ferchichi**

[![GitHub](https://img.shields.io/badge/GitHub-wassimferchichi--web-181717?style=for-the-badge&logo=github)](https://github.com/wassimferchichi-web)

---

<div align="center">

⭐ **If you found this project helpful, please give it a star!** ⭐

Built with ❤️ using Java, Spring Boot, and LLaMA 3

</div>
