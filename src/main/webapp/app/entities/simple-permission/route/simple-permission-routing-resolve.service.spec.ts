import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ISimplePermission, SimplePermission } from '../simple-permission.model';
import { SimplePermissionService } from '../service/simple-permission.service';

import { SimplePermissionRoutingResolveService } from './simple-permission-routing-resolve.service';

describe('SimplePermission routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: SimplePermissionRoutingResolveService;
  let service: SimplePermissionService;
  let resultSimplePermission: ISimplePermission | undefined;

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
    routingResolveService = TestBed.inject(SimplePermissionRoutingResolveService);
    service = TestBed.inject(SimplePermissionService);
    resultSimplePermission = undefined;
  });

  describe('resolve', () => {
    it('should return ISimplePermission returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultSimplePermission = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultSimplePermission).toEqual({ id: 'ABC' });
    });

    it('should return new ISimplePermission if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultSimplePermission = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultSimplePermission).toEqual(new SimplePermission());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as SimplePermission })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultSimplePermission = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultSimplePermission).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
