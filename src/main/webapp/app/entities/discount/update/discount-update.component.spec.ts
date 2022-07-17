import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DiscountService } from '../service/discount.service';
import { IDiscount, Discount } from '../discount.model';

import { DiscountUpdateComponent } from './discount-update.component';

describe('Discount Management Update Component', () => {
  let comp: DiscountUpdateComponent;
  let fixture: ComponentFixture<DiscountUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let discountService: DiscountService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DiscountUpdateComponent],
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
      .overrideTemplate(DiscountUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DiscountUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    discountService = TestBed.inject(DiscountService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const discount: IDiscount = { id: 'CBA' };

      activatedRoute.data = of({ discount });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(discount));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Discount>>();
      const discount = { id: 'ABC' };
      jest.spyOn(discountService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ discount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: discount }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(discountService.update).toHaveBeenCalledWith(discount);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Discount>>();
      const discount = new Discount();
      jest.spyOn(discountService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ discount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: discount }));
      saveSubject.complete();

      // THEN
      expect(discountService.create).toHaveBeenCalledWith(discount);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Discount>>();
      const discount = { id: 'ABC' };
      jest.spyOn(discountService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ discount });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(discountService.update).toHaveBeenCalledWith(discount);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
