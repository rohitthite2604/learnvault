export interface ChatRequest {
    conversationId: number | null;
    knowledgeSpaceId: number;
    message: string;
}

export interface SourceResponse {
    documentId: number;
    documentName: string;
    pageNumber: number;
}

export interface ChatResponse {
    conversationId: number;
    answer: string;
    sources: SourceResponse[];
}

export interface ConversationResponse {
    id: number;
    title: string;
    createdAt: string;
    updatedAt: string;
}

export interface MessageResponse {
    id: number;
    role: 'USER' | 'ASSISTANT';
    content: string;
    createdAt: string;
}