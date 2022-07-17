import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CompleteService } from '../service/complete.service';

import { CompleteComponent } from './complete.component';

describe('Complete Management Component', () => {
  let comp: CompleteComponent;
  let fixture: ComponentFixture<CompleteComponent>;
  let service: CompleteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'complete', component: CompleteComponent }]), HttpClientTestingModule],
      declarations: [CompleteComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(CompleteComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CompleteComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CompleteService);

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
    expect(comp.completes?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
