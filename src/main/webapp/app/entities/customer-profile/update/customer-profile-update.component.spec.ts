import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CustomerProfileService } from '../service/customer-profile.service';
import { ICustomerProfile, CustomerProfile } from '../customer-profile.model';

import { CustomerProfileUpdateComponent } from './customer-profile-update.component';

describe('CustomerProfile Management Update Component', () => {
  let comp: CustomerProfileUpdateComponent;
  let fixture: ComponentFixture<CustomerProfileUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let customerProfileService: CustomerProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CustomerProfileUpdateComponent],
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
      .overrideTemplate(CustomerProfileUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CustomerProfileUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    customerProfileService = TestBed.inject(CustomerProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const customerProfile: ICustomerProfile = { id: 'CBA' };

      activatedRoute.data = of({ customerProfile });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(customerProfile));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<CustomerProfile>>();
      const customerProfile = { id: 'ABC' };
      jest.spyOn(customerProfileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customerProfile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: customerProfile }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(customerProfileService.update).toHaveBeenCalledWith(customerProfile);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<CustomerProfile>>();
      const customerProfile = new CustomerProfile();
      jest.spyOn(customerProfileService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customerProfile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: customerProfile }));
      saveSubject.complete();

      // THEN
      expect(customerProfileService.create).toHaveBeenCalledWith(customerProfile);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<CustomerProfile>>();
      const customerProfile = { id: 'ABC' };
      jest.spyOn(customerProfileService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customerProfile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(customerProfileService.update).toHaveBeenCalledWith(customerProfile);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
