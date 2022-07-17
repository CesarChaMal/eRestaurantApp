import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { RestaurantAdService } from '../service/restaurant-ad.service';

import { RestaurantAdComponent } from './restaurant-ad.component';

describe('RestaurantAd Management Component', () => {
  let comp: RestaurantAdComponent;
  let fixture: ComponentFixture<RestaurantAdComponent>;
  let service: RestaurantAdService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'restaurant-ad', component: RestaurantAdComponent }]), HttpClientTestingModule],
      declarations: [RestaurantAdComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(RestaurantAdComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RestaurantAdComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RestaurantAdService);

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
    expect(comp.restaurantAds?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
