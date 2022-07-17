import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { RestaurantDiscountService } from '../service/restaurant-discount.service';

import { RestaurantDiscountComponent } from './restaurant-discount.component';

describe('RestaurantDiscount Management Component', () => {
  let comp: RestaurantDiscountComponent;
  let fixture: ComponentFixture<RestaurantDiscountComponent>;
  let service: RestaurantDiscountService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'restaurant-discount', component: RestaurantDiscountComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [RestaurantDiscountComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(RestaurantDiscountComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RestaurantDiscountComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RestaurantDiscountService);

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
    expect(comp.restaurantDiscounts?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
