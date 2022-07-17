import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { INewOrder } from '../new-order.model';
import { NewOrderService } from '../service/new-order.service';

@Component({
  templateUrl: './new-order-delete-dialog.component.html',
})
export class NewOrderDeleteDialogComponent {
  newOrder?: INewOrder;

  constructor(protected newOrderService: NewOrderService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.newOrderService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
