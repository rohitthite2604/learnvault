import { Component, inject, OnInit, signal, TemplateRef } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { KnowledgeSpaceService } from './knowledge-space.service';
import { KnowledgeSpace } from './knowledge-space.model';
import { RouterLink } from '@angular/router';
import { MatDialog, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Dialog, DialogData } from '../../shared/components/dialog/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  imports: [
    MatCardModule, 
    MatButtonModule, 
    MatDialogModule, 
    RouterLink,
    ReactiveFormsModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule
  ],
  selector: 'app-knowledge-spaces',
  styleUrl: './knowledge-spaces.css',
  templateUrl: './knowledge-spaces.html',
})
export class KnowledgeSpaces implements OnInit {

  private readonly knowledgeSpaceService = inject(KnowledgeSpaceService);
  private readonly dialog = inject(MatDialog);
  private readonly formBuilder = inject(FormBuilder);

  knowledgeSpaces = signal<KnowledgeSpace[]>([]);
  saving = signal(false);

  createForm = this.formBuilder.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(100)]],
    description: ['', [Validators.maxLength(500)]]
  })

  ngOnInit(): void {
    this.loadKnowledgeSpaces();
  }

  private loadKnowledgeSpaces(): void {
    this.knowledgeSpaceService.getKnowledgeSpaces().subscribe({
      next: (spaces) => {
        this.knowledgeSpaces.set(spaces);
      },
      error: (error) => {
        console.error("Failed to load knowledge spaces", error);
      }
    })
  }

  openCreateDialog(content: TemplateRef<unknown>): void {
    this.createForm.reset({
      name: '',
      description: ''
    });

    let dialogRef: MatDialogRef<Dialog, boolean>;

    const dialogData: DialogData = {
      title: 'Create Knowledge Space',
      content,
      cancelText: 'Cancel',
      confirmText: 'Create',
      isBusy: signal(false),
      errorMessage: signal(null),

      onConfirm: () => {
        if (this.createForm.invalid) {
          this.createForm.markAllAsTouched();
          return false;
        }

        dialogData.errorMessage.set(null);
        dialogData.isBusy.set(true);

        this.createKnowledgeSpace(dialogRef, dialogData);

        // Keep the dialog open while the API request is running.
        return false;
      }
    };

    dialogRef = this.dialog.open<Dialog, DialogData, boolean>(Dialog, {
      width: '32rem',
      maxWidth: '90vw',
      data: dialogData
    });

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (confirmed) {
        this.loadKnowledgeSpaces();
      }
    });
  }

  private createKnowledgeSpace(
    dialogRef: MatDialogRef<Dialog, boolean>,
    dialogData: DialogData
  ): void {
    if (this.createForm.invalid || this.saving()) {
      this.createForm.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    dialogData.isBusy.set(true);
    dialogData.errorMessage.set(null);

    const request = {
      name: this.createForm.controls.name.value.trim(),
      description: this.createForm.controls.description.value.trim()
    };

    this.knowledgeSpaceService.createKnowledgeSpace(request).subscribe({
      next: () => {
        this.saving.set(false);
        dialogData.isBusy.set(false);
        
        // Close only after successful creation.
        dialogRef.close(true);
      },
      error: (error) => {
        this.saving.set(false);
        dialogData.isBusy.set(false);

        dialogData.errorMessage.set(
          error.error?.message ??
          error.error?.detail ??
          (error.status === 400
            ? 'The request was rejected. Check the Knowledge Space name and try again.'
            : 'Unable to create the Knowledge Space. Please try again.')
        );
      }
    });
  }
}
