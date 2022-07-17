import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IRestaurantAd, RestaurantAd } from '../restaurant-ad.model';
import { RestaurantAdService } from '../service/restaurant-ad.service';

import { RestaurantAdRoutingResolveService } from './restaurant-ad-routing-resolve.service';

describe('RestaurantAd routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: RestaurantAdRoutingResolveService;
  let service: RestaurantAdService;
  let resultRestaurantAd: IRestaurantAd | undefined;

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
    routingResolveService = TestBed.inject(RestaurantAdRoutingResolveService);
    service = TestBed.inject(RestaurantAdService);
    resultRestaurantAd = undefined;
  });

  describe('resolve', () => {
    it('should return IRestaurantAd returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultRestaurantAd = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultRestaurantAd).toEqual({ id: 'ABC' });
    });

    it('should return new IRestaurantAd if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultRestaurantAd = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultRestaurantAd).toEqual(new RestaurantAd());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as RestaurantAd })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultRestaurantAd = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultRestaurantAd).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
