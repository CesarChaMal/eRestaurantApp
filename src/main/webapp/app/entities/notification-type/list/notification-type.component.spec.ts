import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { NotificationTypeService } from '../service/notification-type.service';

import { NotificationTypeComponent } from './notification-type.component';

describe('NotificationType Management Component', () => {
  let comp: NotificationTypeComponent;
  let fixture: ComponentFixture<NotificationTypeComponent>;
  let service: NotificationTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'notification-type', component: NotificationTypeComponent }]),
        HttpClientTestingModule,
      ],
      declarations: [NotificationTypeComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(NotificationTypeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NotificationTypeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(NotificationTypeService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 'ABC' }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.notificationTypes?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
