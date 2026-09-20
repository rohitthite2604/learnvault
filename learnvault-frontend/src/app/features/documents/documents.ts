import { Component, inject, OnInit, signal } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DocumentService } from './document.service';
import { DatePipe } from '@angular/common';
import { DocumentResponse } from './document.model';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  imports: [MatCardModule, DatePipe, RouterLink, MatButtonModule, MatIconModule],
  selector: 'app-documents',
  styleUrl: './documents.css',
  templateUrl: './documents.html',
})
export class Documents implements OnInit {

  private readonly route = inject(ActivatedRoute);
  private readonly documentService = inject(DocumentService);
  private readonly snackBar = inject(MatSnackBar);

  knowledgeSpaceId = signal<number | null>(null);
  documents = signal<DocumentResponse[]>([]);
  uploading = signal(false);

  ngOnInit(): void {
    this.loadDocuments();
  }

  private loadDocuments(): void {
    const id = this.route.snapshot.paramMap.get('knowledgeSpaceId');

    if (!id) { return; }

    const knowledgeSpaceId = Number(id);

    this.knowledgeSpaceId.set(knowledgeSpaceId);

    this.documentService.getByKnowledgeSpace(knowledgeSpaceId)
      .subscribe({
        next: (documents) => {
          this.documents.set(documents);
        },
        error: (error) => {
          console.error('Failed to load documents', error);
        }
      })
  }

  onFileSelected(event: Event): void {

    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) { return; }

    if (file.type !== 'application/pdf') {
      this.snackBar.open(
        'Please select a PDF file.',
        'Close',
        {
          duration: 3000
        }
      );

      input.value = '';
      return;
    }

    const knowledgeSpaceId = this.knowledgeSpaceId();

    if (knowledgeSpaceId === null) { return; }

    this.uploading.set(true);

    this.documentService.upload(file, knowledgeSpaceId)
      .subscribe({
        next: (response) => {

          console.log(response);

          this.loadDocuments();

          this.uploading.set(false);
          input.value = '';

          this.snackBar.open(
            'Document uploaded successfully.',
            'Close',
            {
              duration: 3000
            }
          );
        },
        error: (error) => {
          console.error('Failed to upload document', error);
          this.uploading.set(false);
          input.value = '';

          this.snackBar.open(
            'Failed to upload document.',
            'Close',
            {
              duration: 3000
            }
          );
        }
      });
  }


}
