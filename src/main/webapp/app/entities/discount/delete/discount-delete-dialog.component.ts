import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IDiscount } from '../discount.model';
import { DiscountService } from '../service/discount.service';

@Component({
  templateUrl: './discount-delete-dialog.component.html',
})
export class DiscountDeleteDialogComponent {
  discount?: IDiscount;

  constructor(protected discountService: DiscountService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.discountService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
