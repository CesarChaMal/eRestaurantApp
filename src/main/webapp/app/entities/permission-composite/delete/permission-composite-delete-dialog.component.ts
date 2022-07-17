import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPermissionComposite } from '../permission-composite.model';
import { PermissionCompositeService } from '../service/permission-composite.service';

@Component({
  templateUrl: './permission-composite-delete-dialog.component.html',
})
export class PermissionCompositeDeleteDialogComponent {
  permissionComposite?: IPermissionComposite;

  constructor(protected permissionCompositeService: PermissionCompositeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.permissionCompositeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
