import { Pipe, PipeTransform } from '@angular/core';
import { DatePipe } from '@angular/common';

@Pipe({
  name: 'dateFormat'
})
export class DateFormatPipe implements PipeTransform {
  transform(value: string): string {
    const datePipe = new DatePipe('en-US');
    // Chuyển đổi ngày thành dạng 'dd-MM-yyyy'
    return datePipe.transform(value, 'dd-MM-yyyy');
  }
}