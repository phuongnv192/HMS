import { Component, OnInit } from "@angular/core";
import { Router, NavigationEnd, NavigationStart } from "@angular/router";
import { Location, PopStateEvent } from "@angular/common";
import { ROUTES } from "../sidebar/sidebar.component";
import { AuthService } from "src/app/services/auth.service";

@Component({
  selector: "app-navbar",
  templateUrl: "./navbar.component.html",
  styleUrls: ["./navbar.component.scss"],
})
export class NavbarComponent implements OnInit {
  public isCollapsed = true;
  public focus;
  private lastPoppedUrl: string;
  private yScrollStack: number[] = [];
  listTitles: any[];
<<<<<<< HEAD
=======
  adminNavbar = false;
>>>>>>> d15a1657bfc7f05681c843344bc133d35448396b

  constructor(
    public location: Location,
    private router: Router,
    private authService: AuthService
  ) {}
<<<<<<< HEAD

  ngOnInit() {
    this.listTitles = ROUTES.filter((listTitle) => listTitle);

=======

  ngOnInit() {
    if(this.authService.getJwtToken()){
      this.adminNavbar = true;
    }
    this.listTitles = ROUTES.filter((listTitle) => listTitle);

>>>>>>> d15a1657bfc7f05681c843344bc133d35448396b
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
        this.router.navigate(["/login"]);
      },
    });
  }
}