import { TestBed } from '@angular/core/testing';

import { CertService } from './cert-service.service';

describe('CertServiceService', () => {
  let service: CertService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CertService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
