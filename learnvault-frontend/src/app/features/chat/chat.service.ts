import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { ChatRequest, ChatResponse } from "./chat.model";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class ChatService {

    private readonly http = inject(HttpClient);

    private readonly apiUrl = 'http://localhost:8080/api/chat';

    sendMessage(request: ChatRequest): Observable<ChatResponse> {
        return this.http.post<ChatResponse>(
            this.apiUrl,
            request
        );
    }
}