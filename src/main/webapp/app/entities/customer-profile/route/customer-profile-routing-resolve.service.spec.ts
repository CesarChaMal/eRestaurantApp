import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ICustomerProfile, CustomerProfile } from '../customer-profile.model';
import { CustomerProfileService } from '../service/customer-profile.service';

import { CustomerProfileRoutingResolveService } from './customer-profile-routing-resolve.service';

describe('CustomerProfile routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: CustomerProfileRoutingResolveService;
  let service: CustomerProfileService;
  let resultCustomerProfile: ICustomerProfile | undefined;

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
    routingResolveService = TestBed.inject(CustomerProfileRoutingResolveService);
    service = TestBed.inject(CustomerProfileService);
    resultCustomerProfile = undefined;
  });

  describe('resolve', () => {
    it('should return ICustomerProfile returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCustomerProfile = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultCustomerProfile).toEqual({ id: 'ABC' });
    });

    it('should return new ICustomerProfile if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCustomerProfile = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultCustomerProfile).toEqual(new CustomerProfile());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as CustomerProfile })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultCustomerProfile = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultCustomerProfile).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
