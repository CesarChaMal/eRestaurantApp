import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IComplete } from '../complete.model';
import { CompleteService } from '../service/complete.service';

@Component({
  templateUrl: './complete-delete-dialog.component.html',
})
export class CompleteDeleteDialogComponent {
  complete?: IComplete;

  constructor(protected completeService: CompleteService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.completeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
