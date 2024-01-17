import { Subscription } from 'rxjs';
import { NgZone, OnInit, Renderer2 } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AddServiceDialogData } from '../calendar-dialog';
import { addHours, format, isBefore, parse } from 'date-fns';



@Component({
  selector: 'app-add-service-dialog',
  templateUrl: './add-service-dialog.html',
  styleUrls: ['./add-service-dialog.scss']
})

export class AddServiceDialog implements OnDestroy, OnInit {
  public mobile: any;
  private _subscription = Subscription.EMPTY;
  public name: any;
  public id: any;
  date: any;
  parentService = [];
  selectedServiceList: any;
  listAdvanceService = [];
  textListAdvanceService: any;
  checkTickService = false;
  listService: any;
  listAdvanceServiceId = [];
  _inTime: any;
  detail: { service: any[]; time: any; };
  isoStringST: any;
  isMorning: any;
  duration: any;
  isoStringET: any;
  scheduleTimeDescription: string;
  c_time: any;
  flag: any;

  constructor(
    public dialogRef: MatDialogRef<AddServiceDialog>,
    @Inject(MAT_DIALOG_DATA) public data: AddServiceDialogData,
    private renderer: Renderer2,
    private ngZone: NgZone) {
  }


  ngOnInit(): void {
    this.date = this.data.data;
    this.listService = this.data.addonService;
    this.selectedServiceList = this.data.servicePick;
    this._inTime = this.data.servicePick.startTime && this.reverseDateFormat(this.data.servicePick.startTime) != this.data.time ? this.reverseDateFormat(this.data.servicePick.startTime) : this.data.time;
    console.log("COMPARE", this.reverseDateFormat(this.data.servicePick.startTime), this.data.time);
    console.log("this.data.servicePick.startTime", this.data.servicePick.startTime);
    console.log("this.reverseDateFormat(this.data.servicePick.startTime)", this.reverseDateFormat(this.data.servicePick.startTime));

    if (this.data.servicePick && this.selectedServiceList.serviceAddOnIds) {
      this.data.addonService.forEach(val => {
        let checkedValue = false;
        if (this.selectedServiceList && this.selectedServiceList.serviceAddOnIds.includes(val.parent.id)) {
          checkedValue = true;
          this.checkService(val.parent.name, val.parent.id);

        }
        this.parentService.push({
          name: val,
          checked: checkedValue,
        });
      });
      

    } else {
      this.data.addonService.forEach(val => {
        let checkedValue = false;
        if (this.selectedServiceList.length > 0 && this.selectedServiceList.serviceAddOnIds.includes(val.parent.id)) {
          checkedValue = true;
        }
        this.parentService.push({
          name: val,
          checked: checkedValue,
        });
      });
    }

  }

  ValidateExpDate(_val: any, event) {
    this.c_time = false;
    // this.error_post_message = '';
    var ua = navigator.userAgent.toLowerCase();
    let v = "";
    // check for safari macbook
    if (ua.indexOf("safari") != -1 && this.isMacintosh()) {
      if (ua.indexOf("chrome") > -1) {
        this._inTime = _val.value.replace(/\s+/g, "").replace(/[^0-9]/gi, "");
        v = this._inTime;
      } else {
        if (_val.value) {
          if (
            event.keyCode === 8 ||
            event.key === "Backspace" ||
            event.inputType == "deleteContentBackward"
          ) {
            if (event.target.name == "exp_date" && this._inTime) {
              if (this._inTime.length != 3) {
                this._inTime = this._inTime.slice(0, -1);
              } else {
                this._inTime = this._inTime.slice(0, -2);
              }
            }
            event.preventDefault();
          } else if (
            !isNaN(_val.value.substr(_val.value.length - 1)) &&
            event.inputType == "insertCompositionText"
          ) {
            this._inTime =
              this._inTime + _val.value.substr(_val.value.length - 1);
          }
        } else {
          this._inTime = "";
        }
        v = this._inTime;
      }
    } else {
      this._inTime = _val.value.replace(/\s+/g, "").replace(/[^0-9]/gi, "");
      v = this._inTime;
    }
    const newdate = this.time_format(v);
    _val.value = newdate;
    this._inTime = newdate;
    if (this.validTime(this._inTime)) {
      this.c_time = false;
    } else {
    }
    // this.enablePayNow();
  }
  isMacintosh() {
    return navigator.platform.toLowerCase().indexOf("mac") > -1;
  }

