import { Component, inject, OnInit, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { KnowledgeSpaceService } from './knowledge-space.service';
import { KnowledgeSpace } from './knowledge-space.model';
import { RouterLink } from '@angular/router';

@Component({
  imports: [MatCardModule, MatButtonModule, RouterLink],
  selector: 'app-knowledge-spaces',
  styleUrl: './knowledge-spaces.css',
  templateUrl: './knowledge-spaces.html',
})
export class KnowledgeSpaces implements OnInit {

  private readonly knowledgeSpaceService = inject(KnowledgeSpaceService);

  knowledgeSpaces = signal<KnowledgeSpace[]>([]);

  ngOnInit(): void {
    this.loadKnowledgeSpaces();
  }

  private loadKnowledgeSpaces(): void {
    this.knowledgeSpaceService.getAll().subscribe({
      next: (spaces) => {
        this.knowledgeSpaces.set(spaces);
      },
      error: (error) => {
        console.error("Failed to load knowledge spaces", error);
      }
    })
  }
}
