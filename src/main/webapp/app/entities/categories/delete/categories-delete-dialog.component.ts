import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ICategories } from '../categories.model';
import { CategoriesService } from '../service/categories.service';

@Component({
  templateUrl: './categories-delete-dialog.component.html',
})
export class CategoriesDeleteDialogComponent {
  categories?: ICategories;

  constructor(protected categoriesService: CategoriesService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.categoriesService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
