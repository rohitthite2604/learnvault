import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { DocumentResponse } from "./document.model";

@Injectable({
    providedIn: 'root'
})
export class DocumentService {

    private readonly http = inject(HttpClient);

    private readonly apiUrl = 'http://localhost:8080/api/documents';

    getByKnowledgeSpace(knowledgeSpaceId: number): Observable<DocumentResponse[]> {
        return this.http.get<DocumentResponse[]>(
            `${this.apiUrl}?knowledgeSpaceId=${knowledgeSpaceId}`
        );
    }

    upload(file: File, knowledgeSpaceId: number): Observable<string> {

        const formData = new FormData();

        formData.append('file', file);

        return this.http.post(
            `${this.apiUrl}/upload?knowledgeSpaceId=${knowledgeSpaceId}`,
            formData,
            {
            responseType: 'text'
            }
        );
    }
}