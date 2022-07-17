import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IState } from '../state.model';
import { StateService } from '../service/state.service';

@Component({
  templateUrl: './state-delete-dialog.component.html',
})
export class StateDeleteDialogComponent {
  state?: IState;

  constructor(protected stateService: StateService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.stateService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
