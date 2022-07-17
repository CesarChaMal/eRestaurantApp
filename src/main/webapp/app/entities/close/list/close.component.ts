import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IClose } from '../close.model';
import { CloseService } from '../service/close.service';
import { CloseDeleteDialogComponent } from '../delete/close-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-close',
  templateUrl: './close.component.html',
})
export class CloseComponent implements OnInit {
  closes?: IClose[];
  isLoading = false;
  currentSearch: string;

  constructor(
    protected closeService: CloseService,
    protected dataUtils: DataUtils,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.closeService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IClose[]>) => {
            this.isLoading = false;
            this.closes = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.closeService.query().subscribe({
      next: (res: HttpResponse<IClose[]>) => {
        this.isLoading = false;
        this.closes = res.body ?? [];
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

  trackId(_index: number, item: IClose): string {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(close: IClose): void {
    const modalRef = this.modalService.open(CloseDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.close = close;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
