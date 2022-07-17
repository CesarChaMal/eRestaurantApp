import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IRefunded } from '../refunded.model';
import { RefundedService } from '../service/refunded.service';
import { RefundedDeleteDialogComponent } from '../delete/refunded-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-refunded',
  templateUrl: './refunded.component.html',
})
export class RefundedComponent implements OnInit {
  refundeds?: IRefunded[];
  isLoading = false;
  currentSearch: string;

  constructor(
    protected refundedService: RefundedService,
    protected dataUtils: DataUtils,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.refundedService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IRefunded[]>) => {
            this.isLoading = false;
            this.refundeds = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.refundedService.query().subscribe({
      next: (res: HttpResponse<IRefunded[]>) => {
        this.isLoading = false;
        this.refundeds = res.body ?? [];
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

  trackId(_index: number, item: IRefunded): string {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(refunded: IRefunded): void {
    const modalRef = this.modalService.open(RefundedDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.refunded = refunded;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
