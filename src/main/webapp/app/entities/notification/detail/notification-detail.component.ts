import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { INotification } from '../notification.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-notification-detail',
  templateUrl: './notification-detail.component.html',
})
export class NotificationDetailComponent implements OnInit {
  notification: INotification | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notification }) => {
      this.notification = notification;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
