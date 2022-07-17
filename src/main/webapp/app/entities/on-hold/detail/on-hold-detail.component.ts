import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOnHold } from '../on-hold.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-on-hold-detail',
  templateUrl: './on-hold-detail.component.html',
})
export class OnHoldDetailComponent implements OnInit {
  onHold: IOnHold | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ onHold }) => {
      this.onHold = onHold;
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
