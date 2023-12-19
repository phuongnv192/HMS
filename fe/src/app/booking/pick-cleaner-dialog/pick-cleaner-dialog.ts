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
  searchResults: any[];
  listPickNameData: any;
  dataPickInfo: { listPickData: any[]; listPickDataName: any; };

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
    // this.listB = this.listCleaner.slice(5);
    if (this.data.listPick && this.data.listPick.length > 0) {
      this.listPickData = this.data.listPick;
      this.filterCleaners();
      console.log(1111, this.listPick);
      console.log(3333, this.listCleaner);

      this.listCleaner.forEach(element => {

        const index = this.listPick.findIndex(res => res.cleanerId == element.cleanerId);
        console.log(4444, index);
        if (index !== -1) {
          this.listCleaner = this.listCleaner.filter((item, i) => i !== index);
          console.log(555, this.listA);
        }
      });
      this.listA = this.listCleaner.slice(0, 10);
    } else {
      this.listPickData = [];
      this.listPick = [];
      this.listA = this.listCleaner.slice(0, 10);
    }
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

  searchCleaner() {
    this.searchResults = [];
    this.noDataSearch = false;

    this.data.data.forEach(res => {
      const isPicked = this.listPick.some(pickedCleaner => pickedCleaner.cleanerId === res.cleanerId);
      // Nếu cleaner chưa được chọn và tên chứa trong searchname
      if (!isPicked && res.name.includes(this.searchname)) {
        this.searchResults.push(res);
      }
    });

    // Hiển thị kết quả tìm kiếm hoặc thông báo không có dữ liệu
    if (this.searchResults.length === 0) {
      console.log("no data search found");
      this.noDataSearch = true;
    } else {
      console.log("have data");
      this.noDataSearch = false;
      // Cập nhật listCleaner để hiển thị kết quả tìm kiếm
      this.listCleaner = this.searchResults;
      this.listA = this.listCleaner.slice(0, 10);

    }
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

  viewDetailCleaner(cleanerId: any) {
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

  addCleaner(cleaner: any, index: any): void {
    this.listPick.push(cleaner);
    this.listPickData = this.listPick.map(cleaner => cleaner.cleanerId);
    this.listPickNameData = this.listPick.map(cleaner => cleaner.name);
    this.listCleaner = this.listCleaner.filter((item, i) => i !== index);
    this.listA = this.listCleaner.slice(0, 10);
    // this.updateListCleaner(index, 'remove', cleaner);
  }

  removeCleaner(index: any): void {
    const removedCleaner = this.listPick.splice(index, 1)[0];
    this.listCleaner.push(removedCleaner);
    this.listPickData = this.listPick.map(cleaner => cleaner.cleanerId);
    this.listPickNameData = this.listPick.map(cleaner => cleaner.name);
    this.listA = this.listCleaner.slice(0, 10);
    // this.updateListCleaner(index, 'add', []);
  }

  // updateListCleaner(index: any, type:string, cleaner:any): void {
  //   if(type == 'add'){
  //     this.listCleaner.push(cleaner);
  //   } else if (type == 'remove') {
  //     this.listCleaner.splice(index, 1)[0];
  //   }

  //   this.listA = this.listCleaner.slice(0, 10);
  // }

  pickCleaner() {
    this.dataPickInfo = {
      listPickData: this.listPickData,
      listPickDataName: this.listPickNameData
    }
    console.log("this.dataPickInfo", this.dataPickInfo);

    this.cleanerDialogRef.close(this.dataPickInfo);
    this._subscription.unsubscribe();
  }
}