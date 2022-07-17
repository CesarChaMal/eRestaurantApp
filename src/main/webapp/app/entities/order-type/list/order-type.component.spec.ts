import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { OrderTypeService } from '../service/order-type.service';

import { OrderTypeComponent } from './order-type.component';

describe('OrderType Management Component', () => {
  let comp: OrderTypeComponent;
  let fixture: ComponentFixture<OrderTypeComponent>;
  let service: OrderTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'order-type', component: OrderTypeComponent }]), HttpClientTestingModule],
      declarations: [OrderTypeComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(OrderTypeComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrderTypeComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(OrderTypeService);

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
    expect(comp.orderTypes?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
