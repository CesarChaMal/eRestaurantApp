import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OrderService } from '../service/order.service';
import { IOrder, Order } from '../order.model';
import { IOrderType } from 'app/entities/order-type/order-type.model';
import { OrderTypeService } from 'app/entities/order-type/service/order-type.service';
import { IState } from 'app/entities/state/state.model';
import { StateService } from 'app/entities/state/service/state.service';
import { ICustomer } from 'app/entities/customer/customer.model';
import { CustomerService } from 'app/entities/customer/service/customer.service';

import { OrderUpdateComponent } from './order-update.component';

describe('Order Management Update Component', () => {
  let comp: OrderUpdateComponent;
  let fixture: ComponentFixture<OrderUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let orderService: OrderService;
  let orderTypeService: OrderTypeService;
  let stateService: StateService;
  let customerService: CustomerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OrderUpdateComponent],
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
      .overrideTemplate(OrderUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrderUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    orderService = TestBed.inject(OrderService);
    orderTypeService = TestBed.inject(OrderTypeService);
    stateService = TestBed.inject(StateService);
    customerService = TestBed.inject(CustomerService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call type query and add missing value', () => {
      const order: IOrder = { id: 'CBA' };
      const type: IOrderType = { id: 'eca3eb0f-7b1c-4ebc-8a89-b974a3804e63' };
      order.type = type;

      const typeCollection: IOrderType[] = [{ id: 'cf8b640b-3c27-4480-ab70-69b98eb528fc' }];
      jest.spyOn(orderTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: typeCollection })));
      const expectedCollection: IOrderType[] = [type, ...typeCollection];
      jest.spyOn(orderTypeService, 'addOrderTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ order });
      comp.ngOnInit();

      expect(orderTypeService.query).toHaveBeenCalled();
      expect(orderTypeService.addOrderTypeToCollectionIfMissing).toHaveBeenCalledWith(typeCollection, type);
      expect(comp.typesCollection).toEqual(expectedCollection);
    });

    it('Should call state query and add missing value', () => {
      const order: IOrder = { id: 'CBA' };
      const state: IState = { id: 'ac4bb707-0a81-4ccc-a943-3f8afe56f31b' };
      order.state = state;

      const stateCollection: IState[] = [{ id: '76a3bdd0-4ac2-4b51-9834-f26b92a4a05c' }];
      jest.spyOn(stateService, 'query').mockReturnValue(of(new HttpResponse({ body: stateCollection })));
      const expectedCollection: IState[] = [state, ...stateCollection];
      jest.spyOn(stateService, 'addStateToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ order });
      comp.ngOnInit();

      expect(stateService.query).toHaveBeenCalled();
      expect(stateService.addStateToCollectionIfMissing).toHaveBeenCalledWith(stateCollection, state);
      expect(comp.statesCollection).toEqual(expectedCollection);
    });

    it('Should call Customer query and add missing value', () => {
      const order: IOrder = { id: 'CBA' };
      const customer: ICustomer = { id: 'e1a3e7c7-af08-4c08-b360-b75da24a0ecf' };
      order.customer = customer;

      const customerCollection: ICustomer[] = [{ id: 'fce5f373-c519-4af7-bbb3-39ef966e05ed' }];
      jest.spyOn(customerService, 'query').mockReturnValue(of(new HttpResponse({ body: customerCollection })));
      const additionalCustomers = [customer];
      const expectedCollection: ICustomer[] = [...additionalCustomers, ...customerCollection];
      jest.spyOn(customerService, 'addCustomerToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ order });
      comp.ngOnInit();

      expect(customerService.query).toHaveBeenCalled();
      expect(customerService.addCustomerToCollectionIfMissing).toHaveBeenCalledWith(customerCollection, ...additionalCustomers);
      expect(comp.customersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const order: IOrder = { id: 'CBA' };
      const type: IOrderType = { id: '1742af73-da39-4345-8038-0837bb263298' };
      order.type = type;
      const state: IState = { id: '70c59974-65b4-4bf8-b309-876e00c226cc' };
      order.state = state;
      const customer: ICustomer = { id: '648e2631-e8d7-46fe-9965-5474828ee7a6' };
      order.customer = customer;

      activatedRoute.data = of({ order });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(order));
      expect(comp.typesCollection).toContain(type);
      expect(comp.statesCollection).toContain(state);
      expect(comp.customersSharedCollection).toContain(customer);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Order>>();
      const order = { id: 'ABC' };
      jest.spyOn(orderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ order });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: order }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(orderService.update).toHaveBeenCalledWith(order);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Order>>();
      const order = new Order();
      jest.spyOn(orderService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ order });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: order }));
      saveSubject.complete();

      // THEN
      expect(orderService.create).toHaveBeenCalledWith(order);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Order>>();
      const order = { id: 'ABC' };
      jest.spyOn(orderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ order });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(orderService.update).toHaveBeenCalledWith(order);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackOrderTypeById', () => {
      it('Should return tracked OrderType primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackOrderTypeById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackStateById', () => {
      it('Should return tracked State primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackStateById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackCustomerById', () => {
      it('Should return tracked Customer primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackCustomerById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
