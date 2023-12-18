import { Component, OnInit, Renderer2 } from "@angular/core";
import { CleanerService } from "../services/cleaner.service";
import { HttpParams } from "@angular/common/http";
import { NgbToast } from "@ng-bootstrap/ng-bootstrap";
import { Router } from "@angular/router";
import { AuthService } from "../services/auth.service";
import { ToastrService } from "ngx-toastr";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";
import { AddCleanerDialog } from "./add-cleaner/add-cleaner-dialog";

@Component({
  selector: "app-tables",
  templateUrl: "./tables.component.html",
  styleUrls: ["./tables.component.scss"],
})
export class ListCleanerComponent implements OnInit {
  listCleaner: any;
  searchAge: any;
  searchRate: any;
  searchStatus: any;
  ageRange: any;
  rateRange: any;
  statusRange: any;
  empName = "";
  size: number = 10;
  page: number = 0;
  jwtToken: any;
  data: any;

  constructor(private cleanService: CleanerService, private router: Router, private authService: AuthService,
    private renderer: Renderer2,
    public dialogRef: MatDialogRef<AddCleanerDialog>,
    public dialog: MatDialog,
    private toastr: ToastrService,) {}

  getListCleaner() {
    var req = new HttpParams()
      .set("page", this.page)
      .set("size", this.size);
    this.cleanService.getEmployees(req).subscribe({
      next: (res) => {
        if (res && res.data) {
          this.listCleaner = res.data;
        }
      },
      error: (error) => {},
    });
  }

  addCleaner(){
    console.log(12121212);
    
    this.renderer.addClass(document.body, 'modal-open');
          this.dialogRef = this.dialog.open(AddCleanerDialog, {
            width: '700px',
            maxHeight: '65%',
            data: {
              data: this.data,
            },
            panelClass: ['add-cleaner']
          });
          this.dialogRef.afterClosed().subscribe(result => {
            if (result) {
            }
            this.renderer.removeClass(document.body, 'modal-open');
            // this.dialogService.sendDataDialog(false);
          });
  }

  ngOnInit() {
    this.jwtToken = this.authService.getJwtToken();
    this.ageRange = ["18 - 25", "25 - 35", "35 - 45", "> 45"];
    this.rateRange = ["0 - 1", "1 - 2", "2 - 3", "3 - 4", "4 - 5"];
    this.statusRange = ["Đang hoạt động", "Không hoạt động"];
    this.getListCleaner();
    
  }

  removeUser() {}

  changeStatus() {}

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

  searchEmp() {}
}