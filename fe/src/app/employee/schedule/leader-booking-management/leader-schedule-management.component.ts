import { Component, OnInit, Renderer2 } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { BookingDetailDialog } from 'src/app/employee/schedule/booking-detail-dialog/booking-detail-dialog';
import { BookingService } from 'src/app/services/booking.service';
import { CleanerService } from 'src/app/services/cleaner.service';
import { CustomerService } from 'src/app/services/customer.service';

export interface BookingDetailNoteData {
  data: any;
  dataCleaner: any;
  type: any;
}

@Component({
  selector: 'app-leader-schedule-management',
  templateUrl: './leader-schedule-management.component.html',
  styleUrls: ['./leader-schedule-management.component.scss']
})
export class ManagerScheduleComponent implements OnInit {

  name = '';
  data: any;
  date: any;
  schedule: any;
  rateRange: string[];
  houseType: string[];
  searchRate: any;
  searchHouseType: string;
  dateList: any;
  dateSchedule: any[];
  page: any = 0
  size: any = 10
  dataCleaner: any;
  bookingName: any;
  bookingPhone:any;
  status:any;
  
  constructor(
    public dialog: MatDialog, private renderer: Renderer2,
    // private dialogService: DialogService
    public dialogRef: MatDialogRef<BookingDetailDialog>,
    private bookingService: BookingService,
    private customerService: CustomerService,
    private cleanerService: CleanerService
  ) { }

  ngOnInit() {
      
    this.cleanerService
    .getListBooking(this.page, this.size)
    .subscribe((res) => {
      if (res && res.data) {
        console.log("Thông tin danh sách booking", res);
        console.log("Thông tin danh sách booking data", res.data);
        this.data = res.data;
      }
    });
    
  }

  search(bookingName:any, bookingPhone: any, status:any) {
    this.cleanerService
    .getListBooking(this.page, this.size, bookingName, bookingPhone, status)
    .subscribe((res) => {
      if (res && res.data) {
        console.log("Thông tin danh sách booking", res);
        console.log("Thông tin danh sách booking data", res.data);
        this.data = res.data;
      }
    });
  }

  pickCleaner(bookingId: any) {
      // if (this.selectedTimeType == "Sử dụng 1 lần" && this.dateValue) {
      //   this.bookingServicee
      //     .getCleanerAvaiable(this.dateValue, "", "")
      //     .subscribe((item) => {
      //       this.dataCleaner = item.data;
      //       this.renderer.addClass(document.body, "modal-open");
      //       this.cleanerDialogRef = this.dialog.open(PickCleanerDialog, {
      //         width: "1000px",
      //         maxHeight: "85%",
      //         data: {
      //           data: this.dataCleaner,
      //           date: this.dateValue,
      //           listPick: this.cleanerIds,
      //           cleanerNum: this.cleanerNum,
      //         },
      //         panelClass: ["pick-cleaner"],
      //       });
      //       this.cleanerDialogRef.afterClosed().subscribe((result) => {
      //         if (result) {
      //           this.cleanerIds = result.listPickData;
      //           this.cleanerList = result.listPickDataName.join(", ");
      //         }
      //         this.renderer.removeClass(document.body, "modal-open");
      //       });
      //     });
      // } else if (
      //   (this.selectedTimeType =
      //     "Định kỳ" && this.pickDay && this.typeId && this.servicePackageId)
      // ) {
      //   this.bookingServicee
      //     .getCleanerAvaiable(this.pickDay, this.typeId, this.servicePackageId)
      //     .subscribe((item) => {
      //       this.dataCleaner = item.data;
      //       this.renderer.addClass(document.body, "modal-open");
      //       this.cleanerDialogRef = this.dialog.open(PickCleanerDialog, {
      //         width: "1000px",
      //         maxHeight: "85%",
      //         data: {
      //           data: this.dataCleaner,
      //           date: this.pickDay,
      //           listPick: this.cleanerIds,
      //           cleanerNum: this.cleanerNum,
      //         },
      //         panelClass: ["pick-cleaner"],
      //       });
      //       this.cleanerDialogRef.afterClosed().subscribe((result) => {
      //         if (result) {
      //           this.cleanerIds = result.listPickData;
      //           this.cleanerList = result.listPickDataName.join(", ");
      //         }
      //         this.renderer.removeClass(document.body, "modal-open");
      //       });
      //     });
      // } else {
      //   this.toastr.error("Thiếu thông tin diện tích/thời gian dọn dẹp");
      // }

  }

  getDay(todayin) {
    let today = new Date(todayin);
    return today.getDate();
  }

  getMonth(todayin) {
    let today = new Date(todayin);
    return today.getMonth() + 1;
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

  viewDetailinSchedule(data: any) {
    this.renderer.addClass(document.body, 'modal-open');
    this.dialogRef = this.dialog.open(BookingDetailDialog, {
      width: '820px',
      // maxHeight: '70%',
      height: '85%',
      data: {
        data: data,
      },
      panelClass: ['view-detail']
    });
    this.dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('The dialog was closed');
        this.renderer.removeClass(document.body, 'modal-open');
        // this.dialogService.sendDataDialog(false);
      }
    });
    // }
  }

  cancel(id: any){
    let body = {
      bookingId: id
    }
    this.bookingService.cancelBooking(body).subscribe({
      next: (res) => {
        
      }
    });
  }

  changeStatus(id: any){

  }

}