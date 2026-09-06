# LearnVault

**LearnVault — Personal AI Learning Assistant**

LearnVault is a personal AI-powered learning assistant that allows users to upload their learning materials and ask questions about them using a locally running Retrieval-Augmented Generation (RAG) pipeline.

The goal is to create a practical learning assistant that can answer questions based on the user's own knowledge base rather than relying only on the model's general knowledge.

---

## 🚧 Project Status

**Current status: Backend RAG pipeline completed**

The backend currently supports:

* Knowledge Spaces
* PDF upload
* PDF text extraction
* Page-aware document chunking
* Local embeddings
* PostgreSQL + pgvector vector storage
* Semantic similarity search
* Retrieval-Augmented Generation (RAG)
* Local LLM inference through Ollama
* Conversational chat history
* Follow-up question rewriting
* Source references in responses

The Angular frontend is the next major development phase.

---

# 🎯 Problem

Learning materials are often scattered across:

* Course PDFs
* Personal notes
* Documentation
* Technical articles
* Reference material
* Other study resources

It becomes difficult to remember:

> "Where did I learn this?"

LearnVault aims to solve this by creating a personal knowledge base where users can upload their learning material and ask questions directly against it.

For example:

```text
User:
What is LangChain?

LearnVault:
[Answer based on uploaded documents]
[Source: LangChain.pdf, Page 1]
```

Follow-up questions are also supported:

```text
User:
What is LangChain?

User:
What are its main components?
```

LearnVault understands that "its" refers to LangChain and rewrites the question before performing retrieval.

---

# 🏗️ Architecture

The current backend follows a simple RAG architecture:

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
             ┌───────────────┼────────────────┐
             │               │                │
             ▼               ▼                ▼
      Document Upload      Chat          Conversation
             │               │             History
             ▼               │
        PDF Extraction       │
             │               │
             ▼               │
          Chunking            │
             │               │
             ▼               │
        Embeddings            │
             │               │
             ▼               │
      PostgreSQL +            │
         pgvector             │
             │               │
             └───────┬───────┘
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

LearnVault uses Retrieval-Augmented Generation instead of sending the entire document directly to the LLM.

The ingestion pipeline is:

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

The question-answering pipeline is:

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

A major feature of the backend is contextual follow-up question handling.

For example:

```text
User:
What is LangChain?

User:
What are its main components?
```

The second question is ambiguous when considered by itself.

Instead of directly embedding:

```text
What are its main components?
```

LearnVault uses the conversation history to rewrite it into:

```text
What are the main components of LangChain?
```

The rewritten question is then used for vector retrieval.

The original question and conversation history are still provided to the final LLM.

This gives the system:

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
* nomic-embed-text
* 768-dimensional embeddings

## Database

* PostgreSQL 18
* pgvector 0.8.6

## Frontend

* Angular

The Angular frontend is currently under development.

---

# 📦 Main Components

### Knowledge Spaces

Knowledge Spaces provide logical separation between different learning domains.

Examples:

```text
GenAI
Java
Spring Boot
System Design
```

Retrieval is performed within the selected Knowledge Space rather than searching every document in the database.

---

### Documents

Uploaded PDFs belong to a Knowledge Space.

Relationship:

```text
KnowledgeSpace
      │
      └── Documents
             │
             └── Document Chunks
```

---

### Document Chunks

Each document is divided into smaller chunks.

Each chunk stores:

* Document reference
* Text content
* Page number
* Vector embedding

This allows LearnVault to retrieve the most relevant sections of a document.

---

### Vector Search

Document chunks are stored in PostgreSQL using pgvector.

Similarity search is performed using the vector distance operator:

```sql
<=> 
```

The system retrieves the top relevant chunks for a user's question.

---

### Conversation History

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

Conversation history is used to understand follow-up questions.

---

### Source References

Every RAG response can contain references to the documents used to generate the answer.

Example:

```json
{
  "documentId": 5,
  "documentName": "LangChain.pdf",
  "pageNumber": 1
}
```

This allows the frontend to show users where an answer came from.

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

# 🔌 Current API

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
  "answer": "LangChain is a rapidly emerging framework...",
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

# 📄 Document Processing

PDF documents are processed using the following flow:

```text
MultipartFile
     ↓
PDFBox
     ↓
Extract text page-by-page
     ↓
LangChain4j document representation
     ↓
Recursive chunking
     ↓
Embedding generation
     ↓
PostgreSQL
```

Each chunk retains its original page number so that source references can be returned to the client.

---

# 🤖 Local AI

LearnVault currently uses Ollama so that the AI pipeline can run locally.

Required models:

```text
llama3.2
nomic-embed-text
```

Check installed models:

```bash
ollama list
```

The Ollama server runs locally at:

```text
http://localhost:11434
```

---

# ⚙️ Local Setup

## Prerequisites

Install:

* Java 21
* Maven
* PostgreSQL 18
* pgvector
* Ollama

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

---

## 1. Clone the repository

```bash
git clone <repository-url>
cd learnvault-backend
```

---

## 2. Create the database

Create a PostgreSQL database:

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

## 3. Install Ollama models

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

## 4. Configure Spring Boot

Configure PostgreSQL and Ollama in the application's configuration.

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

## 5. Run the application

Using Maven:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

---

# 🧪 Current Testing

The RAG pipeline has been tested with a LangChain PDF.

The system successfully performs:

```text
"What is LangChain?"
        ↓
Relevant PDF chunks
        ↓
LLM answer
        ↓
Source: LangChain.pdf, Page 1
```

Conversational retrieval has also been tested:

```text
"What is LangChain?"
        ↓
"What are its main components?"
        ↓
Question rewritten to:
"What are the main components of LangChain?"
        ↓
Relevant chunks retrieved
        ↓
Grounded answer generated
```

---

# 🚧 Current Limitations

The current V1 intentionally keeps the scope small.

Currently not implemented:

* Authentication
* Multi-user support
* Cloud deployment
* Web search
* Agents
* Multi-agent workflows
* LangGraph workflows
* Voice interaction
* Image understanding
* Multiple file formats
* Advanced reranking
* Hybrid search
* Production-scale infrastructure

These features may be considered in future versions but are intentionally outside the current V1 scope.

---

# 🗺️ Roadmap

## Phase 1 — Backend RAG ✅

* [x] Spring Boot backend
* [x] PostgreSQL
* [x] pgvector
* [x] PDF extraction
* [x] Chunking
* [x] Embeddings
* [x] Vector search
* [x] RAG generation
* [x] Conversation persistence
* [x] Conversational question rewriting
* [x] Source references

## Phase 2 — Angular Frontend 🚧

* [ ] Knowledge Space UI
* [ ] Document upload UI
* [ ] Chat interface
* [ ] Conversation handling
* [ ] Source display
* [ ] Conversation history

## Phase 3 — Product Refinement

* [ ] Better error handling
* [ ] Loading states
* [ ] Improved prompts
* [ ] Better document management
* [ ] Retrieval quality improvements
* [ ] UI/UX improvements

---

# 🎯 Project Philosophy

LearnVault is intentionally being developed as a **small but genuinely useful product** rather than a collection of AI tutorials.

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

The project also serves as a practical implementation of concepts learned from modern LangChain and Generative AI workflows using the Java ecosystem.

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

# 📌 Status

**LearnVault V1 — Backend RAG milestone completed.**

Next milestone:

> **Build the Angular frontend and connect it to the completed RAG backend.**

```
```
