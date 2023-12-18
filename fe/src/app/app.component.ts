import { Component, OnInit, Inject, Renderer2, ElementRef, ViewChild, HostListener } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { DOCUMENT } from '@angular/common';
import { LocationStrategy, PlatformLocation, Location } from '@angular/common';
import { filter, Observable, Subscription } from 'rxjs';
import { AuthService } from './services/auth.service';

var didScroll;
var lastScrollTop = 0;
var delta = 5;
var navbarHeight = 0;

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
    private _router: Subscription;
    adminNavbar = false;
    cleanerNavbar = false;
    customerNavbar = false;
    guestNavbar = false;
    managerNavbar = false;
    leadNavbar = false;
    isAuthenticated = false;
    isAuthenticated$: Observable<boolean>;
    username: any;
    id: any;
    
    constructor(private authService: AuthService, private renderer: Renderer2, private router: Router, @Inject(DOCUMENT,) private document: any, private element: ElementRef, public location: Location) { }
    @HostListener('window:scroll', ['$event'])
    hasScrolled() {

        var st = window.pageYOffset;
        // Make sure they scroll more than delta
        if (Math.abs(lastScrollTop - st) <= delta)
            return;

        var navbar = document.getElementsByTagName('nav')[0];

        // If they scrolled down and are past the navbar, add class .headroom--unpinned.
        // This is necessary so you never see what is "behind" the navbar.
        if (st > lastScrollTop && st > navbarHeight) {
            // Scroll Down
            if (navbar.classList.contains('headroom--pinned')) {
                navbar.classList.remove('headroom--pinned');
                navbar.classList.add('headroom--unpinned');
            }
            // $('.navbar.headroom--pinned').removeClass('headroom--pinned').addClass('headroom--unpinned');
        } else {
            // Scroll Up
            //  $(window).height()
            if (st + window.innerHeight < document.body.scrollHeight) {
                // $('.navbar.headroom--unpinned').removeClass('headroom--unpinned').addClass('headroom--pinned');
                if (navbar.classList.contains('headroom--unpinned')) {
                    navbar.classList.remove('headroom--unpinned');
                    navbar.classList.add('headroom--pinned');
                }
            }
        }

        lastScrollTop = st;
    };
    ngOnInit() {
        this.isAuthenticated$ = this.authService.isAuthenticated$;

            this.guestNavbar = true;
            this.authService.isAuthenticated$.subscribe((status) => {
                this.isAuthenticated = status;            
                if(status){
                    this.guestNavbar = false;
                    this.authService.getUserInfor().subscribe(user => {      
                    this.username = user.data.username;
                    this.id = user.data.id;
                        if(user.data.role.name == "CLEANER"){
                            this.cleanerNavbar = true;                        
                        } else if (user.data.role.name == "CUSTOMER"){
                            this.customerNavbar = true;
                        } else if(user.data.role.name == "MANAGER"){
                            this.managerNavbar = true;
                        } else if(user.data.role.name == "LEADER"){
                            console.log(1111111111);
                            
                            this.leadNavbar = true;
                        } else if(user.data.role.name == "ADMIN"){
                            this.adminNavbar = true;
                        }
                    },
                    (error) => {
                      // Handle error
                      console.error('Error fetching user information:', error);
                    });
                } else {
                    this.guestNavbar = true;
                    this.customerNavbar = false;
                    this.adminNavbar = false;
                    this.cleanerNavbar = false;                        
                    this.managerNavbar = false;                        
                    this.leadNavbar = false;                        
                }
              });
        // }

        
        
        
        var navbar: HTMLElement = this.element.nativeElement.children[0].children[0];
        this._router = this.router.events.pipe(filter(event => event instanceof NavigationEnd)).subscribe((event: NavigationEnd) => {
            if (window.outerWidth > 991) {
                window.document.children[0].scrollTop = 0;
            } else {
                window.document.activeElement.scrollTop = 0;
            }
            this.renderer.listen('window', 'scroll', (event) => {
                const number = window.scrollY;
                if (number > 150 || window.pageYOffset > 150) {
                    // add logic
                    navbar.classList.add('headroom--not-top');
                } else {
                    // remove logic
                    navbar.classList.remove('headroom--not-top');
                }
            });
        });
        this.hasScrolled();
    }
}