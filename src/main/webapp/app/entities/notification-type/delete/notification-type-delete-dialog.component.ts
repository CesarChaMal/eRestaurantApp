import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { INotificationType } from '../notification-type.model';
import { NotificationTypeService } from '../service/notification-type.service';

@Component({
  templateUrl: './notification-type-delete-dialog.component.html',
})
export class NotificationTypeDeleteDialogComponent {
  notificationType?: INotificationType;

  constructor(protected notificationTypeService: NotificationTypeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.notificationTypeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
