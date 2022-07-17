import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IClose } from '../close.model';
import { CloseService } from '../service/close.service';

@Component({
  templateUrl: './close-delete-dialog.component.html',
})
export class CloseDeleteDialogComponent {
  close?: IClose;

  constructor(protected closeService: CloseService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.closeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
