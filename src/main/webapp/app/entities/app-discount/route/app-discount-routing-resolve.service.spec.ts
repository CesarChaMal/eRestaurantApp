import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IAppDiscount, AppDiscount } from '../app-discount.model';
import { AppDiscountService } from '../service/app-discount.service';

import { AppDiscountRoutingResolveService } from './app-discount-routing-resolve.service';

describe('AppDiscount routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: AppDiscountRoutingResolveService;
  let service: AppDiscountService;
  let resultAppDiscount: IAppDiscount | undefined;

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
    routingResolveService = TestBed.inject(AppDiscountRoutingResolveService);
    service = TestBed.inject(AppDiscountService);
    resultAppDiscount = undefined;
  });

  describe('resolve', () => {
    it('should return IAppDiscount returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultAppDiscount = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultAppDiscount).toEqual({ id: 'ABC' });
    });

    it('should return new IAppDiscount if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultAppDiscount = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultAppDiscount).toEqual(new AppDiscount());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as AppDiscount })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultAppDiscount = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultAppDiscount).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
