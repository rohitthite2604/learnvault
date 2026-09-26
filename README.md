# LearnVault

**LearnVault — Personal AI Learning Assistant**

LearnVault is a local AI-powered learning assistant that allows users to upload their learning materials and ask questions about them using a Retrieval-Augmented Generation (RAG) pipeline.

Instead of relying only on an LLM's general knowledge, LearnVault retrieves relevant information from the user's own learning materials and uses that context to generate grounded answers.

The application combines an **Angular frontend**, **Spring Boot backend**, **LangChain4j**, **Ollama**, **PostgreSQL**, and **pgvector** to provide a complete local RAG application.

---

## 🚀 Project Status

**LearnVault V1 — Completed**

The current V1 provides:

* Knowledge Spaces
* PDF document upload and management
* Page-aware PDF text extraction
* Document chunking
* Local embedding generation
* PostgreSQL + pgvector vector storage
* Semantic similarity search
* Retrieval-Augmented Generation
* Local LLM inference through Ollama
* Conversational chat history
* Follow-up question rewriting
* Source references
* Angular-based user interface
* Conversation history and reopening
* Basic validation and error handling
* Loading and error states

The application runs locally and does not require a cloud AI provider.

---

# 🎯 Problem

Learning materials are often scattered across:

* Course PDFs
* Personal notes
* Documentation
* Technical articles
* Reference material
* Other study resources

When learning from multiple sources, it can become difficult to remember:

> "Where did I learn this?"

LearnVault addresses this by creating a personal knowledge base where users can organize learning material into **Knowledge Spaces** and ask questions directly against their uploaded documents.

For example:

```text
User:

What is LangChain?

        ↓

LearnVault retrieves relevant chunks
from the user's uploaded documents

        ↓

LLM generates a grounded answer

        ↓

Source references are returned
```

Follow-up questions are also supported:

```text
User:
What is LangChain?

User:
What are its main components?
```

LearnVault uses the conversation history to understand that **"its"** refers to LangChain before performing retrieval.

---

# 🏗️ Architecture

LearnVault follows a simple full-stack RAG architecture:

```text
                    ┌─────────────────┐
                    │ Angular Client  │
                    └────────┬────────┘
                             │
                             │ REST API
                             ▼
                    ┌─────────────────┐
                    │  Spring Boot    │
                    │    Backend      │
                    └────────┬────────┘
                             │
              ┌──────────────┼───────────────┐
              │              │               │
              ▼              ▼               ▼
        Document Upload    Chat        Conversation
              │                              History
              ▼
       PDF Text Extraction
              │
              ▼
          Chunking
              │
              ▼
         Embeddings
              │
              ▼
      PostgreSQL + pgvector
              │
              ▼
      Similarity Search
              │
              ▼
      Retrieved Context
              │
              ▼
        Ollama / Llama 3.2
              │
              ▼
          Answer
              │
              ▼
      Source References
```

---

# 🧠 RAG Pipeline

LearnVault uses Retrieval-Augmented Generation instead of sending an entire document directly to the LLM.

## Document Ingestion

```text
PDF
 ↓
Text Extraction
 ↓
Page-aware Chunking
 ↓
Embedding Generation
 ↓
PostgreSQL + pgvector
```

Each document is divided into smaller chunks.

Each chunk retains information about:

* Document
* Page number
* Text content
* Vector embedding

This allows relevant portions of the uploaded material to be retrieved later.

## Question Answering

```text
User Question
 ↓
Conversation History
 ↓
Question Rewriting
 ↓
Embedding
 ↓
pgvector Similarity Search
 ↓
Relevant Document Chunks
 ↓
Context Construction
 ↓
Llama 3.2
 ↓
Grounded Answer
 ↓
Source References
```

---

# 🔄 Conversational RAG

LearnVault supports contextual follow-up questions.

For example:

```text
User:
What is LangChain?

User:
What are its main components?
```

The second question is ambiguous when considered independently.

LearnVault uses the conversation history to rewrite the question into a standalone form:

```text
What are the main components of LangChain?
```

The rewritten question is then embedded and used for vector retrieval.

