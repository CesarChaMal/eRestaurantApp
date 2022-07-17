import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CustomerProfileService } from '../service/customer-profile.service';

import { CustomerProfileComponent } from './customer-profile.component';

describe('CustomerProfile Management Component', () => {
  let comp: CustomerProfileComponent;
  let fixture: ComponentFixture<CustomerProfileComponent>;
  let service: CustomerProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'customer-profile', component: CustomerProfileComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [CustomerProfileComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(CustomerProfileComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CustomerProfileComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CustomerProfileService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 'ABC' }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.customerProfiles?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
