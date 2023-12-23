import { Subscription } from 'rxjs';
import { CleanerData, CleanerRateData } from '../booking.component';
import { OnInit, Renderer2 } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';

// import { DialogService } from 'src/app/services/dialog.service';

@Component({
  selector: 'app-cleaner-rate-dialog',
  templateUrl: './cleaner-rate-dialog.html',
  styleUrls: ['./cleaner-rate-dialog.css']
})

export class CleanerRateDialog implements OnDestroy, OnInit {
  public mobile: any;
  private _subscription = Subscription.EMPTY;
  // listCleaner: any[] = [
  //   // ... danh sách cleaner của bạn
  // ];
  listCleaner: any;
  dataCleaner: any;
  groupedCleaners: any[][] = [];

  constructor(
    public dialog: MatDialog, private renderer: Renderer2,
    // private dialogService: DialogService,
    public cleanerDialogRef: MatDialogRef<CleanerRateDialog>,
    @Inject(MAT_DIALOG_DATA) public data: CleanerRateData) {
  }

  ngOnInit(): void {
    if(this.data){
      this.listCleaner = this.data;

    }
    this.groupCleaners();

  }


  onNoClick(): void {
    this.cleanerDialogRef.close();
  }

  ngOnDestroy() {
    this._subscription.unsubscribe();
  }

  submitReview(){

  }

  private groupCleaners() {
    const cleanersPerTab = 1; // Số lượng cleaners trong mỗi tab
    for (let i = 0; i < this.listCleaner.length; i += cleanersPerTab) {
      this.groupedCleaners.push(this.listCleaner.slice(i, i + cleanersPerTab));
    }
  }

}