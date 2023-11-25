import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

declare interface RouteInfo {
  path: string;
  title: string;
  icon: string;
  class: string;
}
export const ROUTES: RouteInfo[] = [
  { path: '/dashboard', title: 'Thống kê', icon: 'ni-tv-2', class: '' },
  { path: '/maps', title: 'Bản đồ', icon: 'ni-pin-3 text-orange', class: '' },
  { path: '/tables', title: 'Danh sách nhân viên', icon: 'ni-bullet-list-67 text-red', class: '' },
  { path: '/schedule', title: 'Lịch công việc', icon: 'ni-calendar-grid-58', class: '' },
  { path: '/history', title: 'Lịch sử công việc', icon: 'fas fa-history', class: '' },
  { path: '/user-profile', title: 'Thông tin cá nhân', icon: 'fas fa-user', class: '' },
];

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {

  public menuItems: any[];
  public isCollapsed = true;
  adminNavbar = false;

  constructor(private router: Router, private route: ActivatedRoute, private authService: AuthService) {
    this.menuItems = this.setActiveClass(ROUTES);
  }

  ngOnInit() {
    if(this.authService.getJwtToken()){
      this.adminNavbar = true;
    }
    this.route.url.subscribe(segments => {
      const currentUrl = segments.join('/');
      this.menuItems = this.setActiveClass(ROUTES, currentUrl);
    });


    this.menuItems = ROUTES.filter(menuItem => menuItem);
    this.router.events.subscribe((event) => {
      this.isCollapsed = true;
    });



  }

  private setActiveClass(routes: RouteInfo[], currentUrl: string = ''): RouteInfo[] {
    return routes.map(route => {
      route.class = (route.path === currentUrl) ? 'text-primary' : '';
      return route;
    });
  }

  logout() {
    this.authService.signout().subscribe({
      next: () => {
        this.router.navigate(["/login"]);
      },
    });
  }
}