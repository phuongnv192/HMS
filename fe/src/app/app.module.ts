import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RouterModule } from '@angular/router';
import { AppRoutingModule } from './app.routing';

import { AppComponent } from './app.component';
import { IntroductionComponent } from './introduction/introduction.component';
import { ProfileComponent } from './profile/profile.component';
import { NavbarComponent } from './shared/navbar/navbar.component';
import { FooterComponent } from './shared/footer/footer.component';

import { HomeModule } from './home/home.module';
import { LoginComponent } from './auth/login/login.component';
import { ForgotPWComponent } from './auth/forgot-password/forgot-pw.component';
import { BookingComponent } from './booking/booking.component';
import { SidebarComponent } from './shared/sidebar/sidebar.component';
import { UserProfileComponent } from './employee/user-profile/user-profile.component';
import { SignupComponent } from './auth/signup/signup.component';
import { DashboardComponent } from './employee/dashboard/dashboard.component';
import { TablesComponent } from './employee/tables.component';
import { HistoryComponent } from './employee/history/history.component';


@NgModule({
  declarations: [
    AppComponent,
    SignupComponent,
    IntroductionComponent,
    BookingComponent,
    ProfileComponent,
    NavbarComponent,
    SidebarComponent,
    FooterComponent,
    LoginComponent,
    ForgotPWComponent,
    UserProfileComponent,
    DashboardComponent,
    TablesComponent,
    HistoryComponent,
  ],
  imports: [
    BrowserModule,
    NgbModule,
    FormsModule,
    RouterModule,
    AppRoutingModule,
    HomeModule,

  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }