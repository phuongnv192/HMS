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
    this.listPick = [];    
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
       // console.log('The dialog was closed');
       this.renderer.removeClass(document.body, 'modal-open');
       // this.dialogService.sendDataDialog(false);
     });
  }

  addCleaner(cleaner: any){
    this.listPick.push(cleaner);
    console.log(this.listPick, "listPick");
  }

  removeCleaner(index: any){
    this.listPick.splice(index, 1);

  }
}