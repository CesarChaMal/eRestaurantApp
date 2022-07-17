import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AppDiscountService } from '../service/app-discount.service';
import { IAppDiscount, AppDiscount } from '../app-discount.model';
import { ICart } from 'app/entities/cart/cart.model';
import { CartService } from 'app/entities/cart/service/cart.service';

import { AppDiscountUpdateComponent } from './app-discount-update.component';

describe('AppDiscount Management Update Component', () => {
  let comp: AppDiscountUpdateComponent;
  let fixture: ComponentFixture<AppDiscountUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let appDiscountService: AppDiscountService;
  let cartService: CartService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AppDiscountUpdateComponent],
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
      .overrideTemplate(AppDiscountUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AppDiscountUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    appDiscountService = TestBed.inject(AppDiscountService);
    cartService = TestBed.inject(CartService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Cart query and add missing value', () => {
      const appDiscount: IAppDiscount = { id: 'CBA' };
      const cart: ICart = { id: 'fd89547c-c063-4628-92a5-6f7199b289f8' };
      appDiscount.cart = cart;

      const cartCollection: ICart[] = [{ id: '005d1913-509f-4e84-8a9d-c999fde3396a' }];
      jest.spyOn(cartService, 'query').mockReturnValue(of(new HttpResponse({ body: cartCollection })));
      const additionalCarts = [cart];
      const expectedCollection: ICart[] = [...additionalCarts, ...cartCollection];
      jest.spyOn(cartService, 'addCartToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ appDiscount });
      comp.ngOnInit();

      expect(cartService.query).toHaveBeenCalled();
      expect(cartService.addCartToCollectionIfMissing).toHaveBeenCalledWith(cartCollection, ...additionalCarts);
      expect(comp.cartsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const appDiscount: IAppDiscount = { id: 'CBA' };
      const cart: ICart = { id: '5c6d02e9-7115-4a2c-96f4-af0d444d0b4d' };
      appDiscount.cart = cart;

      activatedRoute.data = of({ appDiscount });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(appDiscount));
      expect(comp.cartsSharedCollection).toContain(cart);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AppDiscount>>();
      const appDiscount = { id: 'ABC' };
      jest.spyOn(appDiscountService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appDiscount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: appDiscount }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(appDiscountService.update).toHaveBeenCalledWith(appDiscount);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AppDiscount>>();
      const appDiscount = new AppDiscount();
      jest.spyOn(appDiscountService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appDiscount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: appDiscount }));
      saveSubject.complete();

      // THEN
      expect(appDiscountService.create).toHaveBeenCalledWith(appDiscount);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<AppDiscount>>();
      const appDiscount = { id: 'ABC' };
      jest.spyOn(appDiscountService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ appDiscount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(appDiscountService.update).toHaveBeenCalledWith(appDiscount);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackCartById', () => {
      it('Should return tracked Cart primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackCartById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
