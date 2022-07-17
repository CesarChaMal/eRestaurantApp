import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISimplePermission, SimplePermission } from '../simple-permission.model';

import { SimplePermissionService } from './simple-permission.service';

describe('SimplePermission Service', () => {
  let service: SimplePermissionService;
  let httpMock: HttpTestingController;
  let elemDefault: ISimplePermission;
  let expectedResult: ISimplePermission | ISimplePermission[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SimplePermissionService);
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

    it('should create a SimplePermission', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new SimplePermission()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SimplePermission', () => {
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

    it('should partial update a SimplePermission', () => {
      const patchObject = Object.assign(
        {
          description: 'BBBBBB',
        },
        new SimplePermission()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SimplePermission', () => {
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

    it('should delete a SimplePermission', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSimplePermissionToCollectionIfMissing', () => {
      it('should add a SimplePermission to an empty array', () => {
        const simplePermission: ISimplePermission = { id: 'ABC' };
        expectedResult = service.addSimplePermissionToCollectionIfMissing([], simplePermission);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(simplePermission);
      });

      it('should not add a SimplePermission to an array that contains it', () => {
        const simplePermission: ISimplePermission = { id: 'ABC' };
        const simplePermissionCollection: ISimplePermission[] = [
          {
            ...simplePermission,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addSimplePermissionToCollectionIfMissing(simplePermissionCollection, simplePermission);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SimplePermission to an array that doesn't contain it", () => {
        const simplePermission: ISimplePermission = { id: 'ABC' };
        const simplePermissionCollection: ISimplePermission[] = [{ id: 'CBA' }];
        expectedResult = service.addSimplePermissionToCollectionIfMissing(simplePermissionCollection, simplePermission);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(simplePermission);
      });

      it('should add only unique SimplePermission to an array', () => {
        const simplePermissionArray: ISimplePermission[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'a793c544-4720-4eeb-ac5f-f1afd359a5b8' }];
        const simplePermissionCollection: ISimplePermission[] = [{ id: 'ABC' }];
        expectedResult = service.addSimplePermissionToCollectionIfMissing(simplePermissionCollection, ...simplePermissionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const simplePermission: ISimplePermission = { id: 'ABC' };
        const simplePermission2: ISimplePermission = { id: 'CBA' };
        expectedResult = service.addSimplePermissionToCollectionIfMissing([], simplePermission, simplePermission2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(simplePermission);
        expect(expectedResult).toContain(simplePermission2);
      });

      it('should accept null and undefined values', () => {
        const simplePermission: ISimplePermission = { id: 'ABC' };
        expectedResult = service.addSimplePermissionToCollectionIfMissing([], null, simplePermission, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(simplePermission);
      });

      it('should return initial array if no SimplePermission is added', () => {
        const simplePermissionCollection: ISimplePermission[] = [{ id: 'ABC' }];
        expectedResult = service.addSimplePermissionToCollectionIfMissing(simplePermissionCollection, undefined, null);
        expect(expectedResult).toEqual(simplePermissionCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
