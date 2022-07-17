import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICustomerProfile } from '../customer-profile.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-customer-profile-detail',
  templateUrl: './customer-profile-detail.component.html',
})
export class CustomerProfileDetailComponent implements OnInit {
  customerProfile: ICustomerProfile | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ customerProfile }) => {
      this.customerProfile = customerProfile;
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
