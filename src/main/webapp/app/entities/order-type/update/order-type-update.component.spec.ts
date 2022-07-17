import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OrderTypeService } from '../service/order-type.service';
import { IOrderType, OrderType } from '../order-type.model';

import { OrderTypeUpdateComponent } from './order-type-update.component';

describe('OrderType Management Update Component', () => {
  let comp: OrderTypeUpdateComponent;
  let fixture: ComponentFixture<OrderTypeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let orderTypeService: OrderTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OrderTypeUpdateComponent],
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
      .overrideTemplate(OrderTypeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrderTypeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    orderTypeService = TestBed.inject(OrderTypeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const orderType: IOrderType = { id: 'CBA' };

      activatedRoute.data = of({ orderType });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(orderType));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<OrderType>>();
      const orderType = { id: 'ABC' };
      jest.spyOn(orderTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderType }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(orderTypeService.update).toHaveBeenCalledWith(orderType);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<OrderType>>();
      const orderType = new OrderType();
      jest.spyOn(orderTypeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderType }));
      saveSubject.complete();

      // THEN
      expect(orderTypeService.create).toHaveBeenCalledWith(orderType);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<OrderType>>();
      const orderType = { id: 'ABC' };
      jest.spyOn(orderTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(orderTypeService.update).toHaveBeenCalledWith(orderType);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
