import { Subscription } from 'rxjs';
import { PickCleanerData } from '../booking.component';
import { OnInit, Renderer2 } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { DomSanitizer } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';
// import { DialogService } from 'src/app/services/dialog.service';
import { CleanerRateDialog } from '../cleaner-rate-dialog/cleaner-rate-dialog';
import { CleanerDetailDialog } from './cleaner-detail-dialog/cleaner-detail-dialog';

export interface CleanerDetailDialogData {
  data: any;
}

@Component({
  selector: 'app-pick-cleaner-dialog',
  templateUrl: './pick-cleaner-dialog.html',
  styleUrls: ['./pick-cleaner-dialog.scss']
})

export class PickCleanerDialog implements OnDestroy, OnInit {
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

  constructor(
    private sanitizer: DomSanitizer,
    private _activated: ActivatedRoute,
    public dialog: MatDialog, private renderer: Renderer2,
    // private dialogService: DialogService,
    public dialogRef: MatDialogRef<CleanerRateDialog>,
    public dialogRefCleaner: MatDialogRef<CleanerDetailDialog>,
    public cleanerDialogRef: MatDialogRef<PickCleanerDialog>,
    @Inject(MAT_DIALOG_DATA) public data: PickCleanerData) {
  }

  ngOnInit(): void {
    this.listCleaner = this.data.data;
    this.listA = this.listCleaner.slice(0, 5);
    this.listB = this.listCleaner.slice(5);
    if(this.data.listPick){
      this.listPickData = this.data.listPick;
      this.filterCleaners();
    } else {
      this.listPickData = [];
      this.listPick = [];
    }
    console.log("listCleaner", this.listCleaner);
    console.log("LOAD listPick", this.listPick);
    console.log("LOAD listPickData", this.listPickData);
  }

  filterCleaners(): void {
    this.listPick = this.listCleaner.filter(cleaner => this.listPickData.includes(cleaner.cleanerId));
  }


  onNoClick(): void {
    this.cleanerDialogRef.close();
  }

  ngOnDestroy() {
    this._subscription.unsubscribe();
  }

  cleanerRateDetail() {
    // this.dialogService.sendDataDialog(true);
    this.renderer.addClass(document.body, 'modal-open');
    this.dialogRef = this.dialog.open(CleanerRateDialog, {
      width: '400px',
      maxHeight: '75%',
      data: {
        data: this.dataCleaner,
      },
      panelClass: ['cleaner-detail']
    });

    this.dialogRef.afterClosed().subscribe(result => {
      // console.log('The dialog was closed');
      this.renderer.removeClass(document.body, 'modal-open');
      // this.dialogService.sendDataDialog(false);
    });
  }

  searchCleaner(searchname: any) {
    this.listCleaner = []
    this.data.data.forEach(res => {
      if(res.name.includes(searchname)){
        this.listCleaner.push(res);
      } else {
        this.noDataSearch = true;
      }
    })
  }

  generateStarRating(rating: number): string {
    const stars: string[] = [];
    const fullStars = Math.floor(rating);
    const hasHalfStar = rating % 1 !== 0;

    for (let i = 0; i < fullStars; i++) {
      stars.push('<i class="fas fa-star"></i>');
    }

    if (hasHalfStar) {
      stars.push('<i class="fas fa-star-half-alt"></i>');
    }

    return stars.join('');
  }

  viewDetailCleaner(cleanerId: any){
    this.selectedCleaner = cleanerId;
     // this.dialogService.sendDataDialog(true);
     this.renderer.addClass(document.body, 'modal-open');
     this.dialogRefCleaner = this.dialog.open(CleanerDetailDialog, {
       width: '900px',
       maxHeight: '70%',
       data: {
         data: cleanerId,
       },
       panelClass: ['cleaner-detail']
     });
 
     this.dialogRefCleaner.afterClosed().subscribe(result => {
       this.renderer.removeClass(document.body, 'modal-open');
       // this.dialogService.sendDataDialog(false);
     });
  }

  // addCleaner(cleaner: any){
  //   this.listPick.push(cleaner);
  //   this.listPickData = this.listPick.map(cleaner => cleaner.cleanerId);
  // }

  // removeCleaner(index: any){
  //   this.listPick.splice(index, 1);
  //   this.listPickData = this.listPick.map(cleaner => cleaner.cleanerId);
  // }

  addCleaner(cleaner: any): void {
    this.listPick.push(cleaner);
    this.listPickData = this.listPick.map(cleaner => cleaner.cleanerId);
    this.updateListA();
  }
  
  removeCleaner(index: any): void {
    this.listPick.splice(index, 1)[0];
    this.listPickData = this.listPick.map(cleaner => cleaner.cleanerId);
    this.updateListA();
  }
  
  updateListA(): void {
    this.listCleaner.forEach(cleanerA => {
      cleanerA.selected = this.listPick.some(cleanerPick => cleanerPick.cleanerId === cleanerA.cleanerId);
    });
    this.listA = this.listCleaner.slice(0, 5);
    this.listB = this.listCleaner.slice(5);
  }

  pickCleaner(){
    this.cleanerDialogRef.close(this.listPickData);
  }
}


// {
//   "hostName": "hms system",
//   "hostPhone": "0369156413",
//   "hostAddress": "số 6, Minh Khai, Hà Nội",
//   "hostDistance": "6km",
//   "distancePrice": 10000,
//   "houseType": "APARTMENT",
//   "floorNumber": 2,
//   "floorArea": "M260",
//   "cleanerIds": null,
//   "bookingSchedules": [
//       {
//           "floorNumber": 4,
//           "workDate": "2023-12-03",
//           "startTime": null,
//           "endTime": null,
//           "serviceAddOnIds": []
//       }
//   ],
//   "serviceTypeId": 3,
//   "servicePackageId": 3,
//   "serviceAddOnIds": [3],
//   "section": "MOR",
//   "startTime": "2023-12-03T10:12:27.374+00:00",
//   "endTime": "2023-12-03T16:12:27.374+00:00",
//   "workDate": "2023-12-03",
//   "dayOfTheWeek": null,
//   "note": "không có gì"
// }