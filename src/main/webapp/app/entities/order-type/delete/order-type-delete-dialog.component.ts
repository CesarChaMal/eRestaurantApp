import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IOrderType } from '../order-type.model';
import { OrderTypeService } from '../service/order-type.service';

@Component({
  templateUrl: './order-type-delete-dialog.component.html',
})
export class OrderTypeDeleteDialogComponent {
  orderType?: IOrderType;

  constructor(protected orderTypeService: OrderTypeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.orderTypeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
