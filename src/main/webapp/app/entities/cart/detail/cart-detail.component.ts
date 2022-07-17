import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICart } from '../cart.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-cart-detail',
  templateUrl: './cart-detail.component.html',
})
export class CartDetailComponent implements OnInit {
  cart: ICart | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cart }) => {
      this.cart = cart;
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
