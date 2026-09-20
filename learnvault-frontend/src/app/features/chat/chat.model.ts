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