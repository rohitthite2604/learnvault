import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { KnowledgeSpace } from "./knowledge-space.model";

@Injectable({
    providedIn: 'root'
})
export class KnowledgeSpaceService {

    private readonly http = inject(HttpClient);

    private readonly apiUrl = 'http://localhost:8080/api/knowledge-spaces';

    getAll(): Observable<KnowledgeSpace[]> {
        return this.http.get<KnowledgeSpace[]>(this.apiUrl);
    }
}