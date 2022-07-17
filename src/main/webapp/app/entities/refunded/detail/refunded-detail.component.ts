import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRefunded } from '../refunded.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-refunded-detail',
  templateUrl: './refunded-detail.component.html',
})
export class RefundedDetailComponent implements OnInit {
  refunded: IRefunded | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ refunded }) => {
      this.refunded = refunded;
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
