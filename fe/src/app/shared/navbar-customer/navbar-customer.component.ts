import { Component, Input, OnInit, Renderer2 } from "@angular/core";
import { Router, NavigationEnd, NavigationStart } from "@angular/router";
import { Location, PopStateEvent } from "@angular/common";
import { ROUTES1 } from "../sidebar/sidebar.component";
import { ROUTES2 } from "../sidebar/sidebar.component";
import { ROUTES3 } from "../sidebar/sidebar.component";
import { ROUTES4 } from "../sidebar/sidebar.component";
import { AuthService } from "src/app/services/auth.service";
import { CacheService } from "src/app/services/cache.service";
import { CleanerRateDialog } from "src/app/booking/cleaner-rate-dialog/cleaner-rate-dialog";
import { MatDialog, MatDialogRef } from "@angular/material/dialog";

@Component({
  selector: "app-navbar-customer",
  templateUrl: "./navbar-customer.component.html",
  styleUrls: ["./navbar-customer.component.scss"],
})
export class NavbarCustomerComponent implements OnInit {
  public isCollapsed = true;
  public focus;
  private lastPoppedUrl: string;
  private yScrollStack: number[] = [];
  listTitles: any[];
  @Input() public adminNavbar: any;
  @Input() public managerNavbar: any;
  @Input() public cleanerNavbar: any;
  @Input() public customerNavbar: any;
  @Input() public guestNavbar: any;
  @Input() public leadNavbar: any;
  // @Input() public username: any;
  @Input() public id: any;
  username: string;

  constructor(
    public location: Location,
    private router: Router,
    private authService: AuthService,
    private cacheService: CacheService,
    public dialog: MatDialog, private renderer: Renderer2,
    // private dialogService: DialogService
    public ratedialogRef: MatDialogRef<CleanerRateDialog>,
  ) { }

  ngOnInit() {
    if (!this.authService.getJwtToken()) {
      this.username = '';
    }

    if(sessionStorage.getItem("Username")){
      this.username = sessionStorage.getItem("Username");
    }

    this.router.events.subscribe((event) => {
      this.isCollapsed = true;
      if (event instanceof NavigationStart) {
        if (event.url != this.lastPoppedUrl)
          this.yScrollStack.push(window.scrollY);
      } else if (event instanceof NavigationEnd) {
        if (event.url == this.lastPoppedUrl) {
          this.lastPoppedUrl = undefined;
          window.scrollTo(0, this.yScrollStack.pop());
        } else window.scrollTo(0, 0);
      }
    });
    this.location.subscribe((ev: PopStateEvent) => {
      this.lastPoppedUrl = ev.url;
    });
  }

  isHome() {
    var titlee = this.location.prepareExternalUrl(this.location.path());

    if (titlee === "home") {
      return true;
    } else {
      return false;
    }
  }
  isDocumentation() {
    var titlee = this.location.prepareExternalUrl(this.location.path());
    if (titlee === "documentation") {
      return true;
    } else {
      return false;
    }
  }

  getTitle() {
    var titlee = this.location.prepareExternalUrl(this.location.path());
    if (titlee.charAt(0) === "#") {
      titlee = titlee.slice(1);
    }

    for (var item = 0; item < this.listTitles.length; item++) {
      if (this.listTitles[item].path === titlee) {
        return this.listTitles[item].title;
      }
    }
    return "Dashboard";
  }

  logout() {
    this.authService.signout().subscribe({
      next: () => {
        sessionStorage.removeItem("roleName");
        this.authService.setAuthenticationStatus(false);
        // this.refresh();
        console.log("CUSTOMER LOG OUT", sessionStorage.getItem("roleName"));
        this.router.navigate(["/login"]);
      },
    });
  }

  refresh(){
    this.cleanerNavbar = false;
    this.managerNavbar = false;
    this.adminNavbar = false;
    this.customerNavbar = false;
    this.leadNavbar = false;
    this.guestNavbar = true;
  }


  review(id: any){
    this.renderer.addClass(document.body, 'modal-open');
    this.ratedialogRef = this.dialog.open(CleanerRateDialog, {
      width: '500px',
      maxHeight: '65%',
      data: {
        data: id,
      },
      panelClass: ['cleaner-review']
    });

    this.ratedialogRef.afterClosed().subscribe(result => {
      // console.log('The dialog was closed');
      this.renderer.removeClass(document.body, 'modal-open');
      // this.dialogService.sendDataDialog(false);
    });
  }
}