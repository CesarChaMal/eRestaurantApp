import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDiscount } from '../discount.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-discount-detail',
  templateUrl: './discount-detail.component.html',
})
export class DiscountDetailComponent implements OnInit {
  discount: IDiscount | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ discount }) => {
      this.discount = discount;
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
