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
  parentService: any;
  selectedServiceList: any;
  listAdvanceService: any;
  textListAdvanceService: any;
  checkTickService = false;

  constructor(
    public dialogRef: MatDialogRef<AddServiceDialog>,
    @Inject(MAT_DIALOG_DATA) public data: AddServiceDialogData,
    private renderer: Renderer2,
    private ngZone: NgZone) {
  }


  ngOnInit(): void {  
    this.date = this.data.data;
    this.parentService = this.data.addonService;
    this.selectedServiceList = this.data.servicePick;
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
        this.dialogRef.close(this.selectedServiceList);
      });
    } else {
      console.warn('dialogRef không tồn tại.');
    }
  }

  checkService(service: any){
    const index = this.listAdvanceService.indexOf(service);
    if (index === -1) {
      // Nếu giá trị không tồn tại, thêm vào mảng
      this.listAdvanceService.push(service);
    } else {
      // Nếu giá trị đã tồn tại, loại bỏ khỏi mảng
      this.listAdvanceService.splice(index, 1);
    }
    this.textListAdvanceService = this.listAdvanceService.join(', ')
  }
}