  time_format(value) {
    let v = value.replace(/\s+/g, "").replace(/[^0-9]/gi, "");
    if (
      v.length === 2 &&
      this.flag.length === 3 &&
      this.flag.charAt(this.flag.length - 1) === ":"
    ) {
      v = "";
    }
    if (v && !v.startsWith("0") && !v.startsWith("1")) {
      if (v.length === 1) {
        v = "0" + v + ":";
      } else if (v.length === 2) {
        v = v + ":";
      }
    } else if ((v <= 19 && v >= 8) || v.length === 2) {
      v = v + ":";
    }
    const matches = v.match(/\d{2,4}/g);
    const match = (matches && matches[0]) || "";
    const parts = [];
    for (let i = 0, len = match.length; i < len; i += 2) {
      parts.push(match.substring(i, i + 2));
      if (len === 2) {
        parts.push("");
      }
    }
    if (parts.length) {
      this.flag = parts.join(":");
      return parts.join(":");
    } else {
      this.flag = value;
      return value;
    }
  }

  public blurExpDate(_val: any): void {
    this.scheduleTimeDescription = "";
    this.c_time = !this.validTime(this._inTime);
    const v = this._inTime.replace(/\s+/g, "").replace(/[^0-9]/gi, "");
    const newdate = this.time_format(v);
    _val.value = newdate;
    this._inTime = newdate;
    if (!this.c_time) {
      this.scheduleTimeDescription = this.convertTimeFormat(newdate);
    }
  }

  convertTimeFormat(inputTime: string): string {
    // Kiểm tra xem inputTime có đúng định dạng hh:mm không
    const timeRegex = /^(0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$/;
    if (!timeRegex.test(inputTime)) {
      // Nếu không đúng định dạng, trả về thông báo lỗi hoặc giá trị ban đầu
      return "Invalid time format";
    }

    // Chuyển đổi thành đối tượng Date để dễ xử lý
    const inputDate = new Date(`1970-01-01T${inputTime}:00Z`);

    // Lấy giờ và phút
    const hours = inputDate.getHours();
    const minutes = inputDate.getMinutes();

    // Tạo chuỗi kết quả
    const result = `${hours} giờ ${minutes} phút`;

    return result;
  }

  validTime(value) {
    return (
      ((value.length == 4 && value.search("/") == -1) || value.length == 5) &&
      parseInt(value.split(":")[0], 0) >= 8 &&
      parseInt(value.split(":")[0], 0) < 20 &&
      parseInt(value.split(":")[1], 0) <= 60
    );
  }

  convertTime() {    
    const combinedDateTimeString = `${this.date} ${this._inTime}`;
  
    // Sử dụng thư viện date-fns để parse chuỗi ngày giờ
    const dateTimeObject = parse(combinedDateTimeString, 'MM-dd-yyyy HH:mm', new Date());
    
    // Chuyển định dạng ngày thành ISO 8601
    const isoString = format(dateTimeObject, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    
    // Gán giá trị cho isoStringST
    this.isoStringST = isoString;

  }

  reverseDateFormat(inputDate: string): string {    
    const inputDateTime = new Date(inputDate);

    // Kiểm tra xem ngày giờ có hợp lệ không
    if (isNaN(inputDateTime.getTime())) {
      return 'Invalid Date';
    }

    // Lấy giờ và phút từ đối tượng Date
    const hours = String(inputDateTime.getHours()).padStart(2, '0');
    const minutes = String(inputDateTime.getMinutes()).padStart(2, '0');

    // Tạo chuỗi định dạng 'HH:mm'
    const formattedTime = `${hours}:${minutes}`;

    return formattedTime;
  }


  ngOnDestroy(): void {
    this._subscription.unsubscribe();
  }

  cancel(): void {
    if (this.dialogRef) {
      this.ngZone.run(() => {
        console.log('Đóng dialog');
        this.dialogRef.close();
      });
    } else {
      console.warn('dialogRef không tồn tại.');
    }
  }

  confirm() {
    this.convertTime();
    this.detail = {
      service: this.listAdvanceServiceId,
      time: this.isoStringST
    }
    console.log("DETAIL", this.detail);

    if (this.dialogRef) {
      this.ngZone.run(() => {
        console.log('Đóng dialog');
        this.dialogRef.close(this.detail);
      });
    } else {
      console.warn('dialogRef không tồn tại.');
    }
  }

  checkService(service: any, id: any) {
    const index = this.listAdvanceService.indexOf(service);
    const idex = this.listAdvanceServiceId.indexOf(id);
    if (index === -1) {
      // Nếu giá trị không tồn tại, thêm vào mảng
      this.listAdvanceService.push(service);
    } else {
      // Nếu giá trị đã tồn tại, loại bỏ khỏi mảng
      this.listAdvanceService.splice(index, 1);
      // this.listAdvanceServiceId.splice(index, 1);
    }

    if (idex == -1) {
      this.listAdvanceServiceId.push(id);
    } else {
      // Nếu giá trị đã tồn tại, loại bỏ khỏi mảng
      this.listAdvanceServiceId.splice(index, 1);
    }
    this.textListAdvanceService = this.listAdvanceService.join(', ');
  }
}