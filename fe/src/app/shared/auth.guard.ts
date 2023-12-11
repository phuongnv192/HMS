import { Injectable } from "@angular/core";
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  RouterStateSnapshot,
} from "@angular/router";
import { AuthService } from "../services/auth.service";

@Injectable({
  providedIn: "root",
})
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    console.log(state.url);
    console.log(this.authService.isTokenExpired());

    if (this.authService.isTokenExpired()) {
      sessionStorage.removeItem("token");
      this.router.navigate(["/login"]);
      return false;
    }
    return true;
  }
}