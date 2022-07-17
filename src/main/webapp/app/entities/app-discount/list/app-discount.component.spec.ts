import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { AppDiscountService } from '../service/app-discount.service';

import { AppDiscountComponent } from './app-discount.component';

describe('AppDiscount Management Component', () => {
  let comp: AppDiscountComponent;
  let fixture: ComponentFixture<AppDiscountComponent>;
  let service: AppDiscountService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'app-discount', component: AppDiscountComponent }]), HttpClientTestingModule],
      declarations: [AppDiscountComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(AppDiscountComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AppDiscountComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(AppDiscountService);

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
    expect(comp.appDiscounts?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
