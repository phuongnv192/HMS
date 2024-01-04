import { Component, OnInit, Renderer2 } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { BookingDetailDialog } from 'src/app/employee/schedule/booking-detail-dialog/booking-detail-dialog';
import { ApiService } from 'src/app/services/api.service';
import { AuthService } from 'src/app/services/auth.service';
import { BookingService } from 'src/app/services/booking.service';
import { CustomerService } from 'src/app/services/customer.service';
// import { DialogService } from 'src/app/services/dialog.service';
// import { BookingDetailDialog } from '../schedule/booking-detail-dialog/booking-detail-dialog';

export interface BookingDetailNoteData {
  data: any;
  dataCleaner: any;
  type: any;
}

@Component({
  selector: 'app-customer-schedule',
  templateUrl: './customer-schedule.component.html',
  styleUrls: ['./customer-schedule.component.scss']
})
export class CustomerScheduleComponent implements OnInit {

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
  constructor(
    public dialog: MatDialog, private renderer: Renderer2,
    // private dialogService: DialogService
    public dialogRef: MatDialogRef<BookingDetailDialog>,
    private bookingService: BookingService,
    private customerService: CustomerService,
  ) { }

  ngOnInit() {
      
      this.customerService.getListSchedule().subscribe(_res => {
        if(_res && _res.data) {
          this.data = _res.data.filter(a=> a.status != 'DONE');
        }
      })
    
  }

  search() {

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
      height: '80%',
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