The original conversation history and current question are still available to the final answer-generation step.

Conceptually:

```text
Conversation History
        +
Current Question
        ↓
Question Rewriter
        ↓
Standalone Question
        ↓
Vector Retrieval
        ↓
Relevant Context
        ↓
Final Answer
```

---

# 🛠️ Technology Stack

## Frontend

* Angular
* Angular Material
* TypeScript
* Reactive Forms
* Angular Router

## Backend

* Java 21
* Spring Boot
* Spring Data JPA
* Hibernate
* Maven
* REST APIs

## Generative AI

* LangChain4j
* Ollama
* Llama 3.2

## Embeddings

* Ollama
* `nomic-embed-text`
* 768-dimensional embeddings

## Database

* PostgreSQL 18
* pgvector 0.8.6

---

# 📦 Main Features

## Knowledge Spaces

Knowledge Spaces provide logical separation between different learning domains.

Examples:

```text
GenAI
Java
Spring Boot
System Design
```

Documents belong to a Knowledge Space, and retrieval is restricted to the selected Knowledge Space.

This prevents documents from unrelated learning domains from being included in the retrieval process.

---

## Documents

Users can upload PDF learning materials to a selected Knowledge Space.

The document processing flow is:

```text
PDF Upload
    ↓
Text Extraction
    ↓
Page-aware Chunking
    ↓
Embedding Generation
    ↓
Vector Storage
```

Uploaded documents can be viewed from the corresponding Knowledge Space.

---

## Document Chunks

Documents are divided into smaller chunks before generating embeddings.

Each chunk stores:

* Document reference
* Text content
* Page number
* Vector embedding

This allows LearnVault to retrieve only the most relevant portions of a document.

---

## Vector Search

Document chunks are stored in PostgreSQL using the `pgvector` extension.

Semantic similarity search is performed using vector distance:

```sql
<=>
```

The system retrieves the most relevant chunks for a user's question.

---

## Chat

The Angular frontend provides a conversational interface for interacting with the user's knowledge base.

A chat request contains:

```json
{
  "conversationId": null,
  "knowledgeSpaceId": 1,
  "message": "What is LangChain?"
}
```

The backend performs:

```text
Question
 ↓
Question Rewriting
 ↓
Embedding
 ↓
Vector Retrieval
 ↓
Context Construction
 ↓
LLM
 ↓
Answer + Sources
```

---

## Conversation History

Conversations and messages are persisted separately from document data.

Conceptually:

```text
Conversation
    │
    └── Messages
          ├── USER
          ├── ASSISTANT
          ├── USER
          └── ASSISTANT
```

This allows users to:

* Continue existing conversations
* Ask contextual follow-up questions
* Reopen previous conversations
* Preserve conversation history

---

## Source References

RAG responses include references to the document chunks used during retrieval.

Example:

```json
{
  "documentId": 5,
  "documentName": "LangChain.pdf",
  "pageNumber": 1
}
```

These references allow the frontend to show users where the information used to generate an answer came from.

---

# 🗄️ Database Model

The main document relationships are:

```text
KnowledgeSpace
      │
      └───────────────┐
                      ▼
                  Document
                      │
                      ▼
                DocumentChunk
                      │
                      └── embedding vector(768)
```

Conversation data is maintained separately:

```text
Conversation
      │
      ▼
   Message
```

---

# 🧩 Backend Structure

The Spring Boot backend is organized into configuration, controllers, DTOs, domain models, repositories, services, and exception handling.

