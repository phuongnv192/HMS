import { Subscription } from 'rxjs';
import { NgZone, OnInit, Renderer2 } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AddServiceDialogData } from '../calendar-dialog';



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
    console.log(".this.data.servicePick",this.data.servicePick);

    if(this.data.servicePick && this.selectedServiceList.serviceAddOnIds){
      console.log("this.selectedServiceList",this.selectedServiceList);
      
      this.data.addonService.forEach(val => {
        let checkedValue = false;
        if(this.selectedServiceList && this.selectedServiceList.serviceAddOnIds.includes(val.parent.id)){
          checkedValue = true;
        }
        this.parentService.push({
          name: val,
          checked: checkedValue 
          });
      });    
    } else {
      this.data.addonService.forEach(val => {
        let checkedValue = false;        
        if(this.selectedServiceList.length > 0 && this.selectedServiceList.serviceAddOnIds.includes(val.parent.id)){
          checkedValue = true;
        }
        this.parentService.push({
          name: val,
          checked: checkedValue 
          });
      });    
    }
    
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

  confirm(){
    if (this.dialogRef) {
      this.ngZone.run(() => {
        console.log('Đóng dialog');
        this.dialogRef.close(this.listAdvanceServiceId);
      });
    } else {
      console.warn('dialogRef không tồn tại.');
    }
  }

  checkService(service: any, id: any){
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

    if(idex == -1){
      this.listAdvanceServiceId.push(id);
    } else {
      // Nếu giá trị đã tồn tại, loại bỏ khỏi mảng
      this.listAdvanceServiceId.splice(index, 1);
    }
    this.textListAdvanceService = this.listAdvanceService.join(', ');
  }
}