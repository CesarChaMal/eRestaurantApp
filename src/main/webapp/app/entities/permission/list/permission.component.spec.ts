import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { PermissionService } from '../service/permission.service';

import { PermissionComponent } from './permission.component';

describe('Permission Management Component', () => {
  let comp: PermissionComponent;
  let fixture: ComponentFixture<PermissionComponent>;
  let service: PermissionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'permission', component: PermissionComponent }]), HttpClientTestingModule],
      declarations: [PermissionComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(PermissionComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PermissionComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(PermissionService);

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
    expect(comp.permissions?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