```text
src/main/java/com/learnvault/learnvaultbackend

├── config
│   ├── CorsConfig
│   └── OllamaConfig
│
├── controller
│   ├── ChatController
│   ├── ConversationController
│   ├── DocumentController
│   ├── HealthController
│   └── KnowledgeSpaceController
│
├── dto
│   ├── ApiErrorResponse
│   ├── ChatRequest
│   ├── ChatResponse
│   ├── ConversationResponse
│   ├── DocumentResponse
│   ├── KnowledgeSpaceRequest
│   ├── MessageResponse
│   ├── RagResponse
│   ├── RetrievedChunkResponse
│   └── SourceResponse
│
├── exception
│   ├── BadRequestException
│   ├── GlobalExceptionHandler
│   └── ResourceNotFoundException
│
├── model
│   ├── Conversation
│   ├── Document
│   ├── DocumentChunk
│   ├── KnowledgeSpace
│   └── Message
│
├── repository
│   ├── ConversationRepository
│   ├── DocumentChunkRepository
│   ├── DocumentRepository
│   ├── KnowledgeSpaceRepository
│   └── MessageRepository
│
└── service
    ├── ChatService
    ├── ConversationService
    ├── DocumentService
    ├── EmbeddingService
    ├── KnowledgeSpaceService
    ├── PageChunk
    ├── PdfService
    ├── QuestionRetrieverService
    ├── RagService
    └── RetrievalService
```

---

# 🎨 Frontend Structure

The Angular application is organized by feature:

```text
src/app

├── features
│   ├── chat
│   ├── documents
│   └── knowledge-spaces
│
└── shared
    └── components
        ├── dialog
        └── layout
```

The feature-based structure keeps the main application functionality separated into independent areas.

### Chat

Handles:

* Chat interface
* Sending questions
* Conversation history
* Loading states
* Source display
* Opening previous conversations
* Follow-up questions

### Documents

Handles:

* Document listing
* PDF upload
* Knowledge Space-specific documents

### Knowledge Spaces

Handles:

* Knowledge Space listing
* Knowledge Space creation
* Validation
* Duplicate-name handling

### Shared Components

Reusable UI components such as:

* Dialog
* Application layout

---

# 🔌 API

## Chat

### POST

```text
/api/chat
```

Example request:

```json
{
  "conversationId": null,
  "knowledgeSpaceId": 1,
  "message": "What is LangChain?"
}
```

Example response:

```json
{
  "conversationId": 1,
  "answer": "LangChain is ...",
  "sources": [
    {
      "documentId": 5,
      "documentName": "LangChain.pdf",
      "pageNumber": 1
    }
  ]
}
```

For a follow-up question:

```json
{
  "conversationId": 1,
  "knowledgeSpaceId": 1,
  "message": "What are its main components?"
}
```

---

## Knowledge Spaces

```text
GET  /api/knowledge-spaces
POST /api/knowledge-spaces
```

Knowledge Spaces can be created and retrieved through the REST API.

---

## Documents

PDF documents are uploaded to a specific Knowledge Space.

```text
POST /api/documents/upload?knowledgeSpaceId={id}
```

---

## Conversations

Conversations can be retrieved for a Knowledge Space and individual conversation messages can be loaded.

```text
GET /api/conversations?knowledgeSpaceId={id}

GET /api/conversations/{id}/messages
```

---

# 📄 Document Processing

PDF documents are processed page-by-page.

The backend pipeline is:

```text
MultipartFile
     ↓
PDFBox
     ↓
Extract text page-by-page
     ↓
Page-aware document representation
     ↓
Recursive chunking
     ↓
Embedding generation
     ↓
PostgreSQL + pgvector
```

Each chunk retains its original page number so that source references can be returned to the client.

---

# 🤖 Local AI

LearnVault uses Ollama to run the AI components locally.

Required models:

```text
llama3.2
nomic-embed-text
```

Check installed models:

```bash
ollama list
```

Ollama runs locally at:

```text
http://localhost:11434
```

No external LLM API is required for the V1 RAG pipeline.

---

# ⚙️ Local Setup

## Prerequisites

Install:

* Java 21
* Maven
* PostgreSQL 18
* pgvector
* Ollama
* Node.js and npm

Verify Java:

```bash
java -version
```

Verify PostgreSQL:

```bash
psql --version
```

Verify Ollama:

```bash
ollama list
```

Verify Node.js:

```bash
node --version
```

---

## 1. Clone the Repository

```bash
git clone https://github.com/rohitthite2604/learnvault.git
cd learnvault
```

The repository contains both the Angular frontend and Spring Boot backend.

---

## 2. Create the Database

Create the PostgreSQL database:

```sql
CREATE DATABASE learnvault;
```

