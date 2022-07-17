import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRole, Role } from '../role.model';

import { RoleService } from './role.service';

describe('Role Service', () => {
  let service: RoleService;
  let httpMock: HttpTestingController;
  let elemDefault: IRole;
  let expectedResult: IRole | IRole[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RoleService);
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

    it('should create a Role', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Role()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Role', () => {
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

    it('should partial update a Role', () => {
      const patchObject = Object.assign(
        {
          description: 'BBBBBB',
        },
        new Role()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Role', () => {
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

    it('should delete a Role', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addRoleToCollectionIfMissing', () => {
      it('should add a Role to an empty array', () => {
        const role: IRole = { id: 'ABC' };
        expectedResult = service.addRoleToCollectionIfMissing([], role);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(role);
      });

      it('should not add a Role to an array that contains it', () => {
        const role: IRole = { id: 'ABC' };
        const roleCollection: IRole[] = [
          {
            ...role,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addRoleToCollectionIfMissing(roleCollection, role);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Role to an array that doesn't contain it", () => {
        const role: IRole = { id: 'ABC' };
        const roleCollection: IRole[] = [{ id: 'CBA' }];
        expectedResult = service.addRoleToCollectionIfMissing(roleCollection, role);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(role);
      });

      it('should add only unique Role to an array', () => {
        const roleArray: IRole[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'fb2266a9-ef15-4dc0-a7d4-9d173ec0647e' }];
        const roleCollection: IRole[] = [{ id: 'ABC' }];
        expectedResult = service.addRoleToCollectionIfMissing(roleCollection, ...roleArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const role: IRole = { id: 'ABC' };
        const role2: IRole = { id: 'CBA' };
        expectedResult = service.addRoleToCollectionIfMissing([], role, role2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(role);
        expect(expectedResult).toContain(role2);
      });

      it('should accept null and undefined values', () => {
        const role: IRole = { id: 'ABC' };
        expectedResult = service.addRoleToCollectionIfMissing([], null, role, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(role);
      });

      it('should return initial array if no Role is added', () => {
        const roleCollection: IRole[] = [{ id: 'ABC' }];
        expectedResult = service.addRoleToCollectionIfMissing(roleCollection, undefined, null);
        expect(expectedResult).toEqual(roleCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
