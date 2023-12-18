import { Subscription } from 'rxjs';
import { BillDialogData, PriceListDialogData } from '../booking.component';
import { NgZone, OnInit, Renderer2 } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';



@Component({
  selector: 'app-bill-booking-dialog',
  templateUrl: './bill-booking-dialog.html',
  styleUrls: ['./bill-booking-dialog.scss']
})

export class BillBookingDialog implements OnDestroy, OnInit {
  public mobile: any;
  private _subscription = Subscription.EMPTY;
  public name: any;
  public id: any;
  parentService: any;
  childrenService: any;
  billDetail: any;
  duration: any;
  cleanerNum: any;
  cleanerList: any;
  datePickerShow: any;
  billSchedule = false;
  billDay = false;
 

  constructor(
    public dialogRef: MatDialogRef<BillBookingDialog>,
    @Inject(MAT_DIALOG_DATA) public data: BillDialogData,
    private renderer: Renderer2,
    private ngZone: NgZone) {
  }


  ngOnInit(): void {
    this.billDetail = this.data.data;
    this.duration = this.data.data;
    this.cleanerNum = this.data.cleanerNum;
    this.cleanerList = this.data.cleanerList;
    this.datePickerShow = this.data.datePickerShow;
    if(this.billDetail.serviceTypeId && this.billDetail.servicePackageId){
      this.billSchedule = true;
    } else {
      this.billDay = true;
    }
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