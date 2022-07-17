import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RestaurantAdService } from '../service/restaurant-ad.service';
import { IRestaurantAd, RestaurantAd } from '../restaurant-ad.model';
import { IRestaurant } from 'app/entities/restaurant/restaurant.model';
import { RestaurantService } from 'app/entities/restaurant/service/restaurant.service';

import { RestaurantAdUpdateComponent } from './restaurant-ad-update.component';

describe('RestaurantAd Management Update Component', () => {
  let comp: RestaurantAdUpdateComponent;
  let fixture: ComponentFixture<RestaurantAdUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let restaurantAdService: RestaurantAdService;
  let restaurantService: RestaurantService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RestaurantAdUpdateComponent],
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
      .overrideTemplate(RestaurantAdUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RestaurantAdUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    restaurantAdService = TestBed.inject(RestaurantAdService);
    restaurantService = TestBed.inject(RestaurantService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Restaurant query and add missing value', () => {
      const restaurantAd: IRestaurantAd = { id: 'CBA' };
      const restaurant: IRestaurant = { id: 'e194244f-150b-43a0-a1a5-9e14f047ed80' };
      restaurantAd.restaurant = restaurant;

      const restaurantCollection: IRestaurant[] = [{ id: '07ec09de-ecb4-4eb3-bc58-bc9986e45567' }];
      jest.spyOn(restaurantService, 'query').mockReturnValue(of(new HttpResponse({ body: restaurantCollection })));
      const additionalRestaurants = [restaurant];
      const expectedCollection: IRestaurant[] = [...additionalRestaurants, ...restaurantCollection];
      jest.spyOn(restaurantService, 'addRestaurantToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ restaurantAd });
      comp.ngOnInit();

      expect(restaurantService.query).toHaveBeenCalled();
      expect(restaurantService.addRestaurantToCollectionIfMissing).toHaveBeenCalledWith(restaurantCollection, ...additionalRestaurants);
      expect(comp.restaurantsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const restaurantAd: IRestaurantAd = { id: 'CBA' };
      const restaurant: IRestaurant = { id: '58e961f9-15e4-4876-ba80-6390e48806d5' };
      restaurantAd.restaurant = restaurant;

      activatedRoute.data = of({ restaurantAd });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(restaurantAd));
      expect(comp.restaurantsSharedCollection).toContain(restaurant);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<RestaurantAd>>();
      const restaurantAd = { id: 'ABC' };
      jest.spyOn(restaurantAdService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ restaurantAd });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: restaurantAd }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(restaurantAdService.update).toHaveBeenCalledWith(restaurantAd);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<RestaurantAd>>();
      const restaurantAd = new RestaurantAd();
      jest.spyOn(restaurantAdService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ restaurantAd });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: restaurantAd }));
      saveSubject.complete();

      // THEN
      expect(restaurantAdService.create).toHaveBeenCalledWith(restaurantAd);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<RestaurantAd>>();
      const restaurantAd = { id: 'ABC' };
      jest.spyOn(restaurantAdService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ restaurantAd });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(restaurantAdService.update).toHaveBeenCalledWith(restaurantAd);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackRestaurantById', () => {
      it('Should return tracked Restaurant primary key', () => {
        const entity = { id: 'ABC' };
        const trackResult = comp.trackRestaurantById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
