import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IOnHold } from '../on-hold.model';
import { OnHoldService } from '../service/on-hold.service';

@Component({
  templateUrl: './on-hold-delete-dialog.component.html',
})
export class OnHoldDeleteDialogComponent {
  onHold?: IOnHold;

  constructor(protected onHoldService: OnHoldService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.onHoldService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
