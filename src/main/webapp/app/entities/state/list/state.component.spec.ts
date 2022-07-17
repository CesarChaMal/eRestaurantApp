import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { StateService } from '../service/state.service';

import { StateComponent } from './state.component';

describe('State Management Component', () => {
  let comp: StateComponent;
  let fixture: ComponentFixture<StateComponent>;
  let service: StateService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'state', component: StateComponent }]), HttpClientTestingModule],
      declarations: [StateComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(StateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StateComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(StateService);

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
    expect(comp.states?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
