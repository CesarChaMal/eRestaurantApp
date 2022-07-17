import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRestaurantAd } from '../restaurant-ad.model';
import { RestaurantAdService } from '../service/restaurant-ad.service';

@Component({
  templateUrl: './restaurant-ad-delete-dialog.component.html',
})
export class RestaurantAdDeleteDialogComponent {
  restaurantAd?: IRestaurantAd;

  constructor(protected restaurantAdService: RestaurantAdService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.restaurantAdService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