Connect to it:

```bash
psql -d learnvault
```

Enable pgvector:

```sql
CREATE EXTENSION vector;
```

Verify:

```sql
SELECT extversion
FROM pg_extension
WHERE extname = 'vector';
```

---

## 3. Install Ollama Models

Pull the required models:

```bash
ollama pull llama3.2
```

```bash
ollama pull nomic-embed-text
```

Verify:

```bash
ollama list
```

---

## 4. Configure the Backend

Configure the PostgreSQL connection and Ollama settings in the Spring Boot application configuration.

Example:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/learnvault
    username: postgres
    password: ${DB_PASSWORD}

langchain4j:
  ollama:
    chat-model:
      base-url: http://localhost:11434
      model-name: llama3.2
```

Do not commit real database passwords or other secrets to GitHub.

---

## 5. Run the Backend

From the backend directory:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

---

## 6. Run the Angular Frontend

From the frontend directory:

```bash
npm install
```

Start the development server:

```bash
ng serve
```

The Angular development server runs on:

```text
http://localhost:4200
```

The frontend communicates with the Spring Boot backend through the REST API.

---

# 🧪 Testing

The complete V1 application flow has been tested locally.

The primary workflow is:

```text
Create Knowledge Space
        ↓
Upload PDF
        ↓
Document Processing
        ↓
Chunking
        ↓
Embedding Generation
        ↓
Vector Storage
        ↓
Ask Question
        ↓
Retrieve Relevant Chunks
        ↓
Generate Grounded Answer
        ↓
Display Sources
```

Conversational retrieval has also been tested:

```text
"What is LangChain?"
        ↓
"What are its main components?"
        ↓
Question rewritten using conversation context
        ↓
Relevant chunks retrieved
        ↓
Grounded answer generated
```

The application has also been tested for core validation and error-handling scenarios, including duplicate Knowledge Space creation and the main document/chat workflows.

---

# 🚧 Current V1 Scope

LearnVault V1 intentionally keeps the scope focused on the core personal learning assistant workflow.

The following are **outside the current V1 scope**:

* Authentication
* Multi-user support
* Cloud deployment
* Web search
* Agents
* Multi-agent workflows
* LangGraph workflows
* Voice interaction
* Image understanding
* Multiple document formats
* Advanced reranking
* Hybrid search
* Production-scale infrastructure

These can be considered in future versions.

---

# 🗺️ Future Improvements

Potential future improvements include:

* OCR support for PDFs without a usable text layer
* Additional document formats
* Improved retrieval quality
* Hybrid search
* Reranking
* Better document management
* Authentication and multi-user support
* Cloud deployment
* Advanced AI workflows
* More advanced UI/UX
* Knowledge Space deletion with cascading document cleanup
* Document deletion with associated chunk and embedding cleanup
* Conversation deletion and message cleanup

These improvements are intentionally separated from V1 so that the core product remains small and maintainable.

---

# 🎯 Project Philosophy

LearnVault is intentionally developed as a **small but genuinely useful product**, rather than simply a collection of AI tutorials.

The primary goal is to understand and implement the complete AI application pipeline:

```text
Application
     ↓
LLM
     ↓
Embeddings
     ↓
Vector Database
     ↓
Retrieval
     ↓
RAG
     ↓
Conversation
     ↓
User Interface
```

The project also serves as a practical implementation of modern Generative AI and LangChain concepts using the Java ecosystem.

---

# 👨‍💻 Development

Built with:

```text
Angular
Spring Boot
LangChain4j
Ollama
Llama 3.2
nomic-embed-text
PostgreSQL
pgvector
```

---

# 📌 Final Status

**LearnVault V1 — Completed**

LearnVault currently provides a complete local AI learning workflow:

```text
Learning Material
       ↓
Knowledge Space
       ↓
PDF Upload
       ↓
Chunking + Embeddings
       ↓
Vector Database
       ↓
Question
       ↓
Conversational Retrieval
       ↓
Local LLM
       ↓
Grounded Answer
       ↓
Source References
```

The project can now serve as a foundation for future improvements while remaining a complete and functional V1 product.
