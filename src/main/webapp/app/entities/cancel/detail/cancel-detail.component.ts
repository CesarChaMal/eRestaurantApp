import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICancel } from '../cancel.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-cancel-detail',
  templateUrl: './cancel-detail.component.html',
})
export class CancelDetailComponent implements OnInit {
  cancel: ICancel | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cancel }) => {
      this.cancel = cancel;
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
