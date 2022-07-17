import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAppDiscount } from '../app-discount.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-app-discount-detail',
  templateUrl: './app-discount-detail.component.html',
})
export class AppDiscountDetailComponent implements OnInit {
  appDiscount: IAppDiscount | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appDiscount }) => {
      this.appDiscount = appDiscount;
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
