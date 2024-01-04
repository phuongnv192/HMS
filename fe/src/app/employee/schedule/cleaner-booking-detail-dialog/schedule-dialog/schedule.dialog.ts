import { Subscription } from 'rxjs';
import { OnInit, Renderer2 } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { BookingService } from 'src/app/services/booking.service';
import { ToastrService } from 'ngx-toastr';
import { addDays, addMonths, addWeeks, format } from 'date-fns';
import { ChangeStatusDialog } from '../change-status-dialog/change-status-dialog';
import { CleanerService } from 'src/app/services/cleaner.service';
// import { DialogService } from 'src/app/services/dialog.service';

export interface ScheduleDialogData {
  data: any;
  id: any,
  addService: any;
  status: any;
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
  listChecked = [];
  dateArray = [];
  selectedDate: any;
  selectedList: any;
  title_confirm: string;
  role = 'CLEANER';
  selectedData: any;
  currentDay: string;

  constructor(
    private sanitizer: DomSanitizer,
    private _activated: ActivatedRoute,
    public dialog: MatDialog, private renderer: Renderer2,
    private bookingServicee: BookingService,
    private toastr: ToastrService,
    // private dialogService: DialogService,
    private scheduleDialogRef: MatDialogRef<ScheduleDialog>,
    public statusdialogRef: MatDialogRef<ChangeStatusDialog>,
    private cleanerService: CleanerService,
    @Inject(MAT_DIALOG_DATA) public data: ScheduleDialogData) {
  }

  ngOnInit(): void {
    this.role = sessionStorage.getItem("roleName");
    console.log(this.data.data, "ABCABC");
    for (const schedule of this.data.data) {
      const dateInfo = {
        workDate: schedule.workDate,
        scheduleId: schedule.scheduleId,
        listAddOns: schedule.serviceAddOns.map(addOn => addOn.name).join(', '),
        status: schedule.status
      };
      this.dateArray.push(dateInfo);
    }
    this.getCurrentDay();
    // let firstNotDoneScheduleId: number | null = null;

    // for (const schedule of this.dateArray) {
    //   if (schedule.status !== 'DONE') {
    //     firstNotDoneScheduleId = schedule.scheduleId;
    //     break; // Break out of the loop after finding the first element
    //   }
    // }
    const firstNotDoneSchedule = this.dateArray.find(schedule => schedule.status !== 'DONE');
    this.selectedDate = firstNotDoneSchedule.scheduleId ? firstNotDoneSchedule.scheduleId : null;
    this.selectedList = firstNotDoneSchedule ? firstNotDoneSchedule : null;
    if (this.selectedList.status == 'CONFIRMED' || this.selectedList.status == 'RECEIVED') {
      this.title_confirm = '-> Đang di chuyển';
    } else if (this.selectedList.status == 'ON_MOVING') {
      this.title_confirm = '-> Đang dọn';
    } else if (this.selectedList.status == 'ON_PROCESS') {
      this.title_confirm = 'Cập nhật giá dịch vụ';
    }
    console.log("selectedList", this.selectedList);

  }

  getCurrentDay() {
    const today = new Date();
    const year = today.getFullYear();
    const month = this.padZero(today.getMonth() + 1);
    const day = this.padZero(today.getDate());

    this.currentDay = `${year}-${month}-${day}`;
  }

  padZero(num: number): string {
    return num < 10 ? `0${num}` : `${num}`;
  }

  checkOther() {
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

  viewDetailinSchedule(id: any, list: any) {
    this.selectedDate = id;
    this.selectedList = list;
    if (this.selectedList.status == 'CONFIRMED' || this.selectedList.status == 'RECEIVED') {
      this.title_confirm = '-> Đang di chuyển';
    } else if (this.selectedList.status == 'ON_MOVING') {
      this.title_confirm = '-> Đang dọn';
    } else if (this.selectedList.status == 'ON_PROCESS') {
      this.title_confirm = 'Cập nhật giá dịch vụ';
    }

  }

  isConfirmedStatus(status: string): boolean {
    return status == 'CONFIRMED' || status == 'RECEIVED';
  }

  isDoneStatus(status: string): boolean {
    return status == 'DONE' || status == 'CANCELLED';
  }

  isOnMovingOrOnProcess(status: string): boolean {
    return status == 'ON_MOVING' || status == 'ON_PROCESS';
  }

  confirm(id: any) {
    let status = "";
    let body = {};
    this.selectedData = this.data.data.find(schedule => schedule.scheduleId === id);


    if (this.selectedList.status == "CONFIRMED" || this.selectedList.status == "ON_MOVING" || this.selectedList.status == "RECEIVED") {
      if ((this.selectedList.status != "DONE" && this.selectedList.status == "CONFIRMED") || this.selectedList.status == "RECEIVED") {
        status = "ON_MOVING";
        body = {
          "scheduleId": id,
          "status": status,
          "paymentStatus": "PAYMENT_SUCCESS",
          "addOns": [],
          "note": ""
        }
      } else if (this.selectedList.status == "ON_MOVING") {
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
          this.scheduleDialogRef.close(true);
        },
        error: (err) => {
          this.scheduleDialogRef.close(true);
          this.statusdialogRef.close(true);
          console.log(err);
        }, // errorHandler
      })
    } else {
      let addOns = [];
      let note = "";
      console.log("this.selectedData 3", this.selectedData);

      this.renderer.addClass(document.body, 'modal-open');
      this.statusdialogRef = this.dialog.open(ChangeStatusDialog, {
        width: '500px',
        maxHeight: '55%',
        data: {
          data: id,
          id: this.selectedData.bookingId,
          addService: this.selectedData.serviceAddOns,
        },
        panelClass: ['change-stauts']
      });
    console.log("this.selectedData 2", this.selectedData);


      this.statusdialogRef.afterClosed().subscribe(result => {
        if (result) {
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
              this.scheduleDialogRef.close(true);
            },
            error: (err) => {
              this.scheduleDialogRef.close(true);
              this.statusdialogRef.close(true);
              console.log(err);
            }, // errorHandler
          })
        }
        this.renderer.removeClass(document.body, 'modal-open');
      });
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
        this.scheduleDialogRef.close(true);
      },
      error: (err) => {
        this.scheduleDialogRef.close(true);
        this.statusdialogRef.close(true);
        console.log(err);
      }, // errorHandler
    })
  }

  cancel(id: any){
    let body = {
      bookingId: id,
      note: '',
    }
    this.cleanerService.reject(body).subscribe({
      next: (res) => {
        this.scheduleDialogRef.close(true);
        this.statusdialogRef.close(true);      }
    });
  }

  submit() {
    if (this.otherAddons) {
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
