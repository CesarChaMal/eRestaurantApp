import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { RoleService } from '../service/role.service';

import { RoleComponent } from './role.component';

describe('Role Management Component', () => {
  let comp: RoleComponent;
  let fixture: ComponentFixture<RoleComponent>;
  let service: RoleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'role', component: RoleComponent }]), HttpClientTestingModule],
      declarations: [RoleComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { queryParams: {} } },
        },
      ],
    })
      .overrideTemplate(RoleComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RoleComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(RoleService);

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
    expect(comp.roles?.[0]).toEqual(expect.objectContaining({ id: 'ABC' }));
  });
});
