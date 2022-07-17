import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProducts } from '../products.model';
import { ProductsService } from '../service/products.service';

@Component({
  templateUrl: './products-delete-dialog.component.html',
})
export class ProductsDeleteDialogComponent {
  products?: IProducts;

  constructor(protected productsService: ProductsService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.productsService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
