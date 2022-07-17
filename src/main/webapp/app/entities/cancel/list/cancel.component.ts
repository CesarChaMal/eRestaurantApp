import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICancel } from '../cancel.model';
import { CancelService } from '../service/cancel.service';
import { CancelDeleteDialogComponent } from '../delete/cancel-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-cancel',
  templateUrl: './cancel.component.html',
})
export class CancelComponent implements OnInit {
  cancels?: ICancel[];
  isLoading = false;
  currentSearch: string;

  constructor(
    protected cancelService: CancelService,
    protected dataUtils: DataUtils,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.cancelService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<ICancel[]>) => {
            this.isLoading = false;
            this.cancels = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.cancelService.query().subscribe({
      next: (res: HttpResponse<ICancel[]>) => {
        this.isLoading = false;
        this.cancels = res.body ?? [];
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

  trackId(_index: number, item: ICancel): string {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(cancel: ICancel): void {
    const modalRef = this.modalService.open(CancelDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.cancel = cancel;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
