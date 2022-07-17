import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICustomer, Customer } from '../customer.model';

import { CustomerService } from './customer.service';

describe('Customer Service', () => {
  let service: CustomerService;
  let httpMock: HttpTestingController;
  let elemDefault: ICustomer;
  let expectedResult: ICustomer | ICustomer[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CustomerService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      name: 'AAAAAAA',
      description: 'AAAAAAA',
      imageContentType: 'image/png',
      image: 'AAAAAAA',
      email: 'AAAAAAA',
      age: 0,
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

    it('should create a Customer', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Customer()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Customer', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          name: 'BBBBBB',
          description: 'BBBBBB',
          image: 'BBBBBB',
          email: 'BBBBBB',
          age: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Customer', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          image: 'BBBBBB',
          age: 1,
        },
        new Customer()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Customer', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          name: 'BBBBBB',
          description: 'BBBBBB',
          image: 'BBBBBB',
          email: 'BBBBBB',
          age: 1,
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

    it('should delete a Customer', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addCustomerToCollectionIfMissing', () => {
      it('should add a Customer to an empty array', () => {
        const customer: ICustomer = { id: 'ABC' };
        expectedResult = service.addCustomerToCollectionIfMissing([], customer);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(customer);
      });

      it('should not add a Customer to an array that contains it', () => {
        const customer: ICustomer = { id: 'ABC' };
        const customerCollection: ICustomer[] = [
          {
            ...customer,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addCustomerToCollectionIfMissing(customerCollection, customer);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Customer to an array that doesn't contain it", () => {
        const customer: ICustomer = { id: 'ABC' };
        const customerCollection: ICustomer[] = [{ id: 'CBA' }];
        expectedResult = service.addCustomerToCollectionIfMissing(customerCollection, customer);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(customer);
      });

      it('should add only unique Customer to an array', () => {
        const customerArray: ICustomer[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'a729322f-3e23-4804-be09-92ac31e53c36' }];
        const customerCollection: ICustomer[] = [{ id: 'ABC' }];
        expectedResult = service.addCustomerToCollectionIfMissing(customerCollection, ...customerArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const customer: ICustomer = { id: 'ABC' };
        const customer2: ICustomer = { id: 'CBA' };
        expectedResult = service.addCustomerToCollectionIfMissing([], customer, customer2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(customer);
        expect(expectedResult).toContain(customer2);
      });

      it('should accept null and undefined values', () => {
        const customer: ICustomer = { id: 'ABC' };
        expectedResult = service.addCustomerToCollectionIfMissing([], null, customer, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(customer);
      });

      it('should return initial array if no Customer is added', () => {
        const customerCollection: ICustomer[] = [{ id: 'ABC' }];
        expectedResult = service.addCustomerToCollectionIfMissing(customerCollection, undefined, null);
        expect(expectedResult).toEqual(customerCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
