import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAd } from '../ad.model';
import { AdService } from '../service/ad.service';

@Component({
  templateUrl: './ad-delete-dialog.component.html',
})
export class AdDeleteDialogComponent {
  ad?: IAd;

  constructor(protected adService: AdService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.adService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
