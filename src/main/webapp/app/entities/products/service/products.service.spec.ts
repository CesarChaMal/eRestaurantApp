import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProducts, Products } from '../products.model';

import { ProductsService } from './products.service';

describe('Products Service', () => {
  let service: ProductsService;
  let httpMock: HttpTestingController;
  let elemDefault: IProducts;
  let expectedResult: IProducts | IProducts[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProductsService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 'AAAAAAA',
      name: 'AAAAAAA',
      description: 'AAAAAAA',
      imageContentType: 'image/png',
      image: 'AAAAAAA',
      estimatedPreparaingTime: 0,
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

    it('should create a Products', () => {
      const returnedFromService = Object.assign(
        {
          id: 'ID',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Products()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Products', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          name: 'BBBBBB',
          description: 'BBBBBB',
          image: 'BBBBBB',
          estimatedPreparaingTime: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Products', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          description: 'BBBBBB',
        },
        new Products()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Products', () => {
      const returnedFromService = Object.assign(
        {
          id: 'BBBBBB',
          name: 'BBBBBB',
          description: 'BBBBBB',
          image: 'BBBBBB',
          estimatedPreparaingTime: 1,
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

    it('should delete a Products', () => {
      service.delete('ABC').subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addProductsToCollectionIfMissing', () => {
      it('should add a Products to an empty array', () => {
        const products: IProducts = { id: 'ABC' };
        expectedResult = service.addProductsToCollectionIfMissing([], products);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(products);
      });

      it('should not add a Products to an array that contains it', () => {
        const products: IProducts = { id: 'ABC' };
        const productsCollection: IProducts[] = [
          {
            ...products,
          },
          { id: 'CBA' },
        ];
        expectedResult = service.addProductsToCollectionIfMissing(productsCollection, products);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Products to an array that doesn't contain it", () => {
        const products: IProducts = { id: 'ABC' };
        const productsCollection: IProducts[] = [{ id: 'CBA' }];
        expectedResult = service.addProductsToCollectionIfMissing(productsCollection, products);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(products);
      });

      it('should add only unique Products to an array', () => {
        const productsArray: IProducts[] = [{ id: 'ABC' }, { id: 'CBA' }, { id: 'dd9942a7-c0e3-421f-a4d8-11560a4468e7' }];
        const productsCollection: IProducts[] = [{ id: 'ABC' }];
        expectedResult = service.addProductsToCollectionIfMissing(productsCollection, ...productsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const products: IProducts = { id: 'ABC' };
        const products2: IProducts = { id: 'CBA' };
        expectedResult = service.addProductsToCollectionIfMissing([], products, products2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(products);
        expect(expectedResult).toContain(products2);
      });

      it('should accept null and undefined values', () => {
        const products: IProducts = { id: 'ABC' };
        expectedResult = service.addProductsToCollectionIfMissing([], null, products, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(products);
      });

      it('should return initial array if no Products is added', () => {
        const productsCollection: IProducts[] = [{ id: 'ABC' }];
        expectedResult = service.addProductsToCollectionIfMissing(productsCollection, undefined, null);
        expect(expectedResult).toEqual(productsCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
