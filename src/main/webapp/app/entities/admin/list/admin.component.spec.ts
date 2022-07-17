import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { AdminService } from '../service/admin.service';

import { AdminComponent } from './admin.component';

describe('Admin Management Component', () => {
  let comp: AdminComponent;
  let fixture: ComponentFixture<AdminComponent>;
  let service: AdminService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'admin', component: AdminComponent }]), HttpClientTestingModule],
      declarations: [AdminComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(AdminComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AdminComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(AdminService);

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
    expect(comp.admins?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
