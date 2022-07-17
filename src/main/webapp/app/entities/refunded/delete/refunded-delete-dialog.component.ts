import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRefunded } from '../refunded.model';
import { RefundedService } from '../service/refunded.service';

@Component({
  templateUrl: './refunded-delete-dialog.component.html',
})
export class RefundedDeleteDialogComponent {
  refunded?: IRefunded;

  constructor(protected refundedService: RefundedService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.refundedService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
