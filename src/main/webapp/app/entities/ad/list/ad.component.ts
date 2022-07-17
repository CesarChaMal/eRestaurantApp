import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IAd } from '../ad.model';
import { AdService } from '../service/ad.service';
import { AdDeleteDialogComponent } from '../delete/ad-delete-dialog.component';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-ad',
  templateUrl: './ad.component.html',
})
export class AdComponent implements OnInit {
  ads?: IAd[];
  isLoading = false;
  currentSearch: string;

  constructor(
    protected adService: AdService,
    protected dataUtils: DataUtils,
    protected modalService: NgbModal,
    protected activatedRoute: ActivatedRoute
  ) {
    this.currentSearch = this.activatedRoute.snapshot.queryParams['search'] ?? '';
  }

  loadAll(): void {
    this.isLoading = true;
    if (this.currentSearch) {
      this.adService
        .search({
          query: this.currentSearch,
        })
        .subscribe({
          next: (res: HttpResponse<IAd[]>) => {
            this.isLoading = false;
            this.ads = res.body ?? [];
          },
          error: () => {
            this.isLoading = false;
          },
        });
      return;
    }

    this.adService.query().subscribe({
      next: (res: HttpResponse<IAd[]>) => {
        this.isLoading = false;
        this.ads = res.body ?? [];
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

  trackId(_index: number, item: IAd): string {
    return item.id!;
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(ad: IAd): void {
    const modalRef = this.modalService.open(AdDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.ad = ad;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
