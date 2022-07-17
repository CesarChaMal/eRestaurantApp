import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { OnHoldService } from '../service/on-hold.service';

import { OnHoldComponent } from './on-hold.component';

describe('OnHold Management Component', () => {
  let comp: OnHoldComponent;
  let fixture: ComponentFixture<OnHoldComponent>;
  let service: OnHoldService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'on-hold', component: OnHoldComponent }]), HttpClientTestingModule],
      declarations: [OnHoldComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(OnHoldComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OnHoldComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(OnHoldService);

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
    expect(comp.onHolds?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
