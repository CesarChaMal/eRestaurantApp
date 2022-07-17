import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IPermissionComposite } from '../permission-composite.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-permission-composite-detail',
  templateUrl: './permission-composite-detail.component.html',
})
export class PermissionCompositeDetailComponent implements OnInit {
  permissionComposite: IPermissionComposite | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ permissionComposite }) => {
      this.permissionComposite = permissionComposite;
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
