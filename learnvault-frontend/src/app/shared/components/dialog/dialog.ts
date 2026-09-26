import { NgTemplateOutlet } from '@angular/common';
import { Component, inject, TemplateRef, WritableSignal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';

export interface DialogData {
  title: string;
  content: TemplateRef<unknown>;
  cancelText?: string;
  confirmText?: string;
  isBusy: WritableSignal<boolean>;
  errorMessage: WritableSignal<string | null>;
  onConfirm?: () => boolean;
}

@Component({
  imports: [NgTemplateOutlet, MatDialogModule, MatButtonModule],
  selector: 'app-dialog',
  styleUrl: './dialog.css',
  templateUrl: './dialog.html',
})
export class Dialog {

  readonly data = inject<DialogData>(MAT_DIALOG_DATA);

  private readonly dialogRef = inject(MatDialogRef<Dialog>);

  cancel(): void {
    if (this.data.isBusy()) {
      return;
    }
    this.dialogRef.close(false);
  }

  confirm(): void {
    if (this.data.isBusy()) {
      return;
    }
    if (this.data.onConfirm && !this.data.onConfirm()) {
      return;
    }

    this.dialogRef.close(true);
  }
}
