import { Subscription } from 'rxjs';
import { OnInit, Renderer2 } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
import { BookingService } from 'src/app/services/booking.service';
import { ToastrService } from 'ngx-toastr';
// import { DialogService } from 'src/app/services/dialog.service';

export interface ChangeStatusDialogData {
  data: any;
  id: any,
  addService: any;
}

@Component({
  selector: 'app-change-status-dialog',
  templateUrl: './change-status-dialog.html',
  styleUrls: ['./change-status-dialog.scss']
})

export class ChangeStatusDialog implements OnDestroy, OnInit {
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

  constructor(
    private sanitizer: DomSanitizer,
    private _activated: ActivatedRoute,
    public dialog: MatDialog, private renderer: Renderer2,
    private bookingServicee: BookingService,
    private toastr: ToastrService,
    // private dialogService: DialogService,
    private statusDialogRef: MatDialogRef<ChangeStatusDialog>,
    @Inject(MAT_DIALOG_DATA) public data: ChangeStatusDialogData) {
  }

  ngOnInit(): void {
    //  this.serviceAddOns = this.data.addService
    this.bookingServicee.getServiceAddOns("-1").subscribe(data => {
      if (data) {
        this.priceList = data.data;
        this.priceList.forEach(element => {
          console.log("element.parent.id", element.parent.id);
          console.log("this.data.addService", this.data.addService);
          this.data.addService.forEach(res => {
            if (res.id == element.parent.id) {
              this.serviceAddOns.push(element);
              res.isChecked = false;
              // Lặp qua mảng serviceAddOns
            }

          })
          // this.serviceAddOns.forEach(service => {
          //   // Tăng giá trị của biến đếm để có id duy nhất cho phần tử "Other"
          //   let otherIdCounter = -1;
          //   // Tạo một phần tử "Other" với id là otherIdCounter
          //   const otherItem = {
          //     id: otherIdCounter--,
          //     name: 'Other',
          //     parentId: service.parent.id,
          //     price: 0,
          //     duration: 1,
          //     status: 'ACTIVE'
          //   };
          //   // Thêm phần tử "Other" vào mảng children của mảng đầu tiên
          //     service.children.push(otherItem);                
          // });

        });
        console.log("this.serviceAddOns", this.serviceAddOns);

      }
    });
  }

  checkOther(){
    this.otherAddons = !this.otherAddons;
    console.log("otherAddons", this.otherAddons);
  }


  filterCleaners(): void {
    this.listPick = this.listCleaner.filter(cleaner => this.listPickData.includes(cleaner.cleanerId));
  }


  onNoClick(): void {
    this.statusDialogRef.close();
  }

  ngOnDestroy() {
    this._subscription.unsubscribe();
  }

  checkService(parent: any) {
    const index = this.findIndexInListChecked(parent.id);
  
    if (index === -1) {
      // Nếu không tồn tại, thêm vào listChecked
      this.listChecked.push({
        serviceAddOnId: parent.id,
        price: parent.price,
        note: ""
      });
    } else {
      // Nếu tồn tại, xóa khỏi listChecked
      this.listChecked.splice(index, 1);
    }
    console.log("this.listChecked", this.listChecked);
    
  }
  
  // Hàm để tìm kiếm vị trí của phần tử trong listChecked dựa trên serviceAddOnId
  findIndexInListChecked(serviceAddOnId: number): number {
    return this.listChecked.findIndex(item => item.serviceAddOnId === serviceAddOnId);
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
    this.statusDialogRef.close(body);
  }
}