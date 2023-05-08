import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CertifOverviewComponent } from './certif-overview.component';

describe('CertifOverviewComponent', () => {
  let component: CertifOverviewComponent;
  let fixture: ComponentFixture<CertifOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CertifOverviewComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CertifOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
