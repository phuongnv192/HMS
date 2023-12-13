import { Subscription } from 'rxjs';
import { PriceListDialogData } from '../booking.component';
import { NgZone, OnInit, Renderer2 } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';



@Component({
  selector: 'app-price-list-dialog',
  templateUrl: './price-list-dialog.html',
  styleUrls: ['./price-list-dialog.scss']
})

export class PriceListDialog implements OnDestroy, OnInit {
  public mobile: any;
  private _subscription = Subscription.EMPTY;
  public name: any;
  public id: any;
  parentService: any;
  childrenService: any;
 

  constructor(
    public dialogRef: MatDialogRef<PriceListDialog>,
    @Inject(MAT_DIALOG_DATA) public data: PriceListDialogData,
    private renderer: Renderer2,
    private ngZone: NgZone) {
  }


  ngOnInit(): void {
    this.data.data.forEach(element => {
      
    });
    this.parentService = this.data.data;
    this.childrenService = this.data[0].children;
  }


  ngOnDestroy(): void {
    this._subscription.unsubscribe();
  }

  onNoClick(): void {
    if (this.dialogRef) {
      this.ngZone.run(() => {
        console.log('Đóng dialog');
        this.dialogRef.close(true);
      });
    } else {
      console.warn('dialogRef không tồn tại.');
    }
  }

}