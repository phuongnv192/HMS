// cache.service.ts
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class CacheService {
  private hasClearedCache = false;

  getHasClearedCache(): boolean {
    return this.hasClearedCache;
  }

  setHasClearedCache(value: boolean): void {
    this.hasClearedCache = value;
  }
}
