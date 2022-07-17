import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IPermissionComposite, PermissionComposite } from '../permission-composite.model';
import { PermissionCompositeService } from '../service/permission-composite.service';

import { PermissionCompositeRoutingResolveService } from './permission-composite-routing-resolve.service';

describe('PermissionComposite routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: PermissionCompositeRoutingResolveService;
  let service: PermissionCompositeService;
  let resultPermissionComposite: IPermissionComposite | undefined;

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
    routingResolveService = TestBed.inject(PermissionCompositeRoutingResolveService);
    service = TestBed.inject(PermissionCompositeService);
    resultPermissionComposite = undefined;
  });

  describe('resolve', () => {
    it('should return IPermissionComposite returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultPermissionComposite = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultPermissionComposite).toEqual({ id: 'ABC' });
    });

    it('should return new IPermissionComposite if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultPermissionComposite = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultPermissionComposite).toEqual(new PermissionComposite());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as PermissionComposite })));
      mockActivatedRouteSnapshot.params = { id: 'ABC' };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultPermissionComposite = result;
      });

      // THEN
      expect(service.find).toBeCalledWith('ABC');
      expect(resultPermissionComposite).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
