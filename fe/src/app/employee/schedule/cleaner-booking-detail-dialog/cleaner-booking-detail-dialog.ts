import { Subscription } from 'rxjs';
import { Component, Inject, OnDestroy, NgZone, Renderer2, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { BookingService } from 'src/app/services/booking.service';
// import { Cleaner } from 'src/app/customer/customer-booking/customer-booking-list/customer-schedule.component';
import { CleanerBookingDetailNoteData } from '../schedule.component';
import { CleanerService } from 'src/app/services/cleaner.service';
import { ToastrService } from 'ngx-toastr';
import { CleanerRateDialog } from 'src/app/booking/cleaner-rate-dialog/cleaner-rate-dialog';
import { ChangeStatusDialog } from './change-status-dialog/change-status-dialog';
import { ScheduleDialog } from './schedule-dialog/schedule.dialog';

@Component({
  selector: 'app-cleaner-booking-detail-dialog',
  templateUrl: './cleaner-booking-detail-dialog.html',
  styleUrls: ['./cleaner-booking-detail-dialog.scss']
})

export class CleanerBookingDetailDialog implements OnInit, OnDestroy {
  public mobile: any;
  private _subscription = Subscription.EMPTY;
  dateList: any[];
  dateSchedule: any[];
  selectedDate: any;
  date: any;
  public name: any;
  public id: any;
  detail: any;
  scheduleDay: any;
  scheduleDayTime: any;
  scheduleStartTime: any;
  scheduleEndTime: any;
  schedulePaymentStatus: any;
  schedulePaymentNote: any;
  scheduleAddOns: any;
  namesOfScheduleDay: any;
  scheduleDayList: any;
  areaTypes: any;
  cleanerNum: any;
  duration: any;
  priceClean: any;
  formattedPrice: any;
  infor: any;
  type = 'day';
  title_confirm: string;


  constructor(
    public dialogRef: MatDialogRef<CleanerBookingDetailDialog>,
    public dialog: MatDialog,
    public ratedialogRef: MatDialogRef<CleanerRateDialog>,
    public statusdialogRef: MatDialogRef<ChangeStatusDialog>,
    public scheduleDialogRef: MatDialogRef<ScheduleDialog>,
    @Inject(MAT_DIALOG_DATA) public data: CleanerBookingDetailNoteData,
    private bookingServicee: BookingService,
    private cleanerService: CleanerService,
    private renderer: Renderer2,
    private toastr: ToastrService,
    private ngZone: NgZone) {
  }

  ngOnInit(): void {

    this.infor = this.data.data;
    this.type = this.data.type;
    this.id = this.data.data.bookingId;
    this.formattedPrice = this.data.data.totalBookingPrice.toLocaleString('vi-VN', {
      style: 'currency',
      currency: 'VND',
    }); this.bookingServicee.getDataService().subscribe(res => {
      this.areaTypes = res.data;
      this.onAreaChange(this.data.data.floorArea);
    });

    if (this.type == 'day' && this.infor.status == 'CONFIRMED') {
      this.title_confirm = '-> Đang di chuyển';
    } else if (this.type == 'day' && this.infor.status == 'ON_MOVING') {
      this.title_confirm = '-> Đang dọn';
    } else if (this.type == 'day' && this.infor.status == 'ON_PROCESS') {
      this.title_confirm = 'Cập nhật giá dịch vụ';
    }

    this.dateList = [
      {
        id: 'd1',
        workDate: "12/20/2023",
        name: 'Đơn dịch vụ ngày #1',
        startTime: "08:00 AM",
        endTime: "11:00 AM",
        duration: 3
      },
      {
        id: 'd2',
        workDate: "12/21/2023",
        name: 'Đơn dịch vụ ngày #2',
        startTime: "09:00 AM",
        endTime: "10:00 AM",
        duration: 1
      },
      {
        id: 'd1',
        workDate: "12/22/2023",
        name: 'Đơn dịch vụ ngày #3',
        startTime: "03:00 PM",
        endTime: "05:00 AM",
        duration: 3
      },
    ]
    this.namesOfScheduleDay = this.scheduleAddOns.map(item => item.name).join(', ');
    this.scheduleDayList = this.data.data.scheduleList.map(item => item.workDate);

    this.dateSchedule = [{
      id: this.id,
      workDate: this.scheduleDayList
    }];
    // this.date = this.dateSchedule.find((item) => item.id == this.data.id);
  }

  onAreaChange(value: any) {
    // let found = false; // Biến kiểm soát vòng lặp
    this.areaTypes.forEach(element => {
      if (value == element.floorArea) {
        // Nếu tìm thấy phần tử thỏa mãn điều kiện, lưu giá trị và thoát vòng lặp
        this.cleanerNum = element.cleanerNum;
        this.duration = element.duration;
        this.priceClean = element.price;
        this.formattedPrice = this.priceClean.toLocaleString('vi-VN', {
          style: 'currency',
          currency: 'VND',
        });

        // found = true;
      }
    });
  }

  confirm(id: any) {
    let status = "";
    let body = {};
    console.log("id", id);
    // this.renderer.addClass(document.body, 'modal-open');
    // this.statusdialogRef = this.dialog.open(ChangeStatusDialog, {
    //   width: '500px',
    //   height: '70%',
    //   data: {
    //     data: id,
    //     id: this.infor.bookingId,
    //     addService: this.infor.scheduleList[0].serviceAddOns,
    //   },
    //   panelClass: ['change-status']
    // });
    // this.statusdialogRef.afterClosed().subscribe(result => {
    //   if (result) {
    //     body = {
    //       "scheduleId": "58",
    //       "status": "DONE",
    //       "paymentStatus": "PAYMENT_SUCCESS",
    //       "addOns": result.addOns,
    //       "note": result.note
    //     }
    //     this.cleanerService.changeStatus(body).subscribe({
    //       next: (res) => {
    //         this.toastr.success('Đơn đã hoàn tất');
    //         this.dialogRef.close(true);
    //       },
    //       error: (err) => {
    //         this.dialogRef.close(true);
    //         console.log(err);
    //       }, // errorHandler
    //     })
    //   }
    //   this.renderer.removeClass(document.body, 'modal-open');      
    // });

    if (this.type = 'day') {
      if(this.infor.status == "CONFIRMED" || this.infor.status == "ON_MOVING"){
        if (this.infor.status != "DONE" && this.infor.status == "CONFIRMED") {
          status = "ON_MOVING";
          body = {
            "scheduleId": id,
            "status": status,
             "paymentStatus": "PAYMENT_SUCCESS",
            "addOns": [],
            "note": ""
          }
        } else if (this.infor.status == "ON_MOVING") {
          status = "ON_PROCESS";
          body = {
            "scheduleId": id,
            "status": status,
            "paymentStatus": "PAYMENT_SUCCESS",
            "addOns": [],
            "note": ""
          }
        }
        this.cleanerService.changeStatus(body).subscribe({
          next: (res) => {
            this.toastr.success('Cập nhật trạng thái thành công');
            this.dialogRef.close(true);
          },
          error: (err) => {
            this.dialogRef.close(true);
            console.log(err);
          }, // errorHandler
        })
      } else {
        let addOns=[];
        let note= "";
        this.renderer.addClass(document.body, 'modal-open');
        this.statusdialogRef = this.dialog.open(ChangeStatusDialog, {
          width: '500px',
          maxHeight: '65%',
          data: {
            data: id,
            id: this.infor.bookingId,
            addService: this.infor.scheduleList[0].serviceAddOns,
          },
          panelClass: ['change-stauts']
        });

        this.statusdialogRef.afterClosed().subscribe(result => {
          if(result){
            result.addOns = addOns;
            result.note = note;
            status = "DONE"
            body = {
              "scheduleId": id,
              "status": status,
              "paymentStatus": "PAYMENT_SUCCESS",
              "addOns": addOns,
              "note": note
            }
            this.cleanerService.changeStatus(body).subscribe({
              next: (res) => {
                this.toastr.success('Đơn đã hoàn tất');
                this.dialogRef.close(true);
              },
              error: (err) => {
                this.dialogRef.close(true);
                console.log(err);
              }, // errorHandler
            })
          }
          this.renderer.removeClass(document.body, 'modal-open');
        });       
      }  
    }
  }

  complete(id: any) {
    let body = {
      "scheduleId": id,
      "status": "DONE",
      "paymentStatus": "PAYMENT_SUCCESS",
      "addOns": [],
      "note": ""
    }
    this.cleanerService.changeStatus(body).subscribe({
      next: (res) => {
        this.toastr.success('Đơn đã hoàn tất');
        this.dialogRef.close(true);
      },
      error: (err) => {
        this.dialogRef.close(true);
        console.log(err);
      }, // errorHandler
    })
  }

  viewDetail(id: any) {
    this.renderer.addClass(document.body, 'modal-open');
    this.scheduleDialogRef = this.dialog.open(ScheduleDialog, {
      width: '800px',
      maxHeight: '80%',
      data: {
        data: this.infor.scheduleList,
        id: this.infor.bookingId,
      },
      panelClass: ['schedule-stauts']
    });

    this.scheduleDialogRef.afterClosed().subscribe(result => {
      if(result){
       
      }
      this.renderer.removeClass(document.body, 'modal-open');
    }); 
  }

  cancel(id: any) {

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

  ngOnDestroy(): void {
    this._subscription.unsubscribe();
  }

  getDay(todayin) {
    let today = new Date(todayin);
    let day = today.getDate().toString().padStart(2, '0');
    return day;
  }

  getMonth(todayin) {
    let today = new Date(todayin);
    let month = (today.getMonth() + 1).toString().padStart(2, '0');
    return month;
  }

  getDateMonth(todayin) {
    let days = {
      Sunday: "CN",
      Monday: "Thứ 2",
      Tuesday: "Thứ 3",
      Wednesday: "Thứ 4",
      Thursday: "Thứ 5",
      Friday: "Thứ 6",
      Saturday: "Thứ 7"
    }
    let today = new Date(todayin);
    return days[today.toLocaleDateString('en-US', { weekday: 'long' })];
  }

  getYearDateMonth(todayin) {
    let today = new Date(todayin);
    return (today.getMonth() + 1) + '/' + today.getDate() + '/' + today.getFullYear();
  }

}