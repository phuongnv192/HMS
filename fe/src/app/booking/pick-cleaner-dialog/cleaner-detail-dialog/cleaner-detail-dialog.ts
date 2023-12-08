import { Subscription } from 'rxjs';
import { NgZone, OnInit, Renderer2 } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CleanerDetailDialogData } from '../pick-cleaner-dialog';



@Component({
  selector: 'app-cleaner-detail-dialog',
  templateUrl: './cleaner-detail-dialog.html',
  styleUrls: ['./cleaner-detail-dialog.scss']
})

export class CleanerDetailDialog implements OnDestroy, OnInit {
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

  constructor(
    public dialogRef: MatDialogRef<CleanerDetailDialog>,
    @Inject(MAT_DIALOG_DATA) public data: CleanerDetailDialogData,
    private renderer: Renderer2,
    private ngZone: NgZone) {
  }


  ngOnInit(): void {  

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
        this.dialogRef.close(this.listAdvanceService);
      });
    } else {
      console.warn('dialogRef không tồn tại.');
    }
  }

  checkService(service: any){
    console.log("this.parentService 222", this.listAdvanceService);

    const index = this.listAdvanceService.indexOf(service);
    if (index === -1) {
      // Nếu giá trị không tồn tại, thêm vào mảng
      this.listAdvanceService.push(service);
    } else {
      // Nếu giá trị đã tồn tại, loại bỏ khỏi mảng
      this.listAdvanceService.splice(index, 1);
    }
    this.textListAdvanceService = this.listAdvanceService.join(', ');
  }
}