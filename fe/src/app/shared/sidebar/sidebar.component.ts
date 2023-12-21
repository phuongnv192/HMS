import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';
import { CacheService } from 'src/app/services/cache.service';

declare interface RouteInfoManager {
  path: string;
  title: string;
  icon: string;
  class: string;
}

declare interface RouteInfoCleaner {
  path: string;
  title: string;
  icon: string;
  class: string;
}

declare interface RouteInfoAdmin {
  path: string;
  title: string;
  icon: string;
  class: string;
}

declare interface RouteInfoLead {
  path: string;
  title: string;
  icon: string;
  class: string;
}

export const ROUTES1: RouteInfoCleaner[] = [
  { path: '/schedule', title: 'Lịch công việc', icon: 'ni-calendar-grid-58', class: '' },
  { path: '/history', title: 'Lịch sử công việc', icon: 'fas fa-history', class: '' },
  // { path: '/customer-profile', title: 'Thông tin cá nhân', icon: 'fas fa-user', class: '' },
];

export const ROUTES2: RouteInfoManager[] = [
  { path: '/dashboard', title: 'Thống kê', icon: 'ni-tv-2', class: '' },
  // { path: '/list-customer', title: 'Danh sách nhân viên', icon: 'ni-bullet-list-67 text-red', class: '' },
  // { path: '/booking-management', title: 'Quản lý dịch vụ', icon: 'ni-bullet-list-67 text-red', class: '' },
];

export const ROUTES3: RouteInfoAdmin[] = [
  { path: '/list-customer', title: 'Danh sách người dùng', icon: 'people-fill', class: '' },
  { path: '/support-help', title: 'Thông báo hỗ trợ', icon: 'people-fill', class: '' },
];

export const ROUTES4: RouteInfoLead[] = [
  // { path: '/list-cleaner', title: 'Danh sách nhân viên', icon: 'ni-bullet-list-67 text-red', class: '' },
  { path: '/booking-management', title: 'Quản lý dịch vụ', icon: 'ni-bullet-list-67 text-red', class: '' },
  { path: '/support-help', title: 'Thông báo hỗ trợ', icon: 'people-fill', class: '' },
];

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {

  public menuItems: any[];
  public menuItemsCleaner: any[];
  public menuItemsManager: any[];
  public menuItemsAdmin: any[];
  public menuItemsLead: any[];
  public isCollapsed = true;
  @Input() public adminNavbar: any;
  @Input() public cleanerNavbar: any;
  @Input() public leadNavbar: any;
  @Input() public managerNavbar: any;

  constructor(private router: Router, private route: ActivatedRoute, private authService: AuthService, private cacheService: CacheService) {
    this.menuItemsCleaner = this.setActiveClassCleaner(ROUTES1);
    this.menuItemsManager = this.setActiveClassManager(ROUTES2);
    this.menuItemsAdmin = this.setActiveClassAdmin(ROUTES3);
    this.menuItemsLead = this.setActiveClassLead(ROUTES4);
  }

  ngOnInit() {
    // if (this.cacheService.getHasClearedCache()) {
    //   this.refresh();
      
    //   this.cacheService.setHasClearedCache(false);
    // }
    
    this.route.url.subscribe(segments => {
      const currentUrl = segments.join('/');
      if(this.cleanerNavbar){
        this.menuItemsCleaner = this.setActiveClassCleaner(ROUTES1, currentUrl);
        this.menuItemsCleaner = ROUTES1.filter(menuItem => menuItem);
      } else if(this.managerNavbar){
        this.menuItemsManager = this.setActiveClassManager(ROUTES2, currentUrl);
        this.menuItemsManager = ROUTES2.filter(menuItem => menuItem);
      } else if(this.adminNavbar){
        this.menuItemsAdmin = this.setActiveClassAdmin(ROUTES3, currentUrl);
        this.menuItemsAdmin = ROUTES3.filter(menuItem => menuItem);
      } else if(this.leadNavbar){        
        this.menuItemsLead = this.setActiveClassLead(ROUTES4, currentUrl);
        this.menuItemsLead = ROUTES4.filter(menuItem => menuItem);
      }
    });


    this.router.events.subscribe((event) => {
      this.isCollapsed = true;
    });

  }

  private setActiveClassCleaner(routes: RouteInfoCleaner[], currentUrl: string = ''): RouteInfoCleaner[] {
    return routes.map(route => {
      route.class = (route.path === currentUrl) ? 'text-primary' : '';
      return route;
    });
  }

  private setActiveClassManager(routes: RouteInfoManager[], currentUrl: string = ''): RouteInfoManager[] {
    return routes.map(route => {
      route.class = (route.path === currentUrl) ? 'text-primary' : '';
      return route;
    });
  }

  private setActiveClassAdmin(routes: RouteInfoAdmin[], currentUrl: string = ''): RouteInfoAdmin[] {
    return routes.map(route => {
      route.class = (route.path === currentUrl) ? 'text-primary' : '';
      return route;
    });
  }

  private setActiveClassLead(routes: RouteInfoLead[], currentUrl: string = ''): RouteInfoLead[] {
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

  refresh(){
    this.cleanerNavbar = false;
    this.managerNavbar = false;
    this.adminNavbar = false;
    this.leadNavbar = false;
  }
}