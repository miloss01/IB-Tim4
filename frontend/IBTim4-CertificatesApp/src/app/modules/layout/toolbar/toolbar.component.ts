import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.css']
})
export class ToolbarComponent implements OnInit {

  constructor (
    private readonly router: Router,
    private route: ActivatedRoute,
  ) {}

  user: any = null

  ngOnInit(): void {
    
  }
}
