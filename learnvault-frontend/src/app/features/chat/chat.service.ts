import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { ChatRequest, ChatResponse, ConversationResponse, MessageResponse } from "./chat.model";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class ChatService {

    private readonly http = inject(HttpClient);
    private readonly apiUrl = 'http://localhost:8080/api';

    sendMessage(request: ChatRequest): Observable<ChatResponse> {
        return this.http.post<ChatResponse>(
            `${this.apiUrl}/chat`,
            request
        );
    }

    getConversations(knowledgeSpaceId: number): Observable<ConversationResponse[]> {
        return this.http.get<ConversationResponse[]>(
            `${this.apiUrl}/conversations`,
            {
                params: { knowledgeSpaceId }
            }
        );
    }

    getMessages(conversationId: number): Observable<MessageResponse[]> {
        return this.http.get<MessageResponse[]>(
            `${this.apiUrl}/conversations/${conversationId}/messages`
        );
    }
}