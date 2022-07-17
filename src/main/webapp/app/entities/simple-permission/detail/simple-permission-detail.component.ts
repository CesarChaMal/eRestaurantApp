import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISimplePermission } from '../simple-permission.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-simple-permission-detail',
  templateUrl: './simple-permission-detail.component.html',
})
export class SimplePermissionDetailComponent implements OnInit {
  simplePermission: ISimplePermission | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ simplePermission }) => {
      this.simplePermission = simplePermission;
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
