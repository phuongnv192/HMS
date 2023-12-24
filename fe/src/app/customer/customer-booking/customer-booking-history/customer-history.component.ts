import { Component, OnInit, Renderer2 } from "@angular/core";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { CleanerRateDialog } from "src/app/booking/cleaner-rate-dialog/cleaner-rate-dialog";
import { BookingDetailDialog } from "src/app/employee/schedule/booking-detail-dialog/booking-detail-dialog";
import { AuthService } from "src/app/services/auth.service";
import { BookingService } from "src/app/services/booking.service";
import { CustomerService } from "src/app/services/customer.service";

@Component({
  selector: "app-customer-history",
  templateUrl: "./customer-history.component.html",
  styleUrls: ["./customer-history.component.scss"],
})
export class CustomerHistoryComponent implements OnInit {
  name = "";
  data: any;
  date: any;
  history: any;
  rateRange: string[];
  houseType: string[];
  searchRate: any;
  searchHouseType: string;
  customerId: any;
  page: any = 0
  size: any = 10
  dataCleaner = [];
  constructor(
    public dialog: MatDialog, private renderer: Renderer2,
    // private dialogService: DialogService
    public dialogRef: MatDialogRef<BookingDetailDialog>,
    // private bookingService: BookingService
    private bookingService: BookingService,
    private customerService: CustomerService,
    public ratedialogRef: MatDialogRef<CleanerRateDialog>,
  ) { }

  ngOnInit() {
    this.customerService.getListSchedule().subscribe(_res => {
      if (_res && _res.data) {
        this.data = _res.data.filter(a => a.status == 'DONE');
      }
    })

    this.history = this.data.history;
    this.searchRate = "4 - 5";
    this.searchHouseType = "APARTMENT";
    this.houseType = ["APARTMENT", "NORMAL", "Villa"];
    this.rateRange = ["0 - 1", "1 - 2", "2 - 3", "3 - 4", "4 - 5"];
    this.date = "19/11/2023";
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

  viewDetailinSchedule(detail: any) {
    // this.dialogService.sendDataDialog(true);
    console.log('document.body:', document.body); // Kiểm tra xem document.body có tồn tại hay không
    // if (this.dialogRef) {
    this.renderer.addClass(document.body, 'modal-open');
    this.dialogRef = this.dialog.open(BookingDetailDialog, {
      width: '820px',
      height: '80%',
      data: {
        data: detail
      },
      panelClass: ['view-detail']
    });
    console.log('this.dialogRef', this.dialogRef);

    this.dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('The dialog was closed');
        this.renderer.removeClass(document.body, 'modal-open');
        // this.dialogService.sendDataDialog(false);
      }
    });
    // }
  }

  cleanerRateDetail() {
    // this.dialogService.sendDataDialog(true);
    this.renderer.addClass(document.body, 'modal-open');
    this.ratedialogRef = this.dialog.open(CleanerRateDialog, {
      width: '500px',
      maxHeight: '65%',
      data: {
        data: this.data.cleaners,
      },
      panelClass: ['cleaner-detail']
    });

    this.ratedialogRef.afterClosed().subscribe(result => {
      // console.log('The dialog was closed');
      this.renderer.removeClass(document.body, 'modal-open');
      // this.dialogService.sendDataDialog(false);
    });
  }

  search() { }
}