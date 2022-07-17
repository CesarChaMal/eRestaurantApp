import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRole } from '../role.model';
import { RoleService } from '../service/role.service';

@Component({
  templateUrl: './role-delete-dialog.component.html',
})
export class RoleDeleteDialogComponent {
  role?: IRole;

  constructor(protected roleService: RoleService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.roleService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
