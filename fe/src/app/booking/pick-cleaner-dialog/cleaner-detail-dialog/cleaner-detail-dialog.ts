import { Subscription } from 'rxjs';
import { NgZone, OnInit, Renderer2 } from '@angular/core';
import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CleanerDetailDialogData } from '../pick-cleaner-dialog';
import { BookingService } from 'src/app/services/booking.service';



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
  cleanerId: any;
  history: any;
  cleanerName: any;
  cleanerGemder: any;
  branch: string;
  cleanerActivityYear: any;
  averageRating: any;
  ratingNumber: any;

  constructor(
    public dialogRef: MatDialogRef<CleanerDetailDialog>,
    @Inject(MAT_DIALOG_DATA) public data: CleanerDetailDialogData,
    private bookingService: BookingService,
    private renderer: Renderer2,
    private ngZone: NgZone) {
  }


  ngOnInit(): void {  
    this.cleanerId= this.data.data;
    this.bookingService.getCleanerHistory(this.cleanerId).subscribe(item => {
      this.history= item.data.history;
      console.log("HAHAHAH", item.data);
      
      this.cleanerName = item.data.ratingOverview.name;
      this.cleanerGemder = item.data.ratingOverview.gender;
      this.branch = item.data.ratingOverview.branch ? '' : 'Hà Nội';
      this.cleanerActivityYear = item.data.ratingOverview.activityYear;
      this.averageRating = item.data.ratingOverview.averageRating;
      this.ratingNumber = item.data.ratingOverview.ratingNumber;
    })
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


  checkService(service: any){
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
}