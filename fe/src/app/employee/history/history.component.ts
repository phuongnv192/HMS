import { HttpParams } from "@angular/common/http";
import { Component, OnInit, Renderer2 } from "@angular/core";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ActivatedRoute } from "@angular/router";
import { AuthService } from "src/app/services/auth.service";
import { BookingService } from "src/app/services/booking.service";
import { CleanerService } from "src/app/services/cleaner.service";
import { CleanerBookingDetailDialog } from "../schedule/cleaner-booking-detail-dialog/cleaner-booking-detail-dialog";
import { CustomerService } from "src/app/services/customer.service";

@Component({
  selector: "app-history",
  templateUrl: "./history.component.html",
  styleUrls: ["./history.component.scss"],
})
export class HistoryComponent implements OnInit {
  name = "";
  data: any;
  date: any;
  history: any;
  rateRange: string[];
  houseType: string[];
  searchRate: any;
  searchHouseType: string;
  jwtToken: string;
  cleanerId: any;
  listBooking: any;
  size: number = 10;
  page: number = 0;
  type = '';

  constructor(
    private authService: AuthService,
    private bookingService: BookingService,
    private cleanService: CleanerService,
    private customerService: CustomerService,
    private route: ActivatedRoute,
    public dialog: MatDialog, private renderer: Renderer2,
    public dialogRef: MatDialogRef<CleanerBookingDetailDialog>,
  ) { }

  getListCleaner(id: any) {
    var req = new HttpParams()
      .set("page", this.page)
      .set("size", this.size);
    this.cleanService.getListSchedule(id, req).subscribe({
      next: (res) => {
        this.history = res.data.filter(a => a.status == 'DONE' || a.status == 'CANCELLED');
    console.log(this.history, "this.history");
        
      },
      error: (error) => { },
    });
  }

  ngOnInit() {
    // let id = this.route.snapshot.paramMap.get("id");
    this.authService.getUserInfor().subscribe(data=> {
      this.cleanerId = data.data.id;
    this.bookingService.getBookingHistory(this.cleanerId).subscribe((data) => {
      this.data = data;
    });
    this.getListCleaner(this.cleanerId);
    
  });
    this.searchRate = "4 - 5";
    this.searchHouseType = "APARTMENT";
    this.houseType = ["APARTMENT", "NORMAL", "Villa"];
    this.rateRange = ["0 - 1", "1 - 2", "2 - 3", "3 - 4", "4 - 5"];
    this.date = "19/11/2023";
  }

  showDetail(detail: any, type: string) {
    // this.bookingService.getBookingDetail(detail).subscribe((data) => {
    // detail = data.data;
    if(detail.servicePackageName){
      type = 'schedule';
    } else {
      type = 'day';
    }
    this.renderer.addClass(document.body, 'modal-open');
    this.dialogRef = this.dialog.open(CleanerBookingDetailDialog, {
      width: '850px',
      height: '85%',
      data: {
        data: detail,
        type: type
      },
      panelClass: ['view-detail']
    });

    this.dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      this.renderer.removeClass(document.body, 'modal-open');
    });
    // });
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

    return stars.join("");
  }

  search() { }

}
