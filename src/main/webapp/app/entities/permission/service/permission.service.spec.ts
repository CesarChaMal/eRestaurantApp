import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPermission, Permission } from '../permission.model';

import { PermissionService } from './permission.service';

describe('Permission Service', () => {
  let service: PermissionService;
  let httpMock: HttpTestingController;
  let elemDefault: IPermission;
  let expectedResult: IPermission | IPermission[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PermissionService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      description: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find('ABC').subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Permission', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Permission()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Permission', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Permission', () => {
      const patchObject = Object.assign(
        {
          description: 'BBBBBB',
        },
        new Permission()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Permission', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Permission', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPermissionToCollectionIfMissing', () => {
      it('should add a Permission to an empty array', () => {
        const permission: IPermission = { id: 'ABC' };
        expectedResult = service.addPermissionToCollectionIfMissing([], permission);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(permission);
      });

      it('should not add a Permission to an array that contains it', () => {
        const permission: IPermission = { id: 'ABC' };
        const permissionCollection: IPermission[] = [
          {
            ...permission,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addPermissionToCollectionIfMissing(permissionCollection, permission);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Permission to an array that doesn't contain it", () => {
        const permission: IPermission = { id: 'ABC' };
        const permissionCollection: IPermission[] = [{ id: 'CBA' }];
        expectedResult = service.addPermissionToCollectionIfMissing(permissionCollection, permission);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(permission);
      });

      it('should add only unique Permission to an array', () => {
        const permissionArray: IPermission[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'f7de1907-cfb5-43a7-9553-e01fe0278ece' }];
        const permissionCollection: IPermission[] = [{ id: 'ABC' }];
        expectedResult = service.addPermissionToCollectionIfMissing(permissionCollection, ...permissionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const permission: IPermission = { id: 'ABC' };
        const permission2: IPermission = { id: 'CBA' };
        expectedResult = service.addPermissionToCollectionIfMissing([], permission, permission2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(permission);
        expect(expectedResult).toContain(permission2);
      });

      it('should accept null and undefined values', () => {
        const permission: IPermission = { id: 'ABC' };
        expectedResult = service.addPermissionToCollectionIfMissing([], null, permission, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(permission);
      });

      it('should return initial array if no Permission is added', () => {
        const permissionCollection: IPermission[] = [{ id: 'ABC' }];
        expectedResult = service.addPermissionToCollectionIfMissing(permissionCollection, undefined, null);
        expect(expectedResult).toEqual(permissionCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
