import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { NotificationService } from '../service/notification.service';
import { INotification, Notification } from '../notification.model';
import { INotificationType } from 'app/entities/notification-type/notification-type.model';
import { NotificationTypeService } from 'app/entities/notification-type/service/notification-type.service';
import { IRestaurant } from 'app/entities/restaurant/restaurant.model';
import { RestaurantService } from 'app/entities/restaurant/service/restaurant.service';

import { NotificationUpdateComponent } from './notification-update.component';

describe('Notification Management Update Component', () => {
  let comp: NotificationUpdateComponent;
  let fixture: ComponentFixture<NotificationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let notificationService: NotificationService;
  let notificationTypeService: NotificationTypeService;
  let restaurantService: RestaurantService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [NotificationUpdateComponent],
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
      .overrideTemplate(NotificationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NotificationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    notificationService = TestBed.inject(NotificationService);
    notificationTypeService = TestBed.inject(NotificationTypeService);
    restaurantService = TestBed.inject(RestaurantService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call type query and add missing value', () => {
      const notification: INotification = { id: 'CBA' };
      const type: INotificationType = { id: 'a3d16ac1-8b08-4330-9a08-663fbb168978' };
      notification.type = type;

      const typeCollection: INotificationType[] = [{ id: '50a9e299-a0eb-4b4d-b813-d03fc8cc492a' }];
      jest.spyOn(notificationTypeService, 'query').mockReturnValue(of(new HttpResponse({ body: typeCollection })));
      const expectedCollection: INotificationType[] = [type, ...typeCollection];
      jest.spyOn(notificationTypeService, 'addNotificationTypeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      expect(notificationTypeService.query).toHaveBeenCalled();
      expect(notificationTypeService.addNotificationTypeToCollectionIfMissing).toHaveBeenCalledWith(typeCollection, type);
      expect(comp.typesCollection).toEqual(expectedCollection);
    });

    it('Should call Restaurant query and add missing value', () => {
      const notification: INotification = { id: 'CBA' };
      const restaurant: IRestaurant = { id: 'e03043b9-acc1-43c9-8023-77da88652f06' };
      notification.restaurant = restaurant;

      const restaurantCollection: IRestaurant[] = [{ id: 'aaa43149-5e23-45b0-b179-eb66c11de944' }];
      jest.spyOn(restaurantService, 'query').mockReturnValue(of(new HttpResponse({ body: restaurantCollection })));
      const additionalRestaurants = [restaurant];
      const expectedCollection: IRestaurant[] = [...additionalRestaurants, ...restaurantCollection];
      jest.spyOn(restaurantService, 'addRestaurantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      expect(restaurantService.query).toHaveBeenCalled();
      expect(restaurantService.addRestaurantToCollectionIfMissing).toHaveBeenCalledWith(restaurantCollection, ...additionalRestaurants);
      expect(comp.restaurantsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const notification: INotification = { id: 'CBA' };
      const type: INotificationType = { id: '629ce9ed-e11a-43c7-94f5-f7618156d427' };
      notification.type = type;
      const restaurant: IRestaurant = { id: 'c1218ba1-5496-4e0f-86bc-ccf8d219e023' };
      notification.restaurant = restaurant;

      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(notification));
      expect(comp.typesCollection).toContain(type);
      expect(comp.restaurantsSharedCollection).toContain(restaurant);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Notification>>();
      const notification = { id: 'ABC' };
      jest.spyOn(notificationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notification }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(notificationService.update).toHaveBeenCalledWith(notification);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Notification>>();
      const notification = new Notification();
      jest.spyOn(notificationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: notification }));
      saveSubject.complete();

      // THEN
      expect(notificationService.create).toHaveBeenCalledWith(notification);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Notification>>();
      const notification = { id: 'ABC' };
      jest.spyOn(notificationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ notification });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(notificationService.update).toHaveBeenCalledWith(notification);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackNotificationTypeById', () => {
      it('Should return tracked NotificationType primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackNotificationTypeById(0, entity);
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
  });
});
