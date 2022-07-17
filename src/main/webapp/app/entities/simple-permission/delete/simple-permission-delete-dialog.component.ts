import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ISimplePermission } from '../simple-permission.model';
import { SimplePermissionService } from '../service/simple-permission.service';

@Component({
  templateUrl: './simple-permission-delete-dialog.component.html',
})
export class SimplePermissionDeleteDialogComponent {
  simplePermission?: ISimplePermission;

  constructor(protected simplePermissionService: SimplePermissionService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.simplePermissionService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
