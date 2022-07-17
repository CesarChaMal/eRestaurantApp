import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICancel } from '../cancel.model';
import { CancelService } from '../service/cancel.service';

@Component({
  templateUrl: './cancel-delete-dialog.component.html',
})
export class CancelDeleteDialogComponent {
  cancel?: ICancel;

  constructor(protected cancelService: CancelService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.cancelService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
