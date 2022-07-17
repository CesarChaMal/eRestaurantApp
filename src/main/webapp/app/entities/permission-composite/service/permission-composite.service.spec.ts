import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPermissionComposite, PermissionComposite } from '../permission-composite.model';

import { PermissionCompositeService } from './permission-composite.service';

describe('PermissionComposite Service', () => {
  let service: PermissionCompositeService;
  let httpMock: HttpTestingController;
  let elemDefault: IPermissionComposite;
  let expectedResult: IPermissionComposite | IPermissionComposite[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PermissionCompositeService);
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

    it('should create a PermissionComposite', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new PermissionComposite()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PermissionComposite', () => {
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

    it('should partial update a PermissionComposite', () => {
      const patchObject = Object.assign({}, new PermissionComposite());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PermissionComposite', () => {
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

    it('should delete a PermissionComposite', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPermissionCompositeToCollectionIfMissing', () => {
      it('should add a PermissionComposite to an empty array', () => {
        const permissionComposite: IPermissionComposite = { id: 'ABC' };
        expectedResult = service.addPermissionCompositeToCollectionIfMissing([], permissionComposite);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(permissionComposite);
      });

      it('should not add a PermissionComposite to an array that contains it', () => {
        const permissionComposite: IPermissionComposite = { id: 'ABC' };
        const permissionCompositeCollection: IPermissionComposite[] = [
          {
            ...permissionComposite,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addPermissionCompositeToCollectionIfMissing(permissionCompositeCollection, permissionComposite);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PermissionComposite to an array that doesn't contain it", () => {
        const permissionComposite: IPermissionComposite = { id: 'ABC' };
        const permissionCompositeCollection: IPermissionComposite[] = [{ id: 'CBA' }];
        expectedResult = service.addPermissionCompositeToCollectionIfMissing(permissionCompositeCollection, permissionComposite);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(permissionComposite);
      });

      it('should add only unique PermissionComposite to an array', () => {
        const permissionCompositeArray: IPermissionComposite[] = [
          { id: 'ABC' },
          { id: 'CBA' },
          { id: '70bb893e-1226-4e5d-9a66-e3b27fbc25af' },
        ];
        const permissionCompositeCollection: IPermissionComposite[] = [{ id: 'ABC' }];
        expectedResult = service.addPermissionCompositeToCollectionIfMissing(permissionCompositeCollection, ...permissionCompositeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const permissionComposite: IPermissionComposite = { id: 'ABC' };
        const permissionComposite2: IPermissionComposite = { id: 'CBA' };
        expectedResult = service.addPermissionCompositeToCollectionIfMissing([], permissionComposite, permissionComposite2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(permissionComposite);
        expect(expectedResult).toContain(permissionComposite2);
      });

      it('should accept null and undefined values', () => {
        const permissionComposite: IPermissionComposite = { id: 'ABC' };
        expectedResult = service.addPermissionCompositeToCollectionIfMissing([], null, permissionComposite, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(permissionComposite);
      });

      it('should return initial array if no PermissionComposite is added', () => {
        const permissionCompositeCollection: IPermissionComposite[] = [{ id: 'ABC' }];
        expectedResult = service.addPermissionCompositeToCollectionIfMissing(permissionCompositeCollection, undefined, null);
        expect(expectedResult).toEqual(permissionCompositeCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
