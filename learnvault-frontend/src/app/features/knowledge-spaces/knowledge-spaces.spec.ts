import { ComponentFixture, TestBed } from '@angular/core/testing';
import { KnowledgeSpaces } from './knowledge-spaces';

describe('KnowledgeSpaces', () => {
  let component: KnowledgeSpaces;
  let fixture: ComponentFixture<KnowledgeSpaces>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [KnowledgeSpaces],
    }).compileComponents();

    fixture = TestBed.createComponent(KnowledgeSpaces);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
