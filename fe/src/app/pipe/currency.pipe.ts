// src/app/price-format.pipe.ts
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'priceFormat'
})
export class PriceFormatPipe implements PipeTransform {
  transform(value: number): string {
    // const formattedValue = new Intl.NumberFormat('en-US', {
    //   style: 'currency',
    //   currency: 'VND'
    // }).format(value);
    // return formattedValue;
    const formattedValue = value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.');

    return formattedValue + ' VND';
  }
}
