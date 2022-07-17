import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICustomerProfile, CustomerProfile } from '../customer-profile.model';

import { CustomerProfileService } from './customer-profile.service';

describe('CustomerProfile Service', () => {
  let service: CustomerProfileService;
  let httpMock: HttpTestingController;
  let elemDefault: ICustomerProfile;
  let expectedResult: ICustomerProfile | ICustomerProfile[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CustomerProfileService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      name: 'AAAAAAA',
      location: 'AAAAAAA',
      locationRange: 'AAAAAAA',
      referals: 'AAAAAAA',
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

    it('should create a CustomerProfile', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new CustomerProfile()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a CustomerProfile', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          name: 'BBBBBB',
          location: 'BBBBBB',
          locationRange: 'BBBBBB',
          referals: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a CustomerProfile', () => {
      const patchObject = Object.assign(
        {
          referals: 'BBBBBB',
        },
        new CustomerProfile()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of CustomerProfile', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          name: 'BBBBBB',
          location: 'BBBBBB',
          locationRange: 'BBBBBB',
          referals: 'BBBBBB',
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

    it('should delete a CustomerProfile', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCustomerProfileToCollectionIfMissing', () => {
      it('should add a CustomerProfile to an empty array', () => {
        const customerProfile: ICustomerProfile = { id: 'ABC' };
        expectedResult = service.addCustomerProfileToCollectionIfMissing([], customerProfile);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(customerProfile);
      });

      it('should not add a CustomerProfile to an array that contains it', () => {
        const customerProfile: ICustomerProfile = { id: 'ABC' };
        const customerProfileCollection: ICustomerProfile[] = [
          {
            ...customerProfile,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addCustomerProfileToCollectionIfMissing(customerProfileCollection, customerProfile);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a CustomerProfile to an array that doesn't contain it", () => {
        const customerProfile: ICustomerProfile = { id: 'ABC' };
        const customerProfileCollection: ICustomerProfile[] = [{ id: 'CBA' }];
        expectedResult = service.addCustomerProfileToCollectionIfMissing(customerProfileCollection, customerProfile);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(customerProfile);
      });

      it('should add only unique CustomerProfile to an array', () => {
        const customerProfileArray: ICustomerProfile[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: '4b21aa06-a6d4-48e2-a228-40329d8e5798' }];
        const customerProfileCollection: ICustomerProfile[] = [{ id: 'ABC' }];
        expectedResult = service.addCustomerProfileToCollectionIfMissing(customerProfileCollection, ...customerProfileArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const customerProfile: ICustomerProfile = { id: 'ABC' };
        const customerProfile2: ICustomerProfile = { id: 'CBA' };
        expectedResult = service.addCustomerProfileToCollectionIfMissing([], customerProfile, customerProfile2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(customerProfile);
        expect(expectedResult).toContain(customerProfile2);
      });

      it('should accept null and undefined values', () => {
        const customerProfile: ICustomerProfile = { id: 'ABC' };
        expectedResult = service.addCustomerProfileToCollectionIfMissing([], null, customerProfile, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(customerProfile);
      });

      it('should return initial array if no CustomerProfile is added', () => {
        const customerProfileCollection: ICustomerProfile[] = [{ id: 'ABC' }];
        expectedResult = service.addCustomerProfileToCollectionIfMissing(customerProfileCollection, undefined, null);
        expect(expectedResult).toEqual(customerProfileCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
