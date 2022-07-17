import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CloseService } from '../service/close.service';

import { CloseComponent } from './close.component';

describe('Close Management Component', () => {
  let comp: CloseComponent;
  let fixture: ComponentFixture<CloseComponent>;
  let service: CloseService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'close', component: CloseComponent }]), HttpClientTestingModule],
      declarations: [CloseComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(CloseComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CloseComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CloseService);

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
    expect(comp.closes?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
