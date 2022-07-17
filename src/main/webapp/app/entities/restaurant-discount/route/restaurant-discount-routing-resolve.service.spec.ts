import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IRestaurantDiscount, RestaurantDiscount } from '../restaurant-discount.model';
import { RestaurantDiscountService } from '../service/restaurant-discount.service';

import { RestaurantDiscountRoutingResolveService } from './restaurant-discount-routing-resolve.service';

describe('RestaurantDiscount routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: RestaurantDiscountRoutingResolveService;
  let service: RestaurantDiscountService;
  let resultRestaurantDiscount: IRestaurantDiscount | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(RestaurantDiscountRoutingResolveService);
    service = TestBed.inject(RestaurantDiscountService);
    resultRestaurantDiscount = undefined;
  });

  describe('resolve', () => {
    it('should return IRestaurantDiscount returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultRestaurantDiscount = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultRestaurantDiscount).toEqual({ id: 'ABC' });
    });

    it('should return new IRestaurantDiscount if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultRestaurantDiscount = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultRestaurantDiscount).toEqual(new RestaurantDiscount());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as RestaurantDiscount })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultRestaurantDiscount = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultRestaurantDiscount).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
