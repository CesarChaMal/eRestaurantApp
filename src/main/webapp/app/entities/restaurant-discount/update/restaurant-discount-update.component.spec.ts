import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RestaurantDiscountService } from '../service/restaurant-discount.service';
import { IRestaurantDiscount, RestaurantDiscount } from '../restaurant-discount.model';
import { IProducts } from 'app/entities/products/products.model';
import { ProductsService } from 'app/entities/products/service/products.service';

import { RestaurantDiscountUpdateComponent } from './restaurant-discount-update.component';

describe('RestaurantDiscount Management Update Component', () => {
  let comp: RestaurantDiscountUpdateComponent;
  let fixture: ComponentFixture<RestaurantDiscountUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let restaurantDiscountService: RestaurantDiscountService;
  let productsService: ProductsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RestaurantDiscountUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(RestaurantDiscountUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RestaurantDiscountUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    restaurantDiscountService = TestBed.inject(RestaurantDiscountService);
    productsService = TestBed.inject(ProductsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Products query and add missing value', () => {
      const restaurantDiscount: IRestaurantDiscount = { id: 'CBA' };
      const products: IProducts = { id: 'e69ebb34-44c6-4fc5-a515-605c1f3f97c7' };
      restaurantDiscount.products = products;

      const productsCollection: IProducts[] = [{ id: '1ac375ad-35de-4e62-81ff-26ab5d299836' }];
      jest.spyOn(productsService, 'query').mockReturnValue(of(new HttpResponse({ body: productsCollection })));
      const additionalProducts = [products];
      const expectedCollection: IProducts[] = [...additionalProducts, ...productsCollection];
      jest.spyOn(productsService, 'addProductsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ restaurantDiscount });
      comp.ngOnInit();

      expect(productsService.query).toHaveBeenCalled();
      expect(productsService.addProductsToCollectionIfMissing).toHaveBeenCalledWith(productsCollection, ...additionalProducts);
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const restaurantDiscount: IRestaurantDiscount = { id: 'CBA' };
      const products: IProducts = { id: 'c051cc09-1210-431c-85da-91fd1e0e424f' };
      restaurantDiscount.products = products;

      activatedRoute.data = of({ restaurantDiscount });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(restaurantDiscount));
      expect(comp.productsSharedCollection).toContain(products);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<RestaurantDiscount>>();
      const restaurantDiscount = { id: 'ABC' };
      jest.spyOn(restaurantDiscountService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ restaurantDiscount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: restaurantDiscount }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(restaurantDiscountService.update).toHaveBeenCalledWith(restaurantDiscount);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<RestaurantDiscount>>();
      const restaurantDiscount = new RestaurantDiscount();
      jest.spyOn(restaurantDiscountService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ restaurantDiscount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: restaurantDiscount }));
      saveSubject.complete();

      // THEN
      expect(restaurantDiscountService.create).toHaveBeenCalledWith(restaurantDiscount);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<RestaurantDiscount>>();
      const restaurantDiscount = { id: 'ABC' };
      jest.spyOn(restaurantDiscountService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ restaurantDiscount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(restaurantDiscountService.update).toHaveBeenCalledWith(restaurantDiscount);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackProductsById', () => {
      it('Should return tracked Products primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackProductsById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
