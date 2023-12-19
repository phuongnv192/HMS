import { Subscription } from 'rxjs';
import { BillDialogData, PriceListDialogData } from '../booking.component';
import { NgZone, OnInit, Renderer2 } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { BookingService } from 'src/app/services/booking.service';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';



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

  textListAdvanceService: any;
  startTime: any;
  totalAmount: any;
  billDay = true;
  billSchedule: any;
 

  constructor(
    public dialogRef: MatDialogRef<BillBookingDialog>,
    @Inject(MAT_DIALOG_DATA) public data: BillDialogData,
    private bookingServicee: BookingService,
    private renderer: Renderer2,
    private router: Router,
    private toastr: ToastrService,
    private ngZone: NgZone) {
  }


  ngOnInit(): void {
    this.billDetail = this.data.data;
    this.duration = this.data.duration;
    this.cleanerNum = this.data.cleanerNum;
    this.cleanerList = this.data.cleanerList;
    this.datePickerShow = this.data.datePickerShow;
    this.textListAdvanceService = this.data.textListAdvanceService;
    this.startTime = this.data.startTime;
    this.totalAmount = this.data.totalAmount;
    this.billDay = this.data.billDay;
    this.billSchedule = this.data.billSchedule;
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

  Booking(){
    this.bookingServicee.booking(this.billDetail).subscribe({
      next: () => {
        this.toastr.success('Đơn dịch vụ đã được đặt thành công, vui lòng kiểm tra email thông tin chi tiết', 'Thành công');
        // Chuyển hướng sang trang Home và truyền thông báo thành công
        this.router.navigate(["/introduction"], { queryParams: { success: true } });
      },
      error: () => { },
    });;
  }

}