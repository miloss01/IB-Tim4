import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ManagePendingRequestsComponent } from './manage-pending-requests.component';

describe('ManagePendingRequestsComponent', () => {
  let component: ManagePendingRequestsComponent;
  let fixture: ComponentFixture<ManagePendingRequestsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ManagePendingRequestsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ManagePendingRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
