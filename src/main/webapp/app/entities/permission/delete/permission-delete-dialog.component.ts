import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPermission } from '../permission.model';
import { PermissionService } from '../service/permission.service';

@Component({
  templateUrl: './permission-delete-dialog.component.html',
})
export class PermissionDeleteDialogComponent {
  permission?: IPermission;

  constructor(protected permissionService: PermissionService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.permissionService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
