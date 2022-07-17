import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProductsService } from '../service/products.service';
import { IProducts, Products } from '../products.model';
import { ICategories } from 'app/entities/categories/categories.model';
import { CategoriesService } from 'app/entities/categories/service/categories.service';
import { IRestaurant } from 'app/entities/restaurant/restaurant.model';
import { RestaurantService } from 'app/entities/restaurant/service/restaurant.service';
import { ICart } from 'app/entities/cart/cart.model';
import { CartService } from 'app/entities/cart/service/cart.service';

import { ProductsUpdateComponent } from './products-update.component';

describe('Products Management Update Component', () => {
  let comp: ProductsUpdateComponent;
  let fixture: ComponentFixture<ProductsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let productsService: ProductsService;
  let categoriesService: CategoriesService;
  let restaurantService: RestaurantService;
  let cartService: CartService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProductsUpdateComponent],
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
      .overrideTemplate(ProductsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProductsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    productsService = TestBed.inject(ProductsService);
    categoriesService = TestBed.inject(CategoriesService);
    restaurantService = TestBed.inject(RestaurantService);
    cartService = TestBed.inject(CartService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call category query and add missing value', () => {
      const products: IProducts = { id: 'CBA' };
      const category: ICategories = { id: '89b25fca-fcf6-4236-828f-5baa9542e153' };
      products.category = category;

      const categoryCollection: ICategories[] = [{ id: '39c88209-82c9-42c0-a2f0-6ef7227cd5a8' }];
      jest.spyOn(categoriesService, 'query').mockReturnValue(of(new HttpResponse({ body: categoryCollection })));
      const expectedCollection: ICategories[] = [category, ...categoryCollection];
      jest.spyOn(categoriesService, 'addCategoriesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ products });
      comp.ngOnInit();

      expect(categoriesService.query).toHaveBeenCalled();
      expect(categoriesService.addCategoriesToCollectionIfMissing).toHaveBeenCalledWith(categoryCollection, category);
      expect(comp.categoriesCollection).toEqual(expectedCollection);
    });

    it('Should call Restaurant query and add missing value', () => {
      const products: IProducts = { id: 'CBA' };
      const restaurant: IRestaurant = { id: 'd1e5b5ea-6342-4d36-9b31-c88b8ba78b3d' };
      products.restaurant = restaurant;

      const restaurantCollection: IRestaurant[] = [{ id: '958462d4-efd9-4b52-890a-7cd947c31481' }];
      jest.spyOn(restaurantService, 'query').mockReturnValue(of(new HttpResponse({ body: restaurantCollection })));
      const additionalRestaurants = [restaurant];
      const expectedCollection: IRestaurant[] = [...additionalRestaurants, ...restaurantCollection];
      jest.spyOn(restaurantService, 'addRestaurantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ products });
      comp.ngOnInit();

      expect(restaurantService.query).toHaveBeenCalled();
      expect(restaurantService.addRestaurantToCollectionIfMissing).toHaveBeenCalledWith(restaurantCollection, ...additionalRestaurants);
      expect(comp.restaurantsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Cart query and add missing value', () => {
      const products: IProducts = { id: 'CBA' };
      const cart: ICart = { id: '3906b73e-98bb-4168-b0a8-0ed5289f737f' };
      products.cart = cart;

      const cartCollection: ICart[] = [{ id: 'd9c46d9d-dc99-477e-931a-4baf69ce4ab9' }];
      jest.spyOn(cartService, 'query').mockReturnValue(of(new HttpResponse({ body: cartCollection })));
      const additionalCarts = [cart];
      const expectedCollection: ICart[] = [...additionalCarts, ...cartCollection];
      jest.spyOn(cartService, 'addCartToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ products });
      comp.ngOnInit();

      expect(cartService.query).toHaveBeenCalled();
      expect(cartService.addCartToCollectionIfMissing).toHaveBeenCalledWith(cartCollection, ...additionalCarts);
      expect(comp.cartsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const products: IProducts = { id: 'CBA' };
      const category: ICategories = { id: '08e99fa3-14ce-49f9-bf5a-31043631b459' };
      products.category = category;
      const restaurant: IRestaurant = { id: 'b260e939-94d7-4d24-b724-57452d12936e' };
      products.restaurant = restaurant;
      const cart: ICart = { id: 'f7ddbc74-b470-44a3-a573-32b6d1fdccba' };
      products.cart = cart;

      activatedRoute.data = of({ products });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(products));
      expect(comp.categoriesCollection).toContain(category);
      expect(comp.restaurantsSharedCollection).toContain(restaurant);
      expect(comp.cartsSharedCollection).toContain(cart);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Products>>();
      const products = { id: 'ABC' };
      jest.spyOn(productsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ products });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: products }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(productsService.update).toHaveBeenCalledWith(products);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Products>>();
      const products = new Products();
      jest.spyOn(productsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ products });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: products }));
      saveSubject.complete();

      // THEN
      expect(productsService.create).toHaveBeenCalledWith(products);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Products>>();
      const products = { id: 'ABC' };
      jest.spyOn(productsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ products });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(productsService.update).toHaveBeenCalledWith(products);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackCategoriesById', () => {
      it('Should return tracked Categories primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackCategoriesById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackRestaurantById', () => {
      it('Should return tracked Restaurant primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackRestaurantById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackCartById', () => {
      it('Should return tracked Cart primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackCartById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
