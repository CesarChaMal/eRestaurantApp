import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRestaurantDiscount } from '../restaurant-discount.model';
import { RestaurantDiscountService } from '../service/restaurant-discount.service';

@Component({
  templateUrl: './restaurant-discount-delete-dialog.component.html',
})
export class RestaurantDiscountDeleteDialogComponent {
  restaurantDiscount?: IRestaurantDiscount;

  constructor(protected restaurantDiscountService: RestaurantDiscountService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.restaurantDiscountService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
