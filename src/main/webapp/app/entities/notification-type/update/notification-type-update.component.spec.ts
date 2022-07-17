import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { NotificationTypeService } from '../service/notification-type.service';
import { INotificationType, NotificationType } from '../notification-type.model';

import { NotificationTypeUpdateComponent } from './notification-type-update.component';

describe('NotificationType Management Update Component', () => {
  let comp: NotificationTypeUpdateComponent;
  let fixture: ComponentFixture<NotificationTypeUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let notificationTypeService: NotificationTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [NotificationTypeUpdateComponent],
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
      .overrideTemplate(NotificationTypeUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NotificationTypeUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    notificationTypeService = TestBed.inject(NotificationTypeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const notificationType: INotificationType = { id: 'CBA' };

      activatedRoute.data = of({ notificationType });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(notificationType));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<NotificationType>>();
      const notificationType = { id: 'ABC' };
      jest.spyOn(notificationTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notificationType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notificationType }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(notificationTypeService.update).toHaveBeenCalledWith(notificationType);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<NotificationType>>();
      const notificationType = new NotificationType();
      jest.spyOn(notificationTypeService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notificationType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notificationType }));
      saveSubject.complete();

      // THEN
      expect(notificationTypeService.create).toHaveBeenCalledWith(notificationType);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<NotificationType>>();
      const notificationType = { id: 'ABC' };
      jest.spyOn(notificationTypeService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notificationType });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(notificationTypeService.update).toHaveBeenCalledWith(notificationType);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
