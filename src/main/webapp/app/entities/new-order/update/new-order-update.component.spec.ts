import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { NewOrderService } from '../service/new-order.service';
import { INewOrder, NewOrder } from '../new-order.model';

import { NewOrderUpdateComponent } from './new-order-update.component';

describe('NewOrder Management Update Component', () => {
  let comp: NewOrderUpdateComponent;
  let fixture: ComponentFixture<NewOrderUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let newOrderService: NewOrderService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [NewOrderUpdateComponent],
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
      .overrideTemplate(NewOrderUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NewOrderUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    newOrderService = TestBed.inject(NewOrderService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const newOrder: INewOrder = { id: 'CBA' };

      activatedRoute.data = of({ newOrder });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(newOrder));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<NewOrder>>();
      const newOrder = { id: 'ABC' };
      jest.spyOn(newOrderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ newOrder });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: newOrder }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(newOrderService.update).toHaveBeenCalledWith(newOrder);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<NewOrder>>();
      const newOrder = new NewOrder();
      jest.spyOn(newOrderService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ newOrder });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: newOrder }));
      saveSubject.complete();

      // THEN
      expect(newOrderService.create).toHaveBeenCalledWith(newOrder);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<NewOrder>>();
      const newOrder = { id: 'ABC' };
      jest.spyOn(newOrderService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ newOrder });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(newOrderService.update).toHaveBeenCalledWith(newOrder);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
