import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAppAd } from '../app-ad.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-app-ad-detail',
  templateUrl: './app-ad-detail.component.html',
})
export class AppAdDetailComponent implements OnInit {
  appAd: IAppAd | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ appAd }) => {
      this.appAd = appAd;
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
