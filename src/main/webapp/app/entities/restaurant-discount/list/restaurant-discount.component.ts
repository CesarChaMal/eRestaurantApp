import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IRestaurantDiscount } from '../restaurant-discount.model';
import { RestaurantDiscountService } from '../service/restaurant-discount.service';
import { RestaurantDiscountDeleteDialogComponent } from '../delete/restaurant-discount-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-restaurant-discount',
  templateUrl: './restaurant-discount.component.html',
})
export class RestaurantDiscountComponent implements OnInit {
  restaurantDiscounts?: IRestaurantDiscount[];
  isLoading = false;
  currentSearch: string;

  constructor(
    protected restaurantDiscountService: RestaurantDiscountService,
    protected dataUtils: DataUtils,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.restaurantDiscountService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IRestaurantDiscount[]>) => {
            this.isLoading = false;
            this.restaurantDiscounts = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.restaurantDiscountService.query().subscribe({
      next: (res: HttpResponse<IRestaurantDiscount[]>) => {
        this.isLoading = false;
        this.restaurantDiscounts = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  search(query: string): void {
    this.currentSearch = query;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(_index: number, item: IRestaurantDiscount): string {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(restaurantDiscount: IRestaurantDiscount): void {
    const modalRef = this.modalService.open(RestaurantDiscountDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.restaurantDiscount = restaurantDiscount;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
