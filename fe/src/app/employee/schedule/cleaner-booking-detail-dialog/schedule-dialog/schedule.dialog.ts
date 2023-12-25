import { Subscription } from 'rxjs';
import { OnInit, Renderer2 } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { BookingService } from 'src/app/services/booking.service';
import { ToastrService } from 'ngx-toastr';
import { addDays, addMonths, addWeeks, format } from 'date-fns';
// import { DialogService } from 'src/app/services/dialog.service';

export interface ScheduleDialogData {
  data: any;
  id: any,
  addService: any;
}

@Component({
  selector: 'app-schedule.dialog-dialog',
  templateUrl: './schedule.dialog.html',
  styleUrls: ['./schedule.dialog.scss']
})

export class ScheduleDialog implements OnDestroy, OnInit {
  public mobile: any;
  private _subscription = Subscription.EMPTY;
  public listCleaner: any;
  dataCleaner: any;
  selectedCleaner: any;
  listA: any;
  listB: any;
  listPick: any;
  listPickData = [];
  searchname = '';
  noDataSearch = false;
  searchResults: any[];
  listPickNameData: any;
  dataPickInfo: { listPickData: any[]; listPickDataName: any; };
  serviceAddOns = [];
  serviceTypeData: any;
  serviceType = [];
  priceList: any;
  activeBadges: any;
  listAdvanceService: any;
  listAdvanceServiceId: any;
  textListAdvanceService: any;
  note = '';
  selectedParents: { parentId: number, selected: any }[] = [];
  otherAddons = false;
  priceOther = 0;
  listChecked  = [];
  dateArray = [];
  selectedDate: any;

  constructor(
    private sanitizer: DomSanitizer,
    private _activated: ActivatedRoute,
    public dialog: MatDialog, private renderer: Renderer2,
    private bookingServicee: BookingService,
    private toastr: ToastrService,
    // private dialogService: DialogService,
    private scheduleDialogRef: MatDialogRef<ScheduleDialog>,
    @Inject(MAT_DIALOG_DATA) public data: ScheduleDialogData) {
  }

  ngOnInit(): void {
    console.log("this.data.data", this.data.data);
    
    for (const schedule of this.data.data) {
      const dateInfo = {
        workDate: schedule.workDate,
        scheduleId: schedule.scheduleId,
        listAddOns: schedule.serviceAddOns.map(addOn => addOn.name).join(', '),
        status: schedule.status
      };
      this.dateArray.push(dateInfo);
      console.log("this.dateArray", this.dateArray);
      
    }
  }

  checkOther(){
    this.otherAddons = !this.otherAddons;
    console.log("otherAddons", this.otherAddons);
  }


  filterCleaners(): void {
    this.listPick = this.listCleaner.filter(cleaner => this.listPickData.includes(cleaner.cleanerId));
  }


  onNoClick(): void {
    this.scheduleDialogRef.close();
  }

  ngOnDestroy() {
    this._subscription.unsubscribe();
  }
  
  // Hàm để tìm kiếm vị trí của phần tử trong listChecked dựa trên serviceAddOnId
  findIndexInListChecked(serviceAddOnId: number): number {
    return this.listChecked.findIndex(item => item.serviceAddOnId === serviceAddOnId);
  }

  getDay(todayin) {
    let today = new Date(todayin);
    let day = today.getDate().toString().padStart(2, '0');
    return day;
  }

  getMonth(todayin) {
    const inputDate = new Date(todayin); // Ngày tháng năm (MM-dd-yyyy)
    const monthValue = inputDate.getMonth() + 1; // Thêm 1 để chuyển từ 0-11 thành 1-12

    // Định dạng giá trị tháng thành chuỗi
    let formattedMonth = monthValue.toString();
    // let today = new Date(todayin);
    // console.log("today", today);

    // let month = (today.getMonth() + 1).toString().padStart(2, '0');
    return formattedMonth;
  }

  getDateMonth(todayin) {
    let days = {
      Sunday: "Chủ Nhật",
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
    const monthValue = today.getMonth() + 1; // Thêm 1 để chuyển từ 0-11 thành 1-12

    // Định dạng giá trị tháng thành chuỗi
    let formattedMonth = monthValue.toString();
    return today.getDate() + '/' + formattedMonth + '/' + today.getFullYear();
  }


  getListDay(id: any, month: any, date: string) {
    console.log("id", id, month, date);
    
    this.dateArray = [];
    // Giả sử a là số ngày và b là đơn vị thời gian (tháng, ngày, năm)
    let a = 0;
    let b = '';
    // Ngày đầu vào
    let inputDate = new Date(date);
    if (id == 1) {
      a = 30;
      b = 'month';
      for (let i = 0; i < a; i++) {
        const currentDate = addDays(inputDate, i);
        const formattedDate = format(currentDate, 'MM-dd-yyyy');
        this.dateArray.push(formattedDate);
      }
    } else if (id == 2) {
      a = 12;
      b = 'week';
      for (let i = 0; i < a; i++) {
        const currentDate = addWeeks(inputDate, i);
        const formattedDate = format(currentDate, 'MM-dd-yyyy');
        this.dateArray.push(formattedDate);
      }
    } else if (id == 3) {
      let a = month; // Lấy 6 giá trị
      let b = 'month'; // Đơn vị thời gian là tháng

      // Chuyển đổi ngày đầu vào từ chuỗi sang kiểu Date
      // inputDate = new Date(date);

      for (let i = 0; i < a; i++) {
        // Thêm số tháng vào ngày đầu vào
        const currentDate = addMonths(inputDate, i);
        const formattedDate = format(currentDate, 'MM-dd-yyyy');
        this.dateArray.push(formattedDate);
      }
    }
  }

  convertDateToVietnameseFormat(dateStr) {
    const dateParts = dateStr.split('/');
    const day = parseInt(dateParts[1], 10);
    const month = parseInt(dateParts[0], 10);
    const year = parseInt(dateParts[2], 10);

    const monthNames = [
      "tháng 1", "tháng 2", "tháng 3", "tháng 4", "tháng 5", "tháng 6",
      "tháng 7", "tháng 8", "tháng 9", "tháng 10", "tháng 11", "tháng 12"
    ];

    const result = `Ngày ${day} ${monthNames[month - 1]} năm ${year}`;

    return result;
  }

  viewDetailinSchedule(id:any){
    this.selectedDate = id;
  }

  isDoneStatus(status: string): boolean {
    return status == 'DONE';
  }
  
  isOnMovingOrOnProcess(status: string): boolean {
    return status == 'ON_MOVING' || status == 'ON_PROCESS';
  }


  submit() {
    if(this.otherAddons){
      let child = {
          "serviceAddOnId": -1,
          "price": this.priceOther,
          "note": ""
      }
      this.listChecked.push(child)
    }
    let body = {
      addOns: this.listChecked,
      note: this.note
    }
    this.scheduleDialogRef.close(body);
  }
}
