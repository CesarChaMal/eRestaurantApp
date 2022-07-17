import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAdmin } from '../admin.model';
import { AdminService } from '../service/admin.service';

@Component({
  templateUrl: './admin-delete-dialog.component.html',
})
export class AdminDeleteDialogComponent {
  admin?: IAdmin;

  constructor(protected adminService: AdminService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.adminService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
