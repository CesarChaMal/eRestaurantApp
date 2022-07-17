import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICustomerProfile } from '../customer-profile.model';
import { CustomerProfileService } from '../service/customer-profile.service';

@Component({
  templateUrl: './customer-profile-delete-dialog.component.html',
})
export class CustomerProfileDeleteDialogComponent {
  customerProfile?: ICustomerProfile;

  constructor(protected customerProfileService: CustomerProfileService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.customerProfileService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
