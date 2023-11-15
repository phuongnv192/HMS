import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
// import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'term-and-conditions',
  templateUrl: './term-and-conditions.component.html',
  styleUrls: ['./term-and-conditions.component.scss']
})
export class TermAndConditionsComponent implements OnInit {
  // private _subScription: Subscription;
  title = '';
  _locale = '';

  constructor(private router: Router, private _activated: ActivatedRoute) {

  }

  ngOnInit() {
    // this._subScription = this._activated.queryParams.subscribe(params => {
    //   const currentUrl = window.location.href;
    //   if (currentUrl.includes('vi-vn')) {
    //     this._locale = 'vi';
    //   } else if (currentUrl.includes('en-us')) {
    //     this._locale = 'en';
    //   } else {
    //     this._locale = 'vi';
    //   }
    // });
  }
}
//https://wordtohtml.net/
