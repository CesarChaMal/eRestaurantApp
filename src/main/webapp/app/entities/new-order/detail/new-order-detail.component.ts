import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { INewOrder } from '../new-order.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-new-order-detail',
  templateUrl: './new-order-detail.component.html',
})
export class NewOrderDetailComponent implements OnInit {
  newOrder: INewOrder | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ newOrder }) => {
      this.newOrder = newOrder;
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
