import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAppDiscount } from '../app-discount.model';
import { AppDiscountService } from '../service/app-discount.service';

@Component({
  templateUrl: './app-discount-delete-dialog.component.html',
})
export class AppDiscountDeleteDialogComponent {
  appDiscount?: IAppDiscount;

  constructor(protected appDiscountService: AppDiscountService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.appDiscountService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
