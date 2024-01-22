import { Component, OnInit, Renderer2 } from "@angular/core";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { ActivatedRoute, Router } from "@angular/router";
import { ToastrService } from "ngx-toastr";
import { AuthService } from "src/app/services/auth.service";
import { BookingService } from "src/app/services/booking.service";
import { AddServiceManagementDialog } from "./add-service/add-service-dialog";
import { CleanerService } from "src/app/services/cleaner.service";

export interface addOnServiceData {
  data: any;
  id: any;
}

@Component({
  selector: "app-booking-management",
  templateUrl: "./booking-management.component.html",
  styleUrls: ["./booking-management.component.scss"],
})

export class BookingManagementComponent implements OnInit {
  name = "";
  data: any;
  date: any;
  service = [];
  jwtToken: string;
  leadId: any;
  serviceTypeData: any;
  serviceType: any;
  priceList = [];
  parentService: any;
  childrenService: any;
  page = 0;
  size = 20;
  constructor(
    private authService: AuthService,
    private bookingService: BookingService,
    private cleanerService: CleanerService,
    private route: ActivatedRoute,
    private router: Router,
    public dialog: MatDialog, private renderer: Renderer2,
    // private dialogService: DialogService,
    public serviceDialogRef: MatDialogRef<AddServiceManagementDialog>,
    private toastr: ToastrService,
  ) { }

  ngOnInit() {

    this.cleanerService
    .getListBooking(this.page, this.size)
    .subscribe((res) => {
      if (res && res.data) {
        console.log("Thông tin danh sách booking", res);
        console.log("Thông tin danh sách booking data", res.data);
        
      }
    });
    // let id = this.route.snapshot.paramMap.get("id");
    this.authService.getUserInfor().subscribe(data => {
      this.leadId = data.data.id;
    });
    this.bookingService.getServiceType().subscribe(data => {
      if (data) {
        this.serviceTypeData = data.data;
        console.log("serviceTypeData", this.serviceTypeData);
        this.serviceTypeData.forEach(element => {
          this.serviceType.push(element.serviceTypeName);
        });
      }

    });

    this.bookingService.getServiceAddOns("-1").subscribe(data => {
      if (data) {
        this.parentService = data.data;
        // this.parentService = this.priceList.data;
        console.log("parentService", this.parentService);
        this.childrenService = this.data[0].children;
      }
    });

    this.service = [{
      title: "Tổng vệ sinh",
      status: "hoạt động",
      desciption: "Dịch vụ tổng vệ sinh bao gồm các tiêu chí liên quan tới diện tích, khu vực dọn dẹp. Dịch vụ bao gồm cả thời gian định kỳ và trong ngày."
    }];

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

  addServiceChild(id: any){
    let title = '';
    if(id){
      title = "Thêm loại dịch vụ tiện ích theo nhóm "+"("+ id +")";
    } else {
      title = "Thêm nhóm dịch vụ tiện ích";
    }
    this.renderer.addClass(document.body, 'modal-open');
    this.serviceDialogRef = this.dialog.open(AddServiceManagementDialog, {
      width: '700px',
      maxHeight: '50%',
      data: {
        data: title,
        id: id
      },
      panelClass: ['addon-service']
    });

    this.serviceDialogRef.afterClosed().subscribe(result => {
      this.renderer.removeClass(document.body, 'modal-open');
      // this.dialogService.sendDataDialog(false);
    });
  }

}