import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAppAd } from '../app-ad.model';
import { AppAdService } from '../service/app-ad.service';

@Component({
  templateUrl: './app-ad-delete-dialog.component.html',
})
export class AppAdDeleteDialogComponent {
  appAd?: IAppAd;

  constructor(protected appAdService: AppAdService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.appAdService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
