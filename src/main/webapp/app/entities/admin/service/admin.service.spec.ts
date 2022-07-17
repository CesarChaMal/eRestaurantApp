import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAdmin, Admin } from '../admin.model';

import { AdminService } from './admin.service';

describe('Admin Service', () => {
  let service: AdminService;
  let httpMock: HttpTestingController;
  let elemDefault: IAdmin;
  let expectedResult: IAdmin | IAdmin[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AdminService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      name: 'AAAAAAA',
      description: 'AAAAAAA',
      imageContentType: 'image/png',
      image: 'AAAAAAA',
      email: 'AAAAAAA',
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

    it('should create a Admin', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Admin()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Admin', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          name: 'BBBBBB',
          description: 'BBBBBB',
          image: 'BBBBBB',
          email: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Admin', () => {
      const patchObject = Object.assign(
        {
          image: 'BBBBBB',
        },
        new Admin()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Admin', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          name: 'BBBBBB',
          description: 'BBBBBB',
          image: 'BBBBBB',
          email: 'BBBBBB',
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

    it('should delete a Admin', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAdminToCollectionIfMissing', () => {
      it('should add a Admin to an empty array', () => {
        const admin: IAdmin = { id: 'ABC' };
        expectedResult = service.addAdminToCollectionIfMissing([], admin);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(admin);
      });

      it('should not add a Admin to an array that contains it', () => {
        const admin: IAdmin = { id: 'ABC' };
        const adminCollection: IAdmin[] = [
          {
            ...admin,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addAdminToCollectionIfMissing(adminCollection, admin);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Admin to an array that doesn't contain it", () => {
        const admin: IAdmin = { id: 'ABC' };
        const adminCollection: IAdmin[] = [{ id: 'CBA' }];
        expectedResult = service.addAdminToCollectionIfMissing(adminCollection, admin);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(admin);
      });

      it('should add only unique Admin to an array', () => {
        const adminArray: IAdmin[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '212ca6a6-6079-491d-9eff-6723e6cc581d' }];
        const adminCollection: IAdmin[] = [{ id: 'ABC' }];
        expectedResult = service.addAdminToCollectionIfMissing(adminCollection, ...adminArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const admin: IAdmin = { id: 'ABC' };
        const admin2: IAdmin = { id: 'CBA' };
        expectedResult = service.addAdminToCollectionIfMissing([], admin, admin2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(admin);
        expect(expectedResult).toContain(admin2);
      });

      it('should accept null and undefined values', () => {
        const admin: IAdmin = { id: 'ABC' };
        expectedResult = service.addAdminToCollectionIfMissing([], null, admin, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(admin);
      });

      it('should return initial array if no Admin is added', () => {
        const adminCollection: IAdmin[] = [{ id: 'ABC' }];
        expectedResult = service.addAdminToCollectionIfMissing(adminCollection, undefined, null);
        expect(expectedResult).toEqual(adminCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
